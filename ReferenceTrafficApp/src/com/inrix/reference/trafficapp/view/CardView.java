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
import android.widget.TextView;

import com.inrix.reference.trafficapp.R;
import com.squareup.picasso.Picasso;

/**
 * Base card control.
 */
public class CardView extends ShadowLayout {
	private TextView title;
	private TextView footer;
	private ImageView thumbnail;
	private ImageView icon;

	/**
	 * Initializes a new instance of the {@link CardView} class.
	 * 
	 * @param context
	 *            Current context.
	 * @param attrs
	 *            Style attributes.
	 * @param defStyle
	 *            Default style id.
	 */
	public CardView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);

		inflate(getContext(), R.layout.card_layout, this);

		this.title = (TextView) this.findViewById(R.id.title);
		this.footer = (TextView) this.findViewById(R.id.subtitle);
		this.thumbnail = (ImageView) this.findViewById(R.id.thumbnail);
		this.icon = (ImageView) this.findViewById(R.id.icon);
	}

	/**
	 * Initializes a new instance of the {@link CardView} class.
	 * 
	 * @param context
	 *            Current context.
	 * @param attrs
	 *            Style attributes.
	 */
	public CardView(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * Initializes a new instance of the {@link CardView} class.
	 * 
	 * @param context
	 *            Current context.
	 */
	public CardView(final Context context) {
		this(context, null, 0);
	}

	/**
	 * Sets the thumbnail drawable resource id.
	 * 
	 * @param resourceId
	 *            Drawable resource id.
	 */
	public final void setThumbnail(final int resourceId) {
		this.thumbnail.setImageResource(resourceId);
	}

	/**
	 * Sets thumbnail image resource.
	 * Will use {@link Picasso} to download the image from specified resource.
	 * 
	 * @param url
	 *            Resource url.
	 */
	public final void setThumbnail(final String url) {
		Picasso.with(this.getContext()).load(url).into(thumbnail);
	}

	/**
	 * Sets the value indicating whether the thumbnail should be visible.
	 * 
	 * @param show
	 *            True to display thumbnail icon; otherwise false.
	 */
	public final void showThumbnail(final boolean show) {
		this.thumbnail.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	/**
	 * Sets the title text.
	 * 
	 * @param text
	 *            Title text.
	 */
	public final void setTitle(final CharSequence text) {
		this.title.setText(text);
	}

	/**
	 * Sets subtitle text.
	 * 
	 * @param text
	 *            Subtitle text.
	 */
	public final void setSubtitle(final CharSequence text) {
		this.footer.setText(text);
	}

	/**
	 * Sets the drawable resource id for the icon.
	 * 
	 * @param resourceId
	 *            Icon drawable resource id.
	 */
	public final void setIcon(final int resourceId) {
		this.icon.setImageResource(resourceId);
	}

	/**
	 * Sets the value indicating whether icon should be displayed.
	 * 
	 * @param show
	 *            True to show icon; otherwise false.
	 */
	public final void showIcon(final boolean show) {
		this.icon.setVisibility(show ? View.VISIBLE : View.GONE);
	}
}
