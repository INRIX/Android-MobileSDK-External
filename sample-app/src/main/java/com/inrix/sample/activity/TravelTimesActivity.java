/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.inrix.sample.R;
import com.inrix.sdk.Error;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.RouteManager;
import com.inrix.sdk.RouteManager.IRouteResponseListener;
import com.inrix.sdk.RouteManager.ITravelTimeResponseListener;
import com.inrix.sdk.RouteManager.RequestRouteOptions;
import com.inrix.sdk.RouteManager.TravelTimeOptions;
import com.inrix.sdk.model.RequestRouteResults;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Route;
import com.inrix.sdk.model.RouteTravelTime;
import com.inrix.sdk.model.TravelTime;

import java.util.List;

public class TravelTimesActivity extends InrixSdkActivity {
    private static final int ROUTES_TOLERANCE = 24;
    private static final int ROUTES_NUM_ALTERNATES = 0;

    private RouteManager routeManager;
    private Route route;

    private RouteTravelTime routeTravelTime;

    TextView colorControls[];

    private static int COLOR_CONTROL_IDS[] = {R.id.route_color1,
            R.id.route_color2, R.id.route_color3, R.id.route_color4,
            R.id.route_color5, R.id.route_color6, R.id.route_color7,
            R.id.route_color8, R.id.route_color9, R.id.route_color10,};

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_travel_times;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init Controls
        initColorControls();

        // Request the route
        GeoPoint START_LOCATION = new GeoPoint(47.602633, -122.336243);
        GeoPoint END_LOCATION = new GeoPoint(47.616853, -122.193044);

        RequestRouteOptions routeParams = new RequestRouteOptions(START_LOCATION, END_LOCATION);

        routeParams.setTolerance(ROUTES_TOLERANCE);
        routeParams.setNumAlternates(ROUTES_NUM_ALTERNATES);

        this.routeManager = InrixCore.getRouteManager();

        this.routeManager.requestRoutes(routeParams,
                new IRouteResponseListener() {

                    @Override
                    public void onResult(RequestRouteResults data) {
                        route = data.getRoutes().get(0);
                        ((TextView) findViewById(R.id.route_summary))
                                .setText(route.getSummary().getText());
                        getTravelTimes();
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
     * Get the Travel Times for the Route
     */
    private void getTravelTimes() {
        TravelTimeOptions travelTimeOptions = new TravelTimeOptions(route,
                COLOR_CONTROL_IDS.length,
                60 * DateUtils.SECOND_IN_MILLIS);

        routeManager.requestTravelTimes(travelTimeOptions,
                new ITravelTimeResponseListener() {

                    @Override
                    public void onResult(RouteTravelTime response) {
                        routeTravelTime = response;
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
            List<TravelTime> travelTimeList = routeTravelTime.getTravelTimes();

            for (int i = 0; i < COLOR_CONTROL_IDS.length; i++) {
                TravelTime travelTime = travelTimeList.get(i);

                switch (travelTime.getRouteQuality(routeTravelTime
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
