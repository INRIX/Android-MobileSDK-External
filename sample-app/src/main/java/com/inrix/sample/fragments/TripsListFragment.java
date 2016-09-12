/**
 * Copyright (c) 2013-2016 INRIX, Inc.
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.inrix.sample.R;
import com.inrix.sample.activity.SimpleView;
import com.inrix.sample.activity.TripsActivity;
import com.inrix.sdk.Error;
import com.inrix.sdk.TripManager;
import com.inrix.sdk.model.SavedTrip;

import java.util.ArrayList;
import java.util.List;

public class TripsListFragment extends Fragment implements AbsListView.OnItemClickListener {

    private class TripArrayAdapter extends ArrayAdapter<SavedTrip> {

        public TripArrayAdapter(Context context, int resource, List<SavedTrip> data) {
            super(context, resource, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SimpleView featureView;
            if (convertView instanceof SimpleView) {
                featureView = (SimpleView) convertView;
            } else {
                featureView = new SimpleView(getContext());
            }

            SavedTrip entry = getItem(position);
            featureView.setTitle(String.format(getString(R.string.trip_list_item_title),
                    entry.getOrigin().getName(),
                    entry.getDestination().getName()));
            featureView.setDescription(entry.getSchedule().getScheduleOptions().isRecurring() ?
                    getString(R.string.trip_list_item_recurring) :
                    getString(R.string.trip_list_item_one_time));

            return featureView;
        }
    }

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private TripArrayAdapter mAdapter;

    private TripManager tripManager;

    private List<SavedTrip> savedTrips = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mAdapter = new TripArrayAdapter(getActivity(), R.layout.simple_view, this.savedTrips);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip, container, false);

        // Set the adapter
        AbsListView mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        tripManager = ((TripsActivity) activity).getTripsManager();
    }

    @Override
    public void onResume() {
        super.onResume();

        updateList(true);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SavedTrip trip = savedTrips.get(position);

        tripManager.deleteTrip(new TripManager.DeleteTripOptions(trip), new TripManager.IDeleteTripResponseListener() {
                    @Override
                    public void onResult(SavedTrip data) {
                        savedTrips.remove(data);
                        updateList(false);
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(getActivity(), error.getErrorMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    public void updateList(boolean refresh) {
        if (refresh) {
            savedTrips.clear();

            tripManager.getTrips(null, new TripManager.IGetTripsResponseListener() {
                @Override
                public void onResult(List<SavedTrip> data) {
                    savedTrips.addAll(data);
                    notifyDataSetChanged();
                }

                @Override
                public void onError(com.inrix.sdk.Error error) {
                    Toast.makeText(getActivity(), error.getErrorMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            notifyDataSetChanged();
        }
    }

    /**
     * Check if fragment is inactive
     *
     * @return true if inactive
     */
    public boolean isFragmentInactive() {
        return isDetached() || isRemoving() || !isResumed();
    }

    private void notifyDataSetChanged() {
        if (isFragmentInactive()) {
            return;
        }

        TripsListFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
