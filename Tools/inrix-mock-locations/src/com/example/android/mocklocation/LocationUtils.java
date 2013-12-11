/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.mocklocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

/**
 * Constants used in other classes in the app
 */
public final class LocationUtils {

	// Debugging tag for the application
	public static final String APPTAG = "Location Mock Tester";

	// Create an empty string for initializing strings
	public static final String EMPTY_STRING = new String();

	// Conversion factor for boot time
	public static final long NANOSECONDS_PER_MILLISECOND = 1000000;

	// Conversion factor for time values
	public static final long MILLISECONDS_PER_SECOND = 1000;

	// Conversion factor for time values
	public static final long NANOSECONDS_PER_SECOND = NANOSECONDS_PER_MILLISECOND
			* MILLISECONDS_PER_SECOND;

	/*
	 * Action values sent by Intent from the main activity to the service
	 */
	// Request a one-time test
	public static final String ACTION_START_ONCE = "com.example.android.mocklocation.ACTION_START_ONCE";

	// Request continuous testing
	public static final String ACTION_START_CONTINUOUS = "com.example.android.mocklocation.ACTION_START_CONTINUOUS";

	// Stop a continuous test
	public static final String ACTION_STOP_TEST = "com.example.android.mocklocation.ACTION_STOP_TEST";

	/*
	 * Extended data keys for the broadcast Intent sent from the service to the
	 * main activity. Key1 is the base connection message. Key2 is extra data or
	 * error codes.
	 */
	public static final String KEY_EXTRA_CODE1 = "com.example.android.mocklocation.KEY_EXTRA_CODE1";

	public static final String KEY_EXTRA_CODE2 = "com.example.android.mocklocation.KEY_EXTRA_CODE2";

	/*
	 * Codes for communicating status back to the main activity
	 */

	// The location client is disconnected
	public static final int CODE_DISCONNECTED = 0;

	// The location client is connected
	public static final int CODE_CONNECTED = 1;

	// The client failed to connect to Location Services
	public static final int CODE_CONNECTION_FAILED = -1;

	// Report in the broadcast Intent that the test finished
	public static final int CODE_TEST_FINISHED = 3;

	/*
	 * Report in the broadcast Intent that the activity requested the start to a
	 * test, but a test is already underway
	 */
	public static final int CODE_IN_TEST = -2;

	// The test was interrupted by clicking "Stop testing"
	public static final int CODE_TEST_STOPPED = -3;

	// The name used for all mock locations
	public static final String LOCATION_PROVIDER = "fused";

	// An array of latitudes for constructing test data

	public static double[] WAYPOINTS_LAT;

	// An array of longitudes for constructing test data
	public static double[] WAYPOINTS_LNG;

	public static float[] WAYPOINTS_SPEED;
	// An array of accuracy values for constructing test data
	public static float[] WAYPOINTS_ACCURACY;

	public static float[] WAYPOINTS_BEARING;// = {

	// Mark the broadcast Intent with an action
	public static final String ACTION_SERVICE_MESSAGE = "com.example.android.mocklocation.ACTION_SERVICE_MESSAGE";

	/*
	 * Key for extended data in the Activity's outgoing Intent that records the
	 * type of test requested.
	 */
	public static final String EXTRA_TEST_ACTION = "com.example.android.mocklocation.EXTRA_TEST_ACTION";

	/*
	 * Key for extended data in the Activity's outgoing Intent that records the
	 * requested pause value.
	 */
	public static final String EXTRA_PAUSE_VALUE = "com.example.android.mocklocation.EXTRA_PAUSE_VALUE";

	/*
	 * Key for extended data in the Activity's outgoing Intent that records the
	 * requested interval for mock locations sent to Location Services.
	 */
	public static final String EXTRA_SEND_INTERVAL = "com.example.android.mocklocation.EXTRA_SEND_INTERVAL";

	// public ArrayList<String> mData = null;
	private static RouteData routefileName = null;
	private static int routeToFollow = 0;

	public static void initializeRoute(Context testContext, RouteData fileName)
			throws IOException {
		routefileName = fileName;
		getRouteByRouteName();

		ArrayList<String> mData = new ArrayList<String>();

		InputStream is = testContext.getResources()
				.openRawResource(routeToFollow);

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while ( (line = reader.readLine()) != null) {

			mData.add(line);
		}
		createRouteData(mData);

	}

	public static int getRouteByRouteName() {

		switch (routefileName) {
			case validSFpoints: {
				routeToFollow = R.raw.sftrip;
				break;

			}
			case helicopterTripPoint: {
				routeToFollow = R.raw.portlandseattle;
				break;
			}
			case londontrip: {
				routeToFollow = R.raw.londontrip;
				break;
			}
			case invalidSFpoints: {
				routeToFollow = R.raw.sfnonvalidtrip;
				break;
			}
			case trip_to_vet: {
				routeToFollow = R.raw.trip_to_vet;
				break;
			}
			case longtrip:
			default: {
				routeToFollow = R.raw.seattletrip;
				break;
			}
			case seattleredmondtrip: {
				routeToFollow = R.raw.seattleredmondtrip;
				break;
			}
		}
		return routeToFollow;
	}

	public static void createRouteData(ArrayList<String> mData) {
		int i = 0;
		if (mData != null) {
			WAYPOINTS_LAT = new double[mData.size()];
			WAYPOINTS_LNG = new double[mData.size()];
			WAYPOINTS_SPEED = new float[mData.size()];
			WAYPOINTS_BEARING = new float[mData.size()];
			WAYPOINTS_ACCURACY = new float[mData.size()];

			for (String str : mData) {
				String[] parts = str.split(",");

				WAYPOINTS_LAT[i] = Double.valueOf(parts[0]);
				WAYPOINTS_LNG[i] = Double.valueOf(parts[1]);
				WAYPOINTS_SPEED[i] = Float.valueOf(parts[2]);
				WAYPOINTS_BEARING[i] = Float.valueOf(parts[5]);
				WAYPOINTS_ACCURACY[i] = Float.valueOf(parts[6]);
				i++;
			}
		} else
			Log.e(APPTAG, "test route data is null");
	}

}
