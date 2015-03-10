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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowLog;

import utils.FragmentUtils;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.inrix.reference.trafficapp.fragments.PlacesListFragment;
import com.inrix.reference.trafficapp.locationpicker.LocationPickerActivity;
import com.inrix.reference.trafficapp.locationpicker.LocationPickerActivity.PLACES_ACTION;
import com.inrix.reference.trafficapp.test.data.PlacesFragmentTestData;
import com.inrix.reference.trafficapp.view.InrixPlacesLocationView;
import com.inrix.reference.trafficapp.view.RouteCard;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.LocationsManager.LocationType;
import com.inrix.sdk.model.Location;
import com.inrix.sdk.model.Route;

@RunWith(RobolectricTestRunner.class)
public class PlacesFragmentTest {
	PlacesListFragment placesFragment = null;
	FragmentActivity fragActivity = null;
	LinearLayout placesList = null;
	LinearLayout home = null;
	LinearLayout work = null;
	InrixPlacesLocationView homeView = null;
	InrixPlacesLocationView workView = null;
	private ShadowActivity shadowActivity; 
	Location homeLoc = null;
	Location workLoc = null;

	@Before
	public void setup() {
		ShadowLog.stream = System.out;
		InrixCore.initialize(Robolectric.application);
		placesFragment = new PlacesListFragment();
		disableLocationFetch(placesFragment);
		FragmentUtils.startv4Fragment(placesFragment);

		View fragmentParent = placesFragment.getView();
		assertNotNull(fragmentParent);

		placesList = (LinearLayout) fragmentParent
				.findViewById(R.id.route_list);
		shadowActivity = Robolectric.shadowOf(FragmentUtils.getFragmentActivity());
		int countOfElements = placesList.getChildCount();
		int keepCounting = 6;
		while (countOfElements <= 0 && keepCounting > 0) {
			try {
				Thread.sleep(500);
				keepCounting--;
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			countOfElements = placesList.getChildCount();
		}
		homeLoc = new Location(12365412,
				"Home",
				LocationType.HOME.getValue(),
				"",
				"",
				47.526444,
				-121.823616,
				0);
		workLoc = new Location(12365412,
				"Work",
				LocationType.WORK.getValue(),
				"",
				"",
				47.614496,
				-122.328758,
				0);
		assertEquals(2, countOfElements);
		home = (LinearLayout) placesList.getChildAt(0);
		work = (LinearLayout) placesList.getChildAt(1);
		homeView = getLocationView(placesFragment, "homeView");
		workView = getLocationView(placesFragment, "workView");
	}

	@After
	public void teardown(){
		placesFragment = null;
		fragActivity = null;
		placesList = null;
		home = null;
		work = null;
		homeView = null;
		workView = null;
		homeLoc = null;
		workLoc = null;
	}
	
	@Test
	public void testLoading() {
		assertNotNull(placesFragment);
		assertNotNull(home);
		assertNotNull(work);

		verifyLocationView(home, "To Home");
		verifyProgressBarVisibility(home, View.VISIBLE);

		verifyLocationView(work, "To Work");
		verifyProgressBarVisibility(work, View.VISIBLE);
	}

	@Test
	public void testNoLocations() {
		assertNotNull(placesFragment);
		assertNotNull(home);
		assertNotNull(work);

		addLocationsToFragment(placesFragment, new ArrayList<Location>());

		// progress is stopped
		verifyProgressBarVisibility(home, View.GONE);
		verifyProgressBarVisibility(work, View.GONE);
		// show UI to setup home and work
		verifyRouteTitleText(home, "Setup Home");
		verifyRouteTitleText(work, "Setup Work");
	}

	@Test
	public void testLocationLoadSucceeded() {
		assertNotNull(placesFragment);
		assertNotNull(home);
		assertNotNull(work);

		addLocationsToFragment(placesFragment, getDummyLocations());

		// we are still showing the route loading
		verifyProgressBarVisibility(home, View.VISIBLE);
		verifyProgressBarVisibility(work, View.VISIBLE);
	}

	@Test
	public void testRoutesToHome() {
		assertNotNull(placesFragment);
		assertNotNull(home);
		assertNotNull(homeView);

		addLocationsToFragment(placesFragment, getDummyLocations());

		// we are still showing the route loading
		verifyProgressBarVisibility(home, View.VISIBLE);
		// get routes
		List<Route> routeList = PlacesFragmentTestData
				.getTwoRoutesWithTwoIncidents();
		Route route0 = routeList.get(0);
		Route route1 = routeList.get(1);
		// add routes to home
		homeView.addRoutes(homeLoc, routeList);
		// verify we are not spinning
		verifyProgressBarVisibility(home, View.GONE);

		// verify that we are showing two routes
		verifyRouteCount(home, 2);
		
		// verify title is not empty
		String title = getRouteTitle(home, 0);
		assertTrue(!TextUtils.isEmpty(title));
		
		// verify that we are showing the description correctly
		String description = getRouteDescription(home, 0);
		assertEquals(route0.getSummary().getText(), description);
		
		// verify title is not empty
		title = getRouteTitle(home, 1);
		assertTrue(!TextUtils.isEmpty(title));
		
		// verify that we are showing the description correctly
		description = getRouteDescription(home, 1);
		assertEquals(route1.getSummary().getText(), description);
	}
	
	@Test
	public void testRoutesToWork() {
		assertNotNull(placesFragment);
		assertNotNull(work);
		assertNotNull(workView);

		addLocationsToFragment(placesFragment, getDummyLocations());

		// we are still showing the route loading
		verifyProgressBarVisibility(work, View.VISIBLE);
		// get routes
		List<Route> routeList = PlacesFragmentTestData
				.getTwoRoutesWithTwoIncidents();
		Route route0 = routeList.get(0);
		Route route1 = routeList.get(1);
		// add routes to home
		workView.addRoutes(workLoc, routeList);
		// verify we are not spinning
		verifyProgressBarVisibility(work, View.GONE);

		// verify that we are showing two routes
		verifyRouteCount(work, 2);
		
		// verify title is not empty
		String title = getRouteTitle(work, 0);
		assertTrue(!TextUtils.isEmpty(title));
		
		// verify that we are showing the description correctly
		String description = getRouteDescription(work, 0);
		assertEquals(route0.getSummary().getText(), description);
		
		// verify title is not empty
		title = getRouteTitle(work, 1);
		assertTrue(!TextUtils.isEmpty(title));
		
		// verify that we are showing the description correctly
		description = getRouteDescription(work, 1);
		assertEquals(route1.getSummary().getText(), description);
	}
	
	@Test
	public void testTapSetupHome() {
            //pre-test verifications
        assertNotNull(placesFragment);
        assertNotNull(home);
        assertNotNull(work);
        
        //populate places Fragment with empty locations list
	                addLocationsToFragment(placesFragment, new ArrayList<Location>());
	                verifyProgressBarVisibility(home, View.GONE);
                    verifyProgressBarVisibility(work, View.GONE);
 
	                LinearLayout routesContainer = (LinearLayout) home
	                                .findViewById(R.id.route_list);
	                RouteCard card = (RouteCard) routesContainer.getChildAt(0);
	                //click home element
        card.performClick();
            
        //check Intent and activity
        Intent intent = shadowActivity.peekNextStartedActivityForResult().intent;
        assertEquals(PLACES_ACTION.CREATE_HOME.ordinal(), intent.getExtras().get("location_picker_mode"));
        assertEquals(new ComponentName(FragmentUtils.getFragmentActivity(), LocationPickerActivity.class), intent.getComponent());
        
        }
	 		        
        @Test
        public void testPlaceAddedViaSearch() {
                
            //populate places Fragment with empty locations list
            addLocationsToFragment(placesFragment, new ArrayList<Location>());
            verifyProgressBarVisibility(home, View.GONE);
            verifyProgressBarVisibility(work, View.GONE);
            
            LinearLayout routesContainer = (LinearLayout) home
                            .findViewById(R.id.route_list);
            RouteCard card = (RouteCard) routesContainer.getChildAt(0);
            //click home element
            card.performClick();
            
            //check Intent and activity
            Intent intent = shadowActivity.peekNextStartedActivityForResult().intent;
            assertEquals(PLACES_ACTION.CREATE_HOME.ordinal(), intent.getExtras().get("location_picker_mode"));
            assertEquals(new ComponentName(FragmentUtils.getFragmentActivity(), LocationPickerActivity.class), intent.getComponent());
 
        }

	/** helper methods **/

	/**
	 * helper method to get a list of dummy locations
	 * 
	 * @return
	 */
	private List<Location> getDummyLocations() {
		List<Location> returnList = new ArrayList<Location>();
		returnList.add(homeLoc);
		returnList.add(workLoc);
		return returnList;
	}

	private void verifyLocationView(LinearLayout locationView, String title) {
		assertNotNull(locationView);
		TextView titleView = (TextView) locationView
				.findViewById(R.id.place_title);
		assertNotNull(titleView);
		assertEquals(title, titleView.getText().toString());
	}

	private void verifyProgressBarVisibility(LinearLayout locationView,
			int visibility) {
		assertNotNull(locationView);
		LinearLayout routesContainer = (LinearLayout) locationView
				.findViewById(R.id.route_list);
		assertNotNull(routesContainer);
		RouteCard card = (RouteCard) routesContainer.getChildAt(0);
		assertNotNull(card);
		ProgressBar progressBar = (ProgressBar) card
				.findViewById(R.id.loading_control);
		assertEquals(visibility, progressBar.getVisibility());
	}

	private void verifyRouteCount(LinearLayout locationView, int noOfRoutes) {
		assertNotNull(locationView);
		LinearLayout routesContainer = (LinearLayout) locationView
				.findViewById(R.id.route_list);
		assertNotNull(routesContainer);
		int countOfRouteCards = routesContainer.getChildCount();
		assertEquals(noOfRoutes, countOfRouteCards);
	}

	private String getRouteDescription(LinearLayout locationView, int routeIndex) {
		assertNotNull(locationView);
		LinearLayout routesContainer = (LinearLayout) locationView
				.findViewById(R.id.route_list);
		assertNotNull(routesContainer);
		RouteCard card = (RouteCard) routesContainer.getChildAt(routeIndex);
		assertNotNull(card);
		TextView description = (TextView) card.findViewById(R.id.route_desc);
		assertNotNull(description);
		assertEquals(View.VISIBLE, description.getVisibility());
		return description.getText().toString();
	}

	private String getRouteTitle(LinearLayout locationView, int routeIndex) {
		assertNotNull(locationView);
		LinearLayout routesContainer = (LinearLayout) locationView
				.findViewById(R.id.route_list);
		assertNotNull(routesContainer);
		RouteCard card = (RouteCard) routesContainer.getChildAt(routeIndex);
		assertNotNull(card);
		TextView title = (TextView) card.findViewById(R.id.eta_traveltime);
		assertNotNull(title);
		assertEquals(View.VISIBLE, title.getVisibility());
		return title.getText().toString();
	}

	private void verifyRouteTitleText(LinearLayout locationView, String text) {
		assertNotNull(locationView);
		LinearLayout routesContainer = (LinearLayout) locationView
				.findViewById(R.id.route_list);
		assertNotNull(routesContainer);
		RouteCard card = (RouteCard) routesContainer.getChildAt(0);
		assertNotNull(card);
		TextView title = (TextView) card.findViewById(R.id.eta_traveltime);
		assertEquals(text, title.getText().toString());
	}



	private void disableLocationFetch(PlacesListFragment plFrag) {
		if (null != plFrag) {
			try {
				Field haveLocations = PlacesListFragment.class
						.getDeclaredField("haveLocations");
				haveLocations.setAccessible(true);
				haveLocations.setBoolean(plFrag, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void addLocationsToFragment(PlacesListFragment plFrag,
			List<Location> locationList) {
		if (null != plFrag && null != locationList) {
			try {
				Method addLocations = PlacesListFragment.class
						.getDeclaredMethod("addLocations", List.class);
				addLocations.setAccessible(true);
				addLocations.invoke(plFrag, locationList);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private InrixPlacesLocationView getLocationView(PlacesListFragment plFrag,
			String viewName) {
		if (null != plFrag && null != viewName) {
			try {
				Field homeView = PlacesListFragment.class
						.getDeclaredField(viewName);
				homeView.setAccessible(true);
				return (InrixPlacesLocationView) homeView.get(plFrag);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
