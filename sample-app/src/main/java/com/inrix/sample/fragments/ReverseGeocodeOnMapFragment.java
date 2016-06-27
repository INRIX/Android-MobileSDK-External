/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inrix.sample.R;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.SearchManager;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.LocationMatch;

import java.util.List;

import static com.inrix.sample.util.GeoPointHelper.fromLatLng;

/**
 * Demonstrates reverse geocode functions.
 */
public class ReverseGeocodeOnMapFragment extends SupportMapFragment implements SearchManager.ISearchResponseListener, OnMapReadyCallback {
    /**
     * Default start position.
     */
    private static final GeoPoint SEATTLE_POSITION = new GeoPoint(47.614496, -122.328758);

    private GoogleMap map = null;
    private Marker marker;
    private SearchManager searchManager;
    private ICancellable searchRequest;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.searchManager = InrixCore.getSearchManager();
        this.getMapAsync(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        if (this.searchRequest != null) {
            this.searchRequest.cancel();
        }
        super.onDestroyView();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        //noinspection MissingPermission
        this.map.setMyLocationEnabled(true);
        this.map.getUiSettings().setMyLocationButtonEnabled(false);
        this.map.getUiSettings().setZoomControlsEnabled(true);

        this.map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        new LatLng(SEATTLE_POSITION.getLatitude(), SEATTLE_POSITION.getLongitude()), 12)
        );

        this.map.setOnMapLongClickListener(new MapLongClickListener());
    }

    /**
     * Shows the marker.
     *
     * @param text   Text to show.
     * @param latLng Position to show marker.
     */
    private synchronized void showMarker(String text, LatLng latLng) {
        hideMarker();
        this.marker = this.map.addMarker(new MarkerOptions().position(latLng)
                .title(text));
        this.marker.showInfoWindow();
    }

    /**
     * Hides the marker.
     */
    private synchronized void hideMarker() {
        if (this.marker != null && this.marker.isVisible()) {
            this.marker.remove();
        }
    }

    @Override
    public void onResult(List<LocationMatch> data) {
        if (data != null && data.size() > 0) {
            LocationMatch match = data.get(0);
            showMarker(match.getFormattedAddress(), new LatLng(match.getPoint().getLatitude(), match.getPoint().getLongitude()));
        } else {
            Toast.makeText(getActivity(),
                    R.string.geocode_status_no_address_found,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onError(com.inrix.sdk.Error error) {
        Toast.makeText(getActivity(), error.getErrorMessage(),
                Toast.LENGTH_LONG).show();
    }

    /**
     * Handles long clicks on the map.
     */
    private class MapLongClickListener implements OnMapLongClickListener {
        @Override
        public void onMapLongClick(LatLng latLng) {
            showMarker(getString(R.string.geocode_status_in_progress), latLng);

            if (searchRequest != null) {
                searchRequest.cancel();
                searchRequest = null;
            }

            searchRequest = searchManager.reverseGeocode(new SearchManager.ReverseGeocodeOptions(fromLatLng(latLng)), ReverseGeocodeOnMapFragment.this);
        }
    }
}
