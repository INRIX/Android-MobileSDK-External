/**
 * Copyright (c) 2013-2015 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inrix.sample.R;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.SearchManager;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.LocationMatch;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for POI search
 */
public class LocationSearchActivity extends InrixSdkActivity implements SearchManager.ISearchResponseListener {

    /**
     * The map.
     */
    private GoogleMap map = null;

    /**
     * Displays POI address.
     */
    private List<Marker> resultMarkers;

    private Marker searchMarker;

    /**
     * The POI Manager
     */
    private SearchManager manager;

    /**
     * Default start position.
     */
    private final GeoPoint SEATTLE_POSITION = new GeoPoint(47.614496, -122.328758);

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.manager = InrixCore.getSearchManager();
        setUpMapIfNeeded();
    }

    /**
     * Initializes the map if it wasn't initialized yet.
     */
    private void setUpMapIfNeeded() {
        if (this.map != null) {
            return;
        }
        this.map = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();
        if (this.map != null) {
            this.map.setMyLocationEnabled(true);
            this.map.getUiSettings().setMyLocationButtonEnabled(false);
            this.map.getUiSettings().setZoomControlsEnabled(true);

            this.map.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(SEATTLE_POSITION.getLatitude(),
                            SEATTLE_POSITION.getLongitude()), 12));

            this.map.setOnMapLongClickListener(new MapLongClickListener());
        }
    }

    /**
     * Shows the marker.
     *
     * @param text   Text to show.
     * @param latLng Position to show marker.
     */
    private synchronized void showSearchMarker(String text, LatLng latLng) {

        hideMarkers(this.resultMarkers);

        hideMarkers(this.searchMarker);

        this.searchMarker = this.map.addMarker(new MarkerOptions().position(latLng).title(text));

        this.searchMarker.showInfoWindow();
    }

    /**
     * Hides the marker.
     */

    private synchronized void hideMarkers(List<Marker> markers) {
        if (markers != null) {
            for (Marker marker : markers) {
                if (marker != null && marker.isVisible()) {
                    marker.remove();
                }
            }
        }
    }

    private synchronized void hideMarkers(Marker... markers) {
        if (markers != null) {
            for (Marker marker : markers) {
                if (marker != null && marker.isVisible()) {
                    marker.remove();
                }
            }
        }
    }

    /**
     * Handles long clicks on the map.
     */
    private class MapLongClickListener implements GoogleMap.OnMapLongClickListener {
        @Override
        public void onMapLongClick(LatLng latLng) {
            showSearchMarker(getString(R.string.location_search_searching), latLng);

            final GeoPoint point = new GeoPoint(latLng.latitude, latLng.longitude);

            SearchManager.NearbySearchOptions options = new SearchManager.
                    NearbySearchOptions("restaurant", point).
                    setRadius(500);

            manager.searchNearby(options,
                    LocationSearchActivity.this);
        }
    }

    @Override
    public void onResult(final List<LocationMatch> data) {
        if (data != null && data.size() > 0) {
            this.resultMarkers = new ArrayList<>(data.size());

            for (LocationMatch match : data) {

                GeoPoint point = match.getPoint();

                final LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());

                StringBuilder description = new StringBuilder();

                if (!TextUtils.isEmpty(match.getLocationName())) {
                    description.append(match.getLocationName());
                }

                if (!TextUtils.isEmpty(match.getFormattedAddress())) {
                    if (description.length() > 0) {
                        description.append(": ");
                    }
                    description.append(match.getFormattedAddress());
                }

                Marker result = this.map.addMarker(new MarkerOptions().position(latLng).title(description.toString()));

                result.showInfoWindow();

                resultMarkers.add(result);
            }
            hideMarkers(this.searchMarker);
        } else {
            // No matches
            hideMarkers(this.searchMarker);
            Toast.makeText(this, R.string.location_search_no_matches, Toast.LENGTH_LONG).show();
            this.resultMarkers = null;
        }
    }

    @Override
    public void onError(final com.inrix.sdk.Error error) {
        hideMarkers(this.searchMarker);
        Toast.makeText(this, getString(R.string.location_search_error, error.toString()), Toast.LENGTH_LONG).show();
    }
}
