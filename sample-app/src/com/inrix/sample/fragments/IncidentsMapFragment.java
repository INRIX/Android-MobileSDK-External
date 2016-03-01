/**
 * Copyright (c) 2013-2015 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.maps.android.clustering.ClusterManager;
import com.inrix.sample.BusProvider;
import com.inrix.sample.IncidentsReceivedEvent;
import com.inrix.sample.map.MapClusterItem;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Incident;
import com.squareup.otto.Subscribe;

import java.util.List;

public class IncidentsMapFragment extends SupportMapFragment {

    private GoogleMap map = null;
    private ClusterManager<MapClusterItem> clusterManager;
    private final GeoPoint SEATTLE_POSITION = new GeoPoint(47.614496, -122.328758);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        setUpMapIfNeeded();
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

    private void setUpMapIfNeeded() {
        if (this.map != null) {
            return;
        }
        this.map = getMap();
        if (map != null) {
            map.setMyLocationEnabled(false);
            map.getUiSettings().setMyLocationButtonEnabled(false);
            map.getUiSettings().setZoomControlsEnabled(true);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(SEATTLE_POSITION.toLatLng(), 12));
            clusterManager = new ClusterManager<>(getActivity(), map);
            this.map.setOnCameraChangeListener(clusterManager);
        }
    }

    public void setIncidents(List<Incident> incidents) {
        setUpMapIfNeeded();
        if (this.map == null) {
            return;
        }
        this.map.clear();
        if (incidents == null) {
            return;
        }
        for (Incident incident : incidents) {
            clusterManager.addItem(new MapClusterItem(incident.getLatitude(), incident.getLongitude()));
        }
        clusterManager.cluster();
    }

    @Subscribe
    public void onIncidentsReceived(IncidentsReceivedEvent incidentsEvent) {
        setIncidents(incidentsEvent.getIncidents());
    }
}
