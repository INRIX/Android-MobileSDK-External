/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.incidents;

import android.app.Activity;
import android.content.res.Configuration;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.TrafficApp;
import com.inrix.reference.trafficapp.fragments.BaseMapFragment;
import com.inrix.reference.trafficapp.fragments.BaseMapFragment.IOnMapReadyListener;
import com.inrix.reference.trafficapp.incidents.IncidentsProvider.LocalIncidentInfo;
import com.inrix.reference.trafficapp.util.Constants;
import com.inrix.reference.trafficapp.util.Interfaces.IOnFragmentAttachedListener;
import com.inrix.reference.trafficapp.view.RobotoTextView;
import com.inrix.sdk.Error;
import com.inrix.sdk.IncidentsManager;
import com.inrix.sdk.IncidentsManager.IncidentDeleteOptions;
import com.inrix.sdk.IncidentsManager.IncidentReviewOptions;
import com.inrix.sdk.model.Incident;

public class IncidentOnMapFragment extends Fragment implements IOnMapReadyListener, OnClickListener {
	private static final String TAG_INCIDENT_MAP_FRAGMENT = "incident_on_map_fragment";
	private static final String CURRENT_INCIDENT = "current_incident";
	private static final String CURRENT_LOCATION = "current_location";

	private final IncidentsProvider provider = new IncidentsProvider();
	private Incident incident;
	private Location incidentLocation;
	private Location currentLocation;
	private Rect padding;

	private BaseMapFragment mapFragment;
	private ImageView incidentIcon;
	private RobotoTextView descriptionText;
	private RobotoTextView distanceText;
	private RobotoTextView reportedTimeText;
	private View incidentDetailsPanel;

	private View buttonBar;
	private View confirmButton;
	private View allClearButton;
	private View deleteButton;
	private View buttonSeparator;
	
	private IOnFragmentAttachedListener onAttachedListener;

	/**
	 * Creates a new instance of {@link IncidentOnMapFragment} class.
	 * 
	 * @param incident
	 *            An instance of {@link Incident} to be displayed in the fragment.
	 * @param currentLocation
	 *            Current user geographical location.
	 * @return An instance of {@link IncidentOnMapFragment}.
	 */
	public static final IncidentOnMapFragment getInstance(final Incident incident, final Location currentLocation) {
		final Bundle args = new Bundle();
		args.putParcelable(CURRENT_INCIDENT, incident);
		args.putParcelable(CURRENT_LOCATION, currentLocation);

		final IncidentOnMapFragment fragment = new IncidentOnMapFragment();
		fragment.setArguments(args);
		return fragment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public final View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.incident_map_fragment, container, false);

		// Get controls.
		this.incidentIcon = (ImageView) rootView.findViewById(R.id.icon);
		this.descriptionText = (RobotoTextView) rootView.findViewById(R.id.description);
		this.distanceText = (RobotoTextView) rootView.findViewById(R.id.distance);
		this.reportedTimeText = (RobotoTextView) rootView.findViewById(R.id.reported_time);
		this.incidentDetailsPanel = rootView.findViewById(R.id.incident_details);

		this.buttonBar = rootView.findViewById(R.id.buttonBar);
		this.confirmButton = rootView.findViewById(R.id.confirmButton);
		this.allClearButton = rootView.findViewById(R.id.allClearButton);
		this.deleteButton = rootView.findViewById(R.id.deleteButton);
		this.buttonSeparator = rootView.findViewById(R.id.buttonSeparator);

		this.confirmButton.setOnClickListener(this);
		this.allClearButton.setOnClickListener(this);
		this.deleteButton.setOnClickListener(this);

		if (savedInstanceState == null) {
			this.mapFragment = (BaseMapFragment) Fragment.instantiate(getActivity(), BaseMapFragment.class.getName());
			this.getFragmentManager()
					.beginTransaction()
					.replace(R.id.map, mapFragment, TAG_INCIDENT_MAP_FRAGMENT)
					.commit();
		} else {
			this.mapFragment = (BaseMapFragment) getFragmentManager().findFragmentByTag(TAG_INCIDENT_MAP_FRAGMENT);
		}

		return rootView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
	 */
	@Override
	public final void onViewCreated(final View view, final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		view.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				view.getViewTreeObserver().removeOnPreDrawListener(this);
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					int bottomPadding = 0;
					if (buttonBar.getHeight() != 0) {
						bottomPadding = buttonBar.getHeight()
								+ buttonBar.getPaddingTop()
								+ buttonBar.getPaddingBottom()
								+ ((RelativeLayout.LayoutParams) buttonBar.getLayoutParams()).bottomMargin;
					}

					padding = new Rect(0, incidentDetailsPanel.getHeight(), 0, bottomPadding);
				} else {
					padding = new Rect(0, 0, incidentDetailsPanel.getWidth(), 0);
				}

				LatLng position = new LatLng(incident.getLatitude(), incident.getLongitude());
				mapFragment.setMapPadding(padding, position);
				return true;
			}
		});

		final Bundle args = this.getArguments();
		if (args != null) {
			this.incident = args.getParcelable(CURRENT_INCIDENT);
			this.incidentLocation = new Location("");
			this.incidentLocation.setLatitude(incident.getLatitude());
			this.incidentLocation.setLongitude(incident.getLongitude());
			this.currentLocation = args.getParcelable(CURRENT_LOCATION);

			this.setIncident();
			this.mapFragment.setOnMapReadyListener(this);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (activity instanceof IOnFragmentAttachedListener) {
			this.onAttachedListener = (IOnFragmentAttachedListener) activity;
		}
	}

	@Override
	public final void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (this.onAttachedListener != null) {
			onAttachedListener.onFragmentAttached(this);
		}
	}

	@Override
	public final void onDetach() {
		super.onDetach();

		if (this.onAttachedListener != null) {
			this.onAttachedListener.onFragmentDetached(this);
			this.onAttachedListener = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.inrix.reference.trafficapp.fragments.BaseMapFragment.IOnMapReadyListener#onMapReady()
	 */
	@Override
	public final void onMapReady() {
		this.mapFragment.enableCurrentLocationTracking(false);
		this.mapFragment.animateToLocation(incidentLocation, Constants.INCIDENT_DEFAULT_ZOOM_LEVEL);
		this.mapFragment.getMap().addMarker(IncidentDisplayUtils.getMarker(this.getActivity(), this.incident));

		if (null != padding) {
			LatLng position = this.mapFragment.getMap().getCameraPosition().target;
			this.mapFragment.setMapPadding(this.padding, position);
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
		final IncidentReviewOptions reviewOptions = new IncidentReviewOptions(
				this.incident.getId(),
				this.incident.getVersion())
				.setConfirmed(confirm);
		this.provider.reviewIncident(reviewOptions, new IncidentsManager.IIncidentReviewListener() {
			@Override
			public final void onResult(final Boolean result) {
				provider.release();
				Toast.makeText(TrafficApp.getContext(), R.string.thank_you, Toast.LENGTH_SHORT).show();
			}

			@Override
			public final void onError(final Error error) {
				provider.release();
				Toast.makeText(TrafficApp.getContext(), R.string.unable_to_confirm_incident, Toast.LENGTH_SHORT).show();
			}
		}, this.incident);
	}

	/**
	 * Performs delete operation for this incident.
	 */
	private void performDelete() {
		final IncidentDeleteOptions deleteOptions = new IncidentDeleteOptions(
				this.incident.getId(),
				this.incident.getVersion());
		this.provider.deleteIncident(deleteOptions, new IncidentsManager.IIncidentDeleteListener() {
			@Override
			public final void onResult(final Boolean result) {
				provider.release();
			}

			@Override
			public final void onError(final Error error) {
				provider.release();
				Toast.makeText(TrafficApp.getContext(), R.string.unable_to_delete_incident, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void setIncident() {
		this.incidentIcon.setImageResource(IncidentDisplayUtils.getIcon(this.incident));
		this.descriptionText.setText(IncidentDisplayUtils.getDescription(this.getActivity(), this.incident));
		this.distanceText.setText(IncidentDisplayUtils.getDistanceAsStringFormatted(this.getActivity(), this.incident, currentLocation));

		this.reportedTimeText.setText(IncidentDisplayUtils.getReportedTimeAsStringFormatted(this.getActivity(), this.incident));
		this.distanceText.setVisibility(
				((currentLocation == null)) ?
						View.GONE : View.VISIBLE);

		this.updateButtonBarVisibility(this.incident);
	}

	/**
	 * Update the visibility of the action bar.
	 */
	private final void updateButtonBarVisibility(final Incident currentIncident) {
		this.confirmButton.setVisibility(View.GONE);
		this.allClearButton.setVisibility(View.GONE);
		this.deleteButton.setVisibility(View.GONE);
		this.buttonSeparator.setVisibility(View.GONE);

		if (!(currentIncident instanceof LocalIncidentInfo)) {
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
			// User already voted on the incident, and can't delete this incident.
			return;
		case Deleted:
		case DeletePending:
			// Incident should not be in this state and be visible in the UI.
			return;
		case Pending:
		case Reported:
			// Allow user to delete its incidents.
			this.deleteButton.setVisibility(View.VISIBLE);
			break;
		}
	}
}