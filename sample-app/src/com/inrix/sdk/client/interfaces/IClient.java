package com.inrix.sdk.client.interfaces;

import android.content.Context;

import com.inrix.sdk.IncidentsManager;
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
}
