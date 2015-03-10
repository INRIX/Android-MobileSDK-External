/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.fragments;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.incidents.IncidentDisplayUtils;
import com.inrix.reference.trafficapp.incidents.IncidentsProvider;
import com.inrix.reference.trafficapp.incidents.IncidentsProvider.LocalIncidentInfo;
import com.inrix.reference.trafficapp.util.Constants;
import com.inrix.reference.trafficapp.util.Interfaces.IGoogleMapProvider;
import com.inrix.reference.trafficapp.util.Interfaces.IOnFragmentAttachedListener;
import com.inrix.reference.trafficapp.util.Interfaces.IOnUpdateMapPaddingListener;
import com.inrix.reference.trafficapp.util.map.DefaultIncidentsRenderer;
import com.inrix.reference.trafficapp.view.SpinnerButton;
import com.inrix.sdk.Error;
import com.inrix.sdk.IncidentsManager;
import com.inrix.sdk.IncidentsManager.IncidentDeleteOptions;
import com.inrix.sdk.IncidentsManager.IncidentReviewOptions;
import com.inrix.sdk.model.Incident;

/**
 * Fragment for displaying details about incident.
 */
public final class IncidentDetailsFragment extends Fragment implements
		OnClickListener, ActionMode.Callback {
	public static final String INCIDENTS = IncidentDetailsFragment.class
			.getName() + ".INCIDENTS";
	public static final String CURRENT_LOCATION = IncidentDetailsFragment.class
			.getName() + ".CURRENT_LOCATION";
	public static final String MARKER_LOCATION = IncidentDetailsFragment.class
			.getName() + ".MARKER_LOCATION";
	public static final String POSITION_TAG = IncidentDetailsFragment.class
			.getName() + ".POSITION_TAG";
	public static final String CENTER_ON_INCIDENT = IncidentDetailsFragment.class
			.getName() + ".CENTER_ON_INCIDENT";

	private static final String RESET_TIMER_EXTRA = "reset_timer_extra";

	private final IncidentsProvider provider = new IncidentsProvider();
	private List<Incident> incidents;
	private IncidentsAdapter adapter;
	private ViewPager viewPager;

	private IOnFragmentAttachedListener onAttachedListener;
	private IOnUpdateMapPaddingListener onUpdateMapPaddingListener;

	private Location currentLocation;
	private LatLng markerLocation;

	private View buttonBar;
	private View confirmButton;
	private View allClearButton;
	private View deleteButton;
	private View buttonSeparator;
	private SpinnerButton dismissButton;

	private ActionMode actionMode = null;
	private TextView cabTitle = null;
	// I cannot use isDetached() method since it changes asynchronously
	private boolean isAttached = false;
	private int position = -1;

	private Marker marker;
	private DefaultIncidentsRenderer incidentMarkerRenderer;
	private IGoogleMapProvider mapProvider;

	private Timer closeTimer;
	private int timePassed = Constants.SHOW_DIALOG_TIME_SECONDS;

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
			this.mapProvider = (IGoogleMapProvider) activity;
		}

		this.actionMode = activity.startActionMode(this);
		this.incidentMarkerRenderer = new DefaultIncidentsRenderer(activity,
				null,
				null);
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

		this.updateCABTitle();
		this.updateButtonBarVisibility(this.adapter.getItem(this.viewPager
				.getCurrentItem()));
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
								IncidentDetailsFragment.this.getActivity()
										.getSupportFragmentManager()
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
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public final void onDestroy() {
		super.onDestroy();

		if (this.onUpdateMapPaddingListener != null) {
			this.onUpdateMapPaddingListener.onUpdateMapPadding(null, null);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (marker != null) {
			marker.remove();
			marker = null;
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
	public final View onCreateView(final LayoutInflater inflater,
			final ViewGroup container,
			final Bundle savedInstanceState) {
		final Bundle args = this.getArguments();
		if (args != null) {
			this.incidents = Arrays.asList((Incident[]) args
					.getParcelableArray(INCIDENTS));
			this.currentLocation = (Location) args
					.getParcelable(CURRENT_LOCATION);
			this.markerLocation = (LatLng) args.getParcelable(MARKER_LOCATION);
			this.position = args.getInt(POSITION_TAG, 0);
		}

		return inflater.inflate(R.layout.incidents_details_fragment,
				container,
				false);
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

		this.viewPager = (ViewPager) view.findViewById(R.id.viewPager);

		this.buttonBar = view.findViewById(R.id.buttonBar);
		this.confirmButton = view.findViewById(R.id.confirmButton);
		this.allClearButton = view.findViewById(R.id.allClearButton);
		this.deleteButton = view.findViewById(R.id.deleteButton);
		this.buttonSeparator = view.findViewById(R.id.buttonSeparator);
		this.dismissButton = (SpinnerButton) view.findViewById(R.id.dismissButton);

		this.confirmButton.setOnClickListener(this);
		this.allClearButton.setOnClickListener(this);
		this.deleteButton.setOnClickListener(this);
		this.dismissButton.setOnClickListener(this);

		this.adapter = new IncidentsAdapter(this.getActivity(), this.incidents);
		this.viewPager.setAdapter(this.adapter);
		this.viewPager.setCurrentItem(position);
		updateCABTitle();

		// add marker
		MarkerOptions opts = new MarkerOptions();
		opts.position(markerLocation);
		opts.icon(incidentMarkerRenderer.getIcon(incidents.get(position)));
		marker = mapProvider.getGoogleMap().addMarker(opts);

		this.viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public final void onPageSelected(final int position) {
				IncidentDetailsFragment.this.position = position;
				cancelTimer();
				dismissButton.showProgressBar(false);
				updateCABTitle();
				updateButtonBarVisibility(adapter.getItem(position));
			}

			@Override
			public final void onPageScrolled(final int position,
					final float positionOffset,
					final int positionOffsetPixels) {
			}

			@Override
			public final void onPageScrollStateChanged(final int state) {
			}
		});

		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				IncidentDetailsFragment.this.getArguments()
						.putBoolean(CENTER_ON_INCIDENT, true);
				onUpdateMapPaddingListener.onUpdateMapPadding(null,
						markerLocation);
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

	/** Updates map padding. */
	public void updatePadding() {
		if (onUpdateMapPaddingListener != null) {
			Rect padding;
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				int bottomPadding = 0;
				if (buttonBar.getHeight() != 0) {
					bottomPadding = buttonBar.getHeight()
							+ buttonBar.getPaddingTop()
							+ buttonBar.getPaddingBottom()
							+ ((RelativeLayout.LayoutParams) buttonBar
									.getLayoutParams()).bottomMargin;
				}

				padding = new Rect(0, viewPager.getHeight(), 0, bottomPadding);
			} else {
				padding = new Rect(0, 0, viewPager.getWidth(), 0);
			}
			onUpdateMapPaddingListener.onUpdateMapPadding(padding,
					markerLocation);
		}

		viewPager.setTranslationY(-viewPager.getHeight() / 2);
		viewPager.setAlpha(0);
		viewPager.animate().translationY(0).alpha(1).setDuration(150)
				.setInterpolator(new DecelerateInterpolator()).start();

		buttonBar.setTranslationY(buttonBar.getHeight() / 2);
		buttonBar.setAlpha(0);
		buttonBar.animate().translationY(0).alpha(1).setDuration(150)
				.setInterpolator(new DecelerateInterpolator()).start();
	}

	/**
	 * Update the visibility of the action bar.
	 */
	private final void updateButtonBarVisibility(final Incident currentIncident) {
		this.confirmButton.setVisibility(View.GONE);
		this.allClearButton.setVisibility(View.GONE);
		this.deleteButton.setVisibility(View.GONE);
		this.buttonSeparator.setVisibility(View.GONE);
		this.dismissButton.setVisibility(View.VISIBLE);

		if (! (currentIncident instanceof LocalIncidentInfo)) {
			return;
		}

		final LocalIncidentInfo local = (LocalIncidentInfo) currentIncident;
		switch (local.getState()) {
			case Default:
				// Allow to vote only on community incidents.
				if (local.isUgi()) {
					if (!IncidentDisplayUtils.isTooFar(local)) {
						this.confirmButton.setVisibility(View.VISIBLE);
						this.allClearButton.setVisibility(View.VISIBLE);
						this.buttonSeparator.setVisibility(View.VISIBLE);
					}
				}
				break;
			case Cleared:
			case Confirmed:
				// User already voted on the incident, and can't delete this
				// incident.
				return;
			case Deleted:
			case DeletePending:
				// Incident should not be in this state and be visible in the
				// UI.
				return;
			case Pending:
			case Reported:
				// Allow user to delete its incidents.
				this.deleteButton.setVisibility(View.VISIBLE);
				break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public final void onClick(final View view) {
		switch (view.getId()) {
			case R.id.confirmButton:
				this.performReview(true);
				break;
			case R.id.allClearButton:
				this.performReview(false);
				break;
			case R.id.deleteButton:
				this.performDelete();
				break;
			case R.id.dismissButton:
				break;
		}

		this.getActivity().getSupportFragmentManager().popBackStack();
	}

	/**
	 * Reviews current incident.
	 * 
	 * @param confirm
	 *            True if incident was confirmed; otherwise false.
	 */
	private final void performReview(final boolean confirm) {
		final Context contextCapture = this.getActivity();
		final Incident incident = this.adapter.getItem(this.viewPager
				.getCurrentItem());
		final IncidentReviewOptions reviewOptions = new IncidentReviewOptions(
				incident.getId(),
				incident.getVersion())
				.setConfirmed(confirm);
		this.provider.reviewIncident(reviewOptions,
				new IncidentsManager.IIncidentReviewListener() {
					@Override
					public final void onResult(final Boolean result) {
						provider.release();
						Toast.makeText(contextCapture,
								contextCapture.getText(R.string.thank_you),
								Toast.LENGTH_SHORT).show();
					}

					@Override
					public final void onError(final Error error) {
						provider.release();
						Toast.makeText(contextCapture,
								contextCapture
										.getText(R.string.unable_to_confirm_incident),
								Toast.LENGTH_SHORT).show();
					}
				},
				incident);
	}

	/**
	 * Performs delete operation for this incident.
	 */
	private void performDelete() {
		final Context contextCapture = this.getActivity();
		final Incident incident = this.adapter.getItem(this.viewPager
				.getCurrentItem());
		final IncidentDeleteOptions deleteOptions = new IncidentDeleteOptions(
				incident.getId(),
				incident.getVersion());
		this.provider.deleteIncident(deleteOptions,
				new IncidentsManager.IIncidentDeleteListener() {
					@Override
					public final void onResult(final Boolean result) {
						provider.release();
					}

					@Override
					public final void onError(final Error error) {
						provider.release();
						Toast.makeText(contextCapture,
								contextCapture
										.getText(R.string.unable_to_delete_incident),
								Toast.LENGTH_SHORT).show();
					}
				});
	}

	private void updateCABTitle() {
		IncidentsAdapter adapter = (IncidentsAdapter) viewPager.getAdapter();
		if (adapter.getCount() == 1) {
			cabTitle.setText(IncidentDisplayUtils.getTitle(this.getActivity(),
					adapter.getItem(0)));
		} else {
			cabTitle.setText( (viewPager.getCurrentItem() + 1) + "/"
					+ adapter.getCount());
		}
	}

	@SuppressLint("InflateParams")
	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		mode.getMenuInflater().inflate(R.menu.incidents_details_cab, menu);
		mode.setCustomView(getLayoutInflater(null)
				.inflate(R.layout.incidents_details_cab, null));
		cabTitle = (TextView) mode.getCustomView().findViewById(R.id.title);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		if (item.getItemId() == R.id.close) {
			handleBack();
			return true;
		}
		return false;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		actionMode = null;
		if (isAttached) {
			// this happens when we press hardware back button. ActionMode
			// handles it and finishes. I could not find a way to override hw
			// back button within fragment :(
			handleBack();
		}
	}

	private OnClickListener onPageClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			cancelTimer();
			dismissButton.showProgressBar(false);
		}

	};

	/**
	 * Incident page adapter.
	 */
	private final class IncidentsAdapter extends PagerAdapter {
		private final LayoutInflater inflater;
		private final List<Incident> incidents;

		/**
		 * Initializes a new instance of {@link IncidentsAdapter} class.
		 * 
		 * @param context
		 *            Current context instance.
		 * @param incidents
		 *            List of incidents.
		 */
		public IncidentsAdapter(final Context context,
				final List<Incident> incidents) {
			this.inflater = LayoutInflater.from(context);
			this.incidents = incidents;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.view.PagerAdapter#getCount()
		 */
		@Override
		public final int getCount() {
			return this.incidents.size();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.view.PagerAdapter#isViewFromObject(android.view
		 * .View, java.lang.Object)
		 */
		@Override
		public final boolean isViewFromObject(final View view,
				final Object object) {
			return view == object;
		}

		/**
		 * Gets incident from data set by given position.
		 * 
		 * @param position
		 *            Position in the data set.
		 * @return An instance of an {@link Incident}.
		 */
		public final Incident getItem(final int position) {
			return this.incidents.get(position);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.view.PagerAdapter#instantiateItem(android.view
		 * .ViewGroup, int)
		 */
		@Override
		public final Object instantiateItem(final ViewGroup container,
				final int position) {
			// Create view.
			final View view = this.inflater
					.inflate(R.layout.incidents_details_item, container, false);
			view.setOnClickListener(onPageClick);
			container.addView(view);

			// Find controls.
			final ImageView icon = (ImageView) view.findViewById(R.id.icon);
			final TextView description = (TextView) view
					.findViewById(R.id.description);
			final TextView distance = (TextView) view
					.findViewById(R.id.distance);
			final TextView reportedTime = (TextView) view
					.findViewById(R.id.reported_time);
			final View navigateLeftIndicator = view
					.findViewById(R.id.arrowLeft);
			final View navigateRightIndicator = view
					.findViewById(R.id.arrowRight);

			// Fill with data.
			final Incident incident = incidents.get(position);
			icon.setImageResource(IncidentDisplayUtils.getIcon(incident));
			description.setText(IncidentDisplayUtils
					.getDescription(getActivity(), incident));
			description.setOnClickListener(onPageClick);
			reportedTime.setText(IncidentDisplayUtils
					.getReportedTimeAsStringFormatted(getActivity(), incident));

			int visibility = (currentLocation == null) ? View.GONE
					: View.VISIBLE;
			distance.setText(IncidentDisplayUtils
					.getDistanceAsStringFormatted(getActivity(),
							incident,
							currentLocation));
			distance.setVisibility(visibility);

			navigateLeftIndicator.setVisibility(View.VISIBLE);
			navigateRightIndicator.setVisibility(View.VISIBLE);
			if (position == 0) {
				navigateLeftIndicator.setVisibility(View.GONE);
			}
			if (position == getCount() - 1) {
				navigateRightIndicator.setVisibility(View.GONE);
			}

			return view;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.view.PagerAdapter#destroyItem(android.view.ViewGroup
		 * , int, java.lang.Object)
		 */
		@Override
		public final void destroyItem(final ViewGroup container,
				final int position, final Object object) {
			container.removeView((View) object);
		}
	}

	public interface OnIncidentDetailsFragmentListener {
		public void onBeforeIncidentDetailsShow();

		public void onIncidentDetailsHide();
	}

	public void handleBack() {
		getActivity().getSupportFragmentManager().popBackStack();
	}

	private void cancelTimer() {
		if (closeTimer != null) {
			closeTimer.cancel();
			closeTimer = null;
		}
	}
}
