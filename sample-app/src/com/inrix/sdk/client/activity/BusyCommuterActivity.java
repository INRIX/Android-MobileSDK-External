/*
 * Copyright (C) 2013-14 INRIX, Inc.
 *
 */

package com.inrix.sdk.client.activity;

import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.inrix.sdk.Error;
import com.inrix.sdk.IncidentsManager.IIncidentsResponseListener;
import com.inrix.sdk.IncidentsManager.IncidentRadiusOptions;
import com.inrix.sdk.RouteManager.IRouteResponseListener;
import com.inrix.sdk.RouteManager.RouteOptions;
import com.inrix.sdk.client.Place;
import com.inrix.sdk.client.ClientFactory;
import com.inrix.sdk.client.R;
import com.inrix.sdk.client.fragments.InrixConnectionLauncherFragment.IInrixConection;
import com.inrix.sdk.client.fragments.InrixIncidentListFragment;
import com.inrix.sdk.client.fragments.InrixIncidentManagerSetupFragment;
import com.inrix.sdk.client.fragments.InrixIncidentManagerSetupFragment.IOnGetIncidentsListener;
import com.inrix.sdk.client.interfaces.IClient;
import com.inrix.sdk.model.Incident;
import com.inrix.sdk.model.RoutesCollection;

/**
 * The Main Activity for this application
 */
public class BusyCommuterActivity extends FragmentActivity implements IInrixConection,
		IOnGetIncidentsListener {

	// Interface to the Mobile Data
	private IClient client;

	// Fragments
	private InrixIncidentManagerSetupFragment setupInrix = new InrixIncidentManagerSetupFragment(this);
	private InrixIncidentListFragment incidentList = new InrixIncidentListFragment();

	// Dialogs
	private ProgressDialog progressDialog;
	private AlertDialog alertDialog;

	// Status Properties
	private volatile int requestsInProgress = 0;

	// Constants
	private final int INCICENTS_RADIUS = 5;
	private final int ROUTES_TOLERANCE = 24;
	private final int ROUTES_NUM_ALTERNATES = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.busy_commuter);

		// Initialize the client
		this.client = ClientFactory.getInstance().getClient();
		this.client.connect(getApplicationContext());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case R.id.action_refresh:
				if (setupInrix.isAdded()) {
					this.onGetIncidentsInCity(setupInrix.getSelectedCity());
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.inrix.sdk.client.fragments.InrixConnectionLauncherFragment.
	 * IInrixConection#onConnect()
	 */
	@Override
	public void onConnect() {
		FragmentManager manager = this.getSupportFragmentManager();
		manager.beginTransaction().add(R.id.setupFragment, this.setupInrix)
				.commit();
		manager.beginTransaction().show(setupInrix).commit();

		manager.beginTransaction()
				.add(R.id.incidentListFragment, this.incidentList).commit();
		manager.beginTransaction().show(incidentList).commit();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.inrix.sdk.client.fragments.InrixConnectionLauncherFragment.
	 * IInrixConection#onDisconnect()
	 */
	@Override
	public void onDisconnect() {
		FragmentManager manager = this.getSupportFragmentManager();
		manager.beginTransaction().remove(setupInrix).commitAllowingStateLoss();
		manager.beginTransaction().remove(incidentList)
				.commitAllowingStateLoss();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.inrix.sdk.client.fragments.InrixIncidentManagerSetupFragment.
	 * IOnGetIncidentsListener#onGetIncidentsInCity(com.inrix.sdk.client.Place)
	 */
	@Override
	public void onGetIncidentsInCity(final Place selectedCity) {
		progressDialog = ProgressDialog.show(BusyCommuterActivity.this,
				"",
				"Loading...");
		incidentList.clearAll();
		requestsInProgress = 0;

		// Get the Incidents for the selected city
		IncidentRadiusOptions params = new IncidentRadiusOptions(selectedCity.getPoint(),
				INCICENTS_RADIUS);
		this.client.getIncidentManager()
				.getIncidentsInRadius(new IIncidentsResponseListener() {

					@Override
					public void onResult(List<Incident> data) {
						dismissDialog("Incidents received");
						incidentList.setIncidentsList(selectedCity, data);
					}

					@Override
					public void onError(Error error) {
						progressDialog.dismiss();
						incidentList.setIncidentsList(selectedCity, null);
						showError(error.toString());
					}

				}, params);
		requestsInProgress++;

		// Get the routes for the selected city
		Iterator<Place> iterator = selectedCity.getDestinationsList()
				.iterator();
		while (iterator.hasNext()) {
			final Place location = iterator.next();
			RouteOptions routeParams = new RouteOptions(selectedCity.getPoint(),
					location.getPoint());
			routeParams.setTolerance(ROUTES_TOLERANCE);
			routeParams.setNumAlternates(ROUTES_NUM_ALTERNATES);

			this.client.getRouteManager().requestRoutes(routeParams,
					new IRouteResponseListener() {

						@Override
						public void onResult(RoutesCollection data) {
							dismissDialog("Route to " + location.getName()
									+ " received");
							incidentList.addRoutesList(location,
									data.getRoutes());
						}

						@Override
						public void onError(Error error) {
							progressDialog.dismiss();
							showError(error.toString());
						}
					});

			requestsInProgress++;
		}
	}

	/**
	 * Dismiss dialog.
	 * 
	 * @param message
	 *            the message
	 */
	private void dismissDialog(String message) {
		--requestsInProgress;
		if (requestsInProgress <= 0) {
			requestsInProgress = 0;
			progressDialog.dismiss();
		} else {
			progressDialog.setMessage(message);
		}
	}

	/*
	 * Show error message
	 */
	private void showError(String errorMsg) {
		if (alertDialog != null && alertDialog.isShowing()) {
			alertDialog.setMessage(errorMsg);
			return;
		}

		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(BusyCommuterActivity.this);
		alertBuilder.setTitle("Error");
		alertBuilder.setMessage(errorMsg);
		alertBuilder.setPositiveButton("OK", null);
		alertDialog = alertBuilder.show();
	}
}
