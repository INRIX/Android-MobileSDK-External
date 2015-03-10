package com.inrix.inrixrouterecorder;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationService implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private LocationRequest locationRequest;
	private LocationClient locationClient;
	private LocationListener listener;

	public LocationService(Context context) {
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(1500);
		locationRequest.setFastestInterval(500);
		locationClient = new LocationClient(context.getApplicationContext(),
				this,
				this);

		locationClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
	}

	@Override
	public void onConnected(Bundle arg0) {
		if (listener != null) {
			locationClient.requestLocationUpdates(locationRequest, listener);
		}
	}

	@Override
	public void onDisconnected() {
	}

	public void setLocationListener(LocationListener listener) {
		if (this.listener != null) {
			this.locationClient.removeLocationUpdates(listener);
		}

		this.listener = listener;
		if (locationClient.isConnected()) {
			locationClient.requestLocationUpdates(locationRequest, listener);
		}
	}

	public void pause() {
		if (locationClient.isConnected() && listener != null) {
			locationClient.removeLocationUpdates(listener);
		}
		locationClient.disconnect();
	}

	public void resume() {
		if (!locationClient.isConnected() && !locationClient.isConnecting()) {
			locationClient.connect();
		}
	}

}
