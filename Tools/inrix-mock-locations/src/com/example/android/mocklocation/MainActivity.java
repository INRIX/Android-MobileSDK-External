/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.mocklocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Interface to the SendMockLocationService that sends mock locations into Location Services.
 * 
 * This Activity collects parameters from the UI, sends them to the Service, and receives back
 * status messages from the Service.
 * <p>
 * The following parameters are sent:
 * <ul>
 * <li><b>Type of test:</b> one-time cycle through the mock locations, or continuous sending</li>
 * <li><b>Pause interval:</b> Amount of time (in seconds) to wait before starting mock location sending. This pause allows the tester to switch to the app under test before sending begins.</li>
 * <li><b>Send interval:</b> Amount of time (in seconds) before sending a new location. This time is unrelated to the update interval requested by the app under test. For example, the app under test can request updates every second, and the tester can request a mock location send every five seconds. In this case, the app under test will receive the same location 5 times before a new location becomes available.</li>
 */
public class MainActivity extends ActionBarActivity implements OnItemSelectedListener {
	// Broadcast receiver for local broadcasts from SendMockLocationService.
	private ServiceMessageReceiver messageReceiver;

	// Intent to send to SendMockLocationService. Contains the type of test to run.
	private Intent requestIntent;

	// Handle to connection status reporting field in UI.
	public TextView textConnectionStatus;

	// Handle to app status reporting field in UI.
	public TextView textAppStatus;

	// Handle to input field for the interval to wait before starting mock location testing.
	private EditText editPauseInterval;

	// Handle to input field for the interval to wait before sending a new mock location.
	private EditText editSendInterval;

	// Handle to choose a route to simulate.
	private Spinner spinnerRoutes;
	
	// Handle for the current location label.
	private TextView textCurrentLocation;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.supportRequestWindowFeature(Window.FEATURE_PROGRESS);
		this.supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Connect to the main UI.
		this.setContentView(R.layout.activity_main);

		// Get handles to UI elements.
		this.textConnectionStatus = (TextView) this.findViewById(R.id.connection_status);
		this.textAppStatus = (TextView) this.findViewById(R.id.app_status);
		this.editPauseInterval = (EditText) this.findViewById(R.id.pause_value);
		this.editSendInterval = (EditText) this.findViewById(R.id.send_interval_value);
		this.spinnerRoutes = (Spinner) this.findViewById(R.id.choose_route);
		this.textCurrentLocation = (TextView) this.findViewById(R.id.current_location);

		// Initialize spinner with available routes.
		final ArrayAdapter<RouteData> adapter = new ArrayAdapter<RouteData>(
				this,
				R.layout.support_simple_spinner_dropdown_item,
				RouteData.values());
		this.spinnerRoutes.setAdapter(adapter);
		this.spinnerRoutes.setOnItemSelectedListener(this);

		// Instantiate a broadcast receiver for Intents coming from the Service.
		this.messageReceiver = new ServiceMessageReceiver();
		final IntentFilter filter = new IntentFilter(LocationUtils.ACTION_SERVICE_MESSAGE);
		LocalBroadcastManager.getInstance(this).registerReceiver(this.messageReceiver, filter);
		this.requestIntent = new Intent(this, SendMockLocationService.class);

		// Update app UI.
		this.setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
		this.getSupportActionBar().setDisplayShowTitleEnabled(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public final boolean onCreateOptionsMenu(final Menu menu) {
		final MenuInflater menuInflater = this.getMenuInflater();
		menuInflater.inflate(R.menu.activity_main_menu, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public final boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_run_once:
			this.startOnce();
			return true;
		case R.id.action_run_continuous:
			this.startContinuous();
			return true;
		case R.id.action_stop:
			this.stopSimulation();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Respond when Run Once is clicked. Start a one-time mock location test run.
	 */
	private final void startOnce() {
		if (this.getInputValues()) {
			this.requestIntent.setAction(LocationUtils.ACTION_START_ONCE);
			this.textAppStatus.setText(R.string.testing_started);
			this.setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
			this.startService(requestIntent);
		}
	}

	/**
	 * Respond when Run Continuously is clicked. Start a continuous mock location test run.
	 * Mock locations are sent indefinitely, until the tester clicks Stop Continuous Run.
	 */
	private final void startContinuous() {
		if (this.getInputValues()) {
			this.requestIntent.setAction(LocationUtils.ACTION_START_CONTINUOUS);
			this.textAppStatus.setText(R.string.testing_started);
			this.setSupportProgressBarIndeterminateVisibility(Boolean.TRUE);
			this.startService(requestIntent);
		}
	}

	/**
	 * Respond when Stop Test is clicked. Stop the current mock location test run. If the user
	 * requested a one-time run with a short pause interval and fast send interval, this
	 * request may have no effect, because the Service will have already stopped.
	 */
	private final void stopSimulation() {
		this.requestIntent.setAction(LocationUtils.ACTION_STOP_TEST);
		if (null != startService(requestIntent)) {
			this.textAppStatus.setText(R.string.stop_service);
		} else {
			this.textAppStatus.setText(R.string.no_service);
		}

		this.textConnectionStatus.setText(R.string.disconnected);
		this.setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
	}

	/**
	 * Broadcast receiver triggered by broadcast Intents within this app that match the
	 * receiver's filter (see onCreate())
	 */
	private class ServiceMessageReceiver extends BroadcastReceiver {
		/*
		 * Invoked when a broadcast Intent from SendMockLocationService arrives
		 * 
		 * context is the Context of the app
		 * intent is the Intent object that triggered the receiver
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get the message code from the incoming Intent
			int code1 = intent.getIntExtra(LocationUtils.KEY_EXTRA_CODE1, 0);
			int code2 = intent.getIntExtra(LocationUtils.KEY_EXTRA_CODE2, 0);

			// Choose the action, based on the message code
			switch (code1) {
			/*
			 * SendMockLocationService reported that the location client is connected. Update
			 * the app status reporting field in the UI.
			 */
			case LocationUtils.CODE_CONNECTED:
				textConnectionStatus.setText(R.string.connected);
				break;
				
			case LocationUtils.CODE_CURRENT_LOCATION:
				final double latitude = intent.getDoubleExtra(LocationUtils.KEY_EXTRA_LATITUDE, 0.0);
				final double longitude = intent.getDoubleExtra(LocationUtils.KEY_EXTRA_LONGITUDE, 0.0);
				final String text = latitude + " : " + longitude;
				textCurrentLocation.setText(text);
				break;

			/*
			 * SendMockLocationService reported that the location client disconnected. This
			 * happens if Location Services drops the connection. Update the app status and the
			 * connection status reporting fields in the UI.
			 */
			case LocationUtils.CODE_DISCONNECTED:
				textConnectionStatus.setText(R.string.disconnected);
				textAppStatus.setText(R.string.notification_content_test_stop);
				break;

			/*
			 * SendMockLocationService reported that an attempt to connect to Location
			 * Services failed. Testing can't continue. The Service has already stopped itself.
			 * Update the connection status reporting field and include the error code.
			 * Also update the app status field
			 */
			case LocationUtils.CODE_CONNECTION_FAILED:
				setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
				textConnectionStatus.setText(
						context.getString(R.string.connection_failure, code2));
				textAppStatus.setText(R.string.location_test_finish);
				break;

			/*
			 * SendMockLocationService reported that the tester requested a test, but a test
			 * is already underway. Update the app status reporting field.
			 */
			case LocationUtils.CODE_IN_TEST:
				textAppStatus.setText(R.string.not_continuous_test);
				break;

			/*
			 * SendMockLocationService reported that the test run finished. Turn off the
			 * progress indicator, update the app status reporting field and the connection
			 * status reporting field. Since this message can only occur if
			 * SendMockLocationService disconnected the client, the connection status is
			 * "disconnected".
			 */
			case LocationUtils.CODE_TEST_FINISHED:
				setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
				textAppStatus.setText(context.getText(R.string.location_test_finish));
				textConnectionStatus.setText(R.string.disconnected);
				break;

			/*
			 * SendMockLocationService reported that the tester interrupted the test.
			 * Turn off the activity indicator and update the app status reporting field.
			 */
			case LocationUtils.CODE_TEST_STOPPED:
				setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
				textAppStatus.setText(R.string.test_interrupted);
				break;

			/*
			 * An unknown broadcast Intent was received. Log an error.
			 */
			default:
				Log.e(LocationUtils.APPTAG, getString(R.string.invalid_broadcast_code));
				break;
			}
		}
	}

	/**
	 * Verify the pause interval and send interval from the UI. If they're correct, store
	 * them in the Intent that's used to start SendMockLocationService
	 * 
	 * @return true if all the input values are correct; otherwise false
	 */
	public boolean getInputValues() {
		final String pauseIntervalText = this.editPauseInterval.getText().toString();
		final String sendIntervalText = this.editSendInterval.getText().toString();

		if (TextUtils.isEmpty(pauseIntervalText)) {
			this.textAppStatus.setText(R.string.pause_interval_empty);
			this.editPauseInterval.setError(this.getString(R.string.pause_interval_empty));
			return false;
		} else if (Integer.valueOf(pauseIntervalText) <= 0) {
			this.textAppStatus.setText(R.string.pause_interval_not_positive);
			this.editPauseInterval.setError(this.getString(R.string.pause_interval_not_positive));
			return false;
		}

		if (TextUtils.isEmpty(sendIntervalText)) {
			this.textAppStatus.setText(R.string.send_entry_empty);
			this.editSendInterval.setError(this.getString(R.string.send_entry_empty));
			return false;
		} else if (Integer.valueOf(sendIntervalText) <= 0) {
			this.textAppStatus.setText(R.string.send_interval_not_positive);
			this.editSendInterval.setError(this.getString(R.string.send_interval_not_positive));
			return false;
		}

		int pauseValue = Integer.valueOf(pauseIntervalText);
		int sendValue = Integer.valueOf(sendIntervalText);

		requestIntent.putExtra(LocationUtils.EXTRA_PAUSE_VALUE, pauseValue);
		requestIntent.putExtra(LocationUtils.EXTRA_SEND_INTERVAL, sendValue);
		return true;
	}

	@Override
	public final void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
		final String selectedValue = parent.getItemAtPosition(pos).toString();
		final RouteData route = RouteData.fromString(selectedValue);
		this.requestIntent.putExtra(LocationUtils.EXTRA_ROUTE, route.ordinal());
	}

	@Override
	public final void onNothingSelected(AdapterView<?> parent) {
	}
}
