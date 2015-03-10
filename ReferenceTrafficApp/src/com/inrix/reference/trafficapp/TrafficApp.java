/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.inrix.reference.trafficapp.incidents.IncidentsProvider;
import com.inrix.sdk.InrixCore;
import com.squareup.otto.Bus;

/** Traffic Application. */
public class TrafficApp extends Application {

	/** Instance of the Traffic App application. */
	private static TrafficApp app;
	private static Bus bus;

	@Override
	public void onCreate() {
		super.onCreate();
		app = this;
		bus = new Bus();
		ReferenceAppPreferences.load(app);
		NetworkStateReceiver.verifyNetworkConnectivity(this);
		
		InrixCore.initialize(this);
		
		// Take care of expired incidents in local cache.
		new IncidentsProvider().cleanupExpired();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		bus = null;
		app = null;
	}

	public static Bus getBus() {
		return bus;
	}

	/**
	 * Retrieves base application context.
	 * 
	 * @return Base application context.
	 */
	public static Context getContext() {
		return app.getBaseContext();
	}
	
	/**
     * Retrieves application version from manifest file.
     * 
     * @return Application version defined in manifest file.
     */
    public final static String getVersion()	{
        try	{
            PackageManager manager = getContext().getPackageManager();
            PackageInfo packageInfo = manager.getPackageInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);
            return packageInfo.versionName;
        } catch (Exception e) {
        }

        return "";
    }
}
