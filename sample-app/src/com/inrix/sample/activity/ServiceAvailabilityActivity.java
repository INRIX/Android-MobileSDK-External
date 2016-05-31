/**
 * Copyright (c) 2013-2015 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.inrix.sample.R;
import com.inrix.sdk.Error;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.ServiceAvailabilityManager;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.ServiceAvailability;

import java.util.Locale;

public class ServiceAvailabilityActivity extends InrixSdkActivity {

    private Spinner countrySpinner;
    private TextView statusTextView;
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private Button getFromGeoPointButton;
    private Button getFromCountryButton;
    private ServiceAvailabilityManager serviceAvailabilityManager;
    private ICancellable serviceAvailabilityRequest = null;

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_service_availability;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.serviceAvailabilityManager = InrixCore.getServiceAvailabilityManager();

        this.countrySpinner = (Spinner) findViewById(R.id.countryCodeSpinner);
        @SuppressWarnings("unchecked") ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Locale.getISOCountries());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.countrySpinner.setAdapter(adapter);

        this.statusTextView = (TextView) findViewById(R.id.textViewAvailability);
        this.latitudeEditText = (EditText) findViewById(R.id.editTextLatitude);
        this.longitudeEditText = (EditText) findViewById(R.id.editTextLongitude);
        this.getFromCountryButton = (Button) findViewById(R.id.get_service_availability_button_country);
        this.getFromGeoPointButton = (Button) findViewById(R.id.get_service_availability_button_geopoint);
        this.getFromCountryButton.setOnClickListener(new ServiceAvailabilityClickListener(this));
        this.getFromGeoPointButton.setOnClickListener(new ServiceAvailabilityClickListener(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.statusTextView.setText("");
        this.getFromGeoPointButton.setEnabled(true);
        this.getFromCountryButton.setEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (serviceAvailabilityRequest != null) {
            serviceAvailabilityRequest.cancel();
            serviceAvailabilityRequest = null;
        }
    }

    public void makeRequest(boolean fromGeoPoint) {
        statusTextView.setText("");

        ServiceAvailabilityManager.ServiceAvailabilityOptions options;

        if (fromGeoPoint) {
            String lat = latitudeEditText.getText().toString();
            String lon = longitudeEditText.getText().toString();

            GeoPoint geoPoint = null;

            try {

                double latitude = Double.parseDouble(lat);
                double longitude = Double.parseDouble(lon);

                geoPoint = new GeoPoint(latitude, longitude);
            } catch (NumberFormatException ex) {
                statusTextView.setText("Bad Lat Long.");
            }

            options = new ServiceAvailabilityManager.ServiceAvailabilityOptions(geoPoint);
        } else {
            options = new ServiceAvailabilityManager.ServiceAvailabilityOptions((String) this.countrySpinner.getSelectedItem());
        }

        serviceAvailabilityRequest = serviceAvailabilityManager.getServiceAvailability(
                options,
                new ServiceAvailabilityManager.IServiceAvailabilityResponseListener() {
                    @Override
                    public void onResult(ServiceAvailability data) {
                        statusTextView.setText(data.getCountry());
                        statusTextView.setText("\n");

                        StringBuilder builder = new StringBuilder();
                        int counter = 1;

                        for (ServiceAvailability.Service service : data.getServices()) {
                            builder.append(service.toString());
                            if (counter != data.getServices().size()) {
                                builder.append(", ");
                            }

                            counter++;
                        }

                        if (data.getServices().size() > 0) {
                            statusTextView.setText(builder.toString());
                        } else {
                            statusTextView.setText(R.string.service_availability_no_service);
                        }

                        getFromGeoPointButton.setEnabled(true);
                        getFromCountryButton.setEnabled(true);
                        serviceAvailabilityRequest = null;
                    }

                    @Override
                    public void onError(Error error) {
                        statusTextView.setText(String.format(getString(R.string.service_availability_failed), error.getErrorMessage()));
                        getFromGeoPointButton.setEnabled(true);
                        getFromCountryButton.setEnabled(true);
                        serviceAvailabilityRequest = null;
                    }
                }
        );
    }

    private static class ServiceAvailabilityClickListener implements View.OnClickListener {
        private final ServiceAvailabilityActivity activity;

        ServiceAvailabilityClickListener(ServiceAvailabilityActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onClick(View v) {
            v.setEnabled(false);

            if (v.getId() == activity.getFromCountryButton.getId()) {
                activity.makeRequest(false);
            } else if (v.getId() == activity.getFromGeoPointButton.getId()) {
                activity.makeRequest(true);
            }
        }
    }
}
