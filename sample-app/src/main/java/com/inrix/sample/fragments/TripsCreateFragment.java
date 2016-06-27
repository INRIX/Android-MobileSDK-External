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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.Toast;

import com.inrix.sample.R;
import com.inrix.sample.activity.TripsActivity;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.TripManager;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.SavedTrip;
import com.inrix.sdk.model.TripPoint;
import com.inrix.sdk.model.Schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.TimeZone;

public class TripsCreateFragment extends Fragment {

    // Setup Home
    public static TripPoint home = new TripPoint("Home", new GeoPoint(47.643521, -122.203697));
    // Setup Work
    public static TripPoint work = new TripPoint("Work", new GeoPoint(47.620506, -122.349277));
    private OnTripCreatedListener createCallback;
    private CheckBox waypointCheckBox;
    private CheckBox alertCheckBox;
    private RadioButton onetimeRadioButton;
    private RadioButton recurringRadioButton;
    private Button saveTripButton;
    private ICancellable saveTripRequest = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_trips_create, container, false);

        this.waypointCheckBox = (CheckBox) rootView.findViewById(R.id.checkBoxWaypoint);
        this.alertCheckBox = (CheckBox) rootView.findViewById(R.id.checkBoxAlert);
        this.onetimeRadioButton = (RadioButton) rootView.findViewById(R.id.radioButtonOneTime);
        this.recurringRadioButton = (RadioButton) rootView.findViewById(R.id.radioButtonRecurring);
        this.saveTripButton = (Button) rootView.findViewById(R.id.save_trip_button);
        this.saveTripButton.setOnClickListener(new SaveTripClickListener(this));

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            createCallback = (OnTripCreatedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTripCreatedListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        this.saveTripButton.setEnabled(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (saveTripRequest != null) {
            saveTripRequest.cancel();
            saveTripRequest = null;
        }
    }

    public interface OnTripCreatedListener {
        void onTripCreated();
    }

    private static class SaveTripClickListener implements View.OnClickListener {
        private final TripsCreateFragment fragment;

        SaveTripClickListener(TripsCreateFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void onClick(View v) {
            v.setEnabled(false);

            // Add Coffee Shop as waypoint?
            List<TripPoint> waypoints = new ArrayList<>();
            if (fragment.waypointCheckBox.isChecked()) {
                TripPoint coffee = new TripPoint("Coffee Shop", new GeoPoint(47.634508, -122.1972575));
                // Stop 10 minutes to get a drink
                coffee.setDuration(10);
                waypoints.add(coffee);
            }

            // Create Schedule with selected option
            Schedule schedule = null;
            Schedule.NotificationOptions notificationOptions = null;

            // Schedule trip for 30 minutes from now, remember to set timezone to UTC to avoid unintended
            // conversions
            Calendar now = Calendar.getInstance();
            now.add(Calendar.MINUTE, 30);
            Calendar dateTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            dateTime.set(now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH),
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    now.get(Calendar.SECOND));

            if (fragment.alertCheckBox.isChecked()) {
                // Notify the user 10 minutes before the alert time
                notificationOptions = new Schedule.NotificationOptions(true, 10);
            }

            // Create alert options and the alert itself
            if (fragment.onetimeRadioButton.isChecked()) {
                Schedule.OneTimeScheduleOptions scheduleOptions = new Schedule.OneTimeScheduleOptions(
                        Schedule.ScheduleType.DEPARTURE,
                        dateTime.getTime());

                schedule = new Schedule(scheduleOptions, notificationOptions);
            } else if (fragment.recurringRadioButton.isChecked()) {
                // Days of the week to alert, starting at Sunday
                Collection<Integer> daysOfWeek = Arrays.asList(Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY);
                Schedule.RecurringScheduleOptions scheduleOptions = new Schedule.RecurringScheduleOptions(
                        Schedule.ScheduleType.ARRIVAL,
                        dateTime.getTime(),
                        daysOfWeek);

                schedule = new Schedule(scheduleOptions, notificationOptions);
            }

            // Create a new SavedTrip
            SavedTrip savedTrip = new SavedTrip(home, work, waypoints, schedule);

            final TripsActivity activity = (TripsActivity) fragment.getActivity();

            // Save the trip
            fragment.saveTripRequest = activity.getTripsManager().saveTrip(
                    new TripManager.SaveTripOptions(savedTrip),
                    new TripManager.ISaveTripResponseListener() {
                        @Override
                        public void onResult(SavedTrip data) {
                            if (!fragment.isDetached()) {
                                // Check status and display to user
                                Toast.makeText(activity, "Success, saved trip!", Toast.LENGTH_LONG).show();

                                fragment.saveTripButton.setEnabled(true);
                                fragment.saveTripRequest = null;

                                fragment.createCallback.onTripCreated();
                            }
                        }

                        @Override
                        public void onError(com.inrix.sdk.Error error) {
                            Toast.makeText(activity, "Error saving trip: " + error.getErrorMessage(), Toast.LENGTH_LONG).show();

                            fragment.saveTripButton.setEnabled(true);
                            fragment.saveTripRequest = null;
                        }
                    }
            );
        }
    }
}
