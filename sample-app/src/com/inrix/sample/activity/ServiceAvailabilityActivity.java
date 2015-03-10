package com.inrix.sample.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.inrix.sample.R;
import com.inrix.sdk.Error;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.ServiceAvailabilityManager;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.ServiceAvailability;

public class ServiceAvailabilityActivity extends FragmentActivity {

    private TextView statusTextView;
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private Button getStatusButton;
    private ServiceAvailabilityManager serviceAvailabilityManager;
    private ICancellable serviceAvailabilityRequest = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_availability);

        // Initialize INRIX
        InrixCore.initialize(getApplicationContext());

        this.serviceAvailabilityManager = InrixCore.getServiceAvailabiliyManager();

        this.statusTextView = (TextView) findViewById(R.id.textViewAvailability);
        this.latitudeEditText = (EditText) findViewById(R.id.editTextLatitude);
        this.longitudeEditText = (EditText) findViewById(R.id.editTextLongitude);
        this.getStatusButton = (Button) findViewById(R.id.get_service_availability_button);
        this.getStatusButton.setOnClickListener(new ServiceAvailabilityClickListener(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.statusTextView.setText("");
        this.getStatusButton.setEnabled(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (serviceAvailabilityRequest != null) {
            serviceAvailabilityRequest.cancel();
            serviceAvailabilityRequest = null;
        }
    }

    private static class ServiceAvailabilityClickListener implements View.OnClickListener {
        private final ServiceAvailabilityActivity activity;

        ServiceAvailabilityClickListener(ServiceAvailabilityActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onClick(View v) {
            v.setEnabled(false);
            activity.serviceAvailabilityManager.getServiceAvailability(
                    new ServiceAvailabilityManager.ServiceAvailabilityOptions(GeoPoint.parse(String.format(
                            activity.getString(R.string.service_availability_geo_point_format),
                            Double.parseDouble(activity.latitudeEditText.getText().toString()),
                            Double.parseDouble(activity.longitudeEditText.getText().toString())))),
                    new ServiceAvailabilityManager.IServiceAvailabilityResponseListener() {
                        @Override
                        public void onResult(ServiceAvailability data) {
                            if (data.getTrafficServiceAvailability()) {
                                activity.statusTextView.setText(activity.getString(R.string.service_availability_available));
                            } else {
                                activity.statusTextView.setText(activity.getString(R.string.service_availability_not_available));
                            }
                            activity.getStatusButton.setEnabled(true);
                            activity.serviceAvailabilityRequest = null;
                        }

                        @Override
                        public void onError(Error error) {
                            activity.statusTextView.setText(String.format(activity.getString(R.string.service_availability_failed), error.getErrorMessage()));
                            activity.getStatusButton.setEnabled(true);
                            activity.serviceAvailabilityRequest = null;
                        }
                    }
            );
        }
    }
}
