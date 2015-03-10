package com.inrix.sample.interfaces;

import android.content.Context;

import com.inrix.sdk.GasStationManager;
import com.inrix.sdk.IncidentsManager;
import com.inrix.sdk.ParkingManager;
import com.inrix.sdk.RouteManager;

/**
 * The Interface IClient, provides access to
 */
public interface IClient {

	/** The Constant MSG_CONNECTED. */
	public static final String MSG_CONNECTED = "com.inrix.sdk.interfaces.LocalInrixService.connected";

	/** The Constant MSG_DISCONNECTED. */
	public static final String MSG_DISCONNECTED = "com.inrix.sdk.interfaces.LocalInrixService.disconnected";

	/**
	 * Connect to service
	 */
	public void connect(final Context context);

	/**
	 * Disconnect from service
	 */
	public void disconnect();

	/**
	 * Gets the incident manager.
	 * 
	 * @return the incident manager
	 */
	public IncidentsManager getIncidentManager();

	/**
	 * Gets the route manager.
	 * 
	 * @return the route manager
	 */
	public RouteManager getRouteManager();
	
	/**
	 * Gets the gas station manager
	 * 
	 * @return - The gas stations manager
	 */
	public GasStationManager getGasStationManager();
	
	/**
	 * Gets the parking manager
	 * 
	 * @return - The parking manager
	 */
	public ParkingManager getParkingManager();
}
