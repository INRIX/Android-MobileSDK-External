/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.view;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inrix.reference.trafficapp.R;

public class CurrentLocationDot {

	private Marker marker;
	private GoogleMap map;
	private boolean isEnabled = false;
	private Location currentLocation = null;

	public CurrentLocationDot(Context context, GoogleMap googleMap) {
		this.map = googleMap;
	}

	public void enableCurrentLocation(boolean enable) {
		isEnabled = enable;
		if (enable) {
			updateCurrentLocation(currentLocation);
		} else {
			if (marker != null) {
				marker.remove();
				marker = null;
			}
		}
	}

	public void updateCurrentLocation(Location location) {
		currentLocation = location;
		if (!isEnabled || location == null) {
			return;
		}

		if (marker == null) {
			this.marker = this.map.addMarker(generateMarkerOptions());
		}

		this.marker.setPosition(new LatLng(location.getLatitude(), location
				.getLongitude()));
	}

	private MarkerOptions generateMarkerOptions() {
		MarkerOptions options = new MarkerOptions();
		// Anchor center of the icon to current location
		options.anchor(0.5f, 0.5f);
		if (currentLocation != null) {
			options.position(new LatLng(currentLocation.getLatitude(),
					currentLocation.getLongitude()));
		}
		options.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location_dot));
		options.draggable(false);
		return options;
	}
}
