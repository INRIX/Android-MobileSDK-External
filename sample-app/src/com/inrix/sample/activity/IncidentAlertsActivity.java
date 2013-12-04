package com.inrix.sample.activity;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.inrix.sample.R;
import com.inrix.sdk.AlertsManager;
import com.inrix.sdk.AlertsManager.IFilter;
import com.inrix.sdk.AlertsManager.IIncidentsAlertListener;
import com.inrix.sdk.AlertsManager.IncidentAlertOptions;
import com.inrix.sdk.Error;
import com.inrix.sdk.IncidentAlert;
import com.inrix.sdk.Inrix;
import com.inrix.sdk.model.Incident;

public class IncidentAlertsActivity extends FragmentActivity implements
		IIncidentsAlertListener {
	TextView timestamp;
	TextView status;
	IncidentAlert alert;
	ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alerts);
		Inrix.initialize(this);
		this.timestamp = (TextView) findViewById(R.id.timestamp);
		this.progressBar = (ProgressBar)findViewById(R.id.progress_bar);
		// Clear the Incident List
		setIncidentList(null);
	}

	@Override
	protected void onStart() {
		super.onStart();
		AlertsManager alertManager = new AlertsManager();
		progressBar.setVisibility(View.VISIBLE);
		timestamp.setText("Loading...");
		alert = alertManager.createIncidentAlert(this,
				new IncidentAlertOptions(15, 20, new IFilter<Incident>() {

					@Override
					public boolean isItemAllowed(Incident item) {
						return true;
					}
				}));
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (this.alert != null) {
			this.alert.cancel();
			this.alert = null;
		}
	}

	@Override
	public void onResult(List<Incident> data) {
		getSupportFragmentManager().popBackStack();
		setIncidentList(data);
		Date date = new Date(System.currentTimeMillis());

		this.timestamp.setText("Last update: " + date.toString());
		progressBar.setVisibility(View.GONE);
	}

	@Override
	public void onError(Error error) {
		getSupportFragmentManager().popBackStack();
		this.timestamp.setText("Unable to retrieve data: "+error.toString());
		progressBar.setVisibility(View.GONE);
	}

	private static class CustomArrayAdapter extends ArrayAdapter<Incident> {

		/**
		 * @param demos
		 *            An array containing the incidents to be displayed
		 */
		public CustomArrayAdapter(Context context, Incident[] incidents) {
			super(context,
					R.layout.incidents_list_view_item,
					R.id.incident_description,
					incidents);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SimpleView featureView;
			if (convertView instanceof SimpleView) {
				featureView = (SimpleView) convertView;
			} else {
				featureView = new SimpleView(getContext());
			}

			Incident incident = getItem(position);

			if (incident.getShortDescription() == null) {
				featureView.setTitle("No Incidents withing this radius");
			} else {
				featureView.setTitle(incident.getShortDescription().getValue());
			}

			return featureView;
		}
	}

	/**
	 * 
	 * @param incident
	 *            list
	 */
	private void setIncidentList(List<Incident> list) {
		if (list == null) {
			return;
		}
		Incident incidentArray[];

		if (list.isEmpty()) {
			// add dummy incident. Adapter will handle that properly
			list.add(new Incident());
		}
		incidentArray = list.toArray(new Incident[list.size()]);
		CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(this,
				incidentArray);
		((ListView) findViewById(R.id.incidents_list)).setAdapter(arrayAdapter);
	}

}
