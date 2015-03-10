/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.util;

import android.content.Context;
import android.provider.Settings;

public class LocationServicesUtils {
	/**
	 * Checks if is location services enabled.
	 * 
	 * @param context
	 *            the context
	 * @return true, if is location services enabled
	 */
	public static boolean isLocationServicesEnabled(final Context context) {
		@SuppressWarnings("deprecation")
		String provider = Settings.Secure.getString(context
				.getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		return provider != null && !provider.equals("");
	}
}
