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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v4.util.LruCache;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener;
import com.google.maps.android.clustering.view.ClusterRenderer;
import com.inrix.reference.trafficapp.TrafficApp;
import com.inrix.reference.trafficapp.error.ErrorEntity;
import com.inrix.reference.trafficapp.incidents.IncidentStateChangedEvent;
import com.inrix.reference.trafficapp.incidents.IncidentsProvider;
import com.inrix.reference.trafficapp.incidents.IncidentsProvider.IncidentState;
import com.inrix.reference.trafficapp.incidents.IncidentsProvider.LocalIncidentInfo;
import com.inrix.reference.trafficapp.util.Constants;
import com.inrix.reference.trafficapp.util.Interfaces.IIncidentRendered;
import com.inrix.reference.trafficapp.util.RefreshNotifier;
import com.inrix.reference.trafficapp.util.RefreshNotifier.OnRefreshListener;
import com.inrix.reference.trafficapp.util.map.ClusteredIncident;
import com.inrix.reference.trafficapp.util.map.DefaultIncidentsClusteringAlgorithm;
import com.inrix.reference.trafficapp.util.map.DefaultIncidentsRenderer;
import com.inrix.sdk.Error;
import com.inrix.sdk.IncidentsManager.Actions;
import com.inrix.sdk.IncidentsManager.IIncidentsResponseListener;
import com.inrix.sdk.IncidentsManager.IncidentBoxOptions;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Incident;
import com.squareup.otto.Subscribe;

/**
 * Shows traffic and incidents
 * 
 * @author paveld
 * 
 */
public class IncidentsMapFragment extends BaseMapFragment implements
		IIncidentsResponseListener,
		OnClusterItemClickListener<ClusteredIncident>,
		OnClusterClickListener<ClusteredIncident>, OnRefreshListener,
		IIncidentRendered {

	private final static double ADDITIONAL_INCIDENT_AREA = 0.5;
	/* size of the cache for incidents collection */
	private final static int MAX_CACHE_SIZE = 100;

	private static final Logger logger = LoggerFactory.getLogger(IncidentsMapFragment.class);

	private final IncidentsProvider provider = new IncidentsProvider();
	protected ClusterManager<ClusteredIncident> markerManager;
	private DefaultIncidentsClusteringAlgorithm<ClusteredIncident> clusteringAlgorithm;
	private ClusterRenderer<ClusteredIncident> renderer;
	private List<Incident> currentIncidentList;
	private LruCache<Long, ClusteredIncident> cache = new LruCache<Long, ClusteredIncident>(MAX_CACHE_SIZE);

	private boolean incidentsEnabled = true;
	private boolean incidentsShown = false;
	private LatLngBounds currentIncidentsBox = null;
	private IOnIncidentClicked onIncidentClickedCallback = null;

	private RefreshNotifier refreshNotifier;

	/**
	 * Delay between camera change event and requesting incidents from the
	 * server to prevent multiple requests when few camera change events occur
	 * in a short time period.
	 */
	private static final long REFRESH_INCIDENT_DELAY = 500;

	/** Handler to update incidents. */
	private Handler refreshIncidentHandler = new Handler();

	public interface IOnIncidentClicked {
		boolean onIncidentClicked(List<Incident> incidents, LatLng position);
	}

	@Override
	public void onPause() {
		super.onPause();
		refreshNotifier.pauseAutoRefresh();
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshNotifier.resumeAutoRefresh();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		cache.evictAll();
	}

	@Override
	protected void setUpMap() {
		super.setUpMap();
		if (markerManager == null) {
			this.markerManager = new ClusterManager<ClusteredIncident>(getActivity(),
					getMap());
			this.markerManager.setRenderer(getRenderer());
			this.markerManager.setOnClusterItemClickListener(this);
			this.markerManager.setOnClusterClickListener(this);

			clusteringAlgorithm = new DefaultIncidentsClusteringAlgorithm<ClusteredIncident>();

			this.markerManager.setAlgorithm(clusteringAlgorithm);

			this.refreshNotifier = new RefreshNotifier(this);
			this.refreshNotifier.setRefreshPeriod(InrixCore.getIncidentsManager()
					.getRefreshInterval(Actions.GET_INCIDENTS, TimeUnit.MILLISECONDS));
			refreshIncidents();
		}
		getMap().setOnMarkerClickListener(this.markerManager);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof IOnIncidentClicked) {
			this.onIncidentClickedCallback = (IOnIncidentClicked) activity;
		}
		
		TrafficApp.getBus().register(this);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		this.onIncidentClickedCallback = null;
		
		TrafficApp.getBus().unregister(this);
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		super.onCameraChange(position);
		cameraChanged((int)getCurrentZoomLevel());
		this.markerManager.onCameraChange(position);
	}

	protected ClusterRenderer<ClusteredIncident> getRenderer() {
		if (this.renderer == null) {
			this.renderer = new DefaultIncidentsRenderer(getActivity(),
					getMap(),
					markerManager);
		}
		return this.renderer;
	}

	private synchronized boolean requestIncidents() {
		if (getMap() == null) {
			logger.debug("Cannot refresh incidents - map is null");
			return false;
		}

		if (!incidentsEnabled((int)getCurrentZoomLevel())) {
			logger.debug("Skip refreshing incidents - zoom level is low: {}", getMap().getCameraPosition().zoom);
			return false;
		}
		incidentsShown = true;

		logger.debug("Refresh incidents");

		LatLngBounds bounds = getIncidentBounds();

		GeoPoint corner1 = new GeoPoint(bounds.northeast.latitude,
				bounds.northeast.longitude);
		GeoPoint corner2 = new GeoPoint(bounds.southwest.latitude,
				bounds.southwest.longitude);

		// store the current request box
		currentIncidentsBox = bounds;

		final IncidentBoxOptions options = new IncidentBoxOptions(corner1, corner2);
		provider.getIncidentsAsync(options, this);
		return true;
	}

	/**
	 * Retrieves bounds for incident request. The bounds will be larger than
	 * visible area to prevent often incidents requests.
	 * 
	 * @return Bounds for incident request.
	 */
	private LatLngBounds getIncidentBounds() {
		float zoom = getMap().getCameraPosition().zoom;
		if (zoom < getMap().getMaxZoomLevel()) {
			zoom++;
		}

		LatLngBounds visibleBounds = getMap().getProjection()
				.getVisibleRegion().latLngBounds;
		double latSpan = Math.abs(visibleBounds.northeast.latitude
				- visibleBounds.southwest.latitude);
		double lngSpan = Math.abs(visibleBounds.northeast.longitude
				- visibleBounds.southwest.longitude);
		latSpan *= ADDITIONAL_INCIDENT_AREA;
		lngSpan *= ADDITIONAL_INCIDENT_AREA;
		double neLat = visibleBounds.northeast.latitude + latSpan;
		if (neLat > Constants.MAX_LATITUDE) {
			neLat = Constants.MAX_LATITUDE;
		}
		double neLng = visibleBounds.northeast.longitude + lngSpan;
		if (neLng > Constants.MAX_LONGITUDE) {
			neLng = Constants.MAX_LONGITUDE;
		}

		double swLat = visibleBounds.southwest.latitude - latSpan;
		if (swLat < Constants.MIN_LATITUDE) {
			swLat = Constants.MIN_LATITUDE;
		}
		double swLng = visibleBounds.southwest.longitude - lngSpan;
		if (neLng < Constants.MIN_LONGITUDE) {
			neLng = Constants.MIN_LONGITUDE;
		}

		return new LatLngBounds(new LatLng(swLat, swLng), new LatLng(neLat,
				neLng));
	}

	/**
	 * Enable/disable items clustering
	 * 
	 * @param enable
	 */
	public void enableClustering(boolean enable) {
		this.clusteringAlgorithm.enableClustering(enable);
		this.markerManager.cluster();
	}

	protected boolean isRefreshNeeded() {
		return checkCurrentMapBox();
	}

	/**
	 * Returns true if we went out of the last requested incidents box
	 * 
	 * @return
	 */
	private boolean checkCurrentMapBox() {
		if (getMap() == null) {
			return false;
		}
		boolean refreshNeeded = true;
		LatLngBounds newBounds = getMap().getProjection().getVisibleRegion().latLngBounds;
		if (null != currentIncidentsBox && null != newBounds) {
			refreshNeeded = !(currentIncidentsBox
					.contains(newBounds.northeast) && currentIncidentsBox
					.contains(newBounds.southwest));
		}
		return refreshNeeded;
	}

	/**
	 * Replace existing collection of incidents with ones specified
	 * 
	 * @param incidents
	 */
	protected void replaceIncidentsWith(final List<Incident> incidents) {
		markerManager.clearItems();
		if (!incidentsEnabled((int) getCurrentZoomLevel()) || !incidentsEnabled) {
			return;
		}

		if (incidents == null || incidents.isEmpty()) {
			return;
		}

		for (Incident incident : incidents) {
			ClusteredIncident cluster = cache.get(incident.getId());
			if (cluster == null) {
				cluster = new ClusteredIncident(incident);
				cache.put(incident.getId(), cluster);
			}
			markerManager.addItem(cluster);
		}
		markerManager.cluster();
	}

	@Override
	public void onResult(List<Incident> data) {
		currentIncidentList = data;
		replaceIncidentsWith(data);
	}

	@Override
	public void onError(Error error) {
		ErrorEntity errEntity = ErrorEntity.fromInrixError(error);
		if (errEntity != null) {
			TrafficApp.getBus().post(errEntity);
		}
	}

	/**
	 * Handles not-coalesced items clicks. Override if you are interested in
	 * markers clicks
	 */
	@Override
	public boolean onClusterItemClick(ClusteredIncident item) {
		boolean isConsumed = false;
		if (this.onIncidentClickedCallback != null) {
			ArrayList<Incident> collection = new ArrayList<Incident>();
			collection.add(item.getIncident());
			isConsumed = onIncidentClickedCallback
					.onIncidentClicked(collection, item.getPosition());
		}

		if (!isConsumed) {
			animateToLocation(item.getPosition(), map.getCameraPosition().zoom);
		}
		return true;
	}

	/**
	 * Handles coalesced items clicks. Override if you are interested in markers
	 * clicks
	 */
	@Override
	public boolean onClusterClick(Cluster<ClusteredIncident> cluster) {
		boolean isConsumed = false;
		if (this.onIncidentClickedCallback != null) {
			ArrayList<Incident> collection = new ArrayList<Incident>();
			for (ClusteredIncident incident : cluster.getItems()) {
				collection.add(incident.getIncident());
			}
			isConsumed = onIncidentClickedCallback
					.onIncidentClicked(collection, cluster.getPosition());
		}

		if (!isConsumed) {
			animateToLocation(cluster.getPosition(),
					map.getCameraPosition().zoom);
		}
		return true;
	}

	private void cameraChanged(int currentZoomLevel) {
		if (incidentsShown && !incidentsEnabled(currentZoomLevel)) {
			/* show to hide */
			this.markerManager.clearItems();
			incidentsShown = false;
		} else if (!incidentsShown && incidentsEnabled(currentZoomLevel)) {
			/* hide to show */
			replaceIncidentsWith(currentIncidentList);
			incidentsShown = true;
		}
		/* if needed post a refresh */
		if (incidentsShown && isRefreshNeeded()) {
			refreshIncidents();
		}
	}

	private boolean incidentsEnabled(int zoomLevel) {
		if (zoomLevel >= Constants.INCIDENTS_MIN_ALLOWED_ZOOM_LEVEL) {
			return true;
		}
		return false;
	}

	@Override
	public void setMapPadding(Rect padding, LatLng mapCenter) {
		super.setMapPadding(padding, mapCenter);
		if (isRefreshNeeded()) {
			refreshIncidents();
		}
	}
	
	@Override
	public void setMapPadding(Rect padding, LatLng mapCenter, float zoom) {
		super.setMapPadding(padding, mapCenter, zoom);
		if (isRefreshNeeded()) {
			refreshIncidents();
		}
	}

	/**
	 * Refresh incidents
	 */
	public void refreshIncidents() {
		this.refreshIncidentHandler
				.removeCallbacks(this.refreshIncidentRunnable);
		this.refreshIncidentHandler.postDelayed(this.refreshIncidentRunnable,
				REFRESH_INCIDENT_DELAY);
	}

	/** Updates incidents. */
	private Runnable refreshIncidentRunnable = new Runnable() {
		public void run() {
			refreshNotifier.setLastUpdateTime(0);
			refreshNotifier.resumeAutoRefresh();
		}
	};

	/**
	 * Callback triggered by auto refresh timer. <b>Don't call this method
	 * manually</b>. Use {@link #refreshIncidents()} instead
	 */
	@Override
	public boolean onRefreshData(RefreshNotifier notifier) {
		return requestIncidents();
	}

	/**
	 * Get the current request bounds for incidents in box
	 * 
	 * @return - LatLngBounds
	 */
	public LatLngBounds getCurrentIncidentRequestBounds() {
		return currentIncidentsBox;
	}

	/**
	 * Subscribes to the incident state change event from event bus.
	 * 
	 * @param event
	 *            Event instance.
	 */
	@Subscribe
	public void onIncidentStateChanged(final IncidentStateChangedEvent event) {
		if (this.currentIncidentList == null) {
			return;
		}
		
		Incident found = null;
		
		logger.trace("Incident state change received: {} for {}", event.getState(), event.getIncident());

			for (final Incident current : this.currentIncidentList) {
				if (current.getId() == event.getIncident().getId()) {
					found = current;
					break;
				}
			}

		if (found == null) {
			logger.trace("Incident wasn't found, adding new pending incident on the map.");
			
			if (event.getState() == IncidentState.Pending) {
				found = event.getIncident();
				this.currentIncidentList.add(found);
			}
		}

		switch (event.getState()) {
		case Deleted:
		case DeletePending:
			logger.trace("Removing delete incident from local incidents.");
			this.currentIncidentList.remove(found);
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
		
		this.replaceIncidentsWith(this.currentIncidentList);
	}

	@Override
	public void enableIncidents(boolean enable) {
		this.incidentsEnabled = enable;
		if (enable) {
			this.replaceIncidentsWith(this.currentIncidentList);
		} else {
			this.markerManager.clearItems();
		}
	}
}
