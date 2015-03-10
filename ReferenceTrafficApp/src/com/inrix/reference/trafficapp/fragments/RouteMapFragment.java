/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.fragments;

import java.util.ArrayList;

import com.inrix.reference.trafficapp.util.map.RouteTileOverlay;
import com.inrix.sdk.model.Route;

/** Displays routes on the map. */
public class RouteMapFragment extends IncidentsMapFragment {

	/** Overlay to display routes. */
	private RouteTileOverlay routeOverlay;

	/**
	 * Pending routes. Saves routes (if the setRoute method was called) while
	 * route overlay is preparing.
	 */
	private ArrayList<Route> pendingRoutes;

	@Override
	protected void setUpMap() {
		super.setUpMap();

		if (routeOverlay == null) {
			this.routeOverlay = new RouteTileOverlay(getActivity(), this);
		}
		if (this.pendingRoutes != null) {
			this.setRoutes(this.pendingRoutes, true);
			this.pendingRoutes = null;
		}
	}

	/**
	 * Sets the routes to display.
	 * 
	 * @param routes
	 *            Routes to display.
	 * @param zoomToRoutes
	 *            Flag to zoom on routes.
	 */
	public void setRoutes(ArrayList<Route> routes, boolean zoomToRoutes) {
		if (this.routeOverlay != null) {
			if (zoomToRoutes) {
				this.cancelAnimation();
			}
			this.routeOverlay.displayRoutes(routes, zoomToRoutes);
		} else {
			this.pendingRoutes = routes;
		}
	}
}
