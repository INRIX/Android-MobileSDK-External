package com.inrix.sample.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import com.inrix.sample.map.TileRouteOverlay;
import com.inrix.sample.map.PolylineRouteOverlay;
import com.inrix.sdk.Error;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.RouteManager;
import com.inrix.sdk.RouteManager.IRouteResponseListener;
import com.inrix.sdk.RouteManager.RouteOptions;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.RoutesCollection;

public class RouteOnMapActivity extends FragmentActivity {

	private boolean isTileBasedRoute = true;
	private final String IS_TILE_ROUTE_ENABLED_EXTRA = "IS_TILE_ROUTE_ENABLED";

	private GoogleMap map;
	private LatLng SEATTLE = new LatLng(47.616278, -122.352327);

	private LatLng BELLEVUE = new LatLng(47.607135, -122.153715);
	private RouteManager routeManager;

	private ICancellable currentRequest = null;

	private ProgressBar progressBar;

	/*
	 * 2 different ways to display routes.
	 * 
	 * 1) Polyline-based is easier to implement, but it is not flexible - you
	 * get only polyline and you can only change its color and/or thickness.
	 * Looks weird when overlap traffic tiles
	 * 
	 * 2) Tile-based - trickier to implement, but you can control entire drawing
	 * process and you can make it look like you designed
	 */
	private PolylineRouteOverlay polylineRouteOverlay = null;
	private TileRouteOverlay tileRouteOverlay = null;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_route_on_map);
		this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);
		InrixCore.initialize(this);
		setUpMapIfNeeded();
		this.routeManager = InrixCore.getRouteManager();

		if (bundle != null) {
			isTileBasedRoute = bundle.getBoolean("IS_TILE_ROUTE_ENABLED_EXTRA");
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

	private void requestRoute() {
		if (currentRequest != null) {
			currentRequest.cancel();
			currentRequest = null;
		}

		RouteOptions params = new RouteOptions(new GeoPoint(SEATTLE.latitude,
				SEATTLE.longitude), new GeoPoint(BELLEVUE.latitude,
				BELLEVUE.longitude));
		params.setOutputFields(RouteManager.ROUTE_OUTPUT_FIELD_BOUNDING_BOX
				| RouteManager.ROUTE_OUTPUT_FIELD_POINTS);
		params.setNumAlternates(1);
		params.setSpeedBucketsEnabled(true);
		progressBar.setVisibility(View.VISIBLE);

		this.tileRouteOverlay.clear();
		this.polylineRouteOverlay.clear();
		this.currentRequest = routeManager.requestRoutes(params,
				new IRouteResponseListener() {

					@Override
					public void onResult(RoutesCollection data) {
						currentRequest = null;
						if (isTileBasedRoute) {
							tileRouteOverlay.displayRoute(data);
						} else {
							polylineRouteOverlay.displayRoute(data);
						}
						progressBar.setVisibility(View.GONE);
					}

					@Override
					public void onError(Error error) {
						Toast.makeText(RouteOnMapActivity.this,
								"Unable to get route: " + error.toString(),
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
		case R.id.action_refresh:
			requestRoute();
			break;
		case R.id.tile_route:
			isTileBasedRoute = true;
			invalidateOptionsMenu();
			requestRoute();
			break;
		case R.id.polyline_route:
			isTileBasedRoute = false;
			invalidateOptionsMenu();
			requestRoute();
			break;

		default:
			break;
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		requestRoute();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (currentRequest != null) {
			currentRequest.cancel();
			currentRequest = null;
		}
		InrixCore.shutdown();
	}
}
