package com.inrix.sample.activity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.inrix.sample.R;
import com.inrix.sample.synchronous.RouteManagerSynchronousWrapper;
import com.inrix.sample.synchronous.RouteManagerSynchronousWrapper.RoutesCollectionWrapper;
import com.inrix.sample.synchronous.RouteManagerSynchronousWrapper.TravelTimeResponseWrapper;
import com.inrix.sdk.RouteManager.RouteOptions;
import com.inrix.sdk.RouteManager.TravelTimeOptions;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Route;
import com.inrix.sdk.model.TravelTimeResponse;
import com.inrix.sdk.model.TripInformation.TravelTime;

public class TravelTimesSyncActivity extends FragmentActivity {
	private final int ROUTES_TOLERANCE = 24;
	private final int ROUTES_NUM_ALTERNATES = 0;

	RouteManagerSynchronousWrapper wrapper;
	Route route;

	ExecutorService pool = Executors.newSingleThreadExecutor();
	TextView colorControls[];

	private static int COLOR_CONTROL_IDS[] = { R.id.route_color1,
			R.id.route_color2, R.id.route_color3, R.id.route_color4,
			R.id.route_color5, R.id.route_color6, R.id.route_color7,
			R.id.route_color8, R.id.route_color9, R.id.route_color10, };

	/**
	 * onCreate
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_travel_times);
		this.wrapper = new RouteManagerSynchronousWrapper(getApplicationContext());

		// Init Controls
		initColorControls();

		pool.execute(new Runnable() {

			@Override
			public void run() {
				// Request the route
				GeoPoint START_LOCATION = new GeoPoint(47.602633, -122.336243);
				GeoPoint END_LOCATION = new GeoPoint(47.616853, -122.193044);

				RouteOptions routeParams = new RouteOptions(START_LOCATION,
						END_LOCATION);
				routeParams.setTolerance(ROUTES_TOLERANCE);
				routeParams.setNumAlternates(ROUTES_NUM_ALTERNATES);
				RoutesCollectionWrapper routes;
				try {
					routes = wrapper.requestRoutes(routeParams);

					if (routes.error != null) {
						return;
					}

					route = routes.response.getRoutes().get(0);
					TravelTimesSyncActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							((TextView) findViewById(R.id.route_summary))
									.setText(route.getSummary().getText());
						}
					});

					getTravelTimes();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Get the Travel Times for the Route
	 */
	private void getTravelTimes() {
		pool.execute(new Runnable() {

			@Override
			public void run() {
				TravelTimeOptions travelTimeOptions = new TravelTimeOptions(route,
						COLOR_CONTROL_IDS.length,
						60 * DateUtils.SECOND_IN_MILLIS);
				try {
					final TravelTimeResponseWrapper travelTimes = wrapper
							.requestTravelTimes(travelTimeOptions);
					if (travelTimes.error != null) {
						Toast.makeText(TravelTimesSyncActivity.this,
								"Failed to load travel times. Try again",
								Toast.LENGTH_LONG).show();
						return;
					}

					TravelTimesSyncActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							updateColors(travelTimes.response);
						}
					});

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialize the Color Controls
	 */
	private void initColorControls() {

		this.colorControls = new TextView[COLOR_CONTROL_IDS.length];

		for (int i = 0; i < COLOR_CONTROL_IDS.length; i++) {
			this.colorControls[i] = ((TextView) findViewById(COLOR_CONTROL_IDS[i]));
			this.colorControls[i].setBackgroundColor(Color.WHITE);
		}
	}

	/**
	 * Update the Colors to match the travel times for the route
	 */
	private void updateColors(TravelTimeResponse travelTimeResponse) {
		if (this.route != null) {
			List<TravelTime> travelTimeList = travelTimeResponse
					.getTravelTimes();

			for (int i = 0; i < COLOR_CONTROL_IDS.length; i++) {
				TravelTime travelTime = travelTimeList.get(i);

				switch (travelTime.getRouteQuality(travelTimeResponse
						.getUncongestedTravelTime())) {
					case FREE_FLOW:
						this.colorControls[i].setBackgroundColor(Color.GREEN);
						break;
					case MODERATE:
						this.colorControls[i].setBackgroundColor(Color.YELLOW);
						break;
					case HEAVY:
					case STOP_AND_GO:
						this.colorControls[i].setBackgroundColor(Color.RED);
						break;
					case CLOSED:
						this.colorControls[i].setBackgroundColor(Color.DKGRAY);
						break;
					case UNKNOWN:
						break;
					default:
						break;
				}
			}
		}
	}

}
