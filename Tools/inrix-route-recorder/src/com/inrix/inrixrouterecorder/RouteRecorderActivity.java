package com.inrix.inrixrouterecorder;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.inrix.inrixrouterecorder.InrixGeocoder.GeocoderCallBack;
import com.inrix.sdk.Error;
import com.inrix.sdk.Inrix;
import com.inrix.sdk.RouteManager;
import com.inrix.sdk.RouteManager.IRouteResponseListener;
import com.inrix.sdk.RouteManager.RouteOptions;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Point;
import com.inrix.sdk.model.Route;
import com.inrix.sdk.model.RoutesCollection;

public class RouteRecorderActivity extends FragmentActivity implements
		OnClickListener, LocationListener {

	private Button start;
	private Button stop;
	private TextView progress;
	private ProgressBar progressBar;
	private EditText startPoint, endpoint;
	private State curState = State.STOPPED;
	private LocationService locationService;
	private int curProgress = 0;
	private StringBuilder route = new StringBuilder();
	private GeoPoint wp1, wp2;

	// 47.602709,-122.334405,20, 20, 120, 3.0f, 3.12f
	private final String routeFormat = "%s, %s, %s, %s, %s, %sf, %sf";

	private enum State {
		STOPPED, STARTED, PAUSED
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Inrix.initialize(getApplicationContext());
		this.start = (Button) findViewById(R.id.start);
		this.stop = (Button) findViewById(R.id.stop);
		this.progress = (TextView) findViewById(R.id.progress_val);
		this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);
		this.startPoint = (EditText) findViewById(R.id.start_point);
		this.endpoint = (EditText) findViewById(R.id.end_point);
		start.setOnClickListener(this);
		stop.setOnClickListener(this);
		findViewById(R.id.generate).setOnClickListener(this);
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
		} else if (v.getId() == R.id.generate) {
			generate();
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
			// pause
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

		if (progress == 0) {
			this.route = new StringBuilder();
		}
		this.curProgress = progress;
		this.progress.setText("" + curProgress);
	}

	private void generate() {
		this.wp1 = this.wp2 = null;

		this.progressBar.setVisibility(View.VISIBLE);
		String waypoint1 = this.startPoint.getText().toString();
		String waypoint2 = this.endpoint.getText().toString();
		InrixGeocoder geocoder1 = new InrixGeocoder(new GeocoderCallBack() {

			@Override
			public void onResult(GeoPoint point) {
				if (point == null) {
					progressBar.setVisibility(View.GONE);
					Toast.makeText(RouteRecorderActivity.this,
							"Unable to find start address",
							Toast.LENGTH_LONG).show();
					return;
				}

				wp1 = point;
				findRoute();
			}
		});

		InrixGeocoder geocoder2 = new InrixGeocoder(new GeocoderCallBack() {

			@Override
			public void onResult(GeoPoint point) {
				if (point == null) {
					progressBar.setVisibility(View.GONE);
					Toast.makeText(RouteRecorderActivity.this,
							"Unable to find end address",
							Toast.LENGTH_LONG).show();
					return;
				}
				wp2 = point;
				findRoute();
			}
		});

		geocoder1.getAddress(waypoint1);
		geocoder2.getAddress(waypoint2);
	}

	private void findRoute() {
		if (wp1 == null || wp2 == null) {
			return;
		}

		RouteManager routeManager = new RouteManager();
		RouteOptions params = new RouteOptions(wp1, wp2);
		routeManager.requestRoutes(params, new IRouteResponseListener() {

			@Override
			public void onResult(RoutesCollection data) {
				progressBar.setVisibility(View.GONE);
				if(data.getRoutes().isEmpty()){
					Toast.makeText(RouteRecorderActivity.this,
							"Unable to build route beetwen this points",
							Toast.LENGTH_LONG).show();
					return;
				}

				StringBuilder route = new StringBuilder();
				final Route mainRoute = data.getRoutes().get(0);
				if(mainRoute.getPoints().isEmpty()){
					Toast.makeText(RouteRecorderActivity.this,
							"No waypoints available for this routes",
							Toast.LENGTH_LONG).show();
					return;
				}
				
				Point previousPoint = mainRoute.getPoints().get(0);
				for(Point currentPoint: mainRoute.getPoints()){
					float[] results = new float[3];
					Location.distanceBetween(previousPoint.getLatitude(), previousPoint.getLongitude(),
							previousPoint.getLatitude(), previousPoint.getLongitude(), results);
					String routePoint = String.format(routeFormat,
							currentPoint.getLatitude(),
							currentPoint.getLongitude(),
							30,
							20,
							120,
							results[1],
							20);
					route.append(routePoint);
					route.appendCodePoint(Character.LINE_SEPARATOR);
				}
				
				EmailSender.sendRoute(RouteRecorderActivity.this, route.toString());
			}

			@Override
			public void onError(Error error) {
				progressBar.setVisibility(View.GONE);
				Toast.makeText(RouteRecorderActivity.this,
						error.getErrorMessage(),
						Toast.LENGTH_LONG).show();
			}
		});
	}

}
