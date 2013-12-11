package com.inrix.inrixrouterecorder;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;

public class RouteRecorderActivity extends FragmentActivity implements
		OnClickListener, LocationListener {

	private Button start;
	private Button stop;
	private TextView progress;
	private State curState = State.STOPPED;
	private LocationService locationService;
	private int curProgress = 0;
	private StringBuilder route = new StringBuilder();
	// 47.602709,-122.334405,20, 20, 120, 3.0f, 3.12f
	private final String routeFormat = "%s, %s, %s, %s, %s, %sf, %sf";

	private enum State {
		STOPPED, STARTED, PAUSED
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.start = (Button) findViewById(R.id.start);
		this.stop = (Button) findViewById(R.id.stop);
		this.progress = (TextView) findViewById(R.id.progress_val);

		start.setOnClickListener(this);
		stop.setOnClickListener(this);

		this.locationService = new LocationService(this);
		this.locationService.setLocationListener(this);
	}

	@Override
	protected void onDestroy() {

		this.locationService.pause();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.start) {
			start();
		} else if (v.getId() == R.id.stop) {
			stop();
		}
	}

	private void start() {
		if (curState != State.STARTED) {

			if (curState == State.STOPPED) {
				setCurProgress(0);
			}

			this.start.setText("Pause recording");
			this.curState = State.STARTED;
			this.locationService.resume();
		} else {
			//pause
			this.start.setText("Resume recording");
			this.curState = State.PAUSED;
			this.locationService.pause();
		}
	}

	private void stop() {
		if (curState == State.STARTED || route.length() != 0) {
			this.start.setText("Start recording");
			this.curState = State.STOPPED;
			this.locationService.pause();

			EmailSender.sendRoute(this, route.toString());
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		if (curState == State.STARTED) {
			String routePoint = String.format(routeFormat,
					location.getLatitude(),
					location.getLongitude(),
					location.getSpeed(),
					20,
					120,
					location.getBearing(),
					location.getAccuracy());
			route.append(routePoint);
			route.append('\n');
			setCurProgress(curProgress + 1);
		}
	}

	public void setCurProgress(int progress) {
		
		if(progress == 0)
		{
			this.route = new StringBuilder();
		}
		this.curProgress = progress;
		this.progress.setText("" + curProgress);
	}

}
