/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.util;

import com.google.android.gms.maps.model.LatLng;
import com.inrix.sdk.model.GeoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods to work with {@link GeoPoint}s.
 */
public class GeoPointHelper {
    /**
     * Converts {@link LatLng} to {@link GeoPoint}.
     *
     * @param latLng {@link LatLng} to convert from.
     * @return {@link GeoPoint} instance.
     */
    public static GeoPoint fromLatLng(final LatLng latLng) {
        return new GeoPoint(latLng.latitude, latLng.longitude);
    }

    /**
     * Converts {@link GeoPoint} to {@link LatLng}.
     *
     * @param geoPoint {@link GeoPoint} to convert from.
     * @return {@link LatLng} instance.
     */
    public static LatLng toLatLng(final GeoPoint geoPoint) {
        return new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
    }

    /**
     * Convert list of {@link LatLng}s into list of {@link GeoPoint}s.
     *
     * @param points List of {@link LatLng} to convert.
     * @return List of {@link GeoPoint}.
     */
    public static List<GeoPoint> fromLatLngList(final List<LatLng> points) {
        final ArrayList<GeoPoint> result = new ArrayList<>();

        for (final LatLng latLng : points) {
            result.add(fromLatLng(latLng));
        }

        return result;
    }

    /**
     * Convert list of {@link GeoPoint}s into list of {@link LatLng}
     * objects.
     *
     * @param points A list of {@link GeoPoint}s to convert.
     * @return List of {@link LatLng}.
     */
    public static List<LatLng> toLatLngList(final List<GeoPoint> points) {
        final ArrayList<LatLng> result = new ArrayList<>();

        for (final GeoPoint point : points) {
            result.add(toLatLng(point));
        }

        return result;
    }
}
