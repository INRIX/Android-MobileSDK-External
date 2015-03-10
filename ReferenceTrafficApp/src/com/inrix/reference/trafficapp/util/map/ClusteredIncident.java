/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.util.map;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.inrix.sdk.model.Incident;

public class ClusteredIncident implements ClusterItem {

	private Incident incident = null;
	private LatLng position = null;

	public ClusteredIncident(Incident incident) {
		if (incident == null) {
			throw new IllegalArgumentException("Incident should not be null");
		}
		this.incident = incident;
		this.position = new LatLng(incident.getLatitude(),
				incident.getLongitude());
	}

	@Override
	public LatLng getPosition() {
		return this.position;
	}

	public Incident getIncident() {
		return this.incident;
	}
}