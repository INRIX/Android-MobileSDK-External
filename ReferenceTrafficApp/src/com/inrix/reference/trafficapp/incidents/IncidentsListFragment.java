/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.incidents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLngBounds;
import com.inrix.reference.trafficapp.Globals;
import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.TrafficApp;
import com.inrix.reference.trafficapp.error.DismissErrorEntity;
import com.inrix.reference.trafficapp.error.ErrorType;
import com.inrix.reference.trafficapp.incidents.IncidentsProvider.LocalIncidentInfo;
import com.inrix.reference.trafficapp.util.Constants;
import com.inrix.reference.trafficapp.util.RefreshNotifier;
import com.inrix.reference.trafficapp.util.RefreshNotifier.OnRefreshListener;
import com.inrix.sdk.Error;
import com.inrix.sdk.IncidentUtils;
import com.inrix.sdk.IncidentsManager;
import com.inrix.sdk.IncidentsManager.Actions;
import com.inrix.sdk.IncidentsManager.IIncidentsResponseListener;
import com.inrix.sdk.IncidentsManager.IncidentBoxOptions;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Incident;
import com.squareup.otto.Subscribe;

public class IncidentsListFragment extends ListFragment implements
		OnRefreshListener, IIncidentsResponseListener {
	private static final Logger logger = LoggerFactory.getLogger(IncidentsListFragment.class);

	private final IncidentsProvider provider = new IncidentsProvider();
	private IncidentsListAdapter adapter;
	long incidentsRefreshInterval = 0;
	private RefreshNotifier refreshNotifier;

	private static String INCIDENTS_BOX_BOUNDS = "incidents_box_bounds";
	private static String BUFFERED_INCIDENTS = "buffered_incidents";
	private static String INCIDENTS_UPDATE_TIME = "incidents_update_time";
	private static final String CURRENT_LOCATION = "current_location";

	private LatLngBounds incidentsBox = null;
	private Location currentLocation = null;
	private ArrayList<Incident> filterList = new ArrayList<Incident>();

	private long lastIncidentsTimeStamp = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRetainInstance(true);
		incidentsRefreshInterval = InrixCore.getIncidentsManager().getRefreshInterval(Actions.GET_INCIDENTS, TimeUnit.MILLISECONDS);
		if (incidentsRefreshInterval <= 0) {
			incidentsRefreshInterval = (int) Constants.DEFAULT_CONTENT_REFRESH_TIMEOUT_MS;
		}
		this.refreshNotifier = new RefreshNotifier(this);
		this.refreshNotifier.setRefreshPeriod(incidentsRefreshInterval);
		this.refreshNotifier.setLastUpdateTime(0);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		this.setEmptyText("No incidents :)");

		this.setListShown(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.ListFragment#onViewCreated(android.view.View,
	 * android.os.Bundle)
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final Bundle args = this.getArguments();
		if (args != null) {
			this.incidentsBox = (LatLngBounds) args.getParcelable(INCIDENTS_BOX_BOUNDS);
			this.currentLocation = (Location) args.getParcelable(CURRENT_LOCATION);
		}

		this.adapter = new IncidentsListAdapter(this.getActivity());
		this.setListAdapter(adapter);

		this.getListView().setSelector(R.drawable.selectable_item_background);
		this.getListView().setDrawSelectorOnTop(true);
		this.getListView().setDivider(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
		this.getListView().setScrollBarStyle(ListView.SCROLLBARS_OUTSIDE_OVERLAY);
		this.getListView().setDividerHeight(getResources().getDimensionPixelSize(R.dimen.news_cards_list_separator_height));
		this.getListView().setClipChildren(false);

		int padding = getResources().getDimensionPixelSize(R.dimen.news_cards_list_padding);
		this.getListView().setPadding(padding, padding, padding, padding);
		this.getListView().setClipToPadding(false);

		if (savedInstanceState != null && savedInstanceState.containsKey(BUFFERED_INCIDENTS)) {
			this.filterList = savedInstanceState.getParcelableArrayList(BUFFERED_INCIDENTS);
			this.lastIncidentsTimeStamp = savedInstanceState.getLong(INCIDENTS_UPDATE_TIME);
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		TrafficApp.getBus().unregister(this);
		refreshNotifier.pauseAutoRefresh();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList(BUFFERED_INCIDENTS, this.filterList);
		outState.putLong(INCIDENTS_UPDATE_TIME, this.lastIncidentsTimeStamp);
	}

	@Override
	public void onResume() {
		super.onResume();

		TrafficApp.getBus().register(this);

		if (null != this.filterList && this.filterList.size() > 0) {
			refreshNotifier.setLastUpdateTime(lastIncidentsTimeStamp);
			setListShown(true);
			adapter.setData(filterList);
		}

		refreshNotifier.resumeAutoRefresh();
	}
	
	@Override
	public void onDestroy() {
		this.cancelCurrentRequest();
		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public final void onListItemClick(final ListView list, final View view, final int position, final long id) {
		final Incident incident = this.adapter.getItem(position);
		TrafficApp.getBus().post(new IncidentSelectedEvent(position, incident));
	}

	private void cancelCurrentRequest() {
		this.provider.release();
	}

	private boolean requestIncidents() {
		cancelCurrentRequest();

		if (incidentsBox != null) {
			GeoPoint corner1 = new GeoPoint(incidentsBox.northeast.latitude,
					incidentsBox.northeast.longitude);
			GeoPoint corner2 = new GeoPoint(incidentsBox.southwest.latitude,
					incidentsBox.southwest.longitude);
			IncidentBoxOptions options = new IncidentBoxOptions(corner1,
					corner2);
			this.provider.getIncidentsAsync(options, this);
			return true;
		}

		return false;
	}

	@Override
	public boolean onRefreshData(RefreshNotifier notifier) {
		if (Globals.isNetworkAvailable()) {
			setListShown(false);
			return this.requestIncidents();
		}
		setListShown(true);
		return false;
	}
	
	/**
	 * On dismiss error, handles cases when error needs to be removed, without direct access to controller
	 *
	 * @param dismissEntity the dismiss entity
	 */
	@Subscribe
	public void onDismissError(DismissErrorEntity dismissEntity) {
		if ((dismissEntity != null) && (dismissEntity.getType() == ErrorType.NETWORK_OFF)) {
			this.refreshNotifier.resumeAutoRefresh();
		}
	}

	@Override
	public void onError(Error error) {
		if (this.getActivity() == null) {
			return;
		}
		setListShown(true);
	}

	@Override
	public void onResult(List<Incident> incidentList) {
		processResult(incidentList);
	}

	/**
	 * testable function
	 */

	private void processResult(List<Incident> incidentList) {
		setDistance(incidentList);
		Collections.sort(incidentList, IncidentUtils.getDefaultListComparator());
		setIncidentList(incidentList);
	}

	/**
	 * Sets the distance and sort.
	 * 
	 * @param incidents
	 *            the new distance and sort
	 */
	private void setDistance(List<Incident> incidents) {
		if (currentLocation == null || incidents == null || incidents.isEmpty()) {
			return;
		}

		// set distance for a correct sorting
		// TODO: add this as method in SDK.IncdentUtils
		Iterator<Incident> iterator = incidents.iterator();
		while (iterator.hasNext()) {
			Incident currentItem = iterator.next();
			// unlikely, but possible
			if (currentItem == null) {
				continue;
			}

			// remove items with Delay less than minimum
			// TODO: create filter and comparator in incident options, same as Alert manager
			if (currentItem.getType() == IncidentsManager.INCIDENT_TYPE_FLOW
					&& currentItem.getDelayImpact() != null) {
				if (currentItem.getDelayImpact().getTypicalMinutes() < Constants.CONGESTION_MINIMAL_DELAY_IMPACT) {
					iterator.remove();
				}
			}

			float[] distanceResults = new float[3];
			// get Actual distance
			Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
					currentItem.getLatitude(), currentItem.getLongitude(), distanceResults);
			currentItem.setDistanceKM(distanceResults[0] / 1000);
		}
	}

	private void setIncidentList(final List<Incident> incidentList) {
		if (this.getActivity() == null) {
			return;
		}

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (null == filterList) {
					filterList = new ArrayList<Incident>();
				} else {
					filterList.clear();
				}
				if (null != adapter) {
					setListShown(true);
					for (final Incident entry : incidentList) {
						if (isIncidentAllowed(entry)) {
							filterList.add(entry);
						}
					}

					adapter.setData(filterList);
					lastIncidentsTimeStamp = System.currentTimeMillis();
				}
			}
		});
	}

	private boolean isIncidentAllowed(Incident inc) {
		if (null == inc
				|| (inc.getLatitude() == 0.0 && inc.getLongitude() == 0.0)) {
			return false;
		}
		return true;
	}

	/**
	 * Subscribes to the incident state change event from event bus.
	 * 
	 * @param event
	 *            Event instance.
	 */
	@Subscribe
	public void onIncidentStateChanged(final IncidentStateChangedEvent event) {
		Incident found = null;

		logger.trace("Incident state change received: {} for {}", event.getState(), event.getIncident());

		for (final Incident current : this.filterList) {
			if (current.getId() == event.getIncident().getId()) {
				found = current;
				break;
			}
		}

		if (found == null) {
			return;
		}

		switch (event.getState()) {
			case Deleted:
			case DeletePending:
				logger.trace("Removing delete incident from local incidents.");
				this.filterList.remove(found);
				break;
			case Cleared:
			case Confirmed:
				if (found instanceof LocalIncidentInfo) {
					final LocalIncidentInfo local = (LocalIncidentInfo) found;
				logger.trace("Changing state from: {} to {}", local.getState(), event.getState());
					local.setState(event.getState());
				}
				break;
			case Default:
			case Pending:
			case Reported:
			default:
				// Do nothing otherwise.
				break;
		}

		this.adapter.setData(this.filterList);
	}

	public static IncidentsListFragment initInstance(LatLngBounds incidentBounds,
			Location currentLocation) {
		IncidentsListFragment incFrag = new IncidentsListFragment();
		if (null != incidentBounds) {
			Bundle args = new Bundle();
			args.putParcelable(INCIDENTS_BOX_BOUNDS, incidentBounds);
			args.putParcelable(CURRENT_LOCATION, currentLocation);

			incFrag.setArguments(args);
		}
		return incFrag;
	}
}
