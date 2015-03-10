/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.TrafficApp;

public class AboutTitleCard extends ShadowLayout {
	
	/**
	 * Creates new instance of the class.
	 * 
	 * @param context
	 *            Valid context.
	 */
	public AboutTitleCard(Context context) {
		super(context);
		init();
	}

	/**
	 * Creates new instance of the class.
	 * 
	 * @param context
	 *            Valid context.
	 * @param attrs
	 *            Field attributes from layout.
	 */
	public AboutTitleCard(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * Creates new instance of the class.
	 * 
	 * @param context
	 *            Valid context.
	 * @param attrs
	 *            Field attributes from layout file.
	 * @param defStyle
	 *            Default field style.
	 */
	public AboutTitleCard(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();

	}
	
	/** Initializes layout for the field. */
	private void init() {
		inflate(getContext(), R.layout.about_title_card, this);
		TextView versionTextView = (TextView) findViewById(R.id.txt_version);
		String version = getContext().getString(R.string.version_format, TrafficApp.getVersion());
		versionTextView.setText(version);
	}
}
