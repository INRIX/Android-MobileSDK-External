/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.incidents;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.inrix.reference.trafficapp.util.Constants;
import com.inrix.reference.trafficapp.view.IncidentCard;
import com.inrix.sdk.model.Incident;

/**
 * Adapter class for the incidents list view
 * 
 */
public class IncidentsListAdapter extends BaseAdapter {

	private static SimpleDateFormat fmtOut = new SimpleDateFormat(Constants.INCIDENTS_TIME_DISPLAY_FORMAT,
			Locale.getDefault());
	/**
	 * local incidents list
	 */
	private List<Incident> incidents = new ArrayList<Incident>();

	private Context context;

	/**
	 * Initializes a new instance of the {@link IncidentsListAdapter}.
	 * 
	 * @param context
	 *            Current context.
	 */
	public IncidentsListAdapter(final Context context) {
		this.context = context;
	}

	/**
	 * Updates the data source for the adapter.
	 * 
	 * @param data
	 *            New data.
	 */
	public final void setData(final List<Incident> data) {
		this.incidents.clear();

		if (data == null) {
			notifyDataSetChanged();
			return;
		}
		this.incidents.addAll(data);
		notifyDataSetChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public final View getView(final int position,
			final View convertView,
			final ViewGroup parent) {
		IncidentCard card = null;

		if (convertView == null) {
			card = new IncidentCard(context);
		} else {
			card = (IncidentCard) convertView;
		}

		final Incident item = this.getItem(position);
		card.setTitle(IncidentDisplayUtils.getDescription(context, item));
		card.setSubtitle(fmtOut.format(item.getStartTime()));
		card.setIncident(item);
		return card;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getCount()
	 */
	@Override
	public int getCount() {
		return incidents.size();
	}

	@Override
	public Incident getItem(int position) {
		return incidents.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
}
