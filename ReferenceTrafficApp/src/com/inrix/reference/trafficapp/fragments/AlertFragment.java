/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.fragments;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.inrix.reference.trafficapp.util.Interfaces.DTWAlertCallback;
import com.inrix.sdk.AlertsManager.IIncidentsAlertListener;
import com.inrix.sdk.AlertsManager.IOnRouteStatusListener;
import com.inrix.sdk.Error;
import com.inrix.sdk.RouteManager.IRouteResponseListener;
import com.inrix.sdk.model.Incident;
import com.inrix.sdk.model.RoutesCollection;

public class AlertFragment extends Fragment implements DTWAlertCallback {

	private DTWAlertCallback callback;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRetainInstance(true);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof DTWAlertCallback) {
			this.callback = (DTWAlertCallback) activity;
		}
	}
	
	@Override
	public void onDetach() {
		this.callback = null;
		super.onDetach();
	}

	@Override
	public IRouteResponseListener getIRouteResponseListener() {
		return updateRoutePointsListener;
	}

	@Override
	public IIncidentsAlertListener getIIncidentsAlertListener() {
		return incidentsListener;
	}

	@Override
	public IOnRouteStatusListener getIOnRouteStatusListener() {
		return routeStatusListener;
	}

	private IRouteResponseListener updateRoutePointsListener = new IRouteResponseListener() {
		@Override
		public void onResult(RoutesCollection routesCollection) {
			IRouteResponseListener listener;
			if (callback != null
					&& (listener = callback.getIRouteResponseListener()) != null) {
				listener.onResult(routesCollection);
			}
		}

		@Override
		public void onError(Error error) {
			IRouteResponseListener listener;
			if (callback != null
					&& (listener = callback.getIRouteResponseListener()) != null) {
				listener.onError(error);
			}
		}
	};

	private IIncidentsAlertListener incidentsListener = new IIncidentsAlertListener() {

		@Override
		public void onError(Error error) {
			IIncidentsAlertListener listener;
			if (callback != null
					&& (listener = callback.getIIncidentsAlertListener()) != null) {
				listener.onError(error);
			}
		}

		@Override
		public void onResult(List<Incident> data) {
			IIncidentsAlertListener listener;
			if (callback != null
					&& (listener = callback.getIIncidentsAlertListener()) != null) {
				listener.onResult(data);
			}
		}
	};

	private IOnRouteStatusListener routeStatusListener = new IOnRouteStatusListener() {
		@Override
		public void onRouteStatus(OnRouteStatus routeStatus) {
			IOnRouteStatusListener listener;
			if (callback != null
					&& (listener = callback.getIOnRouteStatusListener()) != null) {
				listener.onRouteStatus(routeStatus);
			}
		}
	};
}