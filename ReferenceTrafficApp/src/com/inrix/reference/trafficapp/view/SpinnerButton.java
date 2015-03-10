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
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inrix.reference.trafficapp.R;

/** Button with rotating Progress Bar. */
public class SpinnerButton extends RelativeLayout {

	/** Button text. */
	private TextView title;

	/** Text field to display time. */
	private TextView timer;

	/** Progress Bar with time. */
	private View spinnerContainer;

	/**
	 * Creates new instance of the class.
	 * 
	 * @param context
	 *            Valid context.
	 */
	public SpinnerButton(Context context) {
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
	public SpinnerButton(Context context, AttributeSet attrs) {
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
	public SpinnerButton(Context context, AttributeSet attrs, int style) {
		super(context, attrs, style);
		init();
	}

	/**
	 * Initializes layout for the field.
	 */
	private void init() {
		inflate(getContext(), R.layout.spinner_button, this);
		this.title = (TextView) findViewById(R.id.title);
		this.title.setText(R.string.dismiss);
		this.timer = (TextView) findViewById(R.id.timer);
		this.spinnerContainer = findViewById(R.id.spinner_with_text);
	}

	/**
	 * Sets title value.
	 * 
	 * @param title
	 *            Title value.
	 */
	public void setTitle(String title) {
		this.title.setText(title);
	}

	/**
	 * Sets title value.
	 * 
	 * @param titleId
	 *            Title value resource id.
	 */
	public void setTitle(int titleId) {
		this.title.setText(titleId);
	}

	/**
	 * Sets time to display.
	 * 
	 * @param time
	 *            Time value in seconds.
	 */
	public void setTime(int time) {
		this.timer.setText(String.valueOf(time));
	}

	public void alignChildren(boolean align) {
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(title.getLayoutParams());
		if (align) {
			params.addRule(RelativeLayout.LEFT_OF, spinnerContainer.getId());
			params.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
		} else {
			params.addRule(RelativeLayout.LEFT_OF, 0);
			params.addRule(RelativeLayout.CENTER_IN_PARENT);
			params.addRule(RelativeLayout.CENTER_VERTICAL, 0);
		}
		title.setLayoutParams(params);
	}

	/**
	 * Sets whether Progress Bar must be shown or not.
	 * 
	 * @param show
	 *            True if Progress Bar must be shown, false otherwise.
	 */
	public void showProgressBar(boolean show) {
		this.spinnerContainer.setVisibility(show ? View.VISIBLE
				: View.GONE);
		if (!show) {
			alignChildren(false);
		}
	}
}
