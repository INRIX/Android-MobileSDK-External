/**
 * Copyright (c) 2013-2015 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.map;

import android.graphics.Rect;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.inrix.sdk.model.Route;
import com.inrix.sdk.model.Route.Bucket;
import com.inrix.sdk.utils.GeoUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Polyline-based route overlay. Very simple, but not flexible.
 */
public class PolylineRouteOverlay {
	private GoogleMap map;
	private ArrayList<Polyline> polylines = new ArrayList<>();
	private Object lock = new Object();

	public PolylineRouteOverlay(GoogleMap map) {
		this.map = map;
	}

	public void displayRoute(List<Route> routes) {
		synchronized (lock) {
			this.clear();
			for (Route route : routes) {
				polylines.addAll(generatePolylines(route));
			}
		}
		zoomMapToRoutes(routes);
	}

	private void zoomMapToRoutes(List<Route> routes) {
		Rect boxRect = null;
		// calculating bounding box to zoom map accordingly

		for (Route route : routes) {
			if (boxRect == null) {
				boxRect = new Rect((int) (route.getBoundingBox().getCorner1()
						.getLongitude() * 1E6),
						(int) (route.getBoundingBox().getCorner1()
								.getLatitude() * 1E6),
						(int) (route.getBoundingBox().getCorner2()
								.getLongitude() * 1E6),
						(int) (route.getBoundingBox().getCorner2()
								.getLatitude() * 1E6));
			} else {
				boxRect.union(new Rect((int) (route.getBoundingBox()
						.getCorner1().getLongitude() * 1E6),
						(int) (route.getBoundingBox().getCorner1()
								.getLatitude() * 1E6),
						(int) (route.getBoundingBox().getCorner2()
								.getLongitude() * 1E6),
						(int) (route.getBoundingBox().getCorner2()
								.getLatitude() * 1E6)));
			}
		}
		if (boxRect != null) {
			// zoom map to entire route
			LatLngBounds bounds = new LatLngBounds(new LatLng(Math.min(boxRect.bottom / 1E6,
					boxRect.top / 1E6),
					Math.min(boxRect.left / 1E6, boxRect.right / 1E6)),
					new LatLng(Math
							.max(boxRect.bottom / 1E6, boxRect.top / 1E6), Math
							.max(boxRect.left / 1E6, boxRect.right / 1E6)));
			this.map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
		}
	}

	public void clear() {
		for (Polyline polyline : polylines) {
			polyline.remove();
		}
		polylines.clear();
	}

	private List<Polyline> generatePolylines(Route route) {
		ArrayList<Polyline> polylines = new ArrayList<Polyline>();

		for (Bucket b : route.getSpeedBuckets()) {
			PolylineOptions options = new PolylineOptions();
			options.addAll(GeoUtils.pointsToLatLng(b.getSpeedBucketPoints()));

			// draw black polyline first to add some stroke
			options.width(20);
			options.color(0xFF000000);
			options.zIndex(0);
			polylines.add(map.addPolyline(options));

			options.width(16);
			options.color(speedBucketIdToColor(b.getSpeedBucketID()));
			options.zIndex(1);
			polylines.add(map.addPolyline(options));
		}
		return polylines;
	}

	/**
	 * Converts INRIX SpeedBucketId into color in 0xAARRGGBB format
	 *
	 * @param speedBucketId
	 * @return color
	 */
	private int speedBucketIdToColor(int speedBucketId) {
		int color = 0xFF000000;
		switch (speedBucketId) {
			case 0:
				color = 0xffb0120a; // dark red
				break;
			case 1:
				color = 0xffe84e40; // red
				break;
			case 2:
				color = 0xffffeb3b;// yellow
				break;
			case 3:
				color = 0xff259b24;// green
				break;

		}
		return color;
	}
}
