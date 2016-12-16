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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.inrix.sample.R;
import com.inrix.sdk.Error;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.IncidentsManager;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.XDIncident;

import java.util.ArrayList;
import java.util.List;

import static com.inrix.sample.util.GeoPointHelper.toLatLng;


/**
 * Demonstrates XDIncident search InBox Capability.
 */
public class XDIncidentFragmentInBox extends SupportMapFragment implements
        IncidentsManager.IXDIncidentResponseListener,
        ClusterManager.OnClusterItemClickListener<XDIncidentFragmentInBox.XDIncidentClusterItem> {

    private final GeoPoint CORNER1 = new GeoPoint(47.689874, -122.354808);
    private final GeoPoint CORNER2 = new GeoPoint(47.572138, -122.152290);
    private final GeoPoint box3 = new GeoPoint(CORNER1.getLatitude(), CORNER2.getLongitude());
    private final GeoPoint box4 = new GeoPoint(CORNER2.getLatitude(), CORNER1.getLongitude());

    private GoogleMap map;
    private List<Marker> resultMarkers;

    private ClusterManager<XDIncidentClusterItem> clusterManager;

    private IncidentsManager manager;
    private ICancellable currentRequest;

    private Polygon polygon;
    private PolygonOptions polygonOptions;

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
     * @param googleMap Map instance.
     */
    private void setUpMap(final GoogleMap googleMap) {
        this.map = googleMap;

        //noinspection MissingPermission
        this.map.setMyLocationEnabled(true);
        this.map.getUiSettings().setMyLocationButtonEnabled(false);
        this.map.getUiSettings().setZoomControlsEnabled(true);
        LatLng centerPoint = new LatLng((CORNER1.getLatitude() + CORNER2.getLatitude()) / 2, (CORNER1.getLongitude() + CORNER2.getLongitude()) / 2);
        this.map.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(centerPoint).zoom(10).build()));
        this.clusterManager = new ClusterManager<>(getActivity(), map);
        this.map.setOnCameraIdleListener(clusterManager);
        this.map.setOnMarkerClickListener(clusterManager);
        this.clusterManager.setOnClusterItemClickListener(this);

        IncidentsManager.XDIncidentOptionsInBox options = new IncidentsManager.XDIncidentOptionsInBox(CORNER1, CORNER2);

        this.currentRequest = manager.getXDIncidentsInBox(options, XDIncidentFragmentInBox.this);
        this.polygonOptions = new PolygonOptions()
                .add(toLatLng(CORNER1), toLatLng(box3), toLatLng(CORNER2), toLatLng(box4))
                .strokeColor(Color.BLUE).geodesic(true);

        this.polygon = this.map.addPolygon(this.polygonOptions);
    }

    @Override
    public boolean onClusterItemClick(XDIncidentClusterItem XDIncidentClusterItem) {
        Toast.makeText(getActivity(), XDIncidentClusterItem.getDescription(), Toast.LENGTH_LONG).show();
        return true;
    }


    public void setXdIncidents(List<Marker> incidents) {
        this.map.clear();

        this.polygon = this.map.addPolygon(polygonOptions);

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

        if (polygon != null) {
            polygon.remove();
        }
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

        public String getDescription() {
            return description;
        }
    }

}
