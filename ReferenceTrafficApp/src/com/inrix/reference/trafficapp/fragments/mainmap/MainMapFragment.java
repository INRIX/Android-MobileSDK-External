/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.fragments.mainmap;

import com.inrix.reference.trafficapp.fragments.RouteMapFragment;
import com.inrix.reference.trafficapp.incidents.IncidentStateChangedEvent;
import com.squareup.otto.Subscribe;

/** Implements main map fragment. */
public class MainMapFragment extends RouteMapFragment {

	/** Refreshes map content. */
	public void refresh() {
		refreshIncidents();
		refreshTrafficTiles();
		//TODO:
		//routes?
	}
	
	/**
	 * Subscribes to the incident state change event from event bus.
	 * 
	 * @param event
	 *            Event instance.
	 */
	@Subscribe
	@Override
	public void onIncidentStateChanged(final IncidentStateChangedEvent event) {
		super.onIncidentStateChanged(event);
	}
}
