package com.inrix.sdk.client;

import android.content.Context;

import com.inrix.sdk.client.interfaces.IClient;
import com.inrix.sdk.IncidentsManager;
import com.inrix.sdk.Inrix;
import com.inrix.sdk.RouteManager;

/**
 * The Class InrixClient, handles connection to service and provides wrappers to
 * service
 */
final class InrixLocalClient implements IClient {
	IncidentsManager incidentManager;
	RouteManager routeManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.inrix.sdk.client.interfaces.IClient#connect()
	 */
	@Override
	public void connect(final Context context) {
		Inrix.initialize(context.getApplicationContext());
		this.incidentManager = new IncidentsManager();
		this.routeManager = new RouteManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.inrix.sdk.client.interfaces.IClient#disconnect()
	 */
	@Override
	public void disconnect() {
		this.incidentManager = null;
		this.routeManager = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.inrix.sdk.client.interfaces.IClient#getIncidentManager()
	 */
	@Override
	public IncidentsManager getIncidentManager() {
		return this.incidentManager;
	}

	@Override
	public RouteManager getRouteManager() {
		return routeManager;
	}
}
