/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.text.TextUtils;

/**
 * Modify these values if you need to test something locally. During the ANT
 * build these values will be overridden by values specified in build.properties
 * file
 */
public class Build {

	public static final String TIMEBOMB = "";

	/** Date of Beta expiration */
	private static Calendar timebombDate = new GregorianCalendar();

	private static String timeBombFormat = "yyyy-MM-dd";

	static {
		if (!TextUtils.isEmpty(TIMEBOMB)) {
			timebombDate.setTimeInMillis(parseTimeToMs(TIMEBOMB,
					timeBombFormat));
		}
	}

	/**
	 * Returns whether Timebomb is expired
	 * 
	 * @return true if Timebomb is expired
	 */
	public static boolean hasTimebombExpired() {
		GregorianCalendar now = new GregorianCalendar();
		if (TextUtils.isEmpty(Build.TIMEBOMB)) {
			return false;
		}
		return now.after(timebombDate);
	}

	/***
	 * Creates the time in milliseconds from specified string format. <br/>
	 * 
	 * @param pTimeString
	 * <br/>
	 *            the input time string in UTC/GMT which will be parsed
	 * @param format
	 * <br/>
	 *            the format of timeString, such as
	 *            "yyyy-MM-dd'T'HH:mm:ss.SSSZ". Use CS_DATE_FORMAT for values
	 *            returned by Connected Services
	 * @return time in milliseconds. Returns -1 on failure
	 */
	public static long parseTimeToMs(String timeString, String format) {
		long timeMs = -1;

		SimpleDateFormat dateformat = (SimpleDateFormat) SimpleDateFormat
				.getDateTimeInstance();
		dateformat.applyPattern(format);

		TimeZone tz = TimeZone.getTimeZone("UTC");
		dateformat.setTimeZone(tz);
		Date parsedDate = null;
		try {
			parsedDate = dateformat.parse(timeString);
			timeMs = parsedDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timeMs;
	}
}
