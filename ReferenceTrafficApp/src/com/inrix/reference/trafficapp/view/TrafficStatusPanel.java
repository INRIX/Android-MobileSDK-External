/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.view;

import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.TrafficApp;
import com.inrix.reference.trafficapp.error.ErrorEntity;
import com.inrix.sdk.Error;
import com.inrix.sdk.Error.Type;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.TrafficManager;
import com.inrix.sdk.TrafficManager.ITrafficQualityResponseListener;
import com.inrix.sdk.TrafficManager.TrafficQualityOptions;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Route;
import com.inrix.sdk.model.TrafficQuality;
import com.inrix.sdk.model.TripInformation.TravelTime.RouteQuality;

/** Shows traffic status for route or current location. */
public class TrafficStatusPanel extends LinearLayout {

	/** Text view on left side. */
	private TextView leftTextView;

	/** Text view of right side. */
	private TextView rightTextView;

	private View errorIcon;

	/** Visibility change listener. */
	private OnVisibilityChangeListener visibilityChangeListener;

	/**
	 * Indicator that control has information to show.
	 */
	private boolean hasTrafficInformation;

	/**
	 * Creates new instance of the class.
	 * 
	 * @param context
	 *            Valid context.
	 */
	public TrafficStatusPanel(Context context) {
		super(context);
		init();
	}

	/**
	 * Creates new instance of the class.
	 * 
	 * @param context
	 *            Valid context.
	 * @param attrs
	 *            Control attributes.
	 */
	public TrafficStatusPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * Creates new instance of the class.
	 * 
	 * @param context
	 *            Valid context.
	 * @param attrs
	 *            Control attributes.
	 * @param defStyle
	 *            Default style.
	 */
	public TrafficStatusPanel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * Initialize main controls of the control.
	 */
	private void init() {
		inflate(getContext(), R.layout.traffic_status_panel_view, this);
		this.leftTextView = (TextView) findViewById(R.id.left_side_info);
		this.rightTextView = (TextView) findViewById(R.id.right_side_info);
		this.errorIcon = findViewById(R.id.icon);

		int padding = getResources()
				.getDimensionPixelSize(R.dimen.traffic_status_panel_padding);
		setPadding(padding, padding, padding, padding);
		setDefaultView(R.string.status_panel_determining_quality);
	}

	/**
	 * Sets info to display.
	 * 
	 * @param currentLocation
	 *            Current location.
	 */
	public void setInfo(Location currentLocation) {
		TrafficManager trafficManager = InrixCore.getTrafficManager();
		TrafficQualityOptions options = new TrafficQualityOptions(new GeoPoint(currentLocation
				.getLatitude(), currentLocation.getLongitude()));
		trafficManager.getTrafficQuality(options,
				new ITrafficQualityResponseListener() {
					@Override
					public void onResult(TrafficQuality data) {
						hasTrafficInformation = true;
						errorIcon.setVisibility(View.GONE);
						leftTextView.setText(data.getCityName());
						bindTrafficIndex(rightTextView, data.getBucket());
					}

					@Override
					public void onError(Error error) {
						// if error occurs and no information available,
						// we need to show error message in control.
						if (!hasTrafficInformation) {
							setDefaultView(R.string.current_location);
							errorIcon.setVisibility(View.VISIBLE);
						}

						// Error bar shown only in case of network error.
						if (error.getErrorType() == Type.NETWORK_ERROR) {
							ErrorEntity errEntity = ErrorEntity
									.fromInrixError(error);
							if (errEntity != null) {
								TrafficApp.getBus().post(errEntity);
							}
						}
					}
				});
	}

	/**
	 * Sets info to display.
	 * 
	 * @param route
	 *            Active route.
	 */
	public void setInfo(Route route) {
		setInfo(route, route != null ? route.getTravelTimeMinutes() : 0);
	}

	/**
	 * Sets info to display.
	 * 
	 * @param route
	 *            Active route.
	 * @param travelTimeMinutes
	 *            Travel time in minutes. The time will be used to display
	 *            instead of time from route info.
	 */
	public void setInfo(Route route, int travelTimeMinutes) {
		if (route == null) {
			this.leftTextView.setText(R.string.empty);
			this.rightTextView.setText(R.string.empty);
			setVisibility(View.GONE);
			return;
		}

		this.setBackground(route.getRouteQuality());

		this.leftTextView.setText(route.getSummary().getText());

		Calendar travel = Calendar.getInstance(TimeZone.getDefault());
		travel.add(Calendar.MINUTE, travelTimeMinutes);
		java.text.DateFormat dateFormat = android.text.format.DateFormat
				.getTimeFormat(getContext());

		this.rightTextView.setText(dateFormat.format(travel.getTime()));
	}

	/**
	 * Sets listener to get notification when control visibility is changed.
	 * 
	 * @param listener
	 *            Listener to get notification when control visibility is
	 *            changed.
	 */
	public void setOnVisibilityChangeListener(OnVisibilityChangeListener listener) {
		this.visibilityChangeListener = listener;
	}

	/**
	 * Shows traffic information depending on current traffic quality.
	 * 
	 * @param textView
	 *            Text view to display route quality text.
	 * @param routeIndex
	 *            Route quality index.
	 */
	private void bindTrafficIndex(TextView textView, int routeIndex) {
		switch (routeIndex) {
			case 0:
				this.setBackground(RouteQuality.HEAVY.routeQualityId);
				textView.setText(getContext().getResources()
						.getString(R.string.traffic_index_text_bucket0));
				break;
			case 1:
				this.setBackground(RouteQuality.HEAVY.routeQualityId);
				textView.setText(getContext().getResources()
						.getString(R.string.traffic_index_text_bucket1));
				break;
			case 2:
				this.setBackground(RouteQuality.MODERATE.routeQualityId);
				textView.setText(getContext().getResources()
						.getString(R.string.traffic_index_text_bucket2));
				break;
			case 3:
			default:
				this.setBackground(RouteQuality.FREE_FLOW.routeQualityId);
				textView.setText(getContext().getResources()
						.getString(R.string.traffic_index_text_bucket3));
				break;
		}
	}

	private void setDefaultView(final int messageResourceId) {
		this.setBackgroundColor(getResources()
				.getColor(R.color.traffic_status_background));
		rightTextView.setText("");
		leftTextView.setText(messageResourceId);
	}

	/**
	 * Sets panel background depending on current traffic quality.
	 * 
	 * @param trafficQuality
	 *            Current traffic quality.
	 */
	private void setBackground(int trafficQuality) {
		if (trafficQuality == RouteQuality.FREE_FLOW.routeQualityId) {
			this.setBackgroundColor(getResources()
					.getColor(R.color.route_flow_free));
		} else if (trafficQuality == RouteQuality.MODERATE.routeQualityId) {
			this.setBackgroundColor(getResources()
					.getColor(R.color.route_flow_moderate));
		} else {
			this.setBackgroundColor(getResources()
					.getColor(R.color.route_flow_heavy));
		}
	}

	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		if (this.visibilityChangeListener != null) {
			this.visibilityChangeListener.onVisibilityChange(visibility);
		}
	}

	/** Notifies when visibility of the control was changed. */
	public interface OnVisibilityChangeListener {

		/**
		 * Will be called when control visibility will is changed.
		 * 
		 * @param visibility
		 *            New visibility value.
		 */
		public void onVisibilityChange(int visibility);
	}
}
