/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.fragments;

import java.util.List;

import android.content.res.Configuration;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.fragments.ReportIncidentChooseTypeFragment.OnDismissListener;
import com.inrix.reference.trafficapp.model.GeneratedIncidentTypeModel;
import com.inrix.reference.trafficapp.util.DateUtils;
import com.inrix.sdk.IncidentsManager.RoadSide;
import com.inrix.sdk.utils.AddressLocator;
import com.inrix.sdk.utils.AddressLocator.AddressLocatorListCallback;

/** Displays chosen incident type to report and allows to choose sides. */
public class ReportIncidentChooseSideFragment extends Fragment {

	/** Bundle key to save selected road side. */
	private final static String BUNDLE_KEY_ROAD_SIDE = "road_side";

	/** Bundle key to save address string. */
	private final static String BUNDLE_KEY_ADDRESS = "address";

	/** Chosen incident type. */
	private GeneratedIncidentTypeModel incident;

	/** Displays address of the incident. */
	private TextView addressTextView;

	/** Incident location: latitude. */
	private Double latitude;

	/** Incident location : longitude. */
	private Double longitude;

	/** Side button. */
	private View sideButton;

	/** Content of side button: my side. */
	private View mySideButtonText;

	/** Content of side button: other side. */
	private View otherSideButtonText;

	/** Listener to get notifications when user chooses road side. */
	private OnRoadSideSelectListener listener;

	/** Listener to get callback when user chooses incident type. */
	private OnDismissListener onDismissListener;

	/** Address received from the latitude and longitude. */
	private String addressString;

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.fragment_report_incident_choose_side,
						container,
						false);

		initLayout(rootView);

		TextView timeTypeTextView = (TextView) rootView
				.findViewById(R.id.txt_report_time_type);

		String timeType = String
				.format("%s - %s",
						DateUtils.getFormattedTimeForDisplay(System
								.currentTimeMillis(), getActivity()
								.getBaseContext()),
						this.incident.getName() + " "
								+ getString(R.string.reported)).toString();

		timeTypeTextView.setText(timeType);

		this.addressTextView = (TextView) rootView
				.findViewById(R.id.txt_report_address);

		this.mySideButtonText = rootView.findViewById(R.id.my_side_btn_txt);
		this.otherSideButtonText = rootView
				.findViewById(R.id.other_side_btn_txt);
		this.sideButton = rootView.findViewById(R.id.side_btn);
		this.sideButton.setOnClickListener(selectSideListener);

		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(BUNDLE_KEY_ROAD_SIDE)) {
				int roadSide = savedInstanceState.getInt(BUNDLE_KEY_ROAD_SIDE);
				if (roadSide == RoadSide.OTHER_SIDE.ordinal()) {
					this.mySideButtonText.setVisibility(View.GONE);
					this.otherSideButtonText.setVisibility(View.VISIBLE);
				} else if (roadSide == RoadSide.MY_SIDE.ordinal()) {
					this.otherSideButtonText.setVisibility(View.GONE);
					this.mySideButtonText.setVisibility(View.VISIBLE);
				}
			}

			if (savedInstanceState.containsKey(BUNDLE_KEY_ADDRESS)) {
				this.addressString = savedInstanceState
						.getString(BUNDLE_KEY_ADDRESS);
			}
		}

		notifyOnRoadSideSelectListener();

		if (this.addressString == null) {
			AddressLocator addressLocator = new AddressLocator(getActivity()
					.getApplicationContext(), this.addressLocatorListener);

			if (this.latitude != null && this.longitude != null) {
				addressLocator.getAddress(this.latitude.floatValue(),
						this.longitude.floatValue());
			} else {
				this.addressString = getString(R.string.incident_repoort_no_location);
				this.addressTextView.setText(addressString);
			}
		} else {
			this.addressTextView.setText(addressString);
		}

		View dismissButton = rootView.findViewById(R.id.dismiss_btn);
		dismissButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onDismissListener != null) {
					onDismissListener.onDismiss();
				}
			}
		});

		return rootView;
	}

	/**
	 * Initializes layout for current orientation.
	 * 
	 * @param rootView
	 *            Root fragment view.
	 */
	private void initLayout(View rootView) {
		int orientation = getResources().getConfiguration().orientation;

		ImageView incidentImage = (ImageView) rootView
				.findViewById(R.id.img_incident_type);
		incidentImage.setImageDrawable(this.incident.getLargeDrawable());

		LayoutParams imageParams = (LayoutParams) incidentImage
				.getLayoutParams();
		if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
			// Removes the rule.
			imageParams.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
		} else {
			imageParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		}
		incidentImage.setLayoutParams(imageParams);

		LinearLayout reportInfoLayout = (LinearLayout) rootView
				.findViewById(R.id.report_info_layout);
		LayoutParams reportInfoParams = (LayoutParams) reportInfoLayout
				.getLayoutParams();
		if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
			reportInfoParams.addRule(RelativeLayout.BELOW, 0);
			reportInfoParams.addRule(RelativeLayout.RIGHT_OF,
					R.id.img_incident_type);
			reportInfoParams.addRule(RelativeLayout.ALIGN_TOP,
					R.id.img_incident_type);
		} else {
			reportInfoParams.addRule(RelativeLayout.BELOW,
					R.id.img_incident_type);
		}
		reportInfoLayout.setLayoutParams(reportInfoParams);

		LinearLayout buttonsLaoyut = (LinearLayout) rootView
				.findViewById(R.id.buttons_layout);
		if (Configuration.ORIENTATION_LANDSCAPE == orientation) {
			buttonsLaoyut.setOrientation(LinearLayout.HORIZONTAL);
		} else {
			buttonsLaoyut.setOrientation(LinearLayout.VERTICAL);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (this.otherSideButtonText.getVisibility() == View.VISIBLE) {
			outState.putInt(BUNDLE_KEY_ROAD_SIDE, RoadSide.OTHER_SIDE.ordinal());
		} else if (this.mySideButtonText.getVisibility() == View.VISIBLE) {
			outState.putInt(BUNDLE_KEY_ROAD_SIDE, RoadSide.MY_SIDE.ordinal());
		}

		if (this.addressString != null) {
			outState.putString(BUNDLE_KEY_ADDRESS, this.addressString);
		}
	}

	/**
	 * Sets incident type chosen by user. Must be called before attaching the
	 * fragment to an activity.
	 * 
	 * @param incident
	 *            Incident to set.
	 */
	public void setIncident(GeneratedIncidentTypeModel incident) {
		this.incident = incident;
	}

	/**
	 * Sets latitude and longitude for the incident.
	 * 
	 * @param latitude
	 *            Incident location: latitude.
	 * @param longitude
	 *            Incident location: longitude.
	 */
	public void setLocation(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * Sets listener which will receive notifications when user selects road
	 * side.
	 * 
	 * @param listener
	 *            The listener.
	 */
	public void setOnRoadSideSelectListener(OnRoadSideSelectListener listener) {
		this.listener = listener;
	}

	/** Handles click events on select side controls. */
	private OnClickListener selectSideListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (mySideButtonText.getVisibility() == View.VISIBLE) {
				mySideButtonText.setVisibility(View.GONE);
				otherSideButtonText.setVisibility(View.VISIBLE);
			} else {
				otherSideButtonText.setVisibility(View.GONE);
				mySideButtonText.setVisibility(View.VISIBLE);
			}

			notifyOnRoadSideSelectListener();
		}
	};

	/** Listens and handles address locator response. */
	private AddressLocatorListCallback addressLocatorListener = new AddressLocatorListCallback() {
		@Override
		public void onNoAddressFound() {
			addressString = "";
		}

		@Override
		public void onNetworkError() {
			addressString = "";
		}

		@Override
		public void onGeocoderError() {
			addressString = "";
		}

		@Override
		public void onAddressListFound(List<Address> addresses) {
			addressString = new String();
			int maxAddressIndex = addresses.get(0).getMaxAddressLineIndex();
			for (int i = maxAddressIndex; i >= 0 && maxAddressIndex - i < 2; i--) {
				String line = addresses.get(0).getAddressLine(i);
				if (line != null && line.length() > 0) {
					addressString = line + " " + addressString;
				}
			}

			addressTextView.setText(addressString);
		}
	};

	/** Notifies that new side was selected. */
	private void notifyOnRoadSideSelectListener() {
		if (this.listener == null) {
			return;
		}

		if (this.otherSideButtonText.getVisibility() == View.VISIBLE) {
			this.listener.onRoadSideSelect(RoadSide.OTHER_SIDE);
		} else if (this.mySideButtonText.getVisibility() == View.VISIBLE) {
			this.listener.onRoadSideSelect(RoadSide.MY_SIDE);
		} else {
			this.listener.onRoadSideSelect(null);
		}
	}

	/**
	 * Sets listener to get callback when user presses dismiss.
	 * 
	 * @param listener
	 *            Listener to set.
	 */
	public void setOnDismissListener(OnDismissListener listener) {
		this.onDismissListener = listener;
	}

	/** Interface to get notifications when user chooses road side. */
	public interface OnRoadSideSelectListener {

		/**
		 * Will be called when user selects/unselects road side.
		 * 
		 * @param roadSide
		 *            Road side or null if nothing selected.
		 */
		public void onRoadSideSelect(RoadSide roadSide);
	}
}