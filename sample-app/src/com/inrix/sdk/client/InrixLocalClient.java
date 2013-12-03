package com.inrix.sdk.client;

import android.content.Context;

import com.inrix.sdk.client.interfaces.IClient;
import com.inrix.sdk.GasStationManager;
import com.inrix.sdk.IncidentsManager;
import com.inrix.sdk.Inrix;
import com.inrix.sdk.ParkingManager;
import com.inrix.sdk.RouteManager;

/**
 * The Class InrixClient, handles connection to service and provides wrappers to
 * service
 */
final class InrixLocalClient implements IClient {
	IncidentsManager incidentManager;
	RouteManager routeManager;
	GasStationManager gasStationManager;
	ParkingManager parkingManager;

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
		this.gasStationManager = new GasStationManager();
		this.parkingManager = new ParkingManager();
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
		this.gasStationManager = null;
		this.parkingManager = null;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.inrix.sdk.client.interfaces.IClient#getGasStationManager()
	 */
	@Override
	public GasStationManager getGasStationManager(){
		return this.gasStationManager;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.inrix.sdk.client.interfaces.IClient#getParkingManager()
	 */
	@Override
	public ParkingManager getParkingManager(){
		return this.parkingManager;
	}
}
