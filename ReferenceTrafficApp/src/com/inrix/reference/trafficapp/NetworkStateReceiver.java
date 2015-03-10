/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

/**
 * The Class NetworkStateReceiver, receive information about network changes
 */
public class NetworkStateReceiver extends BroadcastReceiver {

	public static void verifyNetworkConnectivity(final Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// Retrieve information about all possible network connections
		NetworkInfo[] currentNetworkInfo = manager.getAllNetworkInfo();
		boolean isConneted = false;
		for (NetworkInfo state : currentNetworkInfo) {

			if (state == null) {
				continue;
			}

			// Ignore unknown state, means network is not available or setup
			if (state.getState() == State.UNKNOWN) {
				continue;
			}

			// If at least one network has state connected we are good
			isConneted |= (state.getState() == State.CONNECTED || state
					.getState() == State.CONNECTING);
		}

		Globals.setIsNetworkAvailable(isConneted);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		NetworkStateReceiver.verifyNetworkConnectivity(context);
	}
}
