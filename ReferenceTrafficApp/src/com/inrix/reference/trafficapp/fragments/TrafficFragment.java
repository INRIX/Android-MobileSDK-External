/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.fragments;

import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLngBounds;
import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.TrafficApp;
import com.inrix.reference.trafficapp.activity.MainMapActivity;
import com.inrix.reference.trafficapp.error.ActionRefreshEvent;
import com.inrix.reference.trafficapp.incidents.IncidentsActivity;
import com.inrix.reference.trafficapp.util.Constants;
import com.inrix.reference.trafficapp.util.DemoLocationSource;
import com.inrix.reference.trafficapp.util.WeatherAppConfig;
import com.inrix.reference.trafficapp.view.TrafficStatusPanel;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.TrafficManager;
import com.inrix.sdk.TrafficManager.Actions;
import com.inrix.sdk.geolocation.IGeolocationSource;
import com.inrix.sdk.geolocation.IOnGeolocationChangeListener;
import com.inrix.sdk.geolocation.PlayServicesLocationSource;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

/**
 * Traffic view fragment.
 */
/*
 * (non-Javadoc)
 * 
 * @see
 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
 * android.view.ViewGroup, android.os.Bundle)
 */
public class TrafficFragment extends InrixSimpleRefreshFragment implements
		IOnGeolocationChangeListener {

	private static final String TAG_MINI_MAP_FRAGMENT = "mini_map_fragment";
	private static final String PLACES_LIST_FRAGMENT = "places_list_fragment";

	private TrafficStatusPanel trafficStatus = null;
	private View rootView = null;
	private View mapOverlay = null;
	private long trafficRefreshInterval = Constants.DEFAULT_CONTENT_REFRESH_TIMEOUT_MS;

	private MiniMapFragment mapFragment;
	private PlacesListFragment placesFragment;
	private NavigationDrawerFragment navigationDrawer;

	private IGeolocationSource locationService = null;

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater
				.inflate(R.layout.fragment_traffic, container, false);

		if (savedInstanceState == null) {
			mapFragment = (MiniMapFragment) Fragment.instantiate(getActivity(),
					MiniMapFragment.class.getName());
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.map_frame, mapFragment, TAG_MINI_MAP_FRAGMENT)
					.commit();

			placesFragment = (PlacesListFragment) Fragment
					.instantiate(getActivity(),
							PlacesListFragment.class.getName());
			getFragmentManager()
					.beginTransaction()
					.replace(R.id.placesListFragment,
							placesFragment,
							PLACES_LIST_FRAGMENT).commit();
		} else {
			mapFragment = (MiniMapFragment) getFragmentManager()
					.findFragmentByTag(TAG_MINI_MAP_FRAGMENT);
			placesFragment = (PlacesListFragment) getFragmentManager()
					.findFragmentByTag(PLACES_LIST_FRAGMENT);
		}

		this.navigationDrawer = (NavigationDrawerFragment) this
				.getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);

		mapOverlay = rootView.findViewById(R.id.map_view_window);
		mapOverlay.setOnClickListener(onMapClickListener);

		this.postRecalculateComponentHeights();
		this.trafficStatus = (TrafficStatusPanel) rootView
				.findViewById(R.id.traffic_status_panel);

		// TODO: we are not allowed to use internal SDK classes. These classes
		// are public just for now, later on they will not be exposed. We need
		// to design a mechanizm for tracking current location on the app level
		boolean demoLocations = WeatherAppConfig
				.getCurrentAppConfig(getActivity()).getDemoLocations();
		if (demoLocations) {
			locationService = new DemoLocationSource(getActivity());
		} else {
			locationService = new PlayServicesLocationSource(getActivity());

		}
		locationService.activate(this);

		TrafficManager tManager = InrixCore.getTrafficManager();

		// get refresh interval gives the refresh interval in seconds.
		trafficRefreshInterval = tManager
				.getRefreshInterval(Actions.GET_TRAFFIC_QUALITY, TimeUnit.MILLISECONDS);
		this.setHasOptionsMenu(true);
		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		this.mapOverlay.setEnabled(true);
	}

	@Override
	public void onStart() {
		super.onStart();
		TrafficApp.getBus().register(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		TrafficApp.getBus().unregister(this);
	}

	@Subscribe
	public void actionRefreshEvent(ActionRefreshEvent refresh) {
		getData();
		mapFragment.refreshIncidents();
		mapFragment.refreshTrafficTiles();
		placesFragment.getData();
	}

	@Override
	public void onGeolocationChange(Location location) {
		locationService.deactivate();
		trafficStatus.setInfo(location);
	}

	@Override
	public void getData() {
		locationService.activate(this);
		// enable current location tracking for one geolocation update, so map
		// will be moved to our current location
		mapFragment.enableCurrentLocationTracking(true);
		super.getData();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// recalculation is delayed, because of the issue in onSizeChanged
		// first call will receive old height and width
		this.rootView.postDelayed(new Runnable() {

			@Override
			public void run() {
				postRecalculateComponentHeights();
				rootView.scrollTo(0, 0);
			}
		},
				100);
	}

	@Override
	public long getRefreshInterval() {
		return trafficRefreshInterval;
	}

	/** Handles on map clicks. */
	private OnClickListener onMapClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// Disables mapOverlay. It will be enabled in onResume method.
			// It prevents opening of few activities on multi-clicks.
			v.setEnabled(false);
			MainMapActivity.startActivity(getActivity(), null, null, 0);
		}
	};

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (this.navigationDrawer != null && this.navigationDrawer.isDrawerOpen()) {
			super.onCreateOptionsMenu(menu, inflater);
		} else {
			inflater.inflate(R.menu.traffic_page_incidents, menu);
			MenuItem menuItem = menu.findItem(R.id.action_incidents);
			menuItem.getActionView().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					launchIncidentsListFragment();
				}
			});
		}
	}

	/**
	 * Get the latlng bounds from the minimap fragment and launch the incidents
	 * activity with that bounds
	 */
	private void launchIncidentsListFragment() {
		LatLngBounds incidentsBox = null;
		MiniMapFragment miniMapFragment = (MiniMapFragment) getFragmentManager()
				.findFragmentByTag(TAG_MINI_MAP_FRAGMENT);
		if (null != miniMapFragment) {
			incidentsBox = miniMapFragment.getCurrentIncidentRequestBounds();
		}
		Location currentLocation = miniMapFragment.getlastKnownLocation();

		if (incidentsBox != null) {
			IncidentsActivity.launchIncidentsPage(getActivity(),
					incidentsBox,
					currentLocation);
		} else {
			Toast.makeText(getActivity(),
					R.string.incident_box_null,
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Post recalculate component heights.
	 */
	private void postRecalculateComponentHeights() {
		rootView.getViewTreeObserver()
				.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
					public boolean onPreDraw() {
						rootView.getViewTreeObserver()
								.removeOnPreDrawListener(this);
						recalculateComponentHeights();

						// return false here to skip drawing the first frame. If
						// true will be returned - old heights will be drawn
						return false;
					}
				});
	}

	private void recalculateComponentHeights() {

		int parentHeight = rootView.getHeight();
		View mapView = rootView.findViewById(R.id.map_frame);
		int halfParentHeight = parentHeight / 2;
		if (null != mapView) {
			LayoutParams lp = mapView.getLayoutParams();
			lp.height = halfParentHeight;
			mapView.setLayoutParams(lp);
		}
		// position the mapOverlay right over the map control
		if (null != mapOverlay) {
			LayoutParams lp = mapOverlay.getLayoutParams();
			lp.height = halfParentHeight;
			mapOverlay.setLayoutParams(lp);
		}
	}

}
