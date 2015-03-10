package com.inrix.sample;

import android.content.Context;

import com.inrix.sample.interfaces.IClient;
import com.inrix.sdk.GasStationManager;
import com.inrix.sdk.IncidentsManager;
import com.inrix.sdk.InrixCore;
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
		InrixCore.initialize(context.getApplicationContext());
		this.incidentManager = InrixCore.getIncidentsManager();
		this.routeManager = InrixCore.getRouteManager();
		this.gasStationManager = InrixCore.getGasStationManager();
		this.parkingManager = InrixCore.getParkingManager();
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
