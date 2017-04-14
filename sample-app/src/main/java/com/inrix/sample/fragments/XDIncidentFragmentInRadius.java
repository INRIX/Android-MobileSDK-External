/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.inrix.sample.R;
import com.inrix.sdk.Error;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.IncidentsManager;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.XDIncident;
import com.inrix.sdk.utils.GeoUtils;
import com.inrix.sdk.utils.UserPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.inrix.sample.util.GeoPointHelper.toLatLng;

/**
 * Demonstrates XDIncident search in radius Capability.
 */
public class XDIncidentFragmentInRadius extends SupportMapFragment implements
        IncidentsManager.IXDIncidentResponseListener, ClusterManager.OnClusterItemClickListener<XDIncidentFragmentInRadius.XDIncidentClusterItem> {

    private static final double RADIUS = 10;
    /**
     * The map.
     */
    private GoogleMap map;

    /**
     * Displays XDIncident locations.
     */
    private List<Marker> resultMarkers;

    private Marker searchMarker;

    /**
     * The Incidents Manager
     */
    private IncidentsManager manager;

    /**
     * Default start position.
     */
    private final GeoPoint SEATTLE_POSITION = new GeoPoint(47.614496, -122.328758);

    private ICancellable currentRequest;
    private Circle circle;

    private ClusterManager<XDIncidentClusterItem> clusterManager;
    private CircleOptions circleOptions;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        this.manager = InrixCore.getIncidentsManager();

        this.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                setUpMap(googleMap);
            }
        });


        return view;
    }

    /**
     * Initializes the map if it wasn't initialized yet.
     *
     * @param googleMap Mpa instance.
     */
    private void setUpMap(GoogleMap googleMap) {
        this.map = googleMap;

        //noinspection MissingPermission
        this.map.setMyLocationEnabled(true);
        this.map.getUiSettings().setMyLocationButtonEnabled(false);
        this.map.getUiSettings().setZoomControlsEnabled(true);
        this.map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(toLatLng(SEATTLE_POSITION)).zoom(10).build()));
        this.map.setOnMapLongClickListener(new MapLongClickListener());
        this.clusterManager = new ClusterManager<>(getActivity(), map);
        this.map.setOnCameraIdleListener(clusterManager);
        this.map.setOnMarkerClickListener(clusterManager);
        this.clusterManager.setOnClusterItemClickListener(this);
    }

    /**
     * Shows the marker.
     *
     * @param text   Text to show.
     * @param latLng Position to show marker.
     */
    private synchronized void showSearchMarker(String text, LatLng latLng) {
        hideMarkers(this.resultMarkers);

        hideMarkers(this.searchMarker);

        this.searchMarker = this.map.addMarker(new MarkerOptions().position(latLng).title(text));

        this.searchMarker.showInfoWindow();
    }

    /**
     * Hides the marker.
     */
    private synchronized void hideMarkers(List<Marker> markers) {
        if (markers != null) {
            for (Marker marker : markers) {
                if (marker != null && marker.isVisible()) {
                    marker.remove();
                }

            }
        }
    }

    private synchronized void hideMarkers(Marker... markers) {
        hideMarkers(Arrays.asList(markers));
    }

    @Override
    public boolean onClusterItemClick(XDIncidentClusterItem XDIncidentClusterItem) {
        Toast.makeText(getActivity(), XDIncidentClusterItem.getDescription(), Toast.LENGTH_LONG).show();
        return true;
    }

    /**
     * Handles long clicks on the map.
     */
    private class MapLongClickListener implements GoogleMap.OnMapLongClickListener {
        @Override
        public void onMapLongClick(LatLng latLng) {
            map.clear();

            clusterManager.clearItems();

            showSearchMarker(getString(R.string.xdincident_searching), latLng);

            final GeoPoint point = new GeoPoint(latLng.latitude, latLng.longitude);

            IncidentsManager.XDIncidentOptionsInRadius options = new IncidentsManager.XDIncidentOptionsInRadius(point, RADIUS);

            currentRequest = manager.getXDIncidentsInRadius(options, XDIncidentFragmentInRadius.this);

            double radiusInMeters = UserPreferences.getSettingUnits().ordinal() == UserPreferences.Unit.METERS.ordinal() ? RADIUS : (RADIUS / GeoUtils.MILE_METER_CONVERSION_FACTOR);

            circleOptions = new CircleOptions().center(latLng).radius(radiusInMeters).strokeColor(Color.RED);

            if (circle != null) {
                circle.remove();
            }
            circle = map.addCircle(circleOptions);
        }
    }

    public void setXdIncidents(List<Marker> incidents) {
        map.clear();

        circle = map.addCircle(circleOptions);

        if (incidents == null) {
            return;
        }

        for (Marker incident : incidents) {
            this.clusterManager.addItem(
                    new XDIncidentClusterItem(incident.getPosition().latitude, incident.getPosition().longitude, incident.getTitle()));
        }

        this.clusterManager.cluster();
    }


    @Override
    public void onResult(List<XDIncident> data) {
        this.clusterManager.clearItems();
        hideMarkers(this.searchMarker);

        if (data != null && data.size() > 0) {
            this.resultMarkers = new ArrayList<>();
            for (XDIncident incident : data) {
                GeoPoint point = incident.getLocation();
                Marker result = this.map.addMarker(new MarkerOptions().position(toLatLng(point)).title(incident.getShortDescription()));
                result.showInfoWindow();
                resultMarkers.add(result);
            }
            setXdIncidents(resultMarkers);

        } else {
            // No matches
            Toast.makeText(getActivity(), R.string.xdincident_noresults, Toast.LENGTH_LONG).show();
            this.resultMarkers = null;
        }

    }


    @Override
    public void onError(Error error) {
        this.clusterManager.clearItems();
        hideMarkers(this.searchMarker);

        switch (error.getErrorType()) {
            case NETWORK_ERROR:
                Toast.makeText(getActivity(), R.string.geocode_status_network_error, Toast.LENGTH_LONG).show();
                break;
            case SDK_ERROR:
                Toast.makeText(getActivity(), R.string.sdk_error, Toast.LENGTH_LONG).show();
                break;
            case SERVER_ERROR:
                Toast.makeText(getActivity(), R.string.inrix_server_error, Toast.LENGTH_LONG).show();
                break;
        }
        this.resultMarkers = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (currentRequest != null) {
            currentRequest.cancel();
            currentRequest = null;
        }

        if (circle != null) {
            circle.remove();
        }
        this.resultMarkers = null;
    }

    public class XDIncidentClusterItem implements ClusterItem {
        private final LatLng position;
        private String description;

        public XDIncidentClusterItem(double lat, double lng, String description) {
            this.position = new LatLng(lat, lng);
            this.description = description;
        }

        @Override
        public LatLng getPosition() {
            return position;
        }

        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public String getSnippet() {
            return null;
        }

        public String getDescription() {
            return description;
        }
    }

}
