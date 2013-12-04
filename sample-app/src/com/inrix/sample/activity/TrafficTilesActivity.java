package com.inrix.sample.activity;

import java.net.MalformedURLException;
import java.net.URL;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.UrlTileProvider;
import com.inrix.sample.R;
import com.inrix.sdk.Inrix;
import com.inrix.sdk.TileManager;
import com.inrix.sdk.TileManager.TileOptions;

public class TrafficTilesActivity extends FragmentActivity {

	private GoogleMap map;
	private LatLng DEFAULT_LOCATION = new LatLng(47.6204, -122.3491); // The
																		// Space
																		// Needle
	private final int TILE_WIDTH = 256;
	private final int TILE_HEIGHT = 256;

	private TileOverlay trafficTileOverlay;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_traffic_tiles);
		Inrix.initialize(this);
		this.map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		setUpMap();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.traffic_tiles_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.refresh) {
			this.trafficTileOverlay.clearTileCache();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setUpMap() {
		TileOverlayOptions opts = new TileOverlayOptions();
		opts.tileProvider(new InrixTrafficTileProvider(TILE_WIDTH, TILE_HEIGHT));
		trafficTileOverlay = this.map.addTileOverlay(opts);
		this.map.animateCamera(CameraUpdateFactory
				.newLatLngZoom(DEFAULT_LOCATION, 13));
	}

	class InrixTrafficTileProvider extends UrlTileProvider {

		private TileManager tilesManager;
		private TileOptions tilesOptions;

		public InrixTrafficTileProvider(int width, int height) {
			super(width, height);
			this.tilesManager = new TileManager();
			this.tilesOptions = new TileOptions();
			tilesOptions.setCoverage(TileManager.TILE_COVERAGE_ALL);
			// Request only major roads traffic
			tilesOptions.setFrcLevel(TileManager.TILE_FRC_LEVEL_1
					| TileManager.TILE_FRC_LEVEL_2
					| TileManager.TILE_FRC_LEVEL_3);
			tilesOptions.setFormat(TileManager.TILE_FORMAT_PNG);

		}

		@Override
		public URL getTileUrl(int x, int y, int zoom) {
			
			if (zoom < 9 || zoom > 16) {
				return null;
			}
			URL url = null;
			try {

				url = new URL(tilesManager.getTrafficTileUrl(x,
						y,
						zoom,
						tilesOptions));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			return url;
		}
	}
}
