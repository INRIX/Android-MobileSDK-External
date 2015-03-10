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

import com.inrix.reference.trafficapp.incidents.IncidentDisplayUtils;
import com.inrix.sdk.model.Incident;

/**
 * Represents incidents card UI.
 */
public class IncidentCard extends CardView {
	public static final int HIDE_ICON = -1;

	private Incident incident;

	/**
	 * Initializes a new instance of the {@link IncidentCard} class.
	 * 
	 * @param context
	 *            Current context.
	 * @param attrs
	 *            Style attributes.
	 * @param defStyle
	 *            Default style id.
	 */
	public IncidentCard(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);

		this.showIcon(false);
	}

	/**
	 * Initializes a new instance of the {@link IncidentCard} class.
	 * 
	 * @param context
	 *            Current context.
	 * @param attrs
	 *            Style attributes.
	 */
	public IncidentCard(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * Initializes a new instance of the {@link IncidentCard} class.
	 * 
	 * @param context
	 *            Current context.
	 */
	public IncidentCard(Context context) {
		this(context, null, 0);
	}

	/**
	 * Sets the incident associated with this card.
	 * 
	 * @param incident
	 *            An instance of {@link Incident}.
	 */
	public final void setIncident(final Incident incident) {
		this.incident = incident;

		final int incidentResourceId = IncidentDisplayUtils.getIcon(this.incident);
		if (incidentResourceId == HIDE_ICON) {
			this.showThumbnail(false);
		} else {
			this.showThumbnail(true);
			this.setThumbnail(incidentResourceId);
		}
	}
}
