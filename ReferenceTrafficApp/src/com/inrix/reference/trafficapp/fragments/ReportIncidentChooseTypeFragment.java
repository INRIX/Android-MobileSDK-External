/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow.LayoutParams;

import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.model.GeneratedIncidentTypeModel;
import com.inrix.reference.trafficapp.view.RobotoTextView;
import com.inrix.sdk.IncidentsManager;

/** Displays incident types to choose for report. */
public class ReportIncidentChooseTypeFragment extends Fragment {

	private final int MAX_INCIDENT_TYPES = 5;

	/** List of incidents to choose for report. */
	private List<GeneratedIncidentTypeModel> incidents;

	/** Listener to get callback when user chooses incident type. */
	private OnIncidentTypeChosen onIncidentTypeChosenlistener;

	/** Listener to get callback when user chooses incident type. */
	private OnDismissListener onDismissListener;

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater
				.inflate(R.layout.fragment_report_incident_choose_type,
						container,
						false);

		if (this.incidents == null) {
			setupIncidents();
		}

		if (Configuration.ORIENTATION_LANDSCAPE == getResources()
				.getConfiguration().orientation) {
			initLandscapeLayout(rootView);
		} else {
			initPortraitLayout(rootView);
		}

		View dismissButton = rootView.findViewById(R.id.dismiss_btn);
		dismissButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (onDismissListener != null) {
					onDismissListener.onDismiss();
				}
			}
		});

		return rootView;
	}

	/**
	 * Prepares fragment layout for landscape orientation.
	 * 
	 * @param view
	 *            Root fragment view.
	 */
	private void initLandscapeLayout(View view) {
		LinearLayout firstContainerRow = (LinearLayout) view
				.findViewById(R.id.report_incident_row_first);
		for (int i = 0; i < MAX_INCIDENT_TYPES; i++) {
			addIncidentType(firstContainerRow, this.incidents.get(i), i);
		}

		LinearLayout secondContainerRow = (LinearLayout) view
				.findViewById(R.id.report_incident_row_second);
		secondContainerRow.setVisibility(View.GONE);

		LinearLayout thirdContainerRow = (LinearLayout) view
				.findViewById(R.id.report_incident_row_third);
		thirdContainerRow.setVisibility(View.GONE);
	}

	/**
	 * Prepares fragment layout for portrait orientation.
	 * 
	 * @param view
	 *            Root fragment view.
	 */
	private void initPortraitLayout(View view) {
		LinearLayout firstContainerRow = (LinearLayout) view
				.findViewById(R.id.report_incident_row_first);
		for (int i = 0; i < 2; i++) {
			addIncidentType(firstContainerRow, this.incidents.get(i), i);
		}

		LinearLayout secondContainerRow = (LinearLayout) view
				.findViewById(R.id.report_incident_row_second);
		for (int i = 2; i < 4; i++) {
			addIncidentType(secondContainerRow, this.incidents.get(i), i);
		}

		LinearLayout thirdContainerRow = (LinearLayout) view
				.findViewById(R.id.report_incident_row_third);
		addIncidentType(thirdContainerRow, this.incidents.get(4), 4);

	}

	/**
	 * Adds incident type to specific layout.
	 * 
	 * @param parent
	 *            Layout to add incident view.
	 * @param incident
	 *            Incident type to add.
	 * @param tag
	 *            View tag (index of the incident in the array list).
	 */
	private void addIncidentType(LinearLayout parent,
			GeneratedIncidentTypeModel incident,
			Integer tag) {
		LayoutInflater layoutInflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RobotoTextView textView = (RobotoTextView) layoutInflater
				.inflate(R.layout.report_incident_type_view, null);
		textView.setText(incident.getName());
		textView.setTag(tag);

		Drawable incidentDrawable = incident.getSmallDrawable();

		incidentDrawable.setBounds(0,
				0,
				incidentDrawable.getIntrinsicWidth(),
				incidentDrawable.getIntrinsicHeight());

		LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
		textView.setLayoutParams(params);

		textView.setCompoundDrawables(null, incidentDrawable, null, null);

		textView.setClickable(true);
		textView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		textView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Integer index = (Integer) v.getTag();
				if (onIncidentTypeChosenlistener != null) {
					onIncidentTypeChosenlistener.onIncidentTypeChosen(incidents
							.get(index));
				}
			}
		});
		parent.addView(textView);
	}

	/** Initializes incidents array list. */
	private void setupIncidents() {
		this.incidents = new ArrayList<GeneratedIncidentTypeModel>();
		this.incidents
				.add(new GeneratedIncidentTypeModel(IncidentsManager.INCIDENT_TYPE_ACCIDENT,
						R.string.incident_type_accident,
						R.drawable.report_accident,
						R.drawable.report_accident_side));
		this.incidents
				.add(new GeneratedIncidentTypeModel(IncidentsManager.INCIDENT_TYPE_HAZARD,
						R.string.incident_type_hazard,
						R.drawable.report_hazard,
						R.drawable.report_hazard_side));

		this.incidents
				.add(new GeneratedIncidentTypeModel(IncidentsManager.INCIDENT_TYPE_POLICE,
						R.string.incident_type_police,
						R.drawable.report_police,
						R.drawable.report_police_side));

		this.incidents
				.add(new GeneratedIncidentTypeModel(IncidentsManager.INCIDENT_TYPE_CONSTRUCTION,
						R.string.incident_type_construction,
						R.drawable.report_construction,
						R.drawable.report_construction_side));

		this.incidents
				.add(new GeneratedIncidentTypeModel(IncidentsManager.INCIDENT_TYPE_FLOW,
						R.string.incident_type_wrond_traffic_color,
						R.drawable.report_wrong_traffic,
						R.drawable.report_wrong_traffic));

	}

	/**
	 * Sets listener to get callback when user chooses incident type.
	 * 
	 * @param listener
	 *            Listener to set.
	 */
	public void setOnIncidentTypeChosenListener(OnIncidentTypeChosen listener) {
		this.onIncidentTypeChosenlistener = listener;
	}

	/**
	 * Sets listener to get callback when user presses dismiss.
	 * 
	 * @param listener
	 *            Listener to set.
	 */
	public void setOnDismissListener(OnDismissListener listener) {
		this.onDismissListener = listener;
	}

	/** Interface to get notification when incident type is chosen. */
	public interface OnIncidentTypeChosen {

		/**
		 * Will be called when user chooses incident type for report.
		 * 
		 * @param incidentType
		 *            Chosen incident type.
		 */
		public void onIncidentTypeChosen(GeneratedIncidentTypeModel incidentType);
	}

	/** Interface to get notification when user presses dismiss button. */
	public interface OnDismissListener {

		/** Will be called when user presses dismiss button. */
		public void onDismiss();
	}
}
