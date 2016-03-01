/**
 * Copyright (c) 2013-2015 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.inrix.sample.R;
import com.inrix.sample.map.PolylineRouteOverlay;
import com.inrix.sample.map.TileRouteOverlay;
import com.inrix.sdk.Error;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.RouteManager;
import com.inrix.sdk.RouteManager.IRouteResponseListener;
import com.inrix.sdk.RouteManager.IUpdatedRouteListener;
import com.inrix.sdk.RouteManager.RequestRouteOptions;
import com.inrix.sdk.RouteManager.UpdatedRouteOptions;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.RequestRouteResults;
import com.inrix.sdk.model.Route;
import com.inrix.sdk.model.UpdatedRouteResults;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RouteOnMapActivity extends InrixSdkActivity {
    private static final String IS_TILE_ROUTE_ENABLED_EXTRA = "IS_TILE_ROUTE_ENABLED";
    private static final LatLng SEATTLE = new LatLng(47.616278, -122.352327);
    private static final LatLng BELLEVUE = new LatLng(47.607135, -122.153715);

    private GoogleMap map;

    @Bind(R.id.progress_bar)
    protected ProgressBar progressBar;

    private RouteManager routeManager;
    private ICancellable currentRequest;

    private boolean isTileBasedRoute = true;
    private List<Route> routes;

    /*
     * 2 different ways to display routes.
     * 1) Polyline-based is easier to implement, but it is not flexible - you
     * get only polyline and you can only change its color and/or thickness.
     * Looks weird when overlap traffic tiles
     * 2) Tile-based - trickier to implement, but you can control entire drawing
     * process and you can make it look like you designed
     */
    private PolylineRouteOverlay polylineRouteOverlay = null;
    private TileRouteOverlay tileRouteOverlay = null;

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_route_on_map;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        setUpMapIfNeeded();
        this.routeManager = InrixCore.getRouteManager();

        if (savedInstanceState != null) {
            this.isTileBasedRoute = savedInstanceState.getBoolean("IS_TILE_ROUTE_ENABLED_EXTRA");
        }
    }

    private void setUpMapIfNeeded() {
        if (this.map != null) {
            return;
        }
        this.map = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(SEATTLE, 13));
        this.polylineRouteOverlay = new PolylineRouteOverlay(map);
        this.tileRouteOverlay = new TileRouteOverlay(this, map);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_TILE_ROUTE_ENABLED_EXTRA, isTileBasedRoute);
        super.onSaveInstanceState(outState);
    }

    private void findRoutes() {
        if (currentRequest != null) {
            currentRequest.cancel();
            currentRequest = null;
        }

        routes = null;

        RequestRouteOptions params = new RequestRouteOptions(
                new GeoPoint(SEATTLE.latitude, SEATTLE.longitude),
                new GeoPoint(BELLEVUE.latitude, BELLEVUE.longitude));

        params.setOutputFields(
                RouteManager.ROUTE_OUTPUT_FIELD_BOUNDING_BOX |
                        RouteManager.ROUTE_OUTPUT_FIELD_POINTS |
                        RouteManager.ROUTE_OUTPUT_FIELD_SUMMARY);

        params.setNumAlternates(1);
        params.setSpeedBucketsEnabled(true);

        progressBar.setVisibility(View.VISIBLE);

        this.tileRouteOverlay.clear();
        this.polylineRouteOverlay.clear();
        this.currentRequest = routeManager.requestRoutes(params,
                new IRouteResponseListener() {

                    @Override
                    public void onResult(RequestRouteResults data) {
                        currentRequest = null;
                        routes = data.getRoutes();
                        if (isTileBasedRoute) {
                            tileRouteOverlay.displayRoute(data.getRoutes());
                        } else {
                            polylineRouteOverlay.displayRoute(data.getRoutes());
                        }
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(RouteOnMapActivity.this, "Unable to find routes: " + error.toString(),
                                Toast.LENGTH_LONG).show();
                        currentRequest = null;
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void getUpdatedRouteInfo(Route route) {
        if (route == null) {
            return;
        }

        if (currentRequest != null) {
            currentRequest.cancel();
            currentRequest = null;
        }

        routes = null;

        //Simulate current location as 1/5 of the way along the route.
        List<GeoPoint> routePoints = route.getPoints();
        GeoPoint currentLocation = routePoints.get((int) (routePoints.size() / 5));

        UpdatedRouteOptions options = new UpdatedRouteOptions(route, currentLocation, true);
        options.setOutputFields(RouteManager.ROUTE_OUTPUT_FIELD_BOUNDING_BOX
                | RouteManager.ROUTE_OUTPUT_FIELD_POINTS | RouteManager.ROUTE_OUTPUT_FIELD_SUMMARY);
        options.setSpeedBucketsEnabled(true);

        progressBar.setVisibility(View.VISIBLE);

        this.tileRouteOverlay.clear();
        this.polylineRouteOverlay.clear();
        this.currentRequest = routeManager.getUpdatedRouteInfo(options, new IUpdatedRouteListener() {
            @Override
            public void onResult(UpdatedRouteResults data) {
                currentRequest = null;
                routes = data.getRoutes();
                if (isTileBasedRoute) {
                    tileRouteOverlay.displayRoute(data.getRoutes());
                } else {
                    polylineRouteOverlay.displayRoute(data.getRoutes());
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Error error) {
                Toast.makeText(RouteOnMapActivity.this, "Unable to get updated route info: " + error.toString(),
                        Toast.LENGTH_LONG).show();
                currentRequest = null;
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.route_on_map_menu, menu);
        menu.findItem(R.id.tile_route).setChecked(isTileBasedRoute);
        menu.findItem(R.id.polyline_route).setChecked(!isTileBasedRoute);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_find_routes:
                findRoutes();
                break;
            case R.id.action_update_route:
                if (routes == null || routes.size() == 0) {
                    Toast.makeText(this, "No routes to update...", Toast.LENGTH_LONG).show();
                } else if (routes.size() == 1) {
                    getUpdatedRouteInfo(routes.get(0));
                } else {
                    createGetUpdatedRouteDialog();
                }
                break;
            case R.id.tile_route:
                isTileBasedRoute = true;
                invalidateOptionsMenu();
                findRoutes();
                break;
            case R.id.polyline_route:
                isTileBasedRoute = false;
                invalidateOptionsMenu();
                findRoutes();
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        findRoutes();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentRequest != null) {
            currentRequest.cancel();
            currentRequest = null;
        }
    }

    private void createGetUpdatedRouteDialog() {
        final int count = routes.size();
        final CharSequence[] items = new CharSequence[count];
        for (int i = 0; i < count; i++) {
            items[i] = (i + 1) + ": " + routes.get(i).getSummary().getText();
        }

        new AlertDialog.Builder(this)
                .setTitle("Which route?")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (routes != null && which < routes.size()) {
                            getUpdatedRouteInfo(routes.get(which));
                        }
                    }
                })
                .show();
    }
}
