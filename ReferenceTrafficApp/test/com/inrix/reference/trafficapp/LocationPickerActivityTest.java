/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLocationManager;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.ActivityController;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.inrix.sdk.utils.UserPreferences;
import com.inrix.reference.trafficapp.locationpicker.LocationPickerActivity;
import com.inrix.reference.trafficapp.locationpicker.LocationPickerActivity.PLACES_ACTION;

@RunWith(RobolectricTestRunner.class)
public class LocationPickerActivityTest {

	private FragmentActivity locationPickerActivity;
	private Location testLocation = new Location(LocationManager.GPS_PROVIDER);
	private static final String LOCATION_PICKER_MODE = "location_picker_mode";


	@Before
	public void setup() {
		ShadowLog.stream = System.out;

		UserPreferences.load(Robolectric.application);
	}

	@After
	public void teardown() {
		locationPickerActivity = null;
	}

	@Test
	public void testStartLocationPickerActivityForHome()
			throws InterruptedException {

		// start activity from the intent
		Intent intent = new Intent(Robolectric.application,
				LocationPickerActivity.class);
		intent.putExtra(LOCATION_PICKER_MODE ,
				PLACES_ACTION.CREATE_HOME.ordinal());
		FragmentActivity locationPickerActivity = Robolectric
				.buildActivity(LocationPickerActivity.class).withIntent(intent)
				.create().get();

		assertEquals(locationPickerActivity.getString(R.string.app_name),
				locationPickerActivity.getTitle());
		View currentLocationView = locationPickerActivity
				.findViewById(R.id.current_location);
		assertEquals(View.VISIBLE, currentLocationView.getVisibility());
		assertEquals(View.VISIBLE,
				currentLocationView.findViewById(R.id.location_icon)
						.getVisibility());
		assertEquals(View.VISIBLE,
				locationPickerActivity.findViewById(R.id.places_search)
						.getVisibility());

		TextView actionTitle = (TextView) locationPickerActivity
				.findViewById(R.id.place_title);
		assertEquals(locationPickerActivity.getString(R.string.add_home_label),
				actionTitle.getText());

		Robolectric.runUiThreadTasksIncludingDelayedTasks();

		// verify search results fragment is null
		assertEquals(null, locationPickerActivity.getFragmentManager()
				.findFragmentByTag("locations_list_fragment"));
	}

	@Test
	public void testStartLocationPickerActivityForWork()
			throws InterruptedException {

		// start activity from the intent
		Intent intent = new Intent(Robolectric.application,
				LocationPickerActivity.class);
		intent.putExtra(LOCATION_PICKER_MODE ,
				PLACES_ACTION.CREATE_WORK.ordinal());
		FragmentActivity locationPickerActivity = Robolectric
				.buildActivity(LocationPickerActivity.class).withIntent(intent)
				.create().get();

		assertEquals(locationPickerActivity.getString(R.string.app_name),
				locationPickerActivity.getTitle());
		View currentLocationView = locationPickerActivity
				.findViewById(R.id.current_location);
		assertEquals(View.VISIBLE, currentLocationView.getVisibility());
		assertEquals(View.VISIBLE,
				currentLocationView.findViewById(R.id.location_icon)
						.getVisibility());
		assertEquals(View.VISIBLE,
				locationPickerActivity.findViewById(R.id.places_search)
						.getVisibility());

		TextView actionTitle = (TextView) locationPickerActivity
				.findViewById(R.id.place_title);
		assertEquals(locationPickerActivity.getString(R.string.add_work_label),
				actionTitle.getText());

		Robolectric.runUiThreadTasksIncludingDelayedTasks();

		// verify search results fragment is null
		assertEquals(null, locationPickerActivity.getFragmentManager()
				.findFragmentByTag("locations_list_fragment"));
	}

	// verify currentLocation not available toast
	@Test
	public void testTapCurrentLocationNAToast() throws InterruptedException {
		Intent intent = new Intent(Robolectric.application,
				LocationPickerActivity.class);
		intent.putExtra(LOCATION_PICKER_MODE ,
				PLACES_ACTION.CREATE_HOME.ordinal());
		FragmentActivity locationPickerActivity = Robolectric
				.buildActivity(LocationPickerActivity.class).withIntent(intent)
				.create().get();

		View currentLocationView = locationPickerActivity
				.findViewById(R.id.current_location);
		currentLocationView.performClick();

		Robolectric.runUiThreadTasksIncludingDelayedTasks();
		assertEquals(
				locationPickerActivity
						.getString(R.string.error_current_location_unavailable),
				ShadowToast.getTextOfLatestToast());

	}

	// verify currentLocation is available
	@Test
	public void testTapCurrentLocationAvailable() throws InterruptedException {
		
		
		testLocation.setLatitude(47.618198);
		testLocation.setLongitude(-122.33963);
	
		LocationManager locationManager = (LocationManager) 
                Robolectric.application.getSystemService(Context.LOCATION_SERVICE);
		ShadowLocationManager slm = Robolectric.shadowOf(locationManager);
		
		slm.setProviderEnabled(LocationManager.GPS_PROVIDER, true);
		slm.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);		
		
		Intent intent = new Intent(Robolectric.application,
				LocationPickerActivity.class);
		intent.putExtra(LOCATION_PICKER_MODE ,
				PLACES_ACTION.CREATE_HOME.ordinal());
		FragmentActivity locationPickerActivity = Robolectric
				.buildActivity(LocationPickerActivity.class).withIntent(intent)
				.create().start().visible().get();
		
	    ((LocationPickerActivity)locationPickerActivity).onGeolocationChange(testLocation);
		Robolectric.runUiThreadTasksIncludingDelayedTasks();

		assertEquals(null,ShadowToast.getTextOfLatestToast());
		assertEquals(View.VISIBLE, locationPickerActivity
				.findViewById(R.id.current_location).getVisibility());

	}
	
    // verify currentLocation is available
	@Test
	public void testTapCurrentLocationChangesAvailableToUnavailable() throws InterruptedException {
		
		//set current location 
		testLocation.setLatitude(47.618198);
		testLocation.setLongitude(-122.33963);
	
		LocationManager locationManager = (LocationManager) 
                Robolectric.application.getSystemService(Context.LOCATION_SERVICE);
		ShadowLocationManager slm = Robolectric.shadowOf(locationManager);
		
		slm.setProviderEnabled(LocationManager.GPS_PROVIDER, true);
		slm.setProviderEnabled(LocationManager.NETWORK_PROVIDER, true);		
		
		Intent intent = new Intent(Robolectric.application,
				LocationPickerActivity.class);
		intent.putExtra(LOCATION_PICKER_MODE ,
				PLACES_ACTION.CREATE_HOME.ordinal());
		
		ActivityController<LocationPickerActivity> controller = Robolectric
				.buildActivity(LocationPickerActivity.class).withIntent(intent);
		
		FragmentActivity locationPickerActivity = controller
				.create().get();
		
	    ((LocationPickerActivity)locationPickerActivity).onGeolocationChange(testLocation);
		Robolectric.runUiThreadTasksIncludingDelayedTasks();

		//disable location provider
		slm.setProviderEnabled(LocationManager.GPS_PROVIDER, false);
		slm.setProviderEnabled(LocationManager.NETWORK_PROVIDER, false);
		
		controller.start().get();
		controller.stop().get();
		controller.start().get();
		
		Robolectric.runUiThreadTasksIncludingDelayedTasks();
		assertEquals(null,ShadowToast.getTextOfLatestToast());
		assertEquals(View.VISIBLE, locationPickerActivity
				.findViewById(R.id.current_location).getVisibility());
	}
	

	@Test
	public void testSearchFieldTextInput() throws InterruptedException {
		//create activity from the intent
		Intent intent = new Intent(Robolectric.application,
				LocationPickerActivity.class);
		intent.putExtra(LOCATION_PICKER_MODE ,
				PLACES_ACTION.CREATE_HOME.ordinal());
		FragmentActivity locationPickerActivity = Robolectric
				.buildActivity(LocationPickerActivity.class).withIntent(intent)
				.create().get();
		SearchView searchView = (SearchView) locationPickerActivity
				.findViewById(R.id.places_search);
		searchView.setQuery("pizza", true);

		Robolectric.runUiThreadTasksIncludingDelayedTasks();

		
		FragmentManager fragmentManager = locationPickerActivity.getSupportFragmentManager();
		ListFragment fragment = (ListFragment) fragmentManager
				.findFragmentByTag("locations_list_fragment");
		assertTrue(fragment.isAdded());
		assertTrue(!fragment.isHidden());
		//assertTrue(fragment.isVisible());
		assertEquals(View.VISIBLE,
				locationPickerActivity.findViewById(R.id.locationsListView)
						.getVisibility());
		
	}

	@Test
	public void testChangeLocationPickerActivityResumed() {
		Intent intent = new Intent(Robolectric.application,
				LocationPickerActivity.class);
		intent.putExtra(LOCATION_PICKER_MODE ,
				PLACES_ACTION.CREATE_HOME.ordinal());

		ActivityController<LocationPickerActivity> controller = Robolectric
				.buildActivity(LocationPickerActivity.class).withIntent(intent);
//		locationPickerActivity = controller.create().start().resume().visible().get();
		locationPickerActivity = controller.create().resume().get();
		
		SearchView searchView = (SearchView) locationPickerActivity
				.findViewById(R.id.places_search);
		searchView.setQuery("pizza", true);

		Robolectric.runUiThreadTasksIncludingDelayedTasks();

		FragmentManager fragmentManager = locationPickerActivity.getSupportFragmentManager();
		ListFragment fragment = (ListFragment) fragmentManager
				.findFragmentByTag("locations_list_fragment");
		assertTrue(fragment.isAdded());

		controller.pause().get();
		controller.resume().get();

		Robolectric.runUiThreadTasksIncludingDelayedTasks();
		assertTrue(locationPickerActivity.getSupportFragmentManager()
				.findFragmentByTag("locations_list_fragment").isAdded());
	}

	@Test
	public void testChangeLocationPickerActivityRestarted() {
		Intent intent = new Intent(Robolectric.application,
				LocationPickerActivity.class);
		intent.putExtra(LOCATION_PICKER_MODE ,
				PLACES_ACTION.CREATE_HOME.ordinal());

		ActivityController<LocationPickerActivity> controller = Robolectric
				.buildActivity(LocationPickerActivity.class).withIntent(intent);
		locationPickerActivity = controller.create().start().get();

		SearchView searchView = (SearchView) locationPickerActivity
				.findViewById(R.id.places_search);
		searchView.setQuery("pizza", true);

		Robolectric.runUiThreadTasksIncludingDelayedTasks();

		FragmentManager fragmentManager = locationPickerActivity.getSupportFragmentManager();
		ListFragment fragment = (ListFragment) fragmentManager
				.findFragmentByTag("locations_list_fragment");
	
		assertTrue(fragment.isAdded());

		locationPickerActivity = controller.stop().get();
		locationPickerActivity = controller.restart().get();

		Robolectric.runUiThreadTasksIncludingDelayedTasks();
		assertTrue(locationPickerActivity.getSupportFragmentManager()
				.findFragmentByTag("locations_list_fragment").isAdded());
	}

	//TODO: tap Current Location
	// TODO: tap search results element
}
