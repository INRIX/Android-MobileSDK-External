package com.inrix.sample.activity;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.inrix.sample.BusProvider;
import com.inrix.sample.InrixIncidentsReceivedEvent;
import com.inrix.sample.R;
import com.inrix.sample.activity.IncidentListActivity.TabsAdapter;
import com.inrix.sample.fragments.IncidentAlertsMapFragment;
import com.inrix.sample.fragments.IncidentListFragment;
import com.inrix.sdk.AlertsManager;
import com.inrix.sdk.AlertsManager.IIncidentsAlertListener;
import com.inrix.sdk.AlertsManager.IncidentAlertOptions;
import com.inrix.sdk.Error;
import com.inrix.sdk.IncidentAlert;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.model.Incident;

public class IncidentAlertsActivity extends FragmentActivity implements
		IIncidentsAlertListener, TabListener {
	TextView timestamp;
	TextView status;
	IncidentAlert alert;
	ProgressBar progressBar;
	private ViewPager viewPager;
	private TabsAdapter tabsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alerts);
		InrixCore.initialize(this);
		this.timestamp = (TextView) findViewById(R.id.timestamp);
		this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		this.viewPager = (ViewPager) findViewById(R.id.pager);
		this.tabsAdapter = new TabsAdapter(this, viewPager);
		tabsAdapter.addTab(actionBar.newTab().setText("List"),
				IncidentListFragment.class,
				null);
		tabsAdapter.addTab(actionBar.newTab().setText("Map"),
				IncidentAlertsMapFragment.class,
				null);
		// Clear the Incident List
		setIncidentList(null);
	}

	@Override
	protected void onStart() {
		super.onStart();
		AlertsManager alertManager = InrixCore.getAlertsManager();
		progressBar.setVisibility(View.VISIBLE);
		timestamp.setText("Loading...");

		/* over ride location provider */
		// Location location = new Location("");
		// location.setLatitude(SEATTLE_POSITION.getLatitude());
		// location.setLongitude(SEATTLE_POSITION.getLongitude());
		// location.setBearing(5f);
		// GeolocationController.getInstance().setLocationSource(null);
		// GeolocationController.getInstance().onGeolocationChange(location);
		/* over ride location provider */

		IncidentAlertOptions alertOptions = new IncidentAlertOptions(alertManager
				.getRefreshInterval(AlertsManager.Actions.SMART_ALERT, TimeUnit.MILLISECONDS));
		alertOptions.setSpeedFactor(1f);
		alertOptions.setForwardConeAngle(120f);
		alert = alertManager.createIncidentAlert(alertOptions, this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (this.alert != null) {
			this.alert.cancel();
			this.alert = null;
		}
	}

	@Override
	public void onResult(List<Incident> data) {
		getSupportFragmentManager().popBackStack();
		setIncidentList(data);
		Date date = new Date(System.currentTimeMillis());

		this.timestamp.setText("Last update: " + date.toString()
				+ "\nLast requested distance:"
				+ this.alert.getLastRequestedDistance());
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public void onError(Error error) {
		getSupportFragmentManager().popBackStack();
		this.timestamp.setText("Unable to retrieve data: " + error.toString());
		progressBar.setVisibility(View.GONE);
	}

	/**
	 * 
	 * @param list
	 *            list
	 */
	private void setIncidentList(List<Incident> list) {
		if (list == null) {
			return;
		}
		if (list.isEmpty()) {
			// add dummy incident. Adapter will handle that properly
			list.add(new Incident());
		}
		BusProvider.getBus().post(new InrixIncidentsReceivedEvent(list));
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

}
