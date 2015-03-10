/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.util;

import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.content.Context;

import com.inrix.sdk.utils.InrixDateUtils;

/** Contains help functions to work with date and time. */
public class DateUtils {
	// TODO: Use InrixDateUtils from SDK.

	/**
	 * Retrieves time string depending on system settings.
	 * 
	 * @param timeInMS
	 *            Time in ms to format.
	 * 
	 * @param context
	 *            Valid application context.
	 * 
	 * @return Time string depending on system settings.
	 */
	public static String getFormattedTimeForDisplay(long timeInMS,
			Context context) {
		java.text.DateFormat dateformat = android.text.format.DateFormat
				.getTimeFormat(context);
		return dateformat.format(new Date(timeInMS));
	}

	/**
	 * Parse time string formatted in INRIX format
	 * 
	 * @param timeString
	 *            - INRIX formatted time string
	 * @return - parsed Date
	 */
	public static Date parseInrixTimeString(String timeString) {
		Date result = new Date();

		try {
			result = InrixDateUtils.getDateFromString(timeString);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Gets the current time in UTC and in milliseconds.
	 * 
	 * @return Current UTC time in milliseconds.
	 */
	public static final long getCurrentTimeUtcMillis() {
		// TODO: Move to SDK.
		return GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC")).getTime().getTime();
	}
}
