package com.inrix.sample.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MapClusterItem implements ClusterItem {
	private final LatLng position;

	public MapClusterItem(double lat, double lng) {
		position = new LatLng(lat, lng);
	}

	@Override
	public LatLng getPosition() {
		return position;
	}
}
