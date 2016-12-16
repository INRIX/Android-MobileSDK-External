/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.activity;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener;
import com.inrix.sample.R;
import com.inrix.sample.fragments.ReportIncidentDialogFragment;
import com.inrix.sample.fragments.ReportIncidentDialogFragment.IReportIncidentListener;
import com.inrix.sample.map.MapIncidentItem;
import com.inrix.sdk.Error;
import com.inrix.sdk.IncidentsManager;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Incident;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Contains samples showing how to manage incidents: report, confirm and delete.
 */
public class IncidentManagementActivity extends InrixSdkActivity implements OnMyLocationChangeListener, OnMapLongClickListener, IReportIncidentListener, OnClusterItemClickListener<MapIncidentItem>, OnMapClickListener, OnClickListener {
    private static final int REFRESH_TIME = 30 * 1000;

    private final Handler refreshHandler = new Handler();
    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            refreshIncidentsOnMap();
            refreshHandler.postDelayed(this, REFRESH_TIME);
        }
    };

    private boolean syncInProgress;

    private GoogleMap map;
    private ClusterManager<MapIncidentItem> clusterManager;

    private IncidentsManager manager;
    private IncidentsManager.IncidentRadiusOptions options;
    private SessionIncidentCache localCache;

    private LinearLayout incidentInfoContainer;
    private ImageView incidentIcon;
    private TextView incidentTitle;
    private TextView incidentDescription;
    private LinearLayout incidentConfirmContainer;
    private Button incidentConfirmButton;
    private Button incidentClearButton;
    private Button incidentDeleteButton;

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_incident_management;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        this.manager = InrixCore.getIncidentsManager();

        this.incidentInfoContainer = (LinearLayout) this.findViewById(R.id.incident_info_container);
        this.incidentIcon = (ImageView) this.findViewById(R.id.incident_info_icon);
        this.incidentTitle = (TextView) this.findViewById(R.id.incident_info_type);
        this.incidentDescription = (TextView) this.findViewById(R.id.incident_info_description);
        this.incidentConfirmContainer = (LinearLayout) this.findViewById(R.id.incident_confirm_container);

        this.incidentConfirmButton = (Button) this.findViewById(R.id.incident_confirm_button);
        this.incidentConfirmButton.setOnClickListener(this);

        this.incidentClearButton = (Button) this.findViewById(R.id.incident_clear_button);
        this.incidentClearButton.setOnClickListener(this);

        this.incidentDeleteButton = (Button) this.findViewById(R.id.incident_delete_button);
        this.incidentDeleteButton.setOnClickListener(this);

        this.localCache = new SessionIncidentCache();

        // Obtain reference to map control.
        final SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                initMap();
            }
        });
    }

    private void initMap() {
        // Setup map.
        //noinspection MissingPermission
        this.map.setMyLocationEnabled(true);
        this.map.setOnMyLocationChangeListener(this);
        this.map.setOnMapLongClickListener(this);

        // Setup incidents clustering on map.
        this.clusterManager = new ClusterManager<>(this, this.map);
        this.clusterManager.setOnClusterItemClickListener(this);

        this.map.setOnCameraIdleListener(this.clusterManager);
        this.map.setOnMarkerClickListener(this.clusterManager);
        this.map.setOnMapClickListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();

        this.refreshHandler.postDelayed(this.refreshRunnable, REFRESH_TIME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        this.refreshHandler.removeCallbacks(this.refreshRunnable);

        super.onPause();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.incident_mgmt, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                this.refreshIncidentsOnMap();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void onMyLocationChange(Location location) {
        if (this.map == null) {
            return;
        }

        final LatLng coords = new LatLng(location.getLatitude(), location.getLongitude());
        this.map.animateCamera(CameraUpdateFactory.newLatLngZoom(coords, 13));

        // Update incidents search options.
        final GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
        this.options = new IncidentsManager.IncidentRadiusOptions(point, 10);
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void onMapLongClick(LatLng coords) {
        if (this.manager == null) {
            return;
        }

        final FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        final DialogFragment current = (DialogFragment) this.getSupportFragmentManager().findFragmentByTag("reportDialog");
        if (current != null) {
            transaction.remove(current);
        }

        transaction.addToBackStack(null);
        ReportIncidentDialogFragment.newInstance(coords).with(this).show(transaction, "reportDialog");
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void onMapClick(LatLng coords) {
        this.incidentInfoContainer.setVisibility(View.GONE);
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public final void reportIncident(final GeoPoint location, final Incident.IncidentType type, final IncidentsManager.RoadSide side) {
        IncidentsManager.IncidentReportOptions options = null;
        switch (type) {
            case ACCIDENT:
                options = IncidentsManager.IncidentReportOptions.getReportAccidentOptions();
                break;
            case CONSTRUCTION:
                options = IncidentsManager.IncidentReportOptions.getReportConstructionOptions();
                break;
            case HAZARD:
                options = IncidentsManager.IncidentReportOptions.getReportHazardOptions();
                break;
            case POLICE:
                options = IncidentsManager.IncidentReportOptions.getReportPoliceOptions();
                break;
        }

        //noinspection ConstantConditions
        options.setSideOfRoad(side);

        this.manager.reportIncident(options, new IncidentsManager.IIncidentReportListener() {
            @Override
            public void onResult(Incident data) {
                localCache.put(data.getId(), data.getVersion(), type, location.getLatitude(), location.getLongitude());
                updateClusterItems();
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(IncidentManagementActivity.this, "Failed to report incident", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onClusterItemClick(MapIncidentItem item) {
        final IncidentViewModel model = this.localCache.get(item.getId());
        final Incident incident = model.getIncident();

        String title = null;
        int typeResource = 0;
        switch (model.getType()) {
            case ACCIDENT:
                title = this.getString(R.string.incident_accident);
                typeResource = R.drawable.accident;
                break;
            case CONSTRUCTION:
                title = this.getString(R.string.incident_construction);
                typeResource = R.drawable.construction;
                break;
            case HAZARD:
                title = this.getString(R.string.incident_hazard);
                typeResource = R.drawable.hazard;
                break;
            case POLICE:
                title = this.getString(R.string.incident_police);
                typeResource = R.drawable.police;
                break;
            case EVENT:
                title = this.getString(R.string.incident_event);
                typeResource = R.drawable.construction;
                break;
            case CONGESTION:
                title = this.getString(R.string.incident_congestion);
                typeResource = R.drawable.congestion;
                break;
        }

        String description;
        if (incident == null) {
            description = "";
        } else {
            if (incident.getShortDescription() == null) {
                if (incident.getFullDescription() == null) {
                    description = "";
                } else {
                    description = incident.getFullDescription();
                }
            } else {
                description = incident.getShortDescription();
            }
        }

        if (model.isUserReported()) {
            this.incidentDeleteButton.setVisibility(View.VISIBLE);
            this.incidentConfirmContainer.setVisibility(View.GONE);
        } else {
            this.incidentDeleteButton.setVisibility(View.GONE);
            if (incident != null && incident.isUgi()) {
                this.incidentConfirmContainer.setVisibility(View.VISIBLE);
            } else {
                this.incidentConfirmContainer.setVisibility(View.GONE);
            }
        }

        this.incidentTitle.setText(title);
        this.incidentDescription.setText(description);
        this.incidentIcon.setImageResource(typeResource);

        this.incidentInfoContainer.setVisibility(View.VISIBLE);
        this.localCache.setActive(item.getId());
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onClick(View view) {
        this.incidentInfoContainer.setVisibility(View.GONE);

        final IncidentViewModel model = this.localCache.getActive();
        if (model == null) {
            return;
        }

        switch (view.getId()) {
            case R.id.incident_confirm_button:
                final IncidentsManager.IncidentReviewOptions confirmOptions =
                        new IncidentsManager.IncidentReviewOptions(model.getId(), model.getVersion()).setConfirmed(true);

                this.manager.reviewIncident(confirmOptions, new IncidentsManager.IIncidentReviewListener() {
                    @Override
                    public void onResult(Boolean data) {
                        Toast.makeText(IncidentManagementActivity.this, "Incident confirmed. Thank you.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(IncidentManagementActivity.this, "Failed to confirm incident", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case R.id.incident_clear_button:
                final IncidentsManager.IncidentReviewOptions clearOptions =
                        new IncidentsManager.IncidentReviewOptions(model.getId(), model.getVersion()).setConfirmed(false);

                this.manager.reviewIncident(clearOptions, new IncidentsManager.IIncidentReviewListener() {
                    @Override
                    public void onResult(Boolean data) {
                        localCache.remove(model.getId());
                        updateClusterItems();
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(IncidentManagementActivity.this, "Failed to cancel incident", Toast.LENGTH_LONG).show();
                    }
                });
                break;
            case R.id.incident_delete_button:
                final IncidentsManager.IncidentDeleteOptions deleteOptions =
                        new IncidentsManager.IncidentDeleteOptions(model.getId(), model.getVersion());

                this.manager.deleteIncident(deleteOptions, new IncidentsManager.IIncidentDeleteListener() {
                    @Override
                    public void onResult(Boolean data) {
                        localCache.remove(model.getId());
                        updateClusterItems();
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(IncidentManagementActivity.this, "Failed to delete incident", Toast.LENGTH_LONG).show();
                    }
                });
                break;
        }

    }

    private void updateClusterItems() {
        clusterManager.clearItems();

        for (final IncidentViewModel current : localCache.getAll()) {
            clusterManager.addItem(new MapIncidentItem(
                    current.getLatitude(),
                    current.getLongitude(),
                    current.getId(),
                    current.getType()));
        }

        clusterManager.cluster();
    }

    /**
     * Requests incidents and display them on the map.
     */
    @SuppressWarnings("deprecation")
    private synchronized void refreshIncidentsOnMap() {
        // Skip refresh, we don't a current location yet.
        if (this.options == null || this.manager == null) {
            return;
        }

        if (this.syncInProgress) {
            return;
        }

        this.syncInProgress = true;
        this.setProgressBarIndeterminateVisibility(true);

        this.manager.getIncidentsInRadius(this.options, new IncidentsManager.IIncidentsResponseListener() {
            @Override
            public void onResult(List<Incident> data) {
                for (final Incident current : data) {
                    android.util.Log.d("INCIDENT", "" + current.getId() + " : " + current.getVersion());
                    localCache.put(current);
                }

                updateClusterItems();

                syncInProgress = false;
                setProgressBarIndeterminateVisibility(false);
            }

            @Override
            public void onError(Error error) {
                syncInProgress = false;
                setProgressBarIndeterminateVisibility(false);
            }
        });
    }

    private static final class IncidentViewModel {
        private Incident incident;
        private long id;
        private int version;
        private Incident.IncidentType type;
        private double latitude;
        private double longitude;
        private boolean userReported;
        private boolean active;

        public IncidentViewModel(long id, int version, Incident.IncidentType type, double lat, double lon) {
            this.id = id;
            this.version = version;
            this.type = type;
            this.longitude = lon;
            this.latitude = lat;
        }

        public long getId() {
            return id;
        }

        public int getVersion() {
            return this.version;
        }

        public Incident.IncidentType getType() {
            return type;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public boolean isUserReported() {
            return this.userReported;
        }

        public IncidentViewModel setUserReported(boolean value) {
            this.userReported = value;
            return this;
        }

        public Incident getIncident() {
            return this.incident;
        }

        public IncidentViewModel setIncident(Incident value) {
            this.incident = value;
            return this;
        }

        public boolean isActive() {
            return this.active;
        }

        public IncidentViewModel setActive(boolean value) {
            this.active = value;
            return this;
        }
    }

    private static final class SessionIncidentCache {
        private HashMap<Long, IncidentViewModel> cache;

        @SuppressLint("UseSparseArrays")
        public SessionIncidentCache() {
            this.cache = new HashMap<>();
        }

        public void remove(long id) {
            this.cache.remove(id);
        }

        public void setActive(long id) {
            final IncidentViewModel current = this.get(id);
            if (current == null) {
                return;
            }

            for (final IncidentViewModel item : this.cache.values()) {
                item.setActive(false);
            }

            current.setActive(true);
        }

        public IncidentViewModel getActive() {
            for (final IncidentViewModel item : this.cache.values()) {
                if (item.isActive()) {
                    return item;
                }
            }

            return null;
        }

        public void put(Incident incident) {
            boolean userReported = false;
            IncidentViewModel model = this.get(incident.getId());
            if (model != null) {
                userReported = model.isUserReported();
            }

            this.cache.put(incident.getId(), new IncidentViewModel(
                    incident.getId(),
                    incident.getVersion(),
                    incident.getType(),
                    incident.getLatitude(),
                    incident.getLongitude())
                    .setUserReported(userReported)
                    .setIncident(incident));
        }

        public void put(long id, int version, Incident.IncidentType type, double latitude, double longitude) {
            this.cache.put(id, new IncidentViewModel(id, version, type, latitude, longitude).setUserReported(true));
        }

        public IncidentViewModel get(long id) {
            return this.cache.get(id);
        }

        public List<IncidentViewModel> getAll() {
            final List<IncidentViewModel> result = new LinkedList<>();

            for (final IncidentViewModel item : this.cache.values()) {
                result.add(item);
            }

            return result;
        }
    }
}
