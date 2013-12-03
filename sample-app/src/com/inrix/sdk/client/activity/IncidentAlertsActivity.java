package com.inrix.sdk.client.activity;

import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.inrix.sdk.AlertsManager;
import com.inrix.sdk.AlertsManager.IFilter;
import com.inrix.sdk.AlertsManager.IIncidentsAlertListener;
import com.inrix.sdk.AlertsManager.IncidentAlertOptions;
import com.inrix.sdk.Error;
import com.inrix.sdk.IncidentAlert;
import com.inrix.sdk.Inrix;
import com.inrix.sdk.client.R;
import com.inrix.sdk.model.Incident;

public class IncidentAlertsActivity extends FragmentActivity implements
		IIncidentsAlertListener {
	TextView timestamp;
	TextView status;
	IncidentAlert alert;
	DialogFragment dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alerts);
		Inrix.initialize(this);
		this.timestamp = (TextView) findViewById(R.id.timestamp);
		this.status = (TextView) findViewById(R.id.status);
		dialog = new CustomDialogFragment();
		// Clear the Incident List
		setIncidentList( null );
		getSupportFragmentManager().beginTransaction()
				.add(dialog, "").commit();
	}

	public static class CustomDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final ProgressDialog dialog = new ProgressDialog(getActivity());
			dialog.setTitle("Loading alerts");
			dialog.setMessage("Please wait");
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			setCancelable(false);
			return dialog;
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		AlertsManager alertManager = new AlertsManager();
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
		}
	}

	@Override
	public void onResult(List<Incident> data) {
		getSupportFragmentManager().beginTransaction().remove(dialog).commit();
		setIncidentList(data);
		Date date = new Date(System.currentTimeMillis());

		this.timestamp.setText("Last update: "+date.toString());
	}

	@Override
	public void onError(Error error) {
		getSupportFragmentManager().beginTransaction().remove(dialog).commit();
		setIncidentList(null);
	}
	
	private static class CustomArrayAdapter extends ArrayAdapter<Incident> {

		/**
		 * @param demos
		 *            An array containing the incidents to be displayed
		 */
		public CustomArrayAdapter(Context context, Incident[] incidents) {
			super(context, R.layout.incidents_list_view_item,
					R.id.incident_description, incidents);
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
				featureView.setTitle("No Incidents");
			} else {
				featureView.setTitle(incident.getShortDescription().getValue());
			}

			return featureView;
		}
	}
	
	/**
	 * 
	 * @param incident list
	 */
	private void setIncidentList(List<Incident> list) {
		Incident incidentArray[];

		if (list == null) {
			incidentArray = new Incident[1];
			incidentArray[0] = new Incident();
			findViewById(R.id.incidents_list).setVisibility(View.GONE);
			this.status.setVisibility(View.GONE);
		} else if(list.isEmpty()){
			incidentArray = list.toArray(new Incident[list.size()]);
			findViewById(R.id.incidents_list).setVisibility(View.GONE);
			this.status.setVisibility(View.VISIBLE);
			this.status.setText("No incidents within this radius");
		} else {
			findViewById(R.id.incidents_list).setVisibility(View.VISIBLE);
			this.status.setVisibility(View.GONE);
			incidentArray = list.toArray(new Incident[list.size()]);
		}

		CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(this, incidentArray);
		((ListView)findViewById(R.id.incidents_list)).setAdapter(arrayAdapter);
	}


}
