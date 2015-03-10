/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.fragments.mainmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.activity.ReportIncidentActivity;
import com.inrix.reference.trafficapp.fragments.BaseMapFragment.IFollowMeChangeListener;
import com.inrix.reference.trafficapp.fragments.IncidentDetailsFragment.OnIncidentDetailsFragmentListener;
import com.inrix.reference.trafficapp.util.BitmapUtils;
import com.inrix.reference.trafficapp.util.Interfaces.IGoogleMapProvider;
import com.inrix.reference.trafficapp.util.Interfaces.IIncidentRendered;
import com.inrix.reference.trafficapp.view.TouchableFrameLayout;
import com.inrix.reference.trafficapp.view.TouchableFrameLayout.OnPanListener;
import com.inrix.reference.trafficapp.view.TrafficStatusPanel;
import com.inrix.reference.trafficapp.view.TrafficStatusPanel.OnVisibilityChangeListener;
import com.inrix.sdk.model.Route;

/** Implements fragment to show content on main map screen. */
public class MainMapContentFragment extends Fragment implements OnPanListener,
		OnVisibilityChangeListener, OnIncidentDetailsFragmentListener,
		IGoogleMapProvider, IIncidentRendered {

	/** Tag to find map fragment. */
	private final static String TAG_MAP_FRAGMENT = "map_fragment";

	private final static String MAP_STATE = "map_state";

	/** Main map fragment. */
	private MainMapFragment mainMapFragment;

	/** Icon to report incidents. */
	private ImageView reportIcon;

	/** Icon to Zoom in. */
	private ImageView zoomIn;

	/** Icon to Zoom out. */
	private ImageView zoomOut;

	private ImageView followMeIcon;

	/** Traffic status panel. */
	private TrafficStatusPanel trafficStatusPanel;

	/** Active route. */
	private Route route;

	/** Whether controls are visible. */
	private boolean areControlsVisible = true;

	private MapState mapState;

	class MapState {
		boolean isFollowMeEnabled = false;
		LatLng mapCenter;
		float zoom;

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("\tfollow me: " + isFollowMeEnabled + "\n");
			sb.append("\tcenter: " + mapCenter.toString() + "\n");
			sb.append("\tzoom: " + zoom + "\n");
			return sb.toString();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main_map, container, false);
	}

	@Override
	public void onViewCreated(View rootView, Bundle savedInstanceState) {
		super.onViewCreated(rootView, savedInstanceState);
		initMapFragment(savedInstanceState);

		this.trafficStatusPanel = (TrafficStatusPanel) rootView
				.findViewById(R.id.traffic_status_panel);
		this.trafficStatusPanel.setOnVisibilityChangeListener(this);
		if (this.route != null) {
			this.trafficStatusPanel.setInfo(this.route);
			this.trafficStatusPanel.setVisibility(View.VISIBLE);
		}

		this.reportIcon = (ImageView) rootView.findViewById(R.id.report_button);
		this.reportIcon.setOnClickListener(this.onReportClickListener);

		this.followMeIcon = (ImageView) rootView
				.findViewById(R.id.follow_me_button);
		this.followMeIcon.setOnClickListener(this.onFollowMeClickListener);

		this.zoomIn = (ImageView) rootView.findViewById(R.id.zoom_in_button);
		this.zoomIn.setOnClickListener(this.onZoomInClickListener);

		this.zoomOut = (ImageView) rootView.findViewById(R.id.zoom_out_button);
		this.zoomOut.setOnClickListener(this.onZoomOutClickListener);

		TouchableFrameLayout mapFrameLayout = (TouchableFrameLayout) rootView
				.findViewById(R.id.map_frame);
		mapFrameLayout.addPanListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		reportIcon.setEnabled(true);
		this.mainMapFragment.setFollowMeListener(new IFollowMeChangeListener() {
			@Override
			public void onFollowMeChanged(boolean enabled) {
				followMeIcon.setActivated(enabled);
			}
		});
		this.followMeIcon.setActivated(this.mainMapFragment.isFollowMeEnabled());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		this.mainMapFragment.setFollowMeListener(null);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(MAP_STATE, new Gson().toJson(mapState));
	}

	public void storeMapState() {
		this.mapState = new MapState();
		this.mapState.isFollowMeEnabled = mainMapFragment.isFollowMeEnabled();
		this.mapState.mapCenter = mainMapFragment.getCameraPosition();
		this.mapState.zoom = mainMapFragment.getCurrentZoomLevel();
	}

	public void restoreMapState() {
		if (mapState == null) {
			return;
		}

		Location l = new Location("cam_pos");
		l.setLatitude(mapState.mapCenter.latitude);
		l.setLongitude(mapState.mapCenter.longitude);
		mainMapFragment.animateToLocation(l, mapState.zoom);
		mainMapFragment.enableFollowMe(mapState.isFollowMeEnabled);
	}

	/**
	 * Initializes map fragment.
	 * 
	 * @param savedInstanceState
	 *            Bundle to store necessary data if the activity is being
	 *            re-initialized.
	 */
	private void initMapFragment(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			this.mainMapFragment = (MainMapFragment) Fragment
					.instantiate(getActivity(), MainMapFragment.class.getName());

			this.mainMapFragment.setRetainInstance(true);

			this.getFragmentManager()
					.beginTransaction()
					.replace(R.id.map_frame,
							this.mainMapFragment,
							TAG_MAP_FRAGMENT).commit();

			if (this.route != null) {
				ArrayList<Route> routes = new ArrayList<Route>();
				routes.add(this.route);
				this.mainMapFragment.setRoutes(routes, areControlsVisible);
			}
		} else {
			this.mainMapFragment = (MainMapFragment) this.getFragmentManager()
					.findFragmentByTag(TAG_MAP_FRAGMENT);
			this.mapState = new Gson().fromJson(savedInstanceState
					.getString(MAP_STATE), MapState.class);
		}
	}

	/**
	 * Sets route to display.
	 * 
	 * @param route
	 *            Active route.
	 * @param zoomToRoutes
	 *            Flag to zoom on routes.
	 */
	public void setRoute(Route route, boolean zoomToRoutes) {
		this.route = route;

		if (this.trafficStatusPanel != null) {
			this.trafficStatusPanel.setInfo(route);
			if (route != null && this.areControlsVisible
					&& this.trafficStatusPanel.getVisibility() != View.VISIBLE) {
				this.trafficStatusPanel.setVisibility(View.VISIBLE);
			}
		}

		if (this.mainMapFragment != null) {
			ArrayList<Route> routes = new ArrayList<Route>();
			if (this.route != null) {
				routes.add(this.route);
			}
			this.mainMapFragment.setRoutes(routes, zoomToRoutes);
		}
	}

	public void updateRouteTime(Route route, int travelTimeMinutes) {
		if (this.trafficStatusPanel != null) {
			this.trafficStatusPanel.setInfo(route, travelTimeMinutes);
			if (route != null && this.areControlsVisible
					&& this.trafficStatusPanel.getVisibility() != View.VISIBLE) {
				this.trafficStatusPanel.setVisibility(View.VISIBLE);
			}
		}
	}

	/** Handles clicks on follow me button. */
	private OnClickListener onFollowMeClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mainMapFragment.enableFollowMe(!mainMapFragment.isFollowMeEnabled());
		}
	};

	/** Handles clicks on report button. */
	private OnClickListener onReportClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			openReportIncidentActivtiy();
			// disable button to prevent multiple clicks. It will be enabled
			// again in setUserVisibleHint
			reportIcon.setEnabled(false);
		}
	};

	/** Refreshes map content. */
	public void refreshMap() {
		this.mainMapFragment.refresh();
	}

	/** Handles clicks on zoom in button. */
	private OnClickListener onZoomInClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mainMapFragment.zoomIn();
		}
	};

	/** Handles clicks on zoom out button. */
	private OnClickListener onZoomOutClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mainMapFragment.zoomOut();
		}
	};

	/** Opens report incident activity. */
	private void openReportIncidentActivtiy() {
		// TODO: find a way to init background inside ReportIncidentActivity
		// instead of passing it.
		this.mainMapFragment.getMap().snapshot(new SnapshotReadyCallback() {
			@Override
			public void onSnapshotReady(Bitmap mapBitmap) {
				Intent reportIncidentIntent = new Intent(getActivity(),
						ReportIncidentActivity.class);
				MainMapFragment mainMapFragment = (MainMapFragment) getFragmentManager()
						.findFragmentByTag(TAG_MAP_FRAGMENT);
				Location location = mainMapFragment.getlastKnownLocation();
				if (location != null) {
					reportIncidentIntent.putExtra(ReportIncidentActivity.INTENT_KEY_INCIDENT_LATITUDE,
							location.getLatitude());
					reportIncidentIntent.putExtra(ReportIncidentActivity.INTENT_KEY_INCIDENT_LONGITUDE,
							location.getLongitude());

					// Prepares blur background to pass it to report activity.
					float scaleFactor = 8;
					int radius = 3;
					Bitmap overlay = Bitmap.createBitmap((int) (mapBitmap
							.getWidth() / scaleFactor),
							(int) (mapBitmap.getHeight() / scaleFactor),
							Bitmap.Config.ARGB_8888);
					Canvas canvas = new Canvas(overlay);
					canvas.scale(1 / scaleFactor, 1 / scaleFactor);
					Paint paint = new Paint();
					paint.setFlags(Paint.FILTER_BITMAP_FLAG);
					canvas.drawBitmap(mapBitmap, 0, 0, paint);
					overlay = BitmapUtils.blur(overlay, radius, getActivity());

					ByteArrayOutputStream outStream = new ByteArrayOutputStream();
					overlay.compress(CompressFormat.PNG, 100, outStream);

					reportIncidentIntent
							.putExtra(ReportIncidentActivity.INTENT_KEY_BACKGROUND_DATA,
									outStream.toByteArray());

					int rotation = getActivity().getWindowManager()
							.getDefaultDisplay().getRotation();
					reportIncidentIntent
							.putExtra(ReportIncidentActivity.INTENT_KEY_BACKGROUND_ROTATION,
									rotation);
					try {
						outStream.close();
					} catch (IOException e) {
					}
					startActivity(reportIncidentIntent);
				}
			}
		});
	}

	@Override
	public void onPan() {
		this.mainMapFragment.enableFollowMe(false);
	}

	@Override
	public void onVisibilityChange(int visibility) {
		Rect padding = this.mainMapFragment.getMapPadding();
		if (visibility == View.VISIBLE) {
			int height = this.trafficStatusPanel.getHeight();
			if (height == 0) {
				// If activity has just started, we couldn't retrieve height.
				// Adds listener to get correct height.
				this.trafficStatusPanel.getViewTreeObserver()
						.addOnPreDrawListener(new OnPreDrawListener() {
							@Override
							public boolean onPreDraw() {
								trafficStatusPanel.getViewTreeObserver()
										.removeOnPreDrawListener(this);
								Rect padding = mainMapFragment.getMapPadding();
								padding.top = trafficStatusPanel.getHeight();
								setMapPadding(padding, null);
								return true;
							}
						});

				return;
			}

			padding.top = height;
		} else {
			padding.top = 0;
		}

		this.setMapPadding(padding, null);
	}

	public void setMapPadding(Rect padding, LatLng mapCenter) {
		this.mainMapFragment.setMapPadding(padding, mapCenter);
	}

	public void setMapPadding(Rect padding, LatLng mapCenter, float zoomLevel) {
		this.mainMapFragment.setMapPadding(padding, mapCenter, zoomLevel);
	}

	public void showMapControls(boolean show) {
		int visibility = (show ? View.VISIBLE : View.GONE);
		this.areControlsVisible = show;
		this.reportIcon.setVisibility(visibility);
		this.zoomIn.setVisibility(visibility);
		this.zoomOut.setVisibility(visibility);
		this.followMeIcon.setVisibility(visibility);

		if (this.route != null) {
			this.trafficStatusPanel.setVisibility(visibility);
		}
	}

	@Override
	public void onBeforeIncidentDetailsShow() {
		storeMapState();
		mainMapFragment.enableFollowMe(false);
	}

	public GoogleMap getMap() {
		return mainMapFragment.getMap();
	}

	@Override
	public void onIncidentDetailsHide() {
		restoreMapState();
	}

	public Location getLastKnownLocation() {
		return this.mainMapFragment.getlastKnownLocation();
	}

	@Override
	public GoogleMap getGoogleMap() {
		return this.mainMapFragment.getMap();
	}

	@Override
	public void enableIncidents(boolean enable) {
		this.mainMapFragment.enableIncidents(enable);
	}
}
