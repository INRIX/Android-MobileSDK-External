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
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.inrix.reference.trafficapp.R;

/** Implements card for About Page. */
public class AboutCard extends ShadowLayout {

	/** Title text view. */
	private TextView title;

	/**
	 * Creates new instance of the class.
	 * 
	 * @param context
	 *            Valid context.
	 */
	public AboutCard(Context context) {
		super(context);
		init(null);
	}

	/**
	 * Creates new instance of the class.
	 * 
	 * @param context
	 *            Valid context.
	 * @param attrs
	 *            Field attributes from layout.
	 */
	public AboutCard(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
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
	public AboutCard(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);

	}

	/**
	 * Initializes layout for the field.
	 * 
	 * @param attrs
	 *            Attributes from layout file.
	 */
	private void init(AttributeSet attrs) {
		inflate(getContext(), R.layout.about_card, this);
		this.title = (TextView) findViewById(R.id.title);

		if (attrs != null) {
			TypedArray cardStyle = getContext().obtainStyledAttributes(attrs,
					R.styleable.AboutCard);

			int cardTextId = cardStyle
					.getResourceId(R.styleable.AboutCard_text, 0);

			if (cardTextId != 0) {
				this.title.setText(cardTextId);
			}

			cardStyle.recycle();
		}
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
}
