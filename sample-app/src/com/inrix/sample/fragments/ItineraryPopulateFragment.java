/**
 * Copyright (c) 2013-2015 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.inrix.sample.R;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.TripManager;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.SavedTrip;
import com.inrix.sdk.model.TripPoint;
import com.inrix.sdk.model.TripSchedule;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

public class ItineraryPopulateFragment extends Fragment {

    private enum RequestType {
        HOME_TO_WORK,
        WORK_TO_HOME,
        DENTIST,
        DELETE
    }

    private Button homeToWorkButton;
    private Button workToHomeButton;
    private Button dentistButton;
    private Button deleteButton;
    private TextView statusTextView;
    private TripManager tripManager;
    private ICancellable request = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_itinerary_populate,
                container,
                false);

        this.homeToWorkButton = (Button) rootView.findViewById(R.id.button_home);
        this.homeToWorkButton.setOnClickListener(new ItineraryButtonClickListener(this, RequestType.HOME_TO_WORK));
        this.workToHomeButton = (Button) rootView.findViewById(R.id.button_work);
        this.workToHomeButton.setOnClickListener(new ItineraryButtonClickListener(this, RequestType.WORK_TO_HOME));
        this.dentistButton = (Button) rootView.findViewById(R.id.button_dentist);
        this.dentistButton.setOnClickListener(new ItineraryButtonClickListener(this, RequestType.DENTIST));
        this.deleteButton = (Button) rootView.findViewById(R.id.button_delete);
        this.deleteButton.setOnClickListener(new ItineraryButtonClickListener(this, RequestType.DELETE));
        this.statusTextView = (TextView) rootView.findViewById(R.id.textViewStatus);

        this.tripManager = InrixCore.getTripManager();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.statusTextView.setText("");
        enableButtons();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (this.request != null) {
            this.request.cancel();
            this.request = null;
        }
    }

    private void disableButtons() {
        this.homeToWorkButton.setEnabled(false);
        this.workToHomeButton.setEnabled(false);
        this.dentistButton.setEnabled(false);
        this.deleteButton.setEnabled(false);
    }

    private void enableButtons() {
        this.homeToWorkButton.setEnabled(true);
        this.workToHomeButton.setEnabled(true);
        this.dentistButton.setEnabled(true);
        this.deleteButton.setEnabled(true);
    }

    private static class ItineraryButtonClickListener implements View.OnClickListener {

        private final ItineraryPopulateFragment fragment;
        private final RequestType requestType;
        private AtomicInteger deleteCounter;
        private TripPoint home = new TripPoint("Home", new GeoPoint(47.620506, -122.349277));
        private TripPoint work = new TripPoint("Work", new GeoPoint(47.5947558, -122.3322532));
        private TripPoint dentist = new TripPoint("Dentist", new GeoPoint(47.6040387, -122.3233334));

        ItineraryButtonClickListener(ItineraryPopulateFragment fragment, RequestType requestType) {
            this.fragment = fragment;
            this.requestType = requestType;
        }

        @Override
        public void onClick(View v) {
            this.fragment.disableButtons();
            this.fragment.statusTextView.setText("");

            switch (this.requestType) {
                case HOME_TO_WORK:
                    homeToWorkRequest();
                    break;
                case WORK_TO_HOME:
                    workToHomeRequest();
                    break;
                case DENTIST:
                    dentistRequest();
                    break;
                case DELETE:
                    deleteAllRequest();
                    break;
            }
        }

        private void homeToWorkRequest() {
            // Schedule trip to arrive at work at 8:00AM every weekday, remember to set timezone to
            // UTC to avoid unintended conversions
            Calendar now = Calendar.getInstance();
            Calendar dateTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            dateTime.set(now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH),
                    9, 0, 0);

            // Days of the week to alert, starting at Sunday
            Collection<Integer> daysOfWeek = Arrays.asList(Calendar.MONDAY, Calendar.TUESDAY,
                    Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY);
            TripSchedule.RecurringScheduleOptions scheduleOptions = new TripSchedule.RecurringScheduleOptions(
                    TripSchedule.ScheduleType.ARRIVAL,
                    dateTime.getTime(),
                    daysOfWeek);

            TripSchedule schedule = new TripSchedule(scheduleOptions, null);

            // Create a new SavedTrip
            SavedTrip savedTrip = new SavedTrip(this.home, this.work, schedule);

            // Save the trip
            this.fragment.request = this.fragment.tripManager.saveTrip(
                    new TripManager.SaveTripOptions(savedTrip),
                    new TripManager.ISaveTripResponseListener() {
                        @Override
                        public void onResult(SavedTrip data) {
                            if (!ItineraryButtonClickListener.this.fragment.isDetached()) {
                                ItineraryButtonClickListener.this.fragment.statusTextView.
                                        setText(ItineraryButtonClickListener.this.fragment.getString(R.string.itinerary_save_success));
                                ItineraryButtonClickListener.this.fragment.enableButtons();
                                ItineraryButtonClickListener.this.fragment.request = null;
                            }
                        }

                        @Override
                        public void onError(com.inrix.sdk.Error error) {
                            ItineraryButtonClickListener.this.fragment.statusTextView.
                                    setText(String.format(fragment.getString(R.string.itinerary_failed),
                                            error.getErrorMessage()));
                            ItineraryButtonClickListener.this.fragment.enableButtons();
                            ItineraryButtonClickListener.this.fragment.request = null;
                        }
                    }
            );
        }

        private void workToHomeRequest() {
            // Schedule trip to leave work at 5:00PM every weekday, remember to set timezone to
            // UTC to avoid unintended conversions
            Calendar now = Calendar.getInstance();
            Calendar dateTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            dateTime.set(now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH),
                    17, 0, 0);

            // Days of the week to alert, starting at Sunday
            Collection<Integer> daysOfWeek = Arrays.asList(Calendar.MONDAY, Calendar.TUESDAY,
                    Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY);
            TripSchedule.RecurringScheduleOptions scheduleOptions = new TripSchedule.RecurringScheduleOptions(
                    TripSchedule.ScheduleType.DEPARTURE,
                    dateTime.getTime(),
                    daysOfWeek);

            TripSchedule schedule = new TripSchedule(scheduleOptions, null);

            // Create a new SavedTrip
            SavedTrip savedTrip = new SavedTrip(this.work, this.home, schedule);

            // Save the trip
            this.fragment.request = this.fragment.tripManager.saveTrip(
                    new TripManager.SaveTripOptions(savedTrip),
                    new TripManager.ISaveTripResponseListener() {
                        @Override
                        public void onResult(SavedTrip data) {
                            if (!ItineraryButtonClickListener.this.fragment.isDetached()) {
                                ItineraryButtonClickListener.this.fragment.statusTextView.
                                        setText(ItineraryButtonClickListener.this.fragment.getString(R.string.itinerary_save_success));
                                ItineraryButtonClickListener.this.fragment.enableButtons();
                                ItineraryButtonClickListener.this.fragment.request = null;
                            }
                        }

                        @Override
                        public void onError(com.inrix.sdk.Error error) {
                            ItineraryButtonClickListener.this.fragment.statusTextView.
                                    setText(String.format(fragment.getString(R.string.itinerary_failed),
                                            error.getErrorMessage()));
                            ItineraryButtonClickListener.this.fragment.enableButtons();
                            ItineraryButtonClickListener.this.fragment.request = null;
                        }
                    }
            );
        }

        private void dentistRequest() {
            // Schedule trip to visit the dentist 2 days from now at 2:00PM, remember to set timezone to
            // UTC to avoid unintended conversions
            Calendar now = Calendar.getInstance();
            now.add(Calendar.DATE, 2);
            Calendar dateTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            dateTime.set(now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH),
                    14, 0, 0);

            TripSchedule.OneTimeScheduleOptions scheduleOptions = new TripSchedule.OneTimeScheduleOptions(
                    TripSchedule.ScheduleType.ARRIVAL,
                    dateTime.getTime());

            TripSchedule schedule = new TripSchedule(scheduleOptions, null);

            // Create a new SavedTrip
            SavedTrip savedTrip = new SavedTrip(this.work, this.dentist, schedule);

            // Save the trip
            this.fragment.request = this.fragment.tripManager.saveTrip(
                    new TripManager.SaveTripOptions(savedTrip),
                    new TripManager.ISaveTripResponseListener() {
                        @Override
                        public void onResult(SavedTrip data) {
                            if (!ItineraryButtonClickListener.this.fragment.isDetached()) {
                                ItineraryButtonClickListener.this.fragment.statusTextView.
                                        setText(ItineraryButtonClickListener.this.fragment.getString(R.string.itinerary_save_success));
                                ItineraryButtonClickListener.this.fragment.enableButtons();
                                ItineraryButtonClickListener.this.fragment.request = null;
                            }
                        }

                        @Override
                        public void onError(com.inrix.sdk.Error error) {
                            ItineraryButtonClickListener.this.fragment.statusTextView.
                                    setText(String.format(fragment.getString(R.string.itinerary_failed),
                                            error.getErrorMessage()));
                            ItineraryButtonClickListener.this.fragment.enableButtons();
                            ItineraryButtonClickListener.this.fragment.request = null;
                        }
                    }
            );
        }

        private void deleteAllRequest() {
            this.fragment.request = this.fragment.tripManager.getTrips(
                    null,
                    new TripManager.IGetTripsResponseListener() {
                        @Override
                        public void onResult(List<SavedTrip> data) {
                            if (!ItineraryButtonClickListener.this.fragment.isDetached()) {
                                deleteAllTrips(data);
                                ItineraryButtonClickListener.this.fragment.request = null;
                            }
                        }

                        @Override
                        public void onError(com.inrix.sdk.Error error) {
                            ItineraryButtonClickListener.this.fragment.statusTextView.
                                    setText(String.format(fragment.getString(R.string.itinerary_failed),
                                            error.getErrorMessage()));
                            ItineraryButtonClickListener.this.fragment.enableButtons();
                            ItineraryButtonClickListener.this.fragment.request = null;
                        }
                    }
            );
        }

        private void deleteAllTrips(List<SavedTrip> trips) {
            this.deleteCounter = new AtomicInteger(trips.size());
            for (SavedTrip trip : trips) {
                this.fragment.tripManager.deleteTrip(
                        new TripManager.DeleteTripOptions(trip),
                        new TripManager.IDeleteTripResponseListener() {
                            @Override
                            public void onResult(SavedTrip trip) {
                                if (ItineraryButtonClickListener.this.deleteCounter.decrementAndGet() == 0) {
                                    ItineraryButtonClickListener.this.fragment.statusTextView.
                                            setText(ItineraryButtonClickListener.this.fragment.getString(R.string.itinerary_delete_success));
                                    ItineraryButtonClickListener.this.fragment.enableButtons();
                                }
                            }

                            @Override
                            public void onError(com.inrix.sdk.Error error) {
                                if (ItineraryButtonClickListener.this.deleteCounter.decrementAndGet() == 0) {
                                    ItineraryButtonClickListener.this.fragment.statusTextView.
                                            setText(ItineraryButtonClickListener.this.fragment.getString(R.string.itinerary_delete_success));
                                    ItineraryButtonClickListener.this.fragment.enableButtons();
                                }
                            }
                        }
                );
            }
        }
    }
}
