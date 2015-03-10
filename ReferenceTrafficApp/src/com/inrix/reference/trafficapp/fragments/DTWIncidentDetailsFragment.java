/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.fragments;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.activity.MainMapActivity;
import com.inrix.reference.trafficapp.incidents.IncidentDisplayUtils;
import com.inrix.reference.trafficapp.util.Constants;
import com.inrix.reference.trafficapp.util.Interfaces.IGoogleMapProvider;
import com.inrix.reference.trafficapp.util.Interfaces.IIncidentRendered;
import com.inrix.reference.trafficapp.util.Interfaces.IOnFragmentAttachedListener;
import com.inrix.reference.trafficapp.util.Interfaces.IOnUpdateMapPaddingListener;
import com.inrix.reference.trafficapp.util.map.DefaultIncidentsRenderer;
import com.inrix.reference.trafficapp.view.SpinnerButton;
import com.inrix.sdk.model.Incident;

/**
 * Fragment for displaying details about incident.
 */
public class DTWIncidentDetailsFragment extends Fragment implements
		OnClickListener, ActionMode.Callback {
	public static final String INCIDENT = DTWIncidentDetailsFragment.class
			.getName() + ".INCIDENT";
	public static final String CURRENT_LOCATION = DTWIncidentDetailsFragment.class
			.getName() + ".CURRENT_LOCATION";
	public static final String MARKER_LOCATION = DTWIncidentDetailsFragment.class
			.getName() + ".MARKER_LOCATION";

	private static final String RESET_TIMER_EXTRA = "reset_timer_extra";

	private Incident incident;
	private View detailsItem;

	private IOnFragmentAttachedListener onAttachedListener;
	private IOnUpdateMapPaddingListener onUpdateMapPaddingListener;
	private IGoogleMapProvider googleMapProvider;
	private IIncidentRendered incidentRendered;

	private Location currentLocation;
	private LatLng markerLocation;

	private View buttonBar;
	private View rerouteButton;
	private SpinnerButton dismissButton;
	private View viewButton;
	private View actionModeView;

	private Timer closeTimer;

	private ActionMode actionMode = null;
	private TextView cabTitle = null;
	private boolean isAttached = false;

	private Marker marker;

	private int timePassed = Constants.SHOW_DIALOG_TIME_SECONDS;

	private DefaultIncidentsRenderer incidentMarkerRenderer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public final void onAttach(final Activity activity) {
		super.onAttach(activity);

		this.isAttached = true;

		if (activity instanceof IOnFragmentAttachedListener) {
			this.onAttachedListener = (IOnFragmentAttachedListener) activity;
		}

		if (activity instanceof IOnUpdateMapPaddingListener) {
			this.onUpdateMapPaddingListener = (IOnUpdateMapPaddingListener) activity;
		}

		if (activity instanceof IGoogleMapProvider) {
			this.googleMapProvider = (IGoogleMapProvider) activity;
		}

		if (activity instanceof IIncidentRendered) {
			this.incidentRendered = (IIncidentRendered) activity; 
		}

		this.incidentMarkerRenderer = new DefaultIncidentsRenderer(activity,
				null,
				null);

		this.actionMode = activity.startActionMode(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public final void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (this.onAttachedListener != null) {
			onAttachedListener.onFragmentAttached(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public final void onResume() {
		super.onResume();
		updateCABTitle();
		if (this.closeTimer != null) {
			this.dismissButton.setTime(timePassed);
			this.closeTimer.schedule(new TimerTask() {

				@Override
				public void run() {
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							dismissButton.setTime(timePassed);
							timePassed -= 1;
							if (timePassed < 0) {
								FragmentActivity activity = DTWIncidentDetailsFragment.this
										.getActivity();
								if (activity == null) {
									return;
								}
								activity.getSupportFragmentManager()
										.popBackStack();
								cancelTimer();
							}
						}
					});

				}
			},
					0,
					DateUtils.SECOND_IN_MILLIS);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (marker != null) {
			marker.remove();
		}
		if (this.incidentRendered != null && !getActivity().isChangingConfigurations()) {
			this.incidentRendered.enableIncidents(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onDetach()
	 */
	@Override
	public final void onDetach() {
		super.onDetach();

		this.isAttached = false;
		cancelTimer();

		if (this.onAttachedListener != null) {
			this.onAttachedListener.onFragmentDetached(this);
		}

		this.onAttachedListener = null;
		this.onUpdateMapPaddingListener = null;

		if (this.actionMode != null) {
			this.actionMode.finish();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public final void onDestroy() {
		super.onDestroy();

		if (this.onUpdateMapPaddingListener != null) {
			this.onUpdateMapPaddingListener.onUpdateMapPadding(null, null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container,
			final Bundle savedInstanceState) {
		final Bundle args = this.getArguments();
		if (args != null) {
			this.incident = args.getParcelable(INCIDENT);
			this.currentLocation = (Location) args
					.getParcelable(CURRENT_LOCATION);
			this.markerLocation = new LatLng(this.incident.getLatitude(),
					this.incident.getLongitude());

			if (this.incidentRendered != null) {
				this.incidentRendered.enableIncidents(false);
			}

			MarkerOptions opts = new MarkerOptions();
			opts.position(markerLocation);
			opts.icon(incidentMarkerRenderer.getIcon(this.incident));
			marker = googleMapProvider.getGoogleMap()
					.addMarker(opts);
		}

		return inflater.inflate(R.layout.dtw_incidents_details_fragment,
				container,
				false);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (this.closeTimer != null) {
			cancelTimer();
			outState.putInt(RESET_TIMER_EXTRA, timePassed);
		}
	}

	@Override
	public void onViewStateRestored(Bundle outState) {
		super.onViewStateRestored(outState);
		if (outState != null) {
			timePassed = outState.getInt(RESET_TIMER_EXTRA, -1);
		}
		boolean reset = (timePassed >= 0);
		this.dismissButton.showProgressBar(reset);
		if (reset) {
			closeTimer = new Timer();
		} else {
			cancelTimer();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onViewCreated(android.view.View,
	 * android.os.Bundle)
	 */
	@Override
	public final void onViewCreated(final View view,
			final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Android API doesn't have direct API to set height or background color
		// for ActionMode.
		int actionModeId = getResources().getIdentifier("action_context_bar",
				"id",
				"android");
		this.actionModeView = this.getActivity().findViewById(actionModeId);
		this.detailsItem = view.findViewById(R.id.item);
		TextView description = (TextView) this.detailsItem.findViewById(R.id.description);
		description.setText(IncidentDisplayUtils.getDescription(getActivity(), incident));
		((TextView) this.detailsItem.findViewById(R.id.distance))
				.setText(IncidentDisplayUtils.getDistanceAsString(incident,
						currentLocation));
		((TextView) this.detailsItem.findViewById(R.id.reported_time))
				.setText(IncidentDisplayUtils.getReportedTimeAsString(
						getActivity(), incident));
		this.detailsItem.setOnClickListener(this.clickListener);
		description.setOnClickListener(this.clickListener);

		this.buttonBar = view.findViewById(R.id.buttonBar);
		this.dismissButton = (SpinnerButton) view
				.findViewById(R.id.dismissButton);
		this.dismissButton.setVisibility(View.VISIBLE);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			this.dismissButton.alignChildren(true);
		}
		this.viewButton = view.findViewById(R.id.viewButton);
		this.rerouteButton = view.findViewById(R.id.rerouteButton);

		this.dismissButton.setOnClickListener(this);
		this.rerouteButton.setOnClickListener(this);
		this.viewButton.setOnClickListener(this);

		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().getSupportFragmentManager().popBackStack();
			}
		});

		view.getViewTreeObserver()
				.addOnPreDrawListener(new OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						view.getViewTreeObserver()
								.removeOnPreDrawListener(this);
						updatePadding();
						return true;
					}
				});
	}

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			cancelTimer();
			dismissButton.showProgressBar(false);
		}

	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public final void onClick(final View view) {
		switch (view.getId()) {
			case R.id.rerouteButton:
			case R.id.viewButton:
				performView();
				break;
			case R.id.dismissButton:
				performDismiss();
				break;
		}
	}

	/** Updates map padding. */
	public void updatePadding() {
		if (onUpdateMapPaddingListener != null) {
			Rect padding;
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				int bottomPadding = detailsItem.getHeight()
						+ detailsItem.getPaddingTop()
						+ detailsItem.getPaddingBottom()
						+ buttonBar.getHeight()
						+ buttonBar.getPaddingTop()
						+ buttonBar.getPaddingBottom()
						+ ((RelativeLayout.LayoutParams) buttonBar
								.getLayoutParams()).bottomMargin;

				padding = new Rect(0, cabTitle.getHeight(), 0, bottomPadding);
			} else {
				padding = new Rect(0, 0, detailsItem.getWidth(), 0);
			}
			onUpdateMapPaddingListener.onUpdateMapPadding(padding,
					markerLocation,
					Constants.INCIDENT_DEFAULT_ZOOM_LEVEL);
		}

		detailsItem.setTranslationY(detailsItem.getHeight() / 2);
		detailsItem.setAlpha(0);
		detailsItem.animate().translationY(0).alpha(1).setDuration(150)
				.setInterpolator(new DecelerateInterpolator()).start();

		buttonBar.setTranslationY(detailsItem.getHeight() / 2
				+ buttonBar.getHeight() / 2);
		buttonBar.setAlpha(0);
		buttonBar.animate().translationY(0).alpha(1).setDuration(150)
				.setInterpolator(new DecelerateInterpolator()).start();
	}

	private void performView() {
		final Bundle args = new Bundle();
		Incident[] incidentArray = new Incident[1];
		incidentArray[0] = this.incident;

		args.putParcelable(IncidentDetailsFragment.CURRENT_LOCATION,
				currentLocation);
		args.putParcelableArray(IncidentDetailsFragment.INCIDENTS,
				incidentArray);
		args.putParcelable(IncidentDetailsFragment.MARKER_LOCATION,
				markerLocation);

		getActivity().getSupportFragmentManager().popBackStack();
		getActivity()
				.getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.content_frame,
						Fragment.instantiate(getActivity(),
								IncidentDetailsFragment.class.getName(),
								args),
						MainMapActivity.TAG_INCIDENTS_DETAILS_FRAGMENT)
				.addToBackStack(null).commitAllowingStateLoss();

	}

	private void performDismiss() {
		this.getActivity().getSupportFragmentManager().popBackStack();
	}

	/** Updates Action Bar for the selected incident. */
	protected void updateCABTitle() {
		cabTitle.setText(IncidentDisplayUtils.getTitle(getActivity(),
				this.incident));
		actionModeView.setBackgroundColor(IncidentDisplayUtils.getColor(this
				.getActivity(), this.incident));
	}

	@SuppressLint("InflateParams")
	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		mode.getMenuInflater().inflate(R.menu.incidents_details_cab, menu);
		mode.setCustomView(getLayoutInflater(null)
				.inflate(R.layout.dtw_incidents_details_cab, null));
		this.cabTitle = (TextView) mode.getCustomView()
				.findViewById(R.id.title);
		menu.findItem(R.id.close).setIcon(R.drawable.fake_close_x_white);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		if (item.getItemId() == R.id.close) {
			getActivity().getSupportFragmentManager().popBackStack();
			return true;
		}
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		actionMode = null;
		actionModeView
				.setBackgroundResource(R.drawable.incident_details_cab_background);
		if (isAttached) {
			getActivity().getSupportFragmentManager().popBackStack();
		}
	}

	private void cancelTimer() {
		if (closeTimer != null) {
			closeTimer.cancel();
			closeTimer = null;
		}
	}
}