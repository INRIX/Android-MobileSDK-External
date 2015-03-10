/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.activity;

import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.ReferenceAppPreferences;
import com.inrix.reference.trafficapp.TrafficApp;
import com.inrix.reference.trafficapp.error.ActionRefreshEvent;
import com.inrix.reference.trafficapp.error.ErrorEntity;
import com.inrix.reference.trafficapp.fragments.AdsFragment;
import com.inrix.reference.trafficapp.fragments.AlertFragment;
import com.inrix.reference.trafficapp.fragments.DTWIncidentDetailsFragment;
import com.inrix.reference.trafficapp.fragments.IncidentDetailsFragment;
import com.inrix.reference.trafficapp.fragments.IncidentsMapFragment.IOnIncidentClicked;
import com.inrix.reference.trafficapp.fragments.mainmap.MainMapContentFragment;
import com.inrix.reference.trafficapp.util.AndroidCompatUtils;
import com.inrix.reference.trafficapp.util.Interfaces.DTWAlertCallback;
import com.inrix.reference.trafficapp.util.Interfaces.IGoogleMapProvider;
import com.inrix.reference.trafficapp.util.Interfaces.IIncidentRendered;
import com.inrix.reference.trafficapp.util.Interfaces.IOnFragmentAttachedListener;
import com.inrix.reference.trafficapp.util.Interfaces.IOnUpdateMapPaddingListener;
import com.inrix.reference.trafficapp.view.SlidingBarErrorPresenter;
import com.inrix.sdk.AlertsManager.IIncidentsAlertListener;
import com.inrix.sdk.AlertsManager.IOnRouteStatusListener;
import com.inrix.sdk.AlertsManager.IncidentAlertOptions;
import com.inrix.sdk.Error;
import com.inrix.sdk.IncidentUtils;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.LocationsManager.LocationType;
import com.inrix.sdk.RouteIncidentAlert;
import com.inrix.sdk.RouteManager;
import com.inrix.sdk.RouteManager.IRouteResponseListener;
import com.inrix.sdk.RouteManager.RouteOptions;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Incident;
import com.inrix.sdk.model.Route;
import com.inrix.sdk.model.RoutesCollection;
import com.squareup.otto.Subscribe;

/**
 * Displays main map fragment. Please use
 * {@link #startActivity(Context, Route, int) startActivity} to start activity.
 */
public class MainMapActivity extends ErrorControllerActivity implements
		IOnIncidentClicked, IOnFragmentAttachedListener,
		IOnUpdateMapPaddingListener, DTWAlertCallback, IGoogleMapProvider,
		IIncidentRendered {

	private final static String TAG_MAIN_MAP_CONTENT_FRAGMENT = "main_map_content_fragment";

	private final static String CURRENT_ERROR_EXTRA = "current_error_extra";

	private final static String RESTART_ALERT_EXTRA = "restart_alert_extra";

	public final static String TAG_INCIDENTS_DETAILS_FRAGMENT = "inrix_incidents_details_fragment";

	public final static String TAG_DTW_INCIDENTS_DETAILS_FRAGMENT = "inrix_dtw_incidents_details_fragment";

	public final static String TAG_ALERT_FRAGMENT = "alert_fragment";

	private final static int DEFAULT_ROUTE_TOLERANCE = 10;

	private final static long ALERT_REFRESH_TIME_MS = 30 * DateUtils.SECOND_IN_MILLIS;

	/** Route to display. */
	private static Route route;

	private static RouteIncidentAlert alert;
	private AlertFragment alertFragment;

	private static com.inrix.sdk.model.Location place;

	private RouteOptions routeOptions;
	private IncidentAlertOptions alertOptions;

	/** Main map content fragment. */
	private MainMapContentFragment mainMapContentFragment;

	private boolean isIncidentFragmentShown;
	private static int routeIndex;

	private boolean shouldRestartAlert = true;

	@SuppressLint("InflateParams")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_map_activity);
		AndroidCompatUtils.fixWindowContentOverlay(this);

		SlidingBarErrorPresenter errorPresenter = (SlidingBarErrorPresenter) findViewById(R.id.error_bar);
		errorPresenter.setMainContent(findViewById(R.id.content_frame));

		this.initializeErrorController(errorPresenter, TrafficApp.getBus());

		final TextView customView = (TextView) LayoutInflater.from(this)
				.inflate(R.layout.main_custom_action_bar, null);
		final ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
				ActionBar.LayoutParams.WRAP_CONTENT,
				Gravity.CENTER);

		this.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);
		this.getActionBar().setCustomView(customView, params);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		this.getActionBar().setHomeButtonEnabled(true);

		this.initFragments(savedInstanceState);
		this.alertOptions = new IncidentAlertOptions(ALERT_REFRESH_TIME_MS);
		this.alertOptions.setFilter(IncidentUtils.getDefaultFilter());

		this.alertFragment = (AlertFragment) getSupportFragmentManager()
				.findFragmentByTag(TAG_ALERT_FRAGMENT);
		if (this.alertFragment == null) {
			this.alertFragment = new AlertFragment();
			getSupportFragmentManager().beginTransaction()
					.add(this.alertFragment, TAG_ALERT_FRAGMENT).commit();
		}
		if (route != null) {
			boolean zoomToRoutes = !isIncidentFragmentShown
					&& savedInstanceState == null;
			mainMapContentFragment.setRoute(route, zoomToRoutes);

			this.routeOptions = new RouteOptions(new GeoPoint(route.getPoints()
					.get(0).getLatitude(), route.getPoints().get(0)
					.getLongitude()), place.getGeoPoint());
		}
		this.setTitle();

		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(CURRENT_ERROR_EXTRA)) {
				getErrorController()
						.onError((ErrorEntity) savedInstanceState.getParcelable(CURRENT_ERROR_EXTRA));
			}
			shouldRestartAlert = savedInstanceState
					.getBoolean(RESTART_ALERT_EXTRA, true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */

	@Override
	protected final void onResume() {
		super.onResume();
		if (shouldRestartAlert && route != null) {
			startRouteAlerts();
		}
	}

	private void startRouteAlerts() {
		if (alert != null) {
			alert.start();
		} else {
			alert = (InrixCore.getAlertsManager())
					.createRouteIncidentAlert(this.routeOptions,
							this.alertOptions,
							routeIndex,
							this.alertFragment.getIRouteResponseListener(),
							this.alertFragment.getIIncidentsAlertListener(),
							this.alertFragment.getIOnRouteStatusListener());
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (!isChangingConfigurations()) {
			if (getSupportFragmentManager()
					.findFragmentByTag(TAG_DTW_INCIDENTS_DETAILS_FRAGMENT) != null
					|| getSupportFragmentManager()
							.findFragmentByTag(TAG_INCIDENTS_DETAILS_FRAGMENT) != null) {
				getSupportFragmentManager().popBackStack();
			}
			if (alert != null) {
				alert.cancel();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#setTitle(int)
	 */
	@Override
	public final void setTitle(final int titleId) {
		if ( (this.getActionBar().getDisplayOptions() & ActionBar.DISPLAY_SHOW_CUSTOM) == ActionBar.DISPLAY_SHOW_CUSTOM) {
			this.setTitle(this.getString(titleId));
		} else {
			super.setTitle(titleId);
		}
	}

	@Subscribe
	public void actionRefreshEvent(ActionRefreshEvent refresh) {
		this.mainMapContentFragment.refreshMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#setTitle(java.lang.CharSequence)
	 */
	@Override
	public final void setTitle(final CharSequence title) {
		if ( (this.getActionBar().getDisplayOptions() & ActionBar.DISPLAY_SHOW_CUSTOM) == ActionBar.DISPLAY_SHOW_CUSTOM) {
			final TextView titleText = (TextView) this
					.findViewById(android.R.id.title);
			titleText.setText(title);
		} else {
			super.setTitle(title);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_activity, menu);
		MenuItem warnings = menu.findItem(R.id.action_drive_time_warnings);
		warnings.setTitle(ReferenceAppPreferences.isDriveTimeWarningsEnabled() ? R.string.drive_time_warnings_menu_off
				: R.string.drive_time_warnings_menu_on);
		return super.onCreateOptionsMenu(menu);
	}

	/** Sets title for the activity depending on passed parameters. */
	private void setTitle() {
		if (place != null) {
			if (place.getLocationType() == LocationType.WORK.getValue()) {
				setTitle(R.string.to_work);
			} else if (place.getLocationType() == LocationType.HOME.getValue()) {
				setTitle(R.string.to_home);
			} else {
				setTitle(R.string.main_map_title);
			}
		} else {
			setTitle(R.string.main_map_title);
		}
	}

	/**
	 * Initializes all fragments.
	 * 
	 * @param savedInstanceState
	 *            Bundle to store necessary data if the activity is being
	 *            re-initialized.
	 */
	private void initFragments(Bundle savedInstanceState) {
		initContentFragment(savedInstanceState);
		initAdsFragment(savedInstanceState);
	}

	/**
	 * Initializes main map content fragment.
	 * 
	 * @param savedInstanceState
	 *            Bundle to store necessary data if the activity is being
	 *            re-initialized.
	 */
	private void initContentFragment(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			this.mainMapContentFragment = (MainMapContentFragment) Fragment
					.instantiate(this, MainMapContentFragment.class.getName());

			this.getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.content_frame,
							this.mainMapContentFragment,
							TAG_MAIN_MAP_CONTENT_FRAGMENT).commit();
		} else {
			this.mainMapContentFragment = (MainMapContentFragment) this
					.getSupportFragmentManager()
					.findFragmentByTag(TAG_MAIN_MAP_CONTENT_FRAGMENT);
		}
	}

	/**
	 * Initializes ADs fragment.
	 * 
	 * @param savedInstanceState
	 *            Bundle to store necessary data if the activity is being
	 *            re-initialized.
	 */
	private void initAdsFragment(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.ads_container,
							Fragment.instantiate(this,
									AdsFragment.class.getName())).commit();
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
			case android.R.id.home:
				finish();
				break;
			case R.id.action_refresh:
				this.mainMapContentFragment.refreshMap();
				if (route != null && alert != null) {
					alert.cancel();
					startRouteAlerts();
				}
				break;
			case R.id.action_drive_time_warnings:
				boolean isEnabled = !ReferenceAppPreferences
						.isDriveTimeWarningsEnabled();
				ReferenceAppPreferences.setDriveTimeWarningsEnabled(isEnabled);
				item.setTitle(isEnabled ? R.string.drive_time_warnings_menu_off
						: R.string.drive_time_warnings_menu_on);
				break;
		}
		return true;
	}

	@Override
	public boolean onIncidentClicked(List<Incident> incidents, LatLng position) {
		Fragment currentFragment = getSupportFragmentManager()
				.findFragmentByTag(TAG_INCIDENTS_DETAILS_FRAGMENT);
		if (currentFragment != null) {
			getSupportFragmentManager().popBackStack();
		}

		this.mainMapContentFragment.onBeforeIncidentDetailsShow();

		Collections.sort(incidents, IncidentUtils.getDefaultComparator());

		final Bundle args = new Bundle();
		args.putParcelable(IncidentDetailsFragment.CURRENT_LOCATION,
				mainMapContentFragment.getLastKnownLocation());
		args.putParcelableArray(IncidentDetailsFragment.INCIDENTS,
				incidents.toArray(new Incident[0]));
		args.putParcelable(IncidentDetailsFragment.MARKER_LOCATION, position);

		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.content_frame,
						Fragment.instantiate(this,
								IncidentDetailsFragment.class.getName(),
								args),
						TAG_INCIDENTS_DETAILS_FRAGMENT).addToBackStack(null)
				.commitAllowingStateLoss();

		// do not consume this event, so underlying fragment can move map
		return false;
	}

	@Override
	public void onFragmentAttached(Fragment fragment) {
		if ( (fragment.getTag().equals(TAG_INCIDENTS_DETAILS_FRAGMENT) || fragment
				.getTag().equals(TAG_DTW_INCIDENTS_DETAILS_FRAGMENT))
				&& mainMapContentFragment != null) {
			mainMapContentFragment.showMapControls(false);
			this.isIncidentFragmentShown = true;
		}
		if (fragment.getTag().equals(TAG_DTW_INCIDENTS_DETAILS_FRAGMENT)) {
			((DTWIncidentDetailsFragment) fragment).updatePadding();
		}
		if (fragment.getTag().equals(TAG_INCIDENTS_DETAILS_FRAGMENT)) {
			((IncidentDetailsFragment) fragment).updatePadding();
		}
	}

	@Override
	public void onFragmentDetached(Fragment fragment) {
		if ( (fragment.getTag().equals(TAG_INCIDENTS_DETAILS_FRAGMENT) || fragment
				.getTag().equals(TAG_DTW_INCIDENTS_DETAILS_FRAGMENT))
				&& mainMapContentFragment != null) {
			this.mainMapContentFragment.showMapControls(true);
			this.isIncidentFragmentShown = false;

			if (fragment.getTag().equals(TAG_INCIDENTS_DETAILS_FRAGMENT)) {
				// if the map was centered by the incident fragment (after
				// click
				// on map), do not restore previous state.
				if (fragment.getArguments()
						.getBoolean(IncidentDetailsFragment.CENTER_ON_INCIDENT,
								false)) {
					return;
				}
			}
			mainMapContentFragment.onIncidentDetailsHide();
		}
	}

	@Override
	public void onUpdateMapPadding(Rect padding, LatLng mapCenter) {
		if (mainMapContentFragment != null) {
			mainMapContentFragment.setMapPadding(padding, mapCenter);
		}
	}

	@Override
	public void onUpdateMapPadding(Rect padding, LatLng mapCenter, float zoom) {
		if (mainMapContentFragment != null) {
			mainMapContentFragment.setMapPadding(padding, mapCenter, zoom);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (getErrorController().getActiveError() != null) {
			outState.putParcelable(CURRENT_ERROR_EXTRA, getErrorController()
					.getActiveError());
		}
		shouldRestartAlert = !isChangingConfigurations();
		outState.putBoolean(RESTART_ALERT_EXTRA, shouldRestartAlert);
		super.onSaveInstanceState(outState);
	}

	private void requestRoute(GeoPoint start,
			GeoPoint finish,
			IRouteResponseListener listener) {
		RouteOptions options = new RouteOptions(start, finish);
		options.setNumAlternates(0);
		options.setTolerance(DEFAULT_ROUTE_TOLERANCE);
		options.setOutputFields(RouteManager.ROUTE_OUTPUT_FIELD_SUMMARY);
		InrixCore.getRouteManager().requestRoutes(options, listener);
	}

	private IRouteResponseListener updateRouteTimeListener = new IRouteResponseListener() {

		@Override
		public void onResult(RoutesCollection routesCollection) {
			List<Route> routes;
			if (routesCollection != null
					&& (routes = routesCollection.getRoutes()) != null
					&& !routes.isEmpty() && routes.get(0) != null) {
				mainMapContentFragment.updateRouteTime(route, routes.get(0)
						.getTravelTimeMinutes());
			}
		}

		@Override
		public void onError(Error error) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(),
							R.string.place_control_route_error,
							Toast.LENGTH_SHORT).show();
				}
			});
		}
	};

	private IRouteResponseListener updateRoutePointsListener = new IRouteResponseListener() {
		@Override
		public void onResult(RoutesCollection routesCollection) {
			List<Route> routes;
			if (routesCollection != null
					&& (routes = routesCollection.getRoutes()) != null
					&& !routes.isEmpty() && place != null
					&& routes.get(alert.getRouteIndex()) != null) {
				route = routes.get(alert.getRouteIndex());
				mainMapContentFragment.setRoute(route, false);
				requestRoute(new GeoPoint(mainMapContentFragment
						.getLastKnownLocation().getLatitude(),
						mainMapContentFragment.getLastKnownLocation()
								.getLongitude()),
						place.getGeoPoint(),
						updateRouteTimeListener);
			}
		}

		@Override
		public void onError(Error error) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(),
							R.string.place_control_route_error,
							Toast.LENGTH_SHORT).show();
				}
			});
		}
	};

	private IIncidentsAlertListener incidentsListener = new IIncidentsAlertListener() {

		@Override
		public void onError(Error error) {

		}

		@Override
		public void onResult(final List<Incident> data) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (!ReferenceAppPreferences.isDriveTimeWarningsEnabled()) {
						return;
					}
					Collections
							.sort(data, IncidentUtils.getDefaultComparator());

					Fragment currentFragment = getSupportFragmentManager()
							.findFragmentByTag(TAG_DTW_INCIDENTS_DETAILS_FRAGMENT);
					if (currentFragment == null) {
						currentFragment = getSupportFragmentManager()
								.findFragmentByTag(TAG_INCIDENTS_DETAILS_FRAGMENT);
					}
					if (currentFragment != null) {
						getSupportFragmentManager().popBackStack();
					} else {
						mainMapContentFragment.onBeforeIncidentDetailsShow();
					}
					final Bundle args = new Bundle();
					args.putParcelable(DTWIncidentDetailsFragment.CURRENT_LOCATION,
							mainMapContentFragment.getLastKnownLocation());
					args.putParcelable(DTWIncidentDetailsFragment.INCIDENT,
							data.get(0));

					getSupportFragmentManager()
							.beginTransaction()
							.add(R.id.content_frame,
									Fragment.instantiate(MainMapActivity.this,
											DTWIncidentDetailsFragment.class
													.getName(),
											args),
									TAG_DTW_INCIDENTS_DETAILS_FRAGMENT)
							.addToBackStack(null).commitAllowingStateLoss();
				}
			});
		}
	};

	private IOnRouteStatusListener routeStatusListener = new IOnRouteStatusListener() {
		@Override
		public void onRouteStatus(OnRouteStatus routeStatus) {
			switch (routeStatus) {
				case AT_DESTINATION:
					route = null;
					place = null;
					mainMapContentFragment.setRoute(null, false);
					setTitle();
					Toast.makeText(MainMapActivity.this,
							R.string.you_are_here,
							Toast.LENGTH_LONG).show();
					alert.cancel();
					alert = null;
					break;
				default:
					break;
			}
		}
	};

	/**
	 * Starts the activity with specific parameters.
	 * 
	 * @param context
	 *            Valid context.
	 * @param place
	 *            Destination place.
	 * @param route
	 *            Route associated with place.
	 * @param selected
	 *            Index of the route.
	 */
	public static void startActivity(Context context,
			com.inrix.sdk.model.Location place,
			Route route,
			int selected) {
		MainMapActivity.route = route;
		MainMapActivity.place = place;
		MainMapActivity.alert = null;
		MainMapActivity.routeIndex = selected;
		Intent mainMapIntent = new Intent(context, MainMapActivity.class);
		context.startActivity(mainMapIntent);
	}

	@Override
	public IRouteResponseListener getIRouteResponseListener() {
		return this.updateRoutePointsListener;
	}

	@Override
	public IIncidentsAlertListener getIIncidentsAlertListener() {
		return this.incidentsListener;
	}

	@Override
	public IOnRouteStatusListener getIOnRouteStatusListener() {
		return this.routeStatusListener;
	}

	@Override
	public GoogleMap getGoogleMap() {
		return this.mainMapContentFragment.getGoogleMap();
	}

	@Override
	public void enableIncidents(boolean enable) {
		this.mainMapContentFragment.enableIncidents(enable);
	}
}
