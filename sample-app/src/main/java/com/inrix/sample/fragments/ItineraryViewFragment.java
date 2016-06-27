/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.inrix.sample.R;
import com.inrix.sample.activity.SimpleView;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.ItineraryManager;
import com.inrix.sdk.model.Itinerary;
import com.inrix.sdk.model.ItineraryEntry;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ItineraryViewFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private class ItineraryArrayAdapter extends ArrayAdapter<ItineraryEntry> {

        public ItineraryArrayAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SimpleView featureView;
            if (convertView instanceof SimpleView) {
                featureView = (SimpleView) convertView;
            } else {
                featureView = new SimpleView(getContext());
            }

            ItineraryEntry entry = getItem(position);
            featureView.setTitle(String.format(getString(R.string.itinerary_list_item_title),
                    entry.getOrigin().getName(),
                    entry.getDestination().getName()));
            SimpleDateFormat format = new SimpleDateFormat(getString(R.string.itinerary_list_item_date_format),
                    Locale.getDefault());
            featureView.setDescription(format.format(entry.getDateTime()));

            return featureView;
        }
    }

    private SwipeRefreshLayout swipeLayout;
    private ListView itineraryListView;
    private ItineraryManager itineraryManager;
    private FetchItineraryTask fetchItineraryTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_itinerary_view,
                container,
                false);

        this.itineraryListView = (ListView) rootView.findViewById(R.id.itineraryListView);
        this.itineraryListView.setAdapter(new ItineraryArrayAdapter(getActivity(), R.layout.simple_view));
        this.swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeLayout);
        this.swipeLayout.setOnRefreshListener(this);

        this.itineraryManager = InrixCore.getItineraryManager();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.fetchItineraryTask = new FetchItineraryTask();
        this.fetchItineraryTask.execute();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.fetchItineraryTask.getStatus() != AsyncTask.Status.FINISHED) {
            this.fetchItineraryTask.cancel(true);
        }
        this.fetchItineraryTask = null;
        this.swipeLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        if (this.fetchItineraryTask.getStatus() != AsyncTask.Status.FINISHED) {
            this.fetchItineraryTask.cancel(true);
        }
        ItineraryViewFragment.this.swipeLayout.setRefreshing(true);
        this.fetchItineraryTask = new FetchItineraryTask();
        this.fetchItineraryTask.execute();
    }

    private class FetchItineraryTask extends AsyncTask<Void, Void, Void> {

        private ICancellable request = null;

        /**
         * {@inheritDoc}
         */
        @Override
        protected Void doInBackground(Void... params) {
            // Get itinerary for next week
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.DATE, 7);

            this.request = ItineraryViewFragment.this.itineraryManager.getItinerary(new ItineraryManager.GetItineraryOptions(start.getTime(),
                            end.getTime()),
                    new ItineraryManager.IGetItineraryResponseListener() {
                        @Override
                        public void onResult(Itinerary data) {
                            ItineraryArrayAdapter adapter = (ItineraryArrayAdapter) ItineraryViewFragment.this.itineraryListView.getAdapter();
                            adapter.clear();
                            if (data.getItineraryEntries().isEmpty()) {
                                Toast.makeText(getActivity(), getString(R.string.itinerary_no_entries_toast),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                adapter.addAll(data.getItineraryEntries());
                            }
                            FetchItineraryTask.this.request = null;
                            ItineraryViewFragment.this.swipeLayout.setRefreshing(false);
                        }

                        @Override
                        public void onError(com.inrix.sdk.Error error) {
                            Toast.makeText(getActivity(), getString(R.string.itinerary_error_toast),
                                    Toast.LENGTH_LONG).show();
                            FetchItineraryTask.this.request = null;
                            ItineraryViewFragment.this.swipeLayout.setRefreshing(false);
                        }
                    });

            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            if (this.request != null) {
                this.request.cancel();
                this.request = null;
            }
            ItineraryViewFragment.this.swipeLayout.setRefreshing(false);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (this.request != null) {
                this.request.cancel();
                this.request = null;
            }
            ItineraryViewFragment.this.swipeLayout.setRefreshing(false);
        }
    }
}
