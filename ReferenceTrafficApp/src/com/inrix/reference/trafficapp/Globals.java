/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp;

import com.inrix.reference.trafficapp.error.DismissErrorEntity;
import com.inrix.reference.trafficapp.error.ErrorEntity;
import com.inrix.reference.trafficapp.error.ErrorType;
import com.inrix.reference.trafficapp.util.LocationServicesUtils;

import android.content.Context;

/**
 * The Class Globals, contains information about important system state
 */
public class Globals {

	/** The is connected. */
	private static volatile boolean isConnected;

	/**
	 * Returns latest connectivity state
	 * 
	 * @return true, if is network available
	 */
	public static boolean isNetworkAvailable() {
		return isConnected;
	}

	/**
	 * Checks if is location services enabled.
	 * 
	 * @param context
	 *            the context
	 * @return true, if is location services enabled
	 */
	public static boolean isLocationServicesEnabled(final Context context) {
		return LocationServicesUtils.isLocationServicesEnabled(context);
	}

	/**
	 * Sets the connectivity state.
	 * 
	 * @param value
	 *            the new connectivity state
	 */
	static void setIsNetworkAvailable(final boolean value) {
		isConnected = value;
		// Notify listeners about changes
		TrafficApp.getBus().post(isConnected ? new DismissErrorEntity(ErrorType.NETWORK_OFF)
				: new ErrorEntity(ErrorType.NETWORK_OFF));
	}

}
