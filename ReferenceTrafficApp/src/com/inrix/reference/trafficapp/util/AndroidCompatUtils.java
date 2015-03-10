/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Utility class provides hacks for different Android versions
 * 
 * @author paveld
 * 
 */
public class AndroidCompatUtils {

	/**
	 * Fixes broken windowContentOverlay attribute in API 4.3. @see <a
	 * href="https://code.google.com/p/android/issues/detail?id=58280"
	 * >https://code.google.com/p/android/issues/detail?id=58280</a>
	 */
	@TargetApi(android.os.Build.VERSION_CODES.JELLY_BEAN_MR2)
	public static void fixWindowContentOverlay(Activity activity) {
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2) {
			// Get the content view
			View contentView = activity.findViewById(android.R.id.content);

			// Make sure it's a valid instance of a FrameLayout
			if (contentView instanceof FrameLayout) {
				TypedValue tv = new TypedValue();

				// Get the windowContentOverlay value of the current theme
				if (activity.getTheme()
						.resolveAttribute(android.R.attr.windowContentOverlay,
								tv,
								true)) {

					// If it's a valid resource, set it as the foreground
					// drawable
					// for the content view
					if (tv.resourceId != 0) {
						((FrameLayout) contentView).setForeground(activity
								.getResources().getDrawable(tv.resourceId));
					}
				}
			}
		}
	}
}
