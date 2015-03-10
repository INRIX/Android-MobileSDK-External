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
import android.location.Location;

import com.inrix.sdk.geolocation.IGeolocationSource;
import com.inrix.sdk.geolocation.IOnGeolocationChangeListener;
import com.inrix.sdk.model.GeoPoint;

public class DemoLocationSource implements IGeolocationSource {

	private IOnGeolocationChangeListener callback;
	private boolean demoLocations;
	private Location demoCurrentLocation;

	public DemoLocationSource(Context context) {
		demoLocations = WeatherAppConfig.getCurrentAppConfig(context)
				.getDemoLocations();
		if (demoLocations) {
			GeoPoint demoLocation = WeatherAppConfig
					.getCurrentAppConfig(context).getDemoCurrentLocation();
			demoCurrentLocation = new Location("");
			demoCurrentLocation.setLatitude(demoLocation.getLatitude());
			demoCurrentLocation.setLongitude(demoLocation.getLongitude());
		}
	}

	@Override
	public void activate(IOnGeolocationChangeListener listener) {
		callback = listener;
		if (null != callback) {
			listener.onGeolocationChange(demoCurrentLocation);
		}

	}

	@Override
	public void deactivate() {
		callback = null;

	}

	/**
	 * Set location change callback
	 * 
	 * @param listener
	 */
	public void setLocationChangeListener(IOnGeolocationChangeListener listener) {
		this.callback = listener;
	}

}
