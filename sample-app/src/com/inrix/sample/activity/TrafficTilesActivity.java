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
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.TileManager;

public class TrafficTilesActivity extends FragmentActivity {
	private GoogleMap map;
	private LatLng DEFAULT_LOCATION = new LatLng(47.6204, -122.3491); // The Needle
	private TileOverlay trafficTileOverlay;
	private final TileManager tileManager = InrixCore.getTileManager();

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_traffic_tiles);
		InrixCore.initialize(this);
		this.map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
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
		opts.tileProvider(new InrixTrafficTileProvider());
		trafficTileOverlay = this.map.addTileOverlay(opts);
		this.map.animateCamera(CameraUpdateFactory
				.newLatLngZoom(DEFAULT_LOCATION, 13));
	}

	class InrixTrafficTileProvider extends UrlTileProvider {
		private final TileManager tilesManager;

		public InrixTrafficTileProvider() {
			super(TileManager.TILE_DEFAULT_WIDTH, TileManager.TILE_DEFAULT_HEIGHT);
			this.tilesManager = InrixCore.getTileManager();
		}

		@Override
		public URL getTileUrl(int x, int y, int zoom) {
			if (!tileManager.showTrafficTiles(zoom)) {
				return null;
			}

			URL url = null;
			try {
				url = new URL(tilesManager.getTileUrl(x, y, zoom));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			return url;
		}
	}
}
