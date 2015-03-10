/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.fragments;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.util.map.ClusteredIncident;

/**
 * Shows traffic and incidents
 * 
 * @author paveld
 * 
 */
public class MiniMapFragment extends IncidentsMapFragment {

	private MiniMapIncidentsRenderer renderer;

	@Override
	protected void setUpMap() {
		isGesturesEnabled = false;
		super.setUpMap();

		enableClustering(false);
		enableCurrentLocationTracking(true);
	}

	public void setMapPadding(int left, int top, int right, int bottom) {
		if (getMap() != null) {
			getMap().setPadding(left, top, right, bottom);
		}
	}

	@Override
	protected ClusterRenderer<ClusteredIncident> getRenderer() {
		if (this.renderer == null) {
			this.renderer = new MiniMapIncidentsRenderer(getActivity(),
					getMap(), markerManager);
		}
		return this.renderer;
	}

	@Override
	public void onGeolocationChange(Location location) {
		super.onGeolocationChange(location);
		animateToLocation(location);
		enableCurrentLocationTracking(false);
	}

	@Override
	public synchronized void refreshIncidents() {
		super.refreshIncidents();
		// enable current location tracking for one geolocation update, so map
		// will be moved to our current location
		enableCurrentLocationTracking(true);
	}

	class MiniMapIncidentsRenderer extends
			DefaultClusterRenderer<ClusteredIncident> {

		public MiniMapIncidentsRenderer(Context context, GoogleMap map,
				ClusterManager<ClusteredIncident> clusterManager) {
			super(context, map, clusterManager);
		}

		@Override
		protected void onBeforeClusterItemRendered(ClusteredIncident item,
				MarkerOptions markerOptions) {
			markerOptions.anchor(0.5f, 0.5f);
			markerOptions.icon(BitmapDescriptorFactory
					.fromResource(R.drawable.map_icon_pin_mini_map));
		}
	}

	@Override
	public boolean onClusterItemClick(ClusteredIncident item) {
		return true;
	}
}
