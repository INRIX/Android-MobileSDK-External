/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.util;

import android.graphics.Rect;
import android.support.v4.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.inrix.sdk.AlertsManager.IIncidentsAlertListener;
import com.inrix.sdk.AlertsManager.IOnRouteStatusListener;
import com.inrix.sdk.RouteManager.IRouteResponseListener;

/**
 * supporting interfaces go here. Sorry for the name - running out of good class
 * names :(
 * 
 * @author paveld
 * 
 */
public class Interfaces {
	/**
	 * Special listener to listen to map padding updates. This can be used by
	 * fragments to notify map activity that map padding needs to be changed.
	 * 
	 * @author paveld
	 * 
	 */
	public interface IOnUpdateMapPaddingListener {
		/**
		 * Called whenever map padding needs to be updated.
		 * 
		 * @param padding
		 * @param mapCenter
		 */
		void onUpdateMapPadding(Rect padding, LatLng mapCenter);

		/**
		 * Called whenever map padding needs to be updated.
		 * 
		 * @param padding
		 * @param mapCenter
		 * @param zoom
		 */
		void onUpdateMapPadding(Rect padding, LatLng mapCenter, float zoom);
	}

	/**
	 * Interface to listen to fragment attach/detach. This can be used by
	 * fragments to notify hosting activity that fragment is attached/detached
	 * 
	 * @author paveld
	 * 
	 */
	public interface IOnFragmentAttachedListener {
		void onFragmentAttached(Fragment fragment);

		void onFragmentDetached(Fragment fragment);
	}

	public interface IGoogleMapProvider {
		GoogleMap getGoogleMap();
	}
	
	public interface IIncidentRendered {
		public void enableIncidents(boolean enable);
	}

	public static interface DTWAlertCallback {
		public IRouteResponseListener getIRouteResponseListener();

		public IIncidentsAlertListener getIIncidentsAlertListener();

		public IOnRouteStatusListener getIOnRouteStatusListener();
	}
}
