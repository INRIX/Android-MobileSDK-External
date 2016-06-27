/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.activity;

import android.os.Bundle;
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

import java.net.MalformedURLException;
import java.net.URL;

public class TrafficTilesActivity extends InrixSdkActivity {
    private GoogleMap map;
    private LatLng DEFAULT_LOCATION_TULSA = new LatLng(36.2151784, -95.888836); // Mohwak peak!
    private TileOverlay trafficTileOverlay;
    private TileManager tileManager;

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_traffic_tiles;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.tileManager = InrixCore.getTileManager();
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
            if (this.trafficTileOverlay != null) {
                this.trafficTileOverlay.clearTileCache();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up google map with the tile stuff.
     */
    private void setUpMap() {
        TileOverlayOptions opts = new TileOverlayOptions();
        opts.tileProvider(new InrixTrafficTileProvider(this.tileManager));
        this.trafficTileOverlay = this.map.addTileOverlay(opts);
        this.map.animateCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION_TULSA, 10));
    }

    /**
     * A wrapper for the url tile provider.
     */
    static class InrixTrafficTileProvider extends UrlTileProvider {
        private final TileManager tileManager;

        public InrixTrafficTileProvider(TileManager tileManager) {
            super(TileManager.TILE_DEFAULT_WIDTH, TileManager.TILE_DEFAULT_HEIGHT);
            this.tileManager = tileManager;
        }

        @Override
        public URL getTileUrl(int x, int y, int zoom) {
            if (!this.tileManager.showTrafficTiles(zoom)) {
                return null;
            }
            URL url = null;
            try {
                url = new URL(this.tileManager.getTileUrl(x, y, zoom));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return url;
        }
    }
}
