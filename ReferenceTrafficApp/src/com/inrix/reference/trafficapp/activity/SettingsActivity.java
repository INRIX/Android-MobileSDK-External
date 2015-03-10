/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.activity;

import android.app.Activity;
import android.os.Bundle;

import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.util.AndroidCompatUtils;

/**
 * Application settings activity.
 */
public class SettingsActivity extends Activity {
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		AndroidCompatUtils.fixWindowContentOverlay(this);
	}
}
