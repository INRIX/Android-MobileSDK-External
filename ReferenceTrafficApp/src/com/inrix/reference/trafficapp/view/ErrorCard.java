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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inrix.reference.trafficapp.R;

/**
 * The Class ErrorCard, contains simple layout for contextual error in application
 */
public class ErrorCard extends RelativeLayout {
	private ImageView icon;
	private TextView message;
	private View root;

	public ErrorCard(Context context) {
		super(context);
		initialize();
	}

	/**
	 * Instantiates a new error card.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attributes
	 */
	public ErrorCard(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	/**
	 * Instantiates a new error card.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attributes
	 * @param style
	 *            the style
	 */
	public ErrorCard(Context context, AttributeSet attrs, int style) {
		super(context, attrs, style);
		initialize();
	}

	/**
	 * Show error card.
	 */
	public void show() {
		this.setVisibility(VISIBLE);
	}

	/**
	 * Hide error card.
	 */
	public void hide() {
		this.setVisibility(GONE);
	}

	/**
	 * Gets the error message control.
	 * 
	 * @return the error message control
	 */
	public TextView getErrorMessage() {
		return this.message;
	}

	/**
	 * Gets the error icon control.
	 * 
	 * @return the error icon
	 */
	public ImageView getErrorIcon() {
		return this.icon;
	}

	/**
	 * Sets the text message,
	 * 
	 * @param text
	 *            the new text
	 */
	public void setText(CharSequence text) {
		this.message.setText(text);
	}

	/**
	 * Sets the text.
	 * 
	 * @param resId
	 *            the new text
	 */
	public void setText(int resId) {
		this.message.setText(resId);
	}

	/**
	 * Sets the image resource.
	 * 
	 * @param resId
	 *            the new image
	 */
	public void setImage(int resId) {
		this.icon.setImageResource(resId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.View#setOnClickListener(android.view.View.OnClickListener)
	 */
	public void setOnClickListener(OnClickListener listener) {
		if (listener == null) {
			return;
		}

		this.root.setOnClickListener(listener);
	}

	/**
	 * Initialize.
	 */
	protected void initialize() {
		this.root = inflate(getContext(), R.layout.error_card, this);
		this.icon = (ImageView) this.root.findViewById(R.id.error_icon);
		this.message = (TextView) this.root.findViewById(R.id.error_message);
	}
}
