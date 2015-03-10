/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.util.map;

import java.util.ArrayList;

import android.graphics.Path;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.geometry.Bounds;
import com.google.maps.android.geometry.Point;
import com.google.maps.android.projection.SphericalMercatorProjection;
import com.inrix.sdk.model.Route;

public class MapRoutesModel {
	private Bounds pointBounds;
	private LatLngBounds geoBounds;
	private ArrayList<Path> paths;
	private ArrayList<Route> routes;
	private SphericalMercatorProjection projection;
	private final int tileSize;

	public MapRoutesModel(ArrayList<Route> routes,
			SphericalMercatorProjection projection, int tileSize) {
		this.routes = routes;
		this.projection = projection;
		this.tileSize = tileSize;

		processRoutes(routes);
	}

	public ArrayList<Route> getRoutes() {
		return this.routes;
	}

	public ArrayList<Path> getPaths() {
		return this.paths;
	}

	public Bounds getPointBounds() {
		return this.pointBounds;
	}

	public LatLngBounds getGeoBounds() {
		return this.geoBounds;
	}

	/**
	 * Calculate bounds, generate paths
	 * 
	 * @param routes
	 */
	private void processRoutes(ArrayList<Route> routes) {
		Point pixelPoint;
		double minX, minY, maxX, maxY;
		minX = minY = maxX = maxY = 0;

		LatLngBounds.Builder geoBoundsBuilder = new LatLngBounds.Builder();
		this.paths = new ArrayList<Path>();
		boolean firstPass = true;

		for (Route route : routes) {
			Path path = null;
			for (LatLng point : PolyUtil.decode(route.getPolyline())) {
				pixelPoint = projection.toPoint(point);

				double x = pixelPoint.x;
				double y = pixelPoint.y;

				if (firstPass) {
					firstPass = false;
					minX = x;
					minY = y;
					maxX = minX;
					maxY = minY;
				}

				if (path == null) {
					path = new Path();
					path.moveTo((float) (x * tileSize), (float) (y * tileSize));
				} else {
					path.lineTo((float) (x * tileSize), (float) (y * tileSize));
				}
				geoBoundsBuilder.include(point);
				// Extend bounds if necessary
				if (x < minX)
					minX = x;
				if (x > maxX)
					maxX = x;
				if (y < minY)
					minY = y;
				if (y > maxY)
					maxY = y;
			}

			if (path != null) {
				this.paths.add(path);
			}
		}

		this.pointBounds = new Bounds(minX, maxX, minY, maxY);
		this.geoBounds = geoBoundsBuilder.build();
	}
}
