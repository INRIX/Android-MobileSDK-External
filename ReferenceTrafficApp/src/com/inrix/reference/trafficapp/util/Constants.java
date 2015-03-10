/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.util;

import com.inrix.sdk.utils.GeoUtils;

public class Constants {

	public static final long DEFAULT_CONTENT_REFRESH_TIMEOUT_MS = 180 * 1000;
	public static final long TRAFFIC_TILES_REFRESH_TIMEOUT_MS = 90 * 1000;

	/* Do not request incidents on zoom levels below this value */
	public static final int INCIDENTS_MIN_ALLOWED_ZOOM_LEVEL = 11;

	/*milliseconds per second*/
	public static final int MS_PER_SECOND = 1000;
	
	/*seconds per minute*/
	public static final int SECONDS_PER_MIN = 60;

	/*minutes_per_hour*/
	public static final int MINS_PER_HR = 60;

	/*maximum distance for you are here*/
	public static final float YOU_ARE_HERE_THRESHOLD = 0.2f;
	
	public static final String INRIX_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String INRIX_DEFAULT_TIMEZONE = "UTC";
	
	/*format string for incidents list view*/
	public static final String INCIDENTS_TIME_DISPLAY_FORMAT = "EEE, dd MMM, yyyy - h:mm a z";
	
	/*Maximum distance to be able to confirm incidents - roughly 2 miles*/
	public static final double MAX_DISTANCE_TO_CONFIRM_INCIDENT_MILES = 2.0;
	public static final double MAX_DISTANCE_TO_CONFIRM_INCIDENT_KM = MAX_DISTANCE_TO_CONFIRM_INCIDENT_MILES * GeoUtils.KM_MILE_CONVERSION_FACTOR;
	
	/** Maximal valid value for latitude. */
	public static final double MAX_LATITUDE = 90;
	
	/** Minimal valid value for latitude. */
	public static final double MIN_LATITUDE = -90;
	
	/** Maximal valid value for longitude. */
	public static final double MAX_LONGITUDE = 180;
	
	/** Minimal valid value for longitude. */
	public static final double MIN_LONGITUDE = -180;
	
	/** 
	 * Minimal delay time to show congestion
	 */
	public static final double CONGESTION_MINIMAL_DELAY_IMPACT = 5;
	
	/** 
	 * Decimal formating for distance to/from object.
	 */
	public static final String DISTANCE_FORMAT = "#0.#";

	/** Time before auto-dismiss the dialog. */
	public static final int SHOW_DIALOG_TIME_SECONDS = 20;

	/** Default incident zoom. */
	public static final int INCIDENT_DEFAULT_ZOOM_LEVEL = 14;

}
