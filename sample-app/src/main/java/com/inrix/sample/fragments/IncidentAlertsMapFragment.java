/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.fragments;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.inrix.sample.BusProvider;
import com.inrix.sample.IncidentsReceivedEvent;
import com.inrix.sample.map.MapClusterItem;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Incident;
import com.squareup.otto.Subscribe;

import java.util.List;

import static com.inrix.sample.util.GeoPointHelper.toLatLng;

public class IncidentAlertsMapFragment extends SupportMapFragment {
    private static final GeoPoint SEATTLE_POSITION = new GeoPoint(47.614496, -122.328758);

    private GoogleMap map;
    private ClusterManager<MapClusterItem> clusterManager;
    private Location lastKnownLocation;
    private List<Incident> pendingIncidents;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        this.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                setUpMapIfNeeded(googleMap);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BusProvider.getBus().register(this);
    }

    @Override
    public void onDetach() {
        BusProvider.getBus().unregister(this);
        super.onDetach();
    }

    private void setUpMapIfNeeded(GoogleMap googleMap) {
        this.map = googleMap;

        //noinspection MissingPermission
        this.map.setMyLocationEnabled(true);
        this.map.getUiSettings().setMyLocationButtonEnabled(false);
        this.map.getUiSettings().setZoomControlsEnabled(true);
        this.map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location location) {
                lastKnownLocation = location;
            }
        });

        this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(toLatLng(SEATTLE_POSITION), 12));

        this.clusterManager = new ClusterManager<>(getActivity(), this.map);

        this.map.setOnCameraIdleListener(this.clusterManager);

        if (this.pendingIncidents != null) {
            this.setIncidents(this.pendingIncidents);
        }
    }

    @Subscribe
    public void onIncidentsReceived(IncidentsReceivedEvent incidentsEvent) {
        if (this.map == null) {
            this.pendingIncidents = incidentsEvent.getIncidents();
        } else {
            setIncidents(incidentsEvent.getIncidents());
        }
    }

    private void moveMapToCurrentLocation() {
        Location currentLocation = this.lastKnownLocation;

        if (currentLocation == null) {
            return;
        }
        this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(toLatLng(GeoPoint.fromLocation(currentLocation)), 10));

        MarkerOptions markerOptions = new MarkerOptions();
        LatLng currentMapPosition = toLatLng(GeoPoint.fromLocation(currentLocation));
        markerOptions.position(currentMapPosition);

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        markerOptions.flat(true);
        markerOptions.rotation(currentLocation.getBearing());

        this.map.addMarker(markerOptions);
    }

    public void setIncidents(List<Incident> incidents) {
        this.map.clear();
        this.clusterManager.clearItems();

        if (incidents == null) {
            return;
        }
        moveMapToCurrentLocation();

        for (Incident incident : incidents) {
            this.clusterManager.addItem(new MapClusterItem(incident.getLatitude(), incident.getLongitude()));
        }
        this.clusterManager.cluster();
        this.pendingIncidents = null;
    }
}
