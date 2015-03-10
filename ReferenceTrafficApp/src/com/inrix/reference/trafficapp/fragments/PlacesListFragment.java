/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.inrix.sdk.Error;
import com.inrix.sdk.Error.Type;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.LocationsManager;
import com.inrix.sdk.LocationsManager.Actions;
import com.inrix.sdk.LocationsManager.ILocationsGetResponseListener;
import com.inrix.sdk.LocationsManager.LocationType;
import com.inrix.sdk.model.Location;
import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.TrafficApp;
import com.inrix.reference.trafficapp.error.ErrorEntity;
import com.inrix.reference.trafficapp.util.Constants;
import com.inrix.reference.trafficapp.view.InrixPlacesLocationView;

public class PlacesListFragment extends InrixSimpleRefreshFragment {
	LinearLayout placesContainer = null;
	LocationsManager locationsManager = InrixCore.getLocationsManager();
	List<ICancellable> requestList = new ArrayList<ICancellable>();
	List<InrixPlacesLocationView> locationViewList = new ArrayList<InrixPlacesLocationView>();
	long locationsRefreshInterval = 0;
	/* this flag controls if we query for locations every refresh cycle.
	 */
	boolean haveLocations = true;
	InrixPlacesLocationView homeView = null;
	InrixPlacesLocationView workView = null;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		locationsRefreshInterval = locationsManager
				.getRefreshInterval(Actions.GET_SAVED_LOCATIONS, TimeUnit.MILLISECONDS);
		if (locationsRefreshInterval <= 0) {
			locationsRefreshInterval = (int) Constants.DEFAULT_CONTENT_REFRESH_TIMEOUT_MS;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.places_fragment,
				container,
				false);
		
		placesContainer = (LinearLayout) rootView;

		homeView = new InrixPlacesLocationView(getActivity(), getInitialHome(), this);
		workView = new InrixPlacesLocationView(getActivity(), getInitialWork(), this);

		//add home and work to layout
		placesContainer.addView(homeView.getRootView());
		placesContainer.addView(workView.getRootView());
		//add home and work to our holding list
		locationViewList.add(homeView);
		locationViewList.add(workView);
		return rootView;
	}

	/**
	 * Method to launch a locations request
	 */
	public void getLocations() {
		if( !haveLocations ){
			/* if we don't have any stored locations
			 * don't query for locations*/
			return;
		}
		homeView.showRouteLoading();
		workView.showRouteLoading();

		ICancellable locationRequest = locationsManager
				.requestSavedLocations(new ILocationsGetResponseListener() {

					@Override
					public void onResult(List<Location> data) {
						addLocations(data);
					}

					@Override
					public void onError(final Error error) {
						homeView.post(new Runnable() {
							@Override
							public void run() {
								ErrorEntity errEntity = ErrorEntity.fromInrixError(error);

								if(error.getErrorType() == Type.NETWORK_ERROR &&
										errEntity != null) {
									TrafficApp.getBus().post(errEntity);
								}

								homeView.showErrorCard(R.string.place_control_error);
								workView.showErrorCard(R.string.place_control_error);
							}
						});
					}
				});
		requestList.add(locationRequest);
	}

	/**
	 * Add the locations returned from the server to the adapter if either home
	 * or work or not present we will add dummy locations for those
	 * 
	 * @param locations
	 */
	private void addLocations(List<Location> locations) {
		Location home = null;
		Location work = null;
		boolean homeSet = true;
		boolean workSet = true;
		if (null != locations && !locations.isEmpty()) {
			for (Location location : locations) {
				if (location.getLocationType() == LocationType.HOME.getValue()) {
					home = location;
				} else if (location.getLocationType() == LocationType.WORK
						.getValue()) {
					work = location;
				}
			}
		}
		if (null == home) {
			homeSet = false;
			home = getInitialHome();
		}
		if (null == work) {
			workSet = false;
			work = getInitialWork();
		}
		haveLocations = (homeSet || workSet);
		homeView.setLocation(home);
		workView.setLocation(work);
	}

	private Location getInitialHome()
    {
    	return new Location(-1,
				"",
				LocationType.HOME.getValue(),
				"",
				"",
				Double.NaN,
				Double.NaN,
				0);
    }
    
    private Location getInitialWork()
    {
    	return new Location(-1,
				"",
				LocationType.WORK.getValue(),
				"",
				"",
				Double.NaN,
				Double.NaN,
				0);
    }
    public void onPause(){
    	resetUpdatedTime();
    	super.onPause();
    }
	/**
	 * When we are destroyed cancel all the outstanding requests
	 */
	@Override
	public void onDestroy() {
		if (null != requestList) {
			for (ICancellable req : requestList) {
				req.cancel();
			}
			requestList.clear();
		}

		if (null != locationViewList) {
			for (InrixPlacesLocationView locView : locationViewList) {
				locView.cancelPendingRequests();
			}
			locationViewList.clear();
		}

		super.onDestroy();
	}

	/**
	 * Method to add a location to the adapter
	 * 
	 * @param location
	 */
	public void addLocation(Location location) {
		InrixPlacesLocationView placeView = new InrixPlacesLocationView(getActivity(),
				location, this);
		placesContainer.addView(placeView.getRootView());

	}
	/**
	 * Refresh the places list root container to 
	 * accommodate view changes
	 */
	public void refreshLayout(){
		placesContainer.invalidate();
		placesContainer.requestLayout();
	}

	@Override
	public void getData() {
		getLocations();
		super.getData();
	}

	@Override
	public long getRefreshInterval() {
		return locationsRefreshInterval;
	}
}
