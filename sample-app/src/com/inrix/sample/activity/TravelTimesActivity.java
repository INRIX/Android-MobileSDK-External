package com.inrix.sample.activity;

import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.inrix.sample.R;
import com.inrix.sdk.Error;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.RouteManager;
import com.inrix.sdk.RouteManager.IRouteResponseListener;
import com.inrix.sdk.RouteManager.ITravelTimeResponseListener;
import com.inrix.sdk.RouteManager.RouteOptions;
import com.inrix.sdk.RouteManager.TravelTimeOptions;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Route;
import com.inrix.sdk.model.RoutesCollection;
import com.inrix.sdk.model.TravelTimeResponse;
import com.inrix.sdk.model.TripInformation.TravelTime;

public class TravelTimesActivity extends FragmentActivity {
	private final int ROUTES_TOLERANCE = 24;
	private final int ROUTES_NUM_ALTERNATES = 0;

	private RouteManager routeManager;
	private Route route;

	private TravelTimeResponse travelTimeResponse;

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

		// Init INRIX
		InrixCore.initialize(this);

		// Init Controls
		initColorControls();

		// Request the route
		GeoPoint START_LOCATION = new GeoPoint(47.602633, -122.336243);
		GeoPoint END_LOCATION = new GeoPoint(47.616853, -122.193044);

		RouteOptions routeParams = new RouteOptions(START_LOCATION,
				END_LOCATION);
		routeParams.setTolerance(ROUTES_TOLERANCE);
		routeParams.setNumAlternates(ROUTES_NUM_ALTERNATES);

		this.routeManager = InrixCore.getRouteManager();

		this.routeManager.requestRoutes(routeParams,
				new IRouteResponseListener() {

					@Override
					public void onResult(RoutesCollection data) {
						route = data.getRoutes().get(0);
						((TextView) findViewById(R.id.route_summary))
								.setText(route.getSummary().getText());
						getTravelTimes();
					}

					@Override
					public void onError(Error error) {
					}
				});
	}

	/**
	 * Get the Travel Times for the Route
	 */
	private void getTravelTimes() {
		TravelTimeOptions travelTimeOptions = new TravelTimeOptions(route,
				COLOR_CONTROL_IDS.length,
				60 * DateUtils.SECOND_IN_MILLIS);

		routeManager.requestTravelTimes(travelTimeOptions,
				new ITravelTimeResponseListener() {

					@Override
					public void onResult(TravelTimeResponse travelTimeResult) {
						travelTimeResponse = travelTimeResult;
						updateColors();
					}

					@Override
					public void onError(Error error) {
						Toast.makeText(TravelTimesActivity.this,
								"Failed to load travel times. Try again",
								Toast.LENGTH_LONG).show();
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
	private void updateColors() {
		if (this.route != null) {
			List<TravelTime> travelTimeList = travelTimeResponse.getTravelTimes();

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
