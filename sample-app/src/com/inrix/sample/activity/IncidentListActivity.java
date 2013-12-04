package com.inrix.sample.activity;

import java.util.List;

import com.inrix.sample.ClientFactory;
import com.inrix.sample.R;
import com.inrix.sample.interfaces.IClient;
import com.inrix.sdk.Error;
import com.inrix.sdk.IncidentsManager.IIncidentsResponseListener;
import com.inrix.sdk.IncidentsManager.IncidentRadiusOptions;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Incident;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class IncidentListActivity extends FragmentActivity {

	private final GeoPoint SEATTLE_POSITION = new GeoPoint(47.614496,
			-122.328758);

	private final int INCIDENT_RADIUS = 5;

	// Interface to the Mobile Data
	private IClient client;

	// Loading Dialog
	ProgressDialog pd;

	/**
	 * A custom array adapter that shows a {@link SimpleView} containing
	 * details about the incident
	 */
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

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_incident_list);

		// Initialize INRIX
		initializeINRIX();

		// Clear the Incident List
		setIncidentList( null );

		pd = new ProgressDialog(this);
		pd.setMessage("loading");
		pd.show();

		// Get the Incidents for the selected city and radius
		IncidentRadiusOptions params = new IncidentRadiusOptions(
				SEATTLE_POSITION, INCIDENT_RADIUS);
		this.client.getIncidentManager().getIncidentsInRadius(
				new IIncidentsResponseListener() {

					@Override
					public void onResult(List<Incident> data) {
						pd.dismiss();
						setIncidentList(data);
					}

					@Override
					public void onError(Error error) {
						pd.dismiss();
						setIncidentList(null);
					}

				}, params);

	}
	
	/**
	 * Initialize the INRIX SDK
	 */
	private void initializeINRIX() {
		this.client = ClientFactory.getInstance().getClient();
		this.client.connect(getApplicationContext());
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
		} else {
			incidentArray = list.toArray(new Incident[list.size()]);
		}

		CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(this, incidentArray);
		((ListView)findViewById(R.id.incident_list)).setAdapter(arrayAdapter);
	}

}
