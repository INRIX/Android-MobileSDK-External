/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.incidents;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.util.Constants;
import com.inrix.reference.trafficapp.view.IncidentCard;
import com.inrix.sdk.IncidentUtils;
import com.inrix.sdk.IncidentsManager;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Incident;
import com.inrix.sdk.utils.GeoUtils;
import com.inrix.sdk.utils.UserPreferences;
import com.inrix.sdk.utils.UserPreferences.Unit;

public final class IncidentDisplayUtils {

	private static final DecimalFormat distanceFormater = new DecimalFormat(Constants.DISTANCE_FORMAT);

	public static final MarkerOptions getMarker(final Context context, final Incident incident) {
		final IconGenerator generator = new IconGenerator(context);
		int iconResource = getIcon(incident);
		generator.setBackground(context.getResources().getDrawable(iconResource));
		generator.setTextAppearance(context, R.style.MapBubbleTextStyle);

		final LatLng markerLocation = new LatLng(incident.getLatitude(), incident.getLongitude());
		final BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(generator.makeIcon(""));
		return new MarkerOptions().position(markerLocation).icon(descriptor);
	}

	public static final int getIcon(final Incident incident) {
		int iconID = IncidentCard.HIDE_ICON;

		if (IncidentUtils.isRoadClosure(incident.getEventCode())) {
			iconID = R.drawable.map_icon_pin_road_closure_selected;
		} else {
			switch (incident.getType()) {
			case IncidentsManager.INCIDENT_TYPE_CONSTRUCTION:
				iconID = R.drawable.map_icon_pin_construction_selected;
				break;
			case IncidentsManager.INCIDENT_TYPE_EVENT:
				iconID = R.drawable.map_icon_pin_event_selected;
				break;
			case IncidentsManager.INCIDENT_TYPE_FLOW:
				iconID = R.drawable.map_icon_pin_congestion_selected;
				break;
			case IncidentsManager.INCIDENT_TYPE_ACCIDENT:
				iconID = R.drawable.map_icon_pin_accident_selected;
				break;
			case IncidentsManager.INCIDENT_TYPE_POLICE:
				iconID = R.drawable.map_icon_pin_police_selected;
				break;
			case IncidentsManager.INCIDENT_TYPE_HAZARD:
				iconID = R.drawable.map_icon_pin_hazard_selected;
				break;
			}
		}

		return iconID;
	}

	public static final int getColor(final Context context, final Incident incident) {
		Resources resources = context.getResources();
		int color;
		if (IncidentUtils.isRoadClosure(incident.getEventCode())) {
			color = resources.getColor(R.color.incident_accident_color);
		} else {
			switch (incident.getType()) {
			case IncidentsManager.INCIDENT_TYPE_CONSTRUCTION:
			case IncidentsManager.INCIDENT_TYPE_EVENT:
				color = resources.getColor(R.color.incident_construction_color);
				break;
			case IncidentsManager.INCIDENT_TYPE_POLICE:
				color = resources.getColor(R.color.incident_police_color);
				break;
			case IncidentsManager.INCIDENT_TYPE_FLOW:
			case IncidentsManager.INCIDENT_TYPE_HAZARD:
			case IncidentsManager.INCIDENT_TYPE_ACCIDENT:
			default:
				color = resources.getColor(R.color.incident_accident_color);
			}
		}
		return color;
	}

	public static final String getTitle(final Context context, final Incident incident) {
		if (IncidentUtils.isRoadClosure(incident.getEventCode())) {
			return context.getResources().getString(R.string.incident_type_road_closure);
		}

		String result = "";
		switch (incident.getType()) {
		case IncidentsManager.INCIDENT_TYPE_ACCIDENT:
			result = context.getResources().getString(R.string.accident);
			break;
		case IncidentsManager.INCIDENT_TYPE_CONSTRUCTION:
			result = context.getResources().getString(R.string.construction);
			break;
		case IncidentsManager.INCIDENT_TYPE_EVENT:
			result = context.getResources().getString(R.string.event);
			break;
		case IncidentsManager.INCIDENT_TYPE_FLOW:
			result = context.getResources().getString(R.string.congestion);
			break;
		case IncidentsManager.INCIDENT_TYPE_HAZARD:
			result = context.getResources().getString(R.string.hazard);
			break;
		case IncidentsManager.INCIDENT_TYPE_POLICE:
			result = context.getResources().getString(R.string.police);
			break;
		default:
			break;
		}

		return result;
	}

	public static final String getDescription(final Context context, final Incident incident) {
		String result = "";
		if (incident.getFullDescription() != null) {
			result = incident.getFullDescription();
		}

		if (TextUtils.isEmpty(result) && incident.getShortDescription() != null) {
			result = incident.getShortDescription();
		}

		if (TextUtils.isEmpty(result)) {
			result = getTitle(context, incident);
		}

		return result;
	}

	public static final String getDistanceAsString(final Incident incident, final Location start) {
		double distance = 0;
		if (start != null) {
			distance = incident.getDistance(new GeoPoint(start.getLatitude(), start.getLongitude()));
		}

		if (UserPreferences.getSettingUnits() == Unit.METERS) {
            //convert to miles
			distance = distance / 1000 / GeoUtils.KM_MILE_CONVERSION_FACTOR;
		}

		incident.setDistanceKM(distance * GeoUtils.KM_MILE_CONVERSION_FACTOR);

		distanceFormater.setMinimumFractionDigits(1);
		return distanceFormater.format(distance);
	}

	public static final Spannable getDistanceAsStringFormatted(final Context context, final Incident incident, final Location start) {
		String formattedDistance = getDistanceAsString(incident, start);
		final String template = context.getResources().getString(R.string.incident_details_distance_format);
		String resultString = String.format(template, formattedDistance);
		Spannable resultSpan = new SpannableString(resultString);
		int distancePosStart = resultString.indexOf(formattedDistance) + formattedDistance.length();
		resultSpan.setSpan(new RelativeSizeSpan(0.8f),
				distancePosStart,
				resultSpan.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		return resultSpan;
	}

	public static final boolean isTooFar(final Incident incident) {
		if (incident == null || Double.isNaN(incident.getDistanceKM())) {
			return false;
		}

		double distanceKm = incident.getDistanceKM();
		return distanceKm > Constants.MAX_DISTANCE_TO_CONFIRM_INCIDENT_KM;
	}

	public static final String getReportedTimeAsString(final Context context, final Incident incident) {
		long minutes = Long.MAX_VALUE;

		if (null != incident.getStartTime()) {
			minutes = (System.currentTimeMillis() - incident.getStartTime().getTime())
					/ Constants.MS_PER_SECOND / Constants.SECONDS_PER_MIN;
			
			// If incident will occur some time in the future, just mark it as such.
			if (minutes < 0) {
				return context.getString(R.string.incident_details_reported_time_upcoming);
			}
		} else {
			/*
			 * if the incident did not have start time which is not likely we
			 * report the incident occurred more than 60 mins ago
			 */
			minutes = Constants.MINS_PER_HR + 1;
		}

		return minutes > Constants.MINS_PER_HR ?
				context.getResources().getString(R.string.incident_details_reported_time_overflow) :
				String.valueOf(minutes);
	}

	public static final Spannable getReportedTimeAsStringFormatted(final Context context, final Incident incident) {
		final String timeString = getReportedTimeAsString(context, incident);

		if (timeString == null || timeString.equalsIgnoreCase(context.getString(R.string.incident_details_reported_time_upcoming))) {
			return new SpannableString(timeString);
		} else {
			final String str = String.format(context.getResources().getString(R.string.incident_details_reported_time_format), timeString);
			final Spannable span = new SpannableString(str);
			final int start = str.indexOf(timeString) + timeString.length();
			span.setSpan(new RelativeSizeSpan(0.8f),
					start,
					str.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			return span;
		}
	}
}
