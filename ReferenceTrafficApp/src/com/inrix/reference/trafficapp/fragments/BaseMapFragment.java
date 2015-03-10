/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.fragments;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.inrix.reference.trafficapp.util.DemoLocationSource;
import com.inrix.reference.trafficapp.util.RefreshNotifier;
import com.inrix.reference.trafficapp.util.RefreshNotifier.OnRefreshListener;
import com.inrix.reference.trafficapp.util.WeatherAppConfig;
import com.inrix.reference.trafficapp.view.CurrentLocationDot;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.InrixCore.OnInrixReadyListener;
import com.inrix.sdk.TileManager;
import com.inrix.sdk.TileManager.TileOptions;
import com.inrix.sdk.geolocation.IGeolocationSource;
import com.inrix.sdk.geolocation.IOnGeolocationChangeListener;
import com.inrix.sdk.geolocation.PlayServicesLocationSource;
import com.inrix.sdk.utils.ZoomLevel;

/**
 * Displays traffic
 * 
 * @author paveld
 * 
 */
public class BaseMapFragment extends SupportMapFragment implements
		OnCameraChangeListener, IOnGeolocationChangeListener,
		OnMyLocationButtonClickListener, OnInrixReadyListener {

	public interface IOnMapReadyListener {
		public void onMapReady();
	}

	public interface IFollowMeChangeListener {
		public void onFollowMeChanged(boolean enabled);
	}

	private static final Logger logger = LoggerFactory
			.getLogger(BaseMapFragment.class);

	private IOnMapReadyListener mapReadyListener = null;
	private IFollowMeChangeListener followMeListener;
	protected GoogleMap map;
	protected boolean isGesturesEnabled = true;
	protected boolean isMyLocationButtonEnabled = false;
	protected boolean isMyLocationEnabeld = true;
	protected boolean isZoomControlsEnabled = false;
	protected boolean isCurrentLocationTrackingEnabled = true;
	protected boolean isFollowMeEnabled = false;
	protected Rect padding = new Rect(0, 0, 0, 0);
	protected boolean demoLocations = false;
	private final TileManager tileManager = InrixCore.getTileManager();

	private CurrentLocationDot curLocationDot = null;
	private IGeolocationSource locationSource;

	private Location lastKnownLocation = null;
	private CameraUpdate requestedCameraUpdate = null;

	private OnMapClickListener pendingOnMapClickListener;

	private RefreshNotifier refreshNotifier;

	/**
	 * Indicates whether it is the very first camera change. As soon as we
	 * acquire current location - we need to animate camera to current location
	 * and set default zoom level
	 */
	private boolean isFirstLocationUpdate = true;

	/**
	 * Indicates whether we need to move map to current location with the next
	 * location update
	 */
	private boolean pendingAnimateToCurrentLocation = false;

	private CopyOnWriteArrayList<OnCameraChangeListener> cameraChangeListeners = new CopyOnWriteArrayList<GoogleMap.OnCameraChangeListener>();
	private TileOverlay trafficOverlay = null;

	private static final int DEFAULT_ZOOM_LEVEL = 11;
	private static final int DEFAULT_TILE_OPACITY = 80;
	private static final int DEFAULT_SPEED_BUCKET = 364253195;

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setUpMap();
	}

	protected void setUpMap() {
		// TODO: check if GooglePlayServices available
		if (map != null) {
			// Already initialized
			return;
		}
		this.map = getMap();

		if (map == null) {
			logger.error("Map is null for some reason! No PlayServices?");
			return;
		}

		this.curLocationDot = new CurrentLocationDot(getActivity(), this.map);
		this.curLocationDot.enableCurrentLocation(true);

		// TODO: since we will reuse this control, we need to retain these
		// settings
		// in bundle, so we can restore settings on orientation change, etc
		map.setMyLocationEnabled(false);
		map.getUiSettings()
				.setMyLocationButtonEnabled(isMyLocationButtonEnabled);
		map.getUiSettings().setAllGesturesEnabled(isGesturesEnabled);
		map.getUiSettings().setZoomControlsEnabled(isZoomControlsEnabled);
		map.setOnCameraChangeListener(this);
		map.setOnMyLocationButtonClickListener(this);
		addTrafficTiles(map);

		this.refreshNotifier = new RefreshNotifier(new RefreshHandler(trafficOverlay));
		this.refreshNotifier.setRefreshPeriod(InrixCore.getTileManager()
				.getRefreshInterval(com.inrix.sdk.TileManager.Actions.GET_TILE, TimeUnit.MILLISECONDS));
		this.refreshNotifier.setLastUpdateTime(System.currentTimeMillis());

		checkDemoLocations();
		if (demoLocations) {
			this.locationSource = new DemoLocationSource(getActivity());
		} else {
			this.locationSource = new PlayServicesLocationSource(getActivity());
		}
		this.curLocationDot.updateCurrentLocation(lastKnownLocation);

		if (this.lastKnownLocation != null) {
			LatLng pos = new LatLng(this.lastKnownLocation.getLatitude(),
					this.lastKnownLocation.getLongitude());
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos,
					DEFAULT_ZOOM_LEVEL));
			isFirstLocationUpdate = false;
		} else {
			pendingAnimateToCurrentLocation = true;
		}

		if (this.pendingOnMapClickListener != null) {
			this.map.setOnMapClickListener(pendingOnMapClickListener);
			this.pendingOnMapClickListener = null;
		}

		if (null != mapReadyListener) {
			mapReadyListener.onMapReady();
		}
	}

	/*
	 * TODO: Remove this when we clean up the demo mode
	 */
	private void checkDemoLocations() {
		demoLocations = WeatherAppConfig.getCurrentAppConfig(getActivity())
				.getDemoLocations();
	}

	/**
	 * Animate to current location (if known) or wait until current location is
	 * retrieved. Zoom level will not be changed
	 */
	protected void animateToCurrentLocation() {
		if (this.map == null) {
			logger.debug("Cannot animate to current location - map is null");
			return;
		}

		if (this.lastKnownLocation != null) {
			logger.debug("Animating to current location: {}",
					lastKnownLocation.toString());
			animateToLocation(lastKnownLocation, map.getCameraPosition().zoom);
		} else {
			logger.debug("Current location is not available - waiting for current location");
			pendingAnimateToCurrentLocation = true;
		}
	}

	/**
	 * Animate to specified location
	 * 
	 * @param location
	 *            to animate to
	 * @param zoom
	 *            level to set
	 */
	public void animateToLocation(Location location, float zoom) {
		if (location != null) {
			animateToLocation(new LatLng(location.getLatitude(),
					location.getLongitude()),
					zoom);
		} else {
			logger.error("Cannot animate to location null");
		}
	}

	protected void animateToLocation(LatLng target, float zoom) {
		if (target != null) {
			pendingAnimateToCurrentLocation = false;
			requestedCameraUpdate = CameraUpdateFactory.newLatLngZoom(target,
					zoom);
			map.animateCamera(requestedCameraUpdate,
					600,
					new CancelableCallback() {

						@Override
						public void onFinish() {
							requestedCameraUpdate = null;
						}

						@Override
						public void onCancel() {
							requestedCameraUpdate = null;
						}
					});
		} else {
			logger.error("Cannot animate to location null");
		}
	}

	/**
	 * The same as {@link #animateToLocation(Location, float)}, but default zoom
	 * level will be used
	 * 
	 * @param location
	 *            to animate to
	 */
	protected void animateToLocation(Location location) {
		animateToLocation(location, DEFAULT_ZOOM_LEVEL);
	}

	/**
	 * Surprisingly, refreshes traffic tiles
	 */
	public void refreshTrafficTiles() {
		if (this.trafficOverlay != null) {
			logger.debug("Request to refresh traffic tiles");
			refreshNotifier.setLastUpdateTime(0);
			refreshNotifier.resumeAutoRefresh();
		} else {
			logger.debug("Cannot refresh traffic tiles - overlay is null");
		}
	}

	private void addTrafficTiles(GoogleMap map) {
		// Doesn't add the tile overlay if auth is not passed yet.
		if (!InrixCore.isReady()) {
			InrixCore.addOnInrixReadyListener(BaseMapFragment.this);
			return;
		}

		final TileOptions options = new TileOptions()
				.setOpacity(DEFAULT_TILE_OPACITY)
				.setSpeedBucketId(DEFAULT_SPEED_BUCKET);
		final TileOverlayOptions overlayOptions = new TileOverlayOptions();
		overlayOptions.visible(true);
		overlayOptions
				.tileProvider(new UrlTileProvider(
						TileManager.TILE_DEFAULT_WIDTH,
						TileManager.TILE_DEFAULT_HEIGHT) {
					@Override
					public final URL getTileUrl(final int x,
							final int y,
							final int zoom) {
						if (!tileManager.showTrafficTiles(zoom)) {
							logger.warn("Tiles config not available for this zoom level: {}",
									zoom);
							return null;
						}

						options.setFrcLevel(ZoomLevel.getFrcLevel(zoom));
						options.setPenWidth(ZoomLevel.getPenWidth(zoom));

						try {
							String url = tileManager.getTileUrl(x,
									y,
									zoom,
									options);
							return new URL(url);
						} catch (Exception e) {
							logger.warn("Failed to get tile url. Reason: {}", e);
							return null;
						}
					}
				});
		this.trafficOverlay = map.addTileOverlay(overlayOptions);
	}

	/**
	 * Add camera change listener. We need this since GoogleMap implementation
	 * allows us to specify only one camera change listener. So this fragment
	 * itself will listen to camera change updates and will populate these
	 * changes to all registered receivers
	 * 
	 * @param listener
	 */
	public synchronized void addOnCameraChangeListener(final OnCameraChangeListener listener) {
		if (listener != null) {
			this.cameraChangeListeners.add(listener);
		}
	}

	/**
	 * Remove camera change listener registered via
	 * {@link #addOnCameraChangeListener(OnCameraChangeListener)}
	 * 
	 * @param listener
	 * @return true if underlying collection was modified. False otherwise.
	 */
	public synchronized boolean removeOnCameraChangeListener(final OnCameraChangeListener listener) {
		return this.cameraChangeListeners.remove(listener);
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		for (OnCameraChangeListener listener : cameraChangeListeners) {
			listener.onCameraChange(position);
		}
	}

	/**
	 * Enables current location source. Please note, it will not enable 'follow
	 * me' mode. It will just enable current location readings. For follow me
	 * mode please see {@link #enableFollowMe(boolean)}
	 * 
	 * @param enable
	 *            - true to enable
	 */
	public void enableCurrentLocationTracking(boolean enable) {
		this.isCurrentLocationTrackingEnabled = enable;
		if (this.locationSource != null) {
			if (enable) {
				this.locationSource.activate(this);
			} else {
				this.locationSource.deactivate();
			}
		}
	}

	/**
	 * Enables 'follow me' mode. Map will be animated to the current location
	 * automatically whenever map receives current location update. Make sure
	 * you called {@link #enableCurrentLocationTracking(boolean)} to enable
	 * current location updates.<br>
	 * As an alternative, you can have your own location source implemented
	 * which listens to current location updates. In this case you don't need to
	 * enable built-in map's current location tracker. You just need to call
	 * {@link #onGeolocationChange(Location)} every time you receive current
	 * location update
	 * 
	 * @param enable
	 */
	public void enableFollowMe(boolean enable) {
		this.isFollowMeEnabled = enable;
		if (this.isFollowMeEnabled) {
			animateToCurrentLocation();
		}

		if (this.followMeListener != null) {
			this.followMeListener.onFollowMeChanged(enable);
		}
	}

	public boolean isFollowMeEnabled() {
		return this.isFollowMeEnabled;
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshNotifier.resumeAutoRefresh();
		if (this.isCurrentLocationTrackingEnabled) {
			this.locationSource.activate(this);
		}
	}

	@Override
	public void onPause() {
		refreshNotifier.pauseAutoRefresh();
		this.locationSource.deactivate();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		InrixCore.removeOnInrixReadyListener(this);
		super.onDestroy();
	}

	@Override
	public void onGeolocationChange(Location location) {
		this.lastKnownLocation = location;

		if (isCurrentLocationTrackingEnabled && this.map != null) {
			float zoom = (isFirstLocationUpdate ? DEFAULT_ZOOM_LEVEL : map
					.getCameraPosition().zoom);
			this.curLocationDot.updateCurrentLocation(lastKnownLocation);
			if (pendingAnimateToCurrentLocation) {
				pendingAnimateToCurrentLocation = false;
				map.moveCamera(CameraUpdateFactory
						.newLatLngZoom(new LatLng(location.getLatitude(),
								location.getLongitude()), zoom));
			} else if (isFollowMeEnabled) {
				animateToLocation(location, zoom);
			}
			isFirstLocationUpdate = false;
		}
	}

	@Override
	public boolean onMyLocationButtonClick() {
		animateToCurrentLocation();
		return true;
	}

	/**
	 * Sets on map click listener.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void setOnMapClickListener(OnMapClickListener listener) {
		if (this.map != null) {
			this.map.setOnMapClickListener(listener);
		} else {
			this.pendingOnMapClickListener = listener;
		}
	}

	/**
	 * Retrieves last known location.
	 * 
	 * @return Last known location.
	 */
	public Location getlastKnownLocation() {
		return this.lastKnownLocation;
	}

	public void zoomIn() {
		if (null != this.map) {
			float currentZoomLevel = this.map.getCameraPosition().zoom;
			if (currentZoomLevel < this.map.getMaxZoomLevel()) {
				this.map.animateCamera(CameraUpdateFactory
						.zoomTo(currentZoomLevel + 1));
			}
		}
	}

	public void zoomOut() {
		if (null != this.map) {
			float currentZoomLevel = this.map.getCameraPosition().zoom;
			if (currentZoomLevel > this.map.getMinZoomLevel()) {
				this.map.animateCamera(CameraUpdateFactory
						.zoomTo(currentZoomLevel - 1));
			}
		}
	}

	public LatLng getCameraPosition() {
		if (this.map != null) {
			return this.map.getCameraPosition().target;
		}

		return null;
	}

	public float getCurrentZoomLevel() {
		if (null != this.map) {
			return this.map.getCameraPosition().zoom;
		}
		return -1;
	}

	/**
	 * Set map padding.
	 * 
	 * @param padding
	 *            - padding or null to remove any padding
	 * @param mapCenter
	 *            Location to move the map.
	 */
	public void setMapPadding(Rect padding, LatLng mapCenter) {
		setMapPadding(padding, mapCenter, getCurrentZoomLevel());
	}

	/**
	 * Set map padding.
	 * 
	 * @param padding
	 *            - padding or null to remove any padding
	 * @param mapCenter
	 *            Location to move the map.
	 * @param zoom
	 *            Zoom level.
	 */
	public void setMapPadding(Rect padding, LatLng mapCenter, float zoom) {
		if (padding == null) {
			padding = new Rect(0, 0, 0, 0);
		}

		this.padding = padding;
		if (this.map == null) {
			return;
		}

		this.map.setPadding(padding.left,
				padding.top,
				padding.right,
				padding.bottom);

		if (mapCenter != null) {
			CameraUpdate newPosition = requestedCameraUpdate;
			if (newPosition == null) {
				newPosition = CameraUpdateFactory
						.newLatLngZoom(mapCenter, zoom);
			}
			this.map.animateCamera(newPosition);
		}
	}

	/**
	 * Retrieves map padding.
	 * 
	 * @return Map padding.
	 */
	public Rect getMapPadding() {
		return this.padding;
	}

	public void setOnMapReadyListener(IOnMapReadyListener readyListener) {
		this.mapReadyListener = readyListener;
	}

	@Override
	public void onInrixReady() {
		InrixCore.removeOnInrixReadyListener(this);
		addTrafficTiles(map);
	}

	/** Cancels map animation and deletes pending animations. */
	protected void cancelAnimation() {
		this.pendingAnimateToCurrentLocation = false;
		this.isFirstLocationUpdate = false;
		this.map.stopAnimation();
	}

	public void setFollowMeListener(IFollowMeChangeListener followMeListener) {
		this.followMeListener = followMeListener;
	}

	static class RefreshHandler implements OnRefreshListener {
		private WeakReference<TileOverlay> tileOverlay;

		public RefreshHandler(TileOverlay tileOverlay) {
			this.tileOverlay = new WeakReference<TileOverlay>(tileOverlay);
		}

		@Override
		public boolean onRefreshData(RefreshNotifier notifier) {
			if (tileOverlay.get() == null) {
				return false;
			}
			logger.debug("Refresh traffic tiles");
			tileOverlay.get().clearTileCache();
			return true;
		}

	}
}
