package com.inrix.sample.fragments;

import java.util.List;

import com.inrix.sample.BusProvider;
import com.inrix.sample.IncidentsReceivedEvent;
import com.inrix.sample.R;
import com.inrix.sample.activity.SimpleView;
import com.inrix.sdk.model.Incident;
import com.squareup.otto.Subscribe;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class IncidentListFragment extends Fragment {

	/**
	 * A custom array adapter that shows a {@link SimpleView} containing details
	 * about the incident
	 */
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
				featureView.setTitle("No Incidents");
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
	public void setIncidents(List<Incident> list) {
		Incident incidentArray[];

		if (list == null) {
			incidentArray = new Incident[1];
			incidentArray[0] = new Incident();
		} else {
			incidentArray = list.toArray(new Incident[list.size()]);
		}

		CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(getActivity(),
				incidentArray);
		((ListView) getView().findViewById(R.id.incident_list))
				.setAdapter(arrayAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_incident_list,
				container,
				false);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Register to revceive Bus broadcast events
		BusProvider.getBus().register(this);
	}

	@Override
	public void onDetach() {
		// Unregister from Bus
		BusProvider.getBus().unregister(this);
		super.onDetach();
	}

	@Subscribe
	public void onIncidentsReceived(IncidentsReceivedEvent incidentsEvent) {
		setIncidents(incidentsEvent.getIncidents());
	}
}
