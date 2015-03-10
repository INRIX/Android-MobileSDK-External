/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.incidents;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLngBounds;
import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.TrafficApp;
import com.inrix.reference.trafficapp.activity.ErrorControllerActivity;
import com.inrix.reference.trafficapp.fragments.AdsFragment;
import com.inrix.reference.trafficapp.util.Interfaces.IOnFragmentAttachedListener;
import com.inrix.reference.trafficapp.view.SlidingBarErrorPresenter;
import com.inrix.sdk.model.Incident;
import com.squareup.otto.Subscribe;

public class IncidentsActivity extends ErrorControllerActivity implements
		IOnFragmentAttachedListener {

	/* tag for the incidents fragment */
	private static final String INCIDENTS_LIST_FRAGMENT_TAG = "incidents_list_fragment";

	/* tag for the incidents fragment */
	private static final String INCIDENTS_MAP_FRAGMENT_TAG = "incidents_map_fragment";

	/* tag for incidents box bounds */
	private static final String INCIDENTS_BOUNDS = "incidents_bounds";

	/* tag for current location */
	private static final String CURRENT_LOCATION = "current_location";

	/* tage for current map incident */
	private static final String CURRENT_MAP_INCIDENT = "current_map_incident";

	private LatLngBounds currentBounds = null;
	private Location currentLocation = null;

	private Incident currentMapIncident = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incidents_list);

		Bundle bundle = getIntent().getExtras();

		if (null != bundle) {
			currentBounds = (LatLngBounds) bundle.get(INCIDENTS_BOUNDS);
			currentLocation = (Location) bundle.get(CURRENT_LOCATION);
		}

		if (savedInstanceState == null) {
			final Fragment incidentsListFragment = IncidentsListFragment.initInstance(
					currentBounds,
					currentLocation);
			final Fragment adsFragment = Fragment.instantiate(this, AdsFragment.class.getName());
			
			final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.add(R.id.incidentsListView, incidentsListFragment, INCIDENTS_LIST_FRAGMENT_TAG);
			transaction.add(R.id.ads_container, adsFragment);
			transaction.commit();
		} else {
			currentMapIncident = savedInstanceState.getParcelable(CURRENT_MAP_INCIDENT);
		}
		
		final TextView customView = (TextView) LayoutInflater.from(this)
				.inflate(R.layout.main_custom_action_bar, null);

		final ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
				ActionBar.LayoutParams.WRAP_CONTENT,
				Gravity.CENTER);

		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(false);
		getActionBar().setTitle("");
		getActionBar().setCustomView(customView, params);
		
		SlidingBarErrorPresenter presenter = (SlidingBarErrorPresenter) findViewById(R.id.error_bar);
		presenter.setMainContent(findViewById(R.id.incidentsListView));
		this.initializeErrorController(presenter, TrafficApp.getBus());


	}

	@Override
	protected void onResume() {
		super.onResume();
		if (null == currentMapIncident) {
			setWindowTitle(getResources().getString(R.string.incidents));
		}else{
			setWindowTitle(IncidentDisplayUtils.getTitle(this, currentMapIncident));
		}
		TrafficApp.getBus().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		TrafficApp.getBus().unregister(this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outBundle) {
		super.onSaveInstanceState(outBundle);
		outBundle.putParcelable(CURRENT_MAP_INCIDENT, currentMapIncident);
	}

	@Override
	public final boolean onOptionsItemSelected(final MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			if (this.getSupportFragmentManager().getBackStackEntryCount() > 0) {
				this.getSupportFragmentManager().popBackStack();
			} else {
				this.finish();
			}
			
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Subscribe
	public void itemSelected(IncidentSelectedEvent event) {
		final Incident incident = event.getIncident();
			setWindowTitle(IncidentDisplayUtils.getTitle(this, incident));
			currentMapIncident = incident;
		addIncidentMap(incident);
	}

	private void setWindowTitle(String title) {
		final TextView titleText = (TextView) this
				.findViewById(android.R.id.title);
		if (titleText != null) {
			titleText.setText(title);
		}
	}

	@Override
	public void onFragmentAttached(Fragment fragment) {
	}

	@Override
	public void onFragmentDetached(Fragment fragment) {
		if (fragment instanceof IncidentOnMapFragment) {
			this.currentMapIncident = null;
			setWindowTitle(getResources().getString(R.string.incidents));
		}
	}

	private void addIncidentMap(Incident selectedIncident) {
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.incidentsListView,
							IncidentOnMapFragment.getInstance(selectedIncident,
									currentLocation),
							INCIDENTS_MAP_FRAGMENT_TAG)
					.addToBackStack(null)
					.commit();
	}
	
	public static void launchIncidentsPage(Context context,
			LatLngBounds bounds,
			Location currentLocation) {
		if (null != context) {
			Intent intent = new Intent(context, IncidentsActivity.class);
			intent.putExtra(INCIDENTS_BOUNDS, bounds);
			intent.putExtra(CURRENT_LOCATION, currentLocation);
			context.startActivity(intent);
		}
	}
}
