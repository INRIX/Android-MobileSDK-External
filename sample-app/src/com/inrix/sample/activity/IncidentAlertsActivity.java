package com.inrix.sample.activity;

import java.util.Date;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.inrix.sample.BusProvider;
import com.inrix.sample.IncidentsReceivedEvent;
import com.inrix.sample.R;
import com.inrix.sample.activity.IncidentListActivity.TabsAdapter;
import com.inrix.sample.fragments.IncidentAlertsMapFragment;
import com.inrix.sample.fragments.IncidentListFragment;
import com.inrix.sample.fragments.IncidentsMapFragment;
import com.inrix.sdk.AlertsManager;
import com.inrix.sdk.AlertsManager.IFilter;
import com.inrix.sdk.AlertsManager.IIncidentsAlertListener;
import com.inrix.sdk.AlertsManager.IncidentAlertOptions;
import com.inrix.sdk.Error;
import com.inrix.sdk.IncidentAlert;
import com.inrix.sdk.Inrix;
import com.inrix.sdk.geolocation.GeolocationController;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Incident;

public class IncidentAlertsActivity extends FragmentActivity implements
		IIncidentsAlertListener,
		TabListener {
	static final int ALERT_INTERVAL = 15;
	private final GeoPoint SEATTLE_POSITION = new GeoPoint(47.614496,
			-122.328758);
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
		//hack the location for now.
		Location location = new Location("");
		location.setLatitude(SEATTLE_POSITION.getLatitude());
		location.setLongitude(SEATTLE_POSITION.getLongitude());
		location.setBearing(113);
		GeolocationController.getInstance().onGeolocationChange(location);
		
		Inrix.initialize(this, "inrixconfig.properties");
		//Inrix.initialize(this);
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
		AlertsManager alertManager = new AlertsManager();
		progressBar.setVisibility(View.VISIBLE);
		timestamp.setText("Loading...");
		
		/*over ride location provider*/
//		Location location = new Location("");
//		location.setLatitude(SEATTLE_POSITION.getLatitude());
//		location.setLongitude(SEATTLE_POSITION.getLongitude());
//		location.setBearing(5f);
//		GeolocationController.getInstance().setLocationSource(null);
//		GeolocationController.getInstance().onGeolocationChange(location);
		/*over ride location provider*/
		
		IncidentAlertOptions alertOptions = new IncidentAlertOptions(alertManager
				.getRefreshInterval(AlertsManager.ACTIONS.SMART_ALERT),
				new IFilter<Incident>() {

			@Override
			public boolean isItemAllowed(Incident item) {
				return true;
			}
		});
		alertOptions.setSpeedFactor(1f);
		alertOptions.setForwardConeAngle(120f);
		alert = alertManager.createIncidentAlert( this,alertOptions );
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
	 * @param incident
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
		BusProvider.getBus().post(new IncidentsReceivedEvent(list));
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
