/**
 * Copyright (c) 2013-2015 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.inrix.sample.BusProvider;
import com.inrix.sample.IncidentsReceivedEvent;
import com.inrix.sample.R;
import com.inrix.sample.activity.IncidentDetailsActivity;
import com.inrix.sample.activity.SimpleView;
import com.inrix.sdk.model.Incident;
import com.squareup.otto.Subscribe;

import java.util.List;

public class IncidentListFragment extends Fragment {

    /**
     * A custom array adapter that shows a {@link SimpleView} containing details
     * about the incident
     */
    private static class CustomArrayAdapter extends ArrayAdapter<Incident> {

        /**
         * @param incidents An array containing the incidents to be displayed
         */
        public CustomArrayAdapter(Context context, Incident[] incidents) {
            super(context, R.layout.incidents_list_view_item, R.id.incident_description, incidents);
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
                featureView.setTitle(incident.getShortDescription());
            }

            return featureView;
        }
    }

    /**
     * @param list list
     */
    public void setIncidents(List<Incident> list) {
        final Incident incidentArray[];

        if (list == null) {
            incidentArray = new Incident[0];
        } else {
            incidentArray = list.toArray(new Incident[list.size()]);
        }

        CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(getActivity(), incidentArray);
        ListView listview = (ListView) getView().findViewById(R.id.incident_list);
        listview.setAdapter(arrayAdapter);
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {
                IncidentListFragment.this.getActivity().startActivity(
                        IncidentDetailsActivity.generateIncidentDetailsActivity(getActivity(), incidentArray[position]));
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BusProvider.getBus().register(this);
    }

    @Override
    public void onDetach() {
        BusProvider.getBus().unregister(this);
        super.onDetach();
    }

    @Subscribe
    public void onIncidentsReceived(IncidentsReceivedEvent incidentsEvent) {
        setIncidents(incidentsEvent.getIncidents());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_incident_list, container, false);
    }
}
