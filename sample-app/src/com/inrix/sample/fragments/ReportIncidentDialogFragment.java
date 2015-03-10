package com.inrix.sample.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;

import com.google.android.gms.maps.model.LatLng;
import com.inrix.sample.R;
import com.inrix.sdk.IncidentsManager;
import com.inrix.sdk.model.GeoPoint;

/**
 * Dialog fragment to report incidents.
 */
public class ReportIncidentDialogFragment extends DialogFragment implements OnCheckedChangeListener, OnClickListener {
	public interface IReportIncidentListener {
		public void reportIncident(final GeoPoint location, final int type, final IncidentsManager.RoadSide sideOfRoad);
	}

	private IReportIncidentListener listener;
	private RadioButton reportAccidentButton;
	private RadioButton reportConstructionButton;
	private RadioButton reportPoliceButton;
	private RadioButton reportHazardButton;
	private RadioButton thisSideButton;
	private Button reportButton;

	/**
	 * Initializes a new instance of the {@link ReportIncidentDialogFragment} class.
	 */
	public ReportIncidentDialogFragment() {
	}

	/**
	 * Creates a new instance of the {@link ReportIncidentDialogFragment}.
	 * 
	 * @param coords
	 *            Current location.
	 * @return An instance of {@link ReportIncidentDialogFragment}.
	 */
	public static final ReportIncidentDialogFragment newInstance(final LatLng coords) {
		final Bundle arguments = new Bundle();
		arguments.putDouble("latitude", coords.latitude);
		arguments.putDouble("longitude", coords.longitude);

		final ReportIncidentDialogFragment fragment = new ReportIncidentDialogFragment();
		fragment.setArguments(arguments);

		return fragment;
	}

	/**
	 * Attaches callback to this dialog.
	 * 
	 * @param listener
	 *            Dialog callback.
	 * @return Current instance.
	 */
	public final ReportIncidentDialogFragment with(IReportIncidentListener listener) {
		this.listener = listener;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.DialogFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setStyle(STYLE_NO_TITLE, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View content = inflater.inflate(R.layout.fragment_report_incident, container, false);

		this.reportAccidentButton = (RadioButton) content.findViewById(R.id.report_accident_button);
		this.reportAccidentButton.setOnCheckedChangeListener(this);

		this.reportConstructionButton = (RadioButton) content.findViewById(R.id.report_construction_button);
		this.reportConstructionButton.setOnCheckedChangeListener(this);

		this.reportHazardButton = (RadioButton) content.findViewById(R.id.report_hazard_button);
		this.reportHazardButton.setOnCheckedChangeListener(this);

		this.reportPoliceButton = (RadioButton) content.findViewById(R.id.report_police_button);
		this.reportPoliceButton.setOnCheckedChangeListener(this);

		this.reportButton = (Button) content.findViewById(R.id.report_incident_button);
		this.reportButton.setOnClickListener(this);

		this.thisSideButton = (RadioButton) content.findViewById(R.id.side_of_road_this_side_radio);

		this.clearChecked();
		this.reportConstructionButton.setChecked(true);

		return content;
	}

	private void clearChecked() {
		this.reportAccidentButton.setChecked(false);
		this.reportConstructionButton.setChecked(false);
		this.reportHazardButton.setChecked(false);
		this.reportPoliceButton.setChecked(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton, boolean)
	 */
	@Override
	public void onCheckedChanged(CompoundButton button, boolean checked) {
		if (checked) {
			this.clearChecked();
			button.setChecked(checked);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {
		int type = 0;
		if (this.reportAccidentButton.isChecked()) {
			type = IncidentsManager.INCIDENT_TYPE_ACCIDENT;
		} else if (this.reportConstructionButton.isChecked()) {
			type = IncidentsManager.INCIDENT_TYPE_CONSTRUCTION;
		} else if (this.reportHazardButton.isChecked()) {
			type = IncidentsManager.INCIDENT_TYPE_HAZARD;
		} else if (this.reportPoliceButton.isChecked()) {
			type = IncidentsManager.INCIDENT_TYPE_POLICE;
		}

		final IncidentsManager.RoadSide side = this.thisSideButton.isChecked() ?
				IncidentsManager.RoadSide.MY_SIDE : IncidentsManager.RoadSide.OTHER_SIDE;

		final double latitude = this.getArguments().getDouble("latitude");
		final double longitude = this.getArguments().getDouble("longitude");
		final GeoPoint location = new GeoPoint(latitude, longitude);
		
		this.listener.reportIncident(location, type, side);
		this.dismiss();
	}
}
