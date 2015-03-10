/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.view;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/** Frame layout to detect touch events. Wrapper to detect touch events on map. */
public class TouchableFrameLayout extends FrameLayout {

	/** Flag to show whether user touched display with two fingers. */
	private boolean isPinchZoom;

	/** Array of listeners to get notifications. */
	private ArrayList<OnPanListener> panListeners = new ArrayList<OnPanListener>();

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	/**
	 * Creates new instance of the class.
	 * 
	 * @param context
	 *            Valid context.
	 */
	public TouchableFrameLayout(Context context) {
		super(context);
	}

	/**
	 * Creates new instance of the class.
	 * 
	 * @param context
	 *            Valid context.
	 * @param attrs
	 *            Layout attributes.
	 */
	public TouchableFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Creates new instance of the class.
	 * 
	 * @param context
	 *            Valid context.
	 * @param attrs
	 *            Layout attributes.
	 * @param defStyle
	 *            Default style.
	 */
	public TouchableFrameLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isPinchZoom = false;
				break;
			case MotionEvent.ACTION_POINTER_2_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
				isPinchZoom = true;
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				if (!isPinchZoom) {
					reportPan();
				}
				break;
			default:
				break;
		}
		return super.dispatchTouchEvent(ev);
	}

	/** Notifies all listener. */
	private void reportPan() {
		for (OnPanListener callback : this.panListeners) {
			callback.onPan();
		}
	}

	/**
	 * Adds the map pan listener.
	 * 
	 * @param listener
	 *            The listener to add.
	 */
	public final void addPanListener(final OnPanListener listener) {
		if (listener != null && !this.panListeners.contains(listener)) {
			this.panListeners.add(listener);
		}
	}

	/**
	 * Removes the map pan listener.
	 * 
	 * @param listener
	 *            The listener to remove.
	 */
	public final void removeChangeListener(final OnPanListener listener) {
		this.panListeners.remove(listener);
	}

	/** Touch action callback contract. */
	public interface OnPanListener {

		/** Called when view is panned. */
		public void onPan();
	}
}
