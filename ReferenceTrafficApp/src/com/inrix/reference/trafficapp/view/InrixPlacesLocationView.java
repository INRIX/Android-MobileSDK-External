/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.animation.LayoutTransition;
import android.animation.LayoutTransition.TransitionListener;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.TrafficApp;
import com.inrix.reference.trafficapp.error.ErrorEntity;
import com.inrix.reference.trafficapp.fragments.PlacesListFragment;
import com.inrix.reference.trafficapp.locationpicker.LocationPickerActivity;
import com.inrix.reference.trafficapp.util.Constants;
import com.inrix.reference.trafficapp.view.RouteCard.RouteViewType;
import com.inrix.sdk.Error;
import com.inrix.sdk.Error.Type;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.LocationsManager.LocationType;
import com.inrix.sdk.RouteManager;
import com.inrix.sdk.RouteManager.IRouteResponseListener;
import com.inrix.sdk.RouteManager.RouteOptions;
import com.inrix.sdk.ServerErrorStatus;
import com.inrix.sdk.geolocation.IGeolocationSource;
import com.inrix.sdk.geolocation.IOnGeolocationChangeListener;
import com.inrix.sdk.geolocation.PlayServicesLocationSource;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Location;
import com.inrix.sdk.model.Route;
import com.inrix.sdk.model.RoutesCollection;

public class InrixPlacesLocationView extends View implements OnClickListener,
		IOnGeolocationChangeListener {
	final static int ROUTE_OPTIONS_NUM_ALTERNATES = 1;

	Location currentLocation = null;
	Context context;
	Activity parentActivity = null;
	View rootView = null;
	Button editButton = null;
	private final int DEFAULT_ROUTE_TOLERANCE = 10;
	ImageView locationIcon = null;
	TextView locationName = null;
	RouteManager routeManager = InrixCore.getRouteManager();
	LinearLayout routesContainer = null;
	ErrorCard errorCard;
	List<ICancellable> requestList = new ArrayList<ICancellable>();
	List<RouteCard> routeCardList = null;

	private IGeolocationSource locationService = null;
	private boolean routeFetchSkipped = false;

	private Timer locationChecker;
	private static long WAIT_FOR_LOCATION_FIX_DELAY = Constants.SECONDS_PER_MIN
			* Constants.MS_PER_SECOND; // one minute
	private PlacesListFragment parentContainer = null;

	public InrixPlacesLocationView(Context context, Location location, PlacesListFragment container) {
		super(context);

		this.context = context;
		this.parentContainer = container;
		try {
			this.parentActivity = (Activity) context;
		} catch (ClassCastException cce) {
			Log.d("PlacesLocationView", "non activity passed as context");
			this.parentActivity = null;
		}

		rootView = ((LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.location_layout, null, false);

		locationName = (TextView) rootView.findViewById(R.id.place_title);
		routesContainer = (LinearLayout) rootView.findViewById(R.id.route_list);
		editButton = (Button) rootView.findViewById(R.id.edit_button);
		locationIcon = (ImageView) rootView.findViewById(R.id.location_icon);
		errorCard = (ErrorCard) rootView.findViewById(R.id.place_error_card);
		
		LayoutTransition layoutTransition = routesContainer
				.getLayoutTransition();
		layoutTransition.addTransitionListener(new TransitionListener() {
			@Override
			public void endTransition(LayoutTransition transition,
					ViewGroup container,
					View view,
					int transitionType) {
				if (null != parentContainer) {
					parentContainer.refreshLayout();
				}
			}

			@Override
			public void startTransition(LayoutTransition transition,
					ViewGroup container,
					View view,
					int transitionType) {
			}
		});

		initRouteCardList();
		initLocation(location, true);
		if (null != editButton) {
			editButton.setOnClickListener(this);
		}
	}

	public View getRootView() {
		return this.rootView;
	}

	/**
	 * Add the returned routes list for this location to the adapter
	 * 
	 * @param location
	 * @param routes
	 */
	public void addRoutes(Location location, List<Route> routes) {
		this.routesContainer.removeAllViews();

		int index = 0;
		for (Route route : routes) {
			RouteCard routeView = getRouteCard(index).setLocation(location)
					.setRouteMode().setRoute(route).setRouteIndex(index)
					.setRouteViewType(RouteViewType.NORMAL_ROUTE_VIEW);
			if (routeView.isYouAreHereCard()) {
				this.routesContainer.removeAllViews();
				this.routesContainer.addView(routeView);
				return;
			}
			this.routesContainer.addView(routeView);
			index++;
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.edit_button) {
			if (this.currentLocation.getLocationType() == LocationType.HOME
					.getValue()) {
				LocationPickerActivity.launchForEditHome(context,
						this.currentLocation.getLocationId());
			} else if (this.currentLocation.getLocationType() == LocationType.WORK
					.getValue()) {
				LocationPickerActivity.launchForEditWork(context,
						this.currentLocation.getLocationId());
			}
		}
	}

	@Override
	public void onGeolocationChange(android.location.Location location) {
		locationService.deactivate();
		if (this.routeFetchSkipped) {
			this.routeFetchSkipped = false;
			getRoutesForLocation(currentLocation, location);
			stopCurrentLocationWatchDog();
		}
	}

	/**
	 * Show error card.
	 * 
	 * @param errorTextResId
	 *            the error text resource id
	 */
	public void showErrorCard(int errorTextResId) {
		this.errorCard.setText(errorTextResId);
		this.errorCard.show();

		this.routesContainer.setVisibility(GONE);
	}

	/**
	 * Show loading card.
	 */
	public void showRouteLoading() {
		this.errorCard.hide();
		this.routesContainer.setVisibility(View.VISIBLE);
		this.routesContainer.removeAllViews();
		this.routesContainer.addView(getRouteCard(0).setLoadingMode());
	}

	
	public boolean selected(Context context) {
		boolean bReturn = false;

		return bReturn;
	}

	public void setLocation(Location location) {
		initLocation(location, false);
	}

	public void cancelPendingRequests() {
		if (null != requestList) {
			for (ICancellable req : requestList) {
				req.cancel();
			}
			requestList.clear();
		}
		stopCurrentLocationWatchDog();
	}

	private void startLocationServices() {
		if (this.locationService != null) {
			this.locationService.deactivate();
		}
		this.locationService = new PlayServicesLocationSource(context);
		locationService.activate(this);
	}

	private String getTitleString() {
		String title = "";
		int locationType = currentLocation.getLocationType();
		if (locationType == LocationType.HOME.getValue()) {
			title = context.getResources().getString(R.string.to_home);
		} else if (locationType == LocationType.WORK.getValue()) {
			title = context.getResources().getString(R.string.to_work);
		}
		return title;
	}

	private void setLocationIcon() {
		if (null != locationIcon && null != currentLocation) {
			int locationType = currentLocation.getLocationType();
			if (locationType == LocationType.HOME.getValue()) {
				locationIcon.setImageDrawable(context.getResources()
						.getDrawable(R.drawable.home));
			} else if (locationType == LocationType.WORK.getValue()) {
				locationIcon.setImageDrawable(context.getResources()
						.getDrawable(R.drawable.work));
			}
		}
	}

	/**
	 * Method to launch the request for routes for a location
	 * 
	 * @param location
	 * @param lastKnownLocation
	 */
	private void getRoutesForLocation(final Location location,
			final android.location.Location lastKnownLocation) {
		if (null != location && null != lastKnownLocation) {
			GeoPoint start = new GeoPoint(lastKnownLocation.getLatitude(),
					lastKnownLocation.getLongitude());
			GeoPoint end = location.getGeoPoint();
			RouteOptions options = new RouteOptions(start, end);
			options.setNumAlternates(ROUTE_OPTIONS_NUM_ALTERNATES);
			options.setTolerance(DEFAULT_ROUTE_TOLERANCE);
			ICancellable routesRequest = routeManager.requestRoutes(options,
					new IRouteResponseListener() {
						@Override
						public void onResult(RoutesCollection routeCollection) {
							addRoutes(location, routeCollection.getRoutes());
						}

						@Override
						public void onError(Error error) {
							resolveRouteError(error);
						}
					});
			requestList.add(routesRequest);
		} else {
			this.routeFetchSkipped = true;
			scheduleLocationWatchDog();
		}
	}

	private void scheduleLocationWatchDog() {
		stopCurrentLocationWatchDog();
		locationChecker = new Timer();
		locationChecker.schedule(new TimerTask() {
			@Override
			public void run() {
				/*
				 * if we are here it means we did not get a location fix within
				 * the specified time interval so show the error card
				 */
				if (null != parentActivity) {
					parentActivity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							showErrorCard(R.string.place_control_route_error);
						}
					});
				}
			}
		}, WAIT_FOR_LOCATION_FIX_DELAY);

		startLocationServices();
	}

	private void stopCurrentLocationWatchDog() {
		if (null != locationChecker) {
			locationChecker.cancel();
			locationChecker = null;
		}
	}

	private void initLocation(Location location, boolean loading) {
		this.currentLocation = location;

		// remove error card
		this.errorCard.hide();
		this.routesContainer.setVisibility(View.VISIBLE);

		// set the location name
		if (null != locationName && null != location) {
			locationName.setText(getTitleString());
		}
		// set the location icon
		setLocationIcon();

		if (currentLocation.getLocationId() != -1) {
			editButton.setVisibility(View.VISIBLE);
			// we have a valid location get the routes
			getRoutesForLocation(currentLocation, null);
		} else {
			editButton.setVisibility(View.INVISIBLE);
			routesContainer.removeAllViews();
			if (loading) {
				// show the loading card
				routesContainer.addView(getRouteCard(0).setLoadingMode());
			} else {
				// show the setup home/work card
				RouteCard routeView = getRouteCard(0).setLocation(location)
						.setRouteViewType(RouteViewType.ADD_HOME_WORK)
						.stopLoadingUI();
				routesContainer.addView(routeView);
			}
		}

	}

	private void initRouteCardList() {
		routeCardList = new ArrayList<RouteCard>();
		routeCardList.add(new RouteCard(getContext()));
	}

	private RouteCard getRouteCard(int index) {
		RouteCard card = null;
		if (index > 0 && index < (routeCardList.size() - 1)) {
			card = routeCardList.get(index);
		} else {
			// we need to create a card
			card = new RouteCard(getContext());
			routeCardList.add(card);
		}
		return card;
	}

	private void resolveRouteError(Error routeError) {
		routeCardList.clear();
		routesContainer.removeAllViews();
		ErrorEntity errEntity = ErrorEntity.fromInrixError(routeError);
		if (errEntity != null && routeError.getErrorType() == Type.NETWORK_ERROR) {
			TrafficApp.getBus().post(errEntity);
		}

		if (routeError.getErrorId() == ServerErrorStatus.UNROUTABLE_WAYPOINT) {
			this.showErrorCard(R.string.place_control_unroutable_route_error);
		} else {
			this.showErrorCard(R.string.place_control_route_error);
		}
	}
}
