/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.util.map;

import java.util.List;

import android.location.Location;

import com.inrix.reference.trafficapp.util.Constants;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Route;

public class RouteTracker {

	private static final double YOU_ARE_HERE_THRESHOLD_METERS = Constants.YOU_ARE_HERE_THRESHOLD * 1000;

	/**
	 * If two points are within this many meters of each other, they are
	 * considered to be at the same point in space
	 */
	private static int GEO_TOLERANCE = 150;

	public enum OnRouteStatus {
		AtStart, OnRoute, AtEnd, OffRoute
	};

	public static OnRouteStatus getOnRouteStatus(Route route,
			Location currentLocation) {
		List<GeoPoint> routePoints = route.getPoints();
		if (routePoints.size() > 1) {
			Location startPoint = getLocationFromPoint(routePoints.get(0));
			if (startPoint.distanceTo(currentLocation) < YOU_ARE_HERE_THRESHOLD_METERS) {
				return OnRouteStatus.AtStart;
			}

			Location endPoint = getLocationFromPoint(routePoints
					.get(routePoints.size() - 1));
			if (endPoint.distanceTo(currentLocation) < YOU_ARE_HERE_THRESHOLD_METERS) {
				return OnRouteStatus.AtEnd;
			}

			for (int idx = 1; idx < routePoints.size(); idx++) {
				Location segStart = getLocationFromPoint(routePoints
						.get(idx - 1));
				Location segEnd = getLocationFromPoint(routePoints.get(idx));
				if (distanceFromPointToSegment(currentLocation,
						segStart,
						segEnd) < GEO_TOLERANCE) {
					return OnRouteStatus.OnRoute;
				}
			}
		}

		return OnRouteStatus.OffRoute;
	}

	private static double distanceFromPointToSegment(Location point,
			Location segmentStart,
			Location segmentEnd) {
		float distanceFromStart = segmentStart.distanceTo(point);
		float distanceFromEnd = segmentEnd.distanceTo(point);
		float lineSegDistance = segmentStart.distanceTo(segmentEnd);

		double ratio = (1 + (distanceFromStart / lineSegDistance)
				* (distanceFromStart / lineSegDistance) - (distanceFromEnd / lineSegDistance)
				* (distanceFromEnd / lineSegDistance)) / 2.0;

		if (ratio <= 0) {
			return distanceFromStart;
		}

		if (ratio >= 1.0) {
			return distanceFromEnd;
		}

		// use Pythagorean theorem to calculate; one leg and the hypotenuse
		double x = lineSegDistance * ratio;
		double h2 = distanceFromStart * distanceFromStart - x * x;
		return Math.sqrt(h2);
	}

	private static Location getLocationFromPoint(GeoPoint point) {
		Location loc = new Location("point");
		loc.setLatitude(point.getLatitude());
		loc.setLongitude(point.getLongitude());
		return loc;
	}
}
