/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.locationpicker;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.inrix.reference.trafficapp.Globals;
import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.TrafficApp;
import com.inrix.reference.trafficapp.activity.ErrorControllerActivity;
import com.inrix.reference.trafficapp.error.ActionRefreshEvent;
import com.inrix.reference.trafficapp.error.ErrorEntity;
import com.inrix.reference.trafficapp.error.ErrorType;
import com.inrix.reference.trafficapp.view.SlidingBarErrorPresenter;
import com.inrix.sdk.geolocation.IOnGeolocationChangeListener;
import com.inrix.sdk.geolocation.PlayServicesLocationSource;
import com.squareup.otto.Subscribe;

public class LocationPickerActivity extends ErrorControllerActivity implements
		IOnGeolocationChangeListener {

	private static final String LOCATIONS_LIST_FRAGMENT_TAG = "locations_list_fragment";

	private static final String BUFFERED_SEARCH_RESULTS = "buffered_search_results";

	private static final String LOCATION_PICKER_MODE = "location_picker_mode";

	private static final String CURRENT_LOCATION_ID = "current_location_id";

	public static final int INVALID_LOCATION_ID = -1;

	public enum PLACES_ACTION {
		NOT_DEFINED, CREATE_HOME, EDIT_HOME, CREATE_WORK, EDIT_WORK
	}

	private ProgressBar progressBar;

	private SearchView searchView;

	private TextView actionTitle;

	private Location lastKnownLocation;

	private GeocoderTask currentLocationGeocodingTask = null;
	private GeocoderTask searchGeocodingTask = null;

	private List<GeocodingResult> content = null;

	private Drawable abBackgroundDrawable = null;

	private static final int AB_BACKGROUND_OPACITY = 0xCC;

	private PLACES_ACTION currentMode;

	private long currentLocationID = INVALID_LOCATION_ID;

	private View currentLocationView = null;
	private TextView currentLocationAddress = null;

	boolean currentLocationGeocoded = false;
	GeocodingResult currentLocationResult = null;
	private PlayServicesLocationSource locationSource;

	private OnSearchViewQueryTextListener queryListener = new OnSearchViewQueryTextListener();
	private String lastQueryString = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.abBackgroundDrawable = new ColorDrawable(Color.argb(255,
				255,
				255,
				255));
		this.abBackgroundDrawable.setAlpha(AB_BACKGROUND_OPACITY);
		this.abBackgroundDrawable.setCallback(this.drawableCallback);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_picker);

		Bundle bundle = getIntent().getExtras();

		int mode = bundle.getInt(LOCATION_PICKER_MODE,
				PLACES_ACTION.NOT_DEFINED.ordinal());
		currentMode = PLACES_ACTION.values()[mode];

		currentLocationID = bundle.getLong(CURRENT_LOCATION_ID,
				INVALID_LOCATION_ID);

		ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater()
				.inflate(R.layout.search_title_layout, null);
		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().setDisplayShowTitleEnabled(false);
		getActionBar().setCustomView(actionBarLayout);
		getActionBar().setBackgroundDrawable(abBackgroundDrawable);

		initActionBarControls(actionBarLayout);

		currentLocationView = findViewById(R.id.current_location);

		SlidingBarErrorPresenter presenter = (SlidingBarErrorPresenter) findViewById(R.id.error_bar);
		presenter.setMainContent(findViewById(R.id.content_frame));
		this.initializeErrorController(presenter, TrafficApp.getBus());

		initCurrentLocationView(currentLocationView);

		locationSource = new PlayServicesLocationSource(this);
		this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
		this.progressBar.getViewTreeObserver()
				.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						progressBar.getViewTreeObserver()
								.removeOnPreDrawListener(this);
						ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) progressBar.getLayoutParams();
						lp.topMargin -= getResources()
								.getDimension(R.dimen.progressbar_negative_top_margin);
						progressBar.setLayoutParams(lp);
						return true;
					}
				});

		if (savedInstanceState != null
				&& savedInstanceState.containsKey(BUFFERED_SEARCH_RESULTS)) {
			this.content = Arrays.asList((GeocodingResult[]) savedInstanceState
					.getParcelableArray(BUFFERED_SEARCH_RESULTS));
		}

		if (savedInstanceState == null
				&& getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			getWindow()
					.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!Globals.isLocationServicesEnabled(this)) {
			currentLocationAddress.setText(getResources()
					.getString(R.string.incident_repoort_no_location));
			ErrorEntity err = new ErrorEntity(ErrorType.LBS_OFF);
			TrafficApp.getBus().post(err);
		} else if (!currentLocationGeocoded) {
			locationSource.activate(this);
		}
		if (!Globals.isNetworkAvailable()) {
			ErrorEntity err = new ErrorEntity(ErrorType.NETWORK_OFF);
			TrafficApp.getBus().post(err);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		locationSource.deactivate();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (this.content != null && !this.content.isEmpty()) {
			outState.putParcelableArray(BUFFERED_SEARCH_RESULTS, this.content
					.toArray(new GeocodingResult[this.content.size()]));
		}

		super.onSaveInstanceState(outState);
	}

	private void initActionBarControls(ViewGroup actionBar) {
		if (null != actionBar) {
			searchView = (SearchView) actionBar
					.findViewById(R.id.places_search);
			searchView.setOnQueryTextListener(queryListener);

			actionTitle = (TextView) actionBar.findViewById(R.id.place_title);
			actionTitle.setText(getCurrentTitle());

			actionBar.findViewById(R.id.navigation_control)
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Fragment listFrag = getSupportFragmentManager()
									.findFragmentByTag(LOCATIONS_LIST_FRAGMENT_TAG);
							if (null != listFrag) {
								FragmentTransaction ft = getSupportFragmentManager()
										.beginTransaction();
								ft.remove(listFrag);
								ft.commit();
								currentLocationView.setVisibility(View.VISIBLE);
							} else {
								finish();
							}
						}
					});
		}
	}

	private void initCurrentLocationView(View rootView) {
		if (null != rootView) {
			((TextView) rootView.findViewById(R.id.title))
					.setText(getResources()
							.getString(R.string.current_location));

			currentLocationAddress = (TextView) rootView
					.findViewById(R.id.description);

			rootView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					GeocodingResult resultToPost = null;
					if (currentLocationGeocoded
							&& null != currentLocationResult) {
						resultToPost = currentLocationResult;
					} else {
						resultToPost = getSearchResultForCurrentLocation();
					}
					if (resultToPost != null) {
						TrafficApp.getBus().post(new ListItemSelectedEvent(0,
								resultToPost));
					} else {
						Toast.makeText(LocationPickerActivity.this,
								getString(R.string.error_current_location_unavailable),
								Toast.LENGTH_SHORT).show();
					}
				}
			});
		}
	}

	private GeocodingResult getSearchResultForCurrentLocation() {
		if (null != lastKnownLocation) {
			GeocodingResult result = new GeocodingResult();
			result.setPosition(new LatLng(lastKnownLocation.getLatitude(),
					lastKnownLocation.getLongitude()));
			result.setTitle(getResources().getString(R.string.current_location));
			return result;
		}
		return null;
	}

	private String getCurrentTitle() {
		String title = getResources().getString(R.string.add_home_label);
		if (currentMode == PLACES_ACTION.CREATE_HOME) {
			title = getResources().getString(R.string.add_home_label);
		} else if (currentMode == PLACES_ACTION.CREATE_WORK) {
			title = getResources().getString(R.string.add_work_label);
		} else if (currentMode == PLACES_ACTION.EDIT_HOME) {
			title = getResources().getString(R.string.edit_home_label);
		} else if (currentMode == PLACES_ACTION.EDIT_WORK) {
			title = getResources().getString(R.string.edit_work_label);
		}
		return title;
	}

	@Subscribe
	public void itemSelected(ListItemSelectedEvent event) {
		LatLng latLng = event.getItem().getPosition();
		String strAddr = event.getItem().getTitle();
		LocationPickerMapActivity.launchLocationPickerMap(this,
				latLng,
				currentMode.ordinal(),
				currentLocationID,
				strAddr);
	}

	@Override
	protected void onResume() {
		super.onResume();
		TrafficApp.getBus().register(this);
		/* if the locations list is shown hide the currentlocation card */
		if (null != getFragmentManager()
				.findFragmentByTag(LOCATIONS_LIST_FRAGMENT_TAG)) {
			this.currentLocationView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onPause() {
		cancelSearchTask();
		super.onPause();
		TrafficApp.getBus().unregister(this);
	}

	@Subscribe
	public void actionRefreshEvent(ActionRefreshEvent refresh) {
		if (!TextUtils.isEmpty(lastQueryString)) {
			queryListener.onQueryTextSubmit(lastQueryString);
		}
	}
	
	private void cancelSearchTask() {
		if (this.searchGeocodingTask != null) {
			this.searchGeocodingTask.cancel(true);
			this.searchGeocodingTask = null;
		}
	}

	private void showResultsList() {
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.add(R.id.locationsListView,
				LocationsListFragment.getInstance(content),
				LOCATIONS_LIST_FRAGMENT_TAG);
		t.commit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private void requestThroughGeocoder(String query) {
		this.progressBar.setVisibility(View.VISIBLE);

		cancelSearchTask();
		this.searchGeocodingTask = new GeocoderTask(this,
				lastKnownLocation,
				GeocoderTask.Mode.GENERIC_LOCATION_SEARCH);
		this.searchGeocodingTask.execute(query);
	}

	private void requestThroughGeocoder(LatLng position) {
		TrafficApp.getBus().post(new StartProgressEvent(true));
		if (this.currentLocationGeocodingTask != null) {
			this.currentLocationGeocodingTask.cancel(true);
			this.currentLocationGeocodingTask = null;
			this.progressBar.setVisibility(View.GONE);
		}
		this.currentLocationGeocodingTask = new GeocoderTask(this,
				lastKnownLocation,
				GeocoderTask.Mode.CURRENT_LOCATION_REVERSE_GEOCODING);
		this.currentLocationGeocodingTask.execute(position);
	}

	private void updateCurrentLocation(List<GeocodingResult> results) {
		this.progressBar.setVisibility(View.GONE);
		if (results == null || results.isEmpty()) {
			currentLocationAddress.setVisibility(View.GONE);
		} else {
			currentLocationResult = results.get(0);
			currentLocationAddress.setText(currentLocationResult.getTitle());
			currentLocationGeocoded = true;
		}
	}

	private void updateSearchResults(List<GeocodingResult> results) {
		this.progressBar.setVisibility(View.GONE);
		this.content = results;
		TrafficApp.getBus().post(new StartProgressEvent(false));

		if (results == null) {
			ErrorEntity errEntity = new ErrorEntity(ErrorType.NETWORK_ERROR);
			TrafficApp.getBus().post(errEntity);
			this.content = new ArrayList<GeocodingResult>();
		}
		if (null == this.content || this.content.isEmpty()) {
			currentLocationView.setVisibility(View.GONE);
		}
		showResultsList();
	}

	private void clearScreen() {
		content = null;
		Fragment locationsList = getSupportFragmentManager()
				.findFragmentByTag(LOCATIONS_LIST_FRAGMENT_TAG);
		if (null != locationsList) {
			getSupportFragmentManager().beginTransaction().remove(locationsList)
					.commit();
		}
	}

	@Override
	public void onAttachFragment(Fragment fragment) {
		super.onAttachFragment(fragment);

		if (fragment.getTag() != null
				&& fragment.getTag().equals(LOCATIONS_LIST_FRAGMENT_TAG)) {
			int alpha = 0xFF;
			ObjectAnimator animator = ObjectAnimator
					.ofInt(abBackgroundDrawable, "alpha", alpha);
			animator.setDuration(300);
			animator.start();
		}
	}

	@Subscribe
	public void onFragmentAnimationStart(FragmentAnimationStartEvent event) {
		if (event.getFragment().getTag() != null
				&& event.getFragment().getTag()
						.equals(LOCATIONS_LIST_FRAGMENT_TAG)) {
			int alpha = event.isEnter() ? 0xFF : AB_BACKGROUND_OPACITY;
			ObjectAnimator animator = ObjectAnimator
					.ofInt(abBackgroundDrawable, "alpha", alpha);
			animator.setDuration(300);
			animator.start();
		}
	}

	class OnSearchViewQueryTextListener implements
			SearchView.OnQueryTextListener {

		@Override
		public boolean onQueryTextSubmit(String query) {
			lastQueryString = query;
			clearScreen();
			requestThroughGeocoder(query);
			searchView.clearFocus();
			return true;
		}

		@Override
		public boolean onQueryTextChange(String newText) {
			return false;
		}
	}

	private static class GeocoderTask extends AsyncTask<Object, Void, List<GeocodingResult>> {

		public static enum Mode {
			INVALID_MODE, CURRENT_LOCATION_REVERSE_GEOCODING, GENERIC_LOCATION_SEARCH
		};

		private WeakReference<LocationPickerActivity> activity;

		private final int MAX_RESULTS = 20;

		private Location currentLocation;

		private Mode currentSearchMode = Mode.INVALID_MODE;

		GeocoderTask(LocationPickerActivity activity, Location currentLocation,
				Mode searchMode) {
			this.activity = new WeakReference<LocationPickerActivity>(activity);
			this.currentLocation = currentLocation;
			this.currentSearchMode = searchMode;
		}

		@Override
		protected List<GeocodingResult> doInBackground(Object... params) {
			LocationPickerActivity mainActivity = activity.get();
			if (mainActivity == null) {
				return null;
			}
			Geocoder geocoder = new Geocoder(mainActivity, Locale.getDefault());
			List<Address> addresses = null;
			ArrayList<GeocodingResult> results = null;
			try {
				if (params[0] instanceof String) {
					addresses = fromString((String) params[0], geocoder);
				} else if (params[0] instanceof LatLng) {
					addresses = fromLatLng((LatLng) params[0], geocoder);
				}
				results = new ArrayList<GeocodingResult>();
				if (addresses != null) {
					float[] distance = new float[1];
					for (Address a : addresses) {
						GeocodingResult r = GeocodingResult.fromAddress(a);
						if (currentLocation != null) {
							Location.distanceBetween(currentLocation
									.getLatitude(),
									currentLocation.getLongitude(),
									r.getPosition().latitude,
									r.getPosition().longitude,
									distance);
							r.setDistance(distance[0]);
						} else {
							r.setDistance(Float.NaN);
						}
						results.add(r);
					}
					Collections.sort(results);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return results;
		}

		private List<Address> fromString(String query, Geocoder geocoder)
				throws IOException {
			List<Address> addresses = null;
			if (currentLocation != null) {
				int radius = 2;
				double lat_bot = currentLocation.getLatitude() - radius;
				double lat_top = currentLocation.getLatitude() + radius;
				double lon_left = currentLocation.getLongitude() - radius;
				double lon_right = currentLocation.getLongitude() + radius;

				addresses = geocoder.getFromLocationName(query,
						MAX_RESULTS,
						lat_bot,
						lon_left,
						lat_top,
						lon_right);
			}
			if (addresses == null || addresses.isEmpty()) {
				addresses = geocoder.getFromLocationName(query, MAX_RESULTS);
			}
			return addresses;
		}

		private List<Address> fromLatLng(LatLng position, Geocoder geocoder)
				throws IOException {
			return geocoder.getFromLocation(position.latitude,
					position.longitude,
					1);
		}

		@Override
		protected void onPostExecute(List<GeocodingResult> results) {
			super.onPostExecute(results);
			if (activity.get() != null && !isCancelled()) {
				if (currentSearchMode == Mode.CURRENT_LOCATION_REVERSE_GEOCODING) {
					activity.get().updateCurrentLocation(results);
				} else if (currentSearchMode == Mode.GENERIC_LOCATION_SEARCH) {
					activity.get().updateSearchResults(results);
				}
			}
		}
	}

	private Drawable.Callback drawableCallback = new Drawable.Callback() {
		@Override
		public void invalidateDrawable(Drawable who) {
			getActionBar().setBackgroundDrawable(who);
		}

		@Override
		public void scheduleDrawable(Drawable who, Runnable what, long when) {

		}

		@Override
		public void unscheduleDrawable(Drawable who, Runnable what) {

		}
	};

	public static void launchForCreateHome(Context context) {
		launchLocationPicker(context,
				PLACES_ACTION.CREATE_HOME.ordinal(),
				INVALID_LOCATION_ID);
	}

	public static void launchForCreateWork(Context context) {
		launchLocationPicker(context,
				PLACES_ACTION.CREATE_WORK.ordinal(),
				INVALID_LOCATION_ID);
	}

	public static void launchForEditHome(Context context, long homeLocationID) {
		launchLocationPicker(context,
				PLACES_ACTION.EDIT_HOME.ordinal(),
				homeLocationID);
	}

	public static void launchForEditWork(Context context, long workLocationID) {
		launchLocationPicker(context,
				PLACES_ACTION.EDIT_WORK.ordinal(),
				workLocationID);
	}

	private static void launchLocationPicker(Context context,
			int mode,
			long locationID) {
		if (null != context) {
			Intent intent = new Intent(context, LocationPickerActivity.class);
			intent.putExtra(LOCATION_PICKER_MODE, mode);
			intent.putExtra(CURRENT_LOCATION_ID, locationID);
			context.startActivity(intent);
		}
	}

	@Override
	public void onGeolocationChange(Location location) {
		lastKnownLocation = location;
		requestThroughGeocoder(new LatLng(lastKnownLocation.getLatitude(),
				lastKnownLocation.getLongitude()));
		locationSource.deactivate();
	}
}
