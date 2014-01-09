package com.inrix.sample.map;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.inrix.sdk.model.Route;
import com.inrix.sdk.model.RoutesCollection;

/**
 * Polyline-based route overlay. Very simple, but not flexible
 * @author paveld
 *
 */
public class PolylineRouteOverlay {
	private GoogleMap map;
	private ArrayList<Polyline> polylines = new ArrayList<Polyline>();

	public PolylineRouteOverlay(GoogleMap map) {
		this.map = map;
	}

	public void displayRoute(RoutesCollection routes) {
		this.clear();
		for (Route route : routes.getRoutes()) {
			polylines.add(map.addPolyline(polylineOptionsFromPoints(PolyUtil
					.decode(route.getPolyline()))));
		}

		zoomMapToRoutes(routes);
	}

	private void zoomMapToRoutes(RoutesCollection routes) {
		Rect boxRect = new Rect();
		// calculating bounding box to zoom map accordingly

		for (Route route : routes.getRoutes()) {
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
			this.map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,
					50));
		}
	}

	public void clear() {
		for (Polyline polyline : polylines) {
			polyline.remove();
		}
		polylines.clear();
	}

	private PolylineOptions polylineOptionsFromPoints(List<LatLng> points) {
		PolylineOptions options = new PolylineOptions();
		options.addAll(points);
		return options;
	}
}
