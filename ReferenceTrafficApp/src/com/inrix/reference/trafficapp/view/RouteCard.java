/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.activity.MainMapActivity;
import com.inrix.reference.trafficapp.locationpicker.LocationPickerActivity;
import com.inrix.reference.trafficapp.util.Constants;
import com.inrix.reference.trafficapp.util.WeatherAppConfig;
import com.inrix.sdk.LocationsManager.LocationType;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Location;
import com.inrix.sdk.model.Route;

public class RouteCard extends ShadowLayout implements OnClickListener {
	public enum RouteViewType {
		NORMAL_ROUTE_VIEW,
		ADD_HOME_WORK,
	};

	public enum RouteQuality {
		StopAndGo(0),
		Heavy(1),
		Moderate(2),
		FreeFlow(3),
		Closed(255),
		Unknown(-1);

		public int routeQualityId;

		RouteQuality(int pRouteQualityId) {
			routeQualityId = pRouteQualityId;
		}

		public static RouteQuality fromInteger(int x) {
			switch (x) {
				case 0:
					return StopAndGo;
				case 1:
					return Heavy;
				case 2:
					return Moderate;
				case 3:
					return FreeFlow;
				case 255:
					return Closed;
				case -1:
				default:
					return Unknown;
			}
		}
	}

	private TextView title;
	private TextView description;
	private ProgressBar progressBar;
	private Route currentRoute = null;
	private Location location = null;
	private RouteViewType routeViewType = null;
	private String dHrMinFormat, hrMinFormat, minFormat;
	private boolean youAreHere = false;
	private boolean controlsInitialized = false;
	boolean demoLocations = false;
	GeoPoint demoHome = null;
	GeoPoint demoWork = null;
	private int routeIndex;

	public RouteCard(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RouteCard(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RouteCard(Context context) {
		super(context);
		init();
	}

	public RouteCard setLocation(Location location) {
		this.location = location;
		return this;
	}

	public RouteCard setLoadingMode() {
		if (this.controlsInitialized) {
			this.title.setVisibility(View.VISIBLE);
			this.description.setVisibility(View.GONE);
			this.progressBar.setVisibility(View.VISIBLE);
		}
		return this;
	}

	public RouteCard stopLoadingUI() {
		if (this.controlsInitialized) {
			this.progressBar.setVisibility(View.GONE);
		}
		return this;
	}

	public RouteCard setRouteMode() {
		if (this.controlsInitialized) {
			this.title.setVisibility(View.VISIBLE);
			this.description.setVisibility(View.VISIBLE);
			this.progressBar.setVisibility(View.GONE);
		}
		return this;
	}

	public RouteCard setRoute(Route route) {
		this.currentRoute = route;
		if (null != this.title && null != this.currentRoute) {
			if (this.currentRoute.getTotalDistance() < Constants.YOU_ARE_HERE_THRESHOLD) {
				this.title.setText(R.string.you_are_here);
				this.description.setVisibility(View.GONE);
				this.youAreHere = true;
			} else {
				this.youAreHere = false;
				String strArrivalTime = getArrivalTime(getContext(),
						System.currentTimeMillis(),
						this.currentRoute.getTravelTimeMinutes());
				String formattedTravelTime = getTravelTimeString(this.currentRoute
						.getTravelTimeMinutes());
				String titleString = getContext()
						.getString(R.string.route_title_format,
								strArrivalTime,
								formattedTravelTime);

				this.title.setText(titleString);
				this.description.setText(this.currentRoute.getSummary()
						.getText());
				RouteQuality routeQuality = RouteQuality
						.fromInteger(this.currentRoute.getRouteQuality());
				bindRouteQuality(this.title, routeQuality);
			}
		}
		return this;
	}

	private String getTravelTimeString(int travelTimeMins) {
		if (TextUtils.isEmpty(this.dHrMinFormat)) {
			this.dHrMinFormat = getContext().getResources()
					.getString(R.string.travel_time_d_hr_min);
		}
		if (TextUtils.isEmpty(this.hrMinFormat)) {
			this.hrMinFormat = getContext().getResources()
					.getString(R.string.travel_time_hr_min);
		}
		if (TextUtils.isEmpty(this.minFormat)) {
			this.minFormat = getContext().getResources()
					.getString(R.string.travel_time_min);
		}
		long travelTimeMs = travelTimeMins * Constants.SECONDS_PER_MIN
				* Constants.MS_PER_SECOND;

		String format;
		if (travelTimeMs >= DateUtils.DAY_IN_MILLIS) {
			format = this.dHrMinFormat;
		} else if (travelTimeMs >= DateUtils.HOUR_IN_MILLIS) {
			format = this.hrMinFormat;
		} else {
			format = this.minFormat;
		}

		Calendar travel = Calendar.getInstance(TimeZone.getDefault());
		travel.set(Calendar.DAY_OF_YEAR, 0);
		travel.set(Calendar.HOUR_OF_DAY, 0);
		travel.set(Calendar.MINUTE, travelTimeMins);

		SimpleDateFormat dateFormat = new SimpleDateFormat(format,
				Locale.getDefault());
		dateFormat.setTimeZone(TimeZone.getDefault());
		String time = dateFormat.format(new Date(travel.getTimeInMillis()));
		return time;

	}

	public RouteCard setRouteViewType(RouteViewType routeViewType) {
		this.routeViewType = routeViewType;
		if (this.routeViewType == RouteViewType.ADD_HOME_WORK) {
			if (this.location.getLocationType() == LocationType.HOME.getValue()) {
				this.title.setText(R.string.setup_home);
			} else if (location.getLocationType() == LocationType.WORK
					.getValue()) {
				this.title.setText(R.string.setup_work);
			}

			this.description.setVisibility(View.GONE);
		}
		return this;
	}

	private void init() {
		View rootView = inflate(getContext(), R.layout.route_card, this);
		rootView.setOnClickListener(this);
		this.title = (TextView) findViewById(R.id.eta_traveltime);
		this.description = (TextView) findViewById(R.id.route_desc);
		this.progressBar = (ProgressBar) findViewById(R.id.loading_control);
		this.controlsInitialized = true;
		// initialize the demo mode
		WeatherAppConfig appConfig = WeatherAppConfig
				.getCurrentAppConfig(getContext());
		if (null != appConfig) {
			this.demoLocations = appConfig.getDemoLocations();
			this.demoHome = appConfig.getDemoHome();
			this.demoWork = appConfig.getDemoWork();
		}
	}

	public void setTitleText(CharSequence text) {
		this.title.setText(text);
	}

	public void setDescription(CharSequence text) {
		this.description.setText(text);
	}

	/* If needed by others move to a Utility class */
	public String getArrivalTime(Context context,
			long currentTimeMS,
			int travelTimeMinutes) {
		return (getFormattedTimeForDisplay(getArrivalTimeMS(currentTimeMS,
				travelTimeMinutes),
				context));
	}

	public long getArrivalTimeMS(long currentTimeMS, int travelTimeMins) {
		final long fixCurrentTimeMS = currentTimeMS < 1 ? System
				.currentTimeMillis() : currentTimeMS;
		final long arrivalTime = fixCurrentTimeMS
				+ (travelTimeMins * Constants.SECONDS_PER_MIN * Constants.MS_PER_SECOND);

		return arrivalTime;
	}

	public String getFormattedTimeForDisplay(long timeInMS, Context context) {
		java.text.DateFormat dateformat = android.text.format.DateFormat
				.getTimeFormat(context);
		return (dateformat.format(new Date(timeInMS)));
	}

	public RouteCard setRouteIndex(int routeIndex) {
		this.routeIndex = routeIndex;
		return this;
	}
	
	public boolean isYouAreHereCard() {
		return this.youAreHere;
	}

	/* If needed by others move to a Utility class */

	@Override
	public void onClick(View v) {
		if (null == this.currentRoute) {
			if (this.routeViewType == RouteViewType.ADD_HOME_WORK) {
				if (this.location.getLocationId() == -1) {
					if (this.location.getLocationType() == LocationType.HOME
							.getValue()) {
						LocationPickerActivity
								.launchForCreateHome(getContext());
					} else if (this.location.getLocationType() == LocationType.WORK
							.getValue()) {
						LocationPickerActivity
								.launchForCreateWork(getContext());
					}
				}
			}
		} else {
			if (!this.youAreHere) {
				selected(getContext());
			} else {
				Toast.makeText(getContext(),
						R.string.you_are_here,
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	public boolean selected(Context context) {
		MainMapActivity.startActivity(context, this.location, this.currentRoute,
				this.routeIndex);
		return false;
	}

	protected void bindRouteQuality(final TextView eta, RouteQuality quality) {
		int color = 0;
		switch (quality) {

			case Moderate:
				color = getContext().getResources()
						.getColor(R.color.route_flow_yellow);
				break;
			case Heavy:
			case StopAndGo:
				color = getContext().getResources()
						.getColor(R.color.route_flow_red);
				break;
			case Closed:
				color = getContext().getResources()
						.getColor(R.color.route_flow_dark_red);
				break;
			case FreeFlow:
			default:
				color = getContext().getResources()
						.getColor(R.color.route_flow_green);
				break;
		}

		eta.setTextColor(color);
	}
}
