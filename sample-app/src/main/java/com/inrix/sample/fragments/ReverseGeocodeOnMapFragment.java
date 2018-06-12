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
import com.inrix.sample.view.MapInfoWindowAdapter;
import com.inrix.sdk.Error;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.SearchManager;
import com.inrix.sdk.SearchManager.ISearchResponseListener;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.LocationMatch;
import com.inrix.sdk.model.LocationMatchGoogle;

import java.util.List;

import static com.inrix.sample.util.GeoPointHelper.fromLatLng;
import static com.inrix.sample.util.GeoPointHelper.toLatLng;

/**
 * Demonstrates reverse geocode functions.
 */
@SuppressWarnings("MissingPermission")
public class ReverseGeocodeOnMapFragment extends SupportMapFragment implements OnMapReadyCallback {
    /**
     * Default start position.
     */
    private static final GeoPoint SEATTLE_POSITION = new GeoPoint(47.614496, -122.328758);

    private GoogleMap map;
    private Marker marker;
    private SearchManager searchManager;
    private ICancellable searchRequest;

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(final LayoutInflater inflater,
                             final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.searchManager = InrixCore.getSearchManager();
        this.getMapAsync(this);
        return view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroyView() {
        if (this.searchRequest != null) {
            this.searchRequest.cancel();
        }

        super.onDestroyView();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.map = googleMap;
        this.map.setInfoWindowAdapter(new MapInfoWindowAdapter(this.getActivity()));
        this.map.setMyLocationEnabled(true);
        this.map.getUiSettings().setMyLocationButtonEnabled(false);
        this.map.getUiSettings().setZoomControlsEnabled(true);

        this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        SEATTLE_POSITION.getLatitude(),
                        SEATTLE_POSITION.getLongitude()),
                12));

        this.map.setOnMapLongClickListener(new OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                performGeocodingForLocation(fromLatLng(latLng));
            }
        });
    }

    /**
     * Perform geocoding.
     *
     * @param geoPoint Target location.
     */
    private void performGeocodingForLocation(final GeoPoint geoPoint) {
        if (this.searchRequest != null) {
            this.searchRequest.cancel();
            this.searchRequest = null;
        }

        if (this.marker != null) {
            this.marker.remove();
        }

        this.marker = this.map.addMarker(
                new MarkerOptions()
                        .position(toLatLng(geoPoint))
                        .title(getString(R.string.geocode_status_in_progress)));
        this.marker.showInfoWindow();

        this.searchRequest = searchManager.reverseGeocode(
                new SearchManager.ReverseGeocodeOptions(geoPoint),
                new ISearchResponseListener<LocationMatchGoogle>() {
                    @Override
                    public void onResult(List<LocationMatchGoogle> data) {
                        if (data != null && data.size() > 0) {
                            LocationMatch match = data.get(0);
                            showResultOnMap(match);
                        } else {
                            showError(getString(R.string.geocode_status_no_address_found));
                        }
                    }

                    @Override
                    public void onError(Error error) {
                        showError(error.getErrorMessage());
                    }
                });
    }

    /**
     * Shows geocode result on map.
     *
     * @param locationMatch Geocode result.
     */
    private void showResultOnMap(final LocationMatch locationMatch) {
        if (this.marker != null && this.marker.isVisible()) {
            this.marker.remove();
        }

        final String title = locationMatch.getLocationName() == null
                ? locationMatch.getFormattedAddress()
                : locationMatch.getLocationName();

        final LatLng markerLocation = toLatLng(locationMatch.getLocation());
        this.marker = this.map.addMarker(
                new MarkerOptions()
                        .position(markerLocation)
                        .title(title)
                        .snippet(formatResult(locationMatch)));
        this.marker.showInfoWindow();

        this.map.animateCamera(CameraUpdateFactory.newLatLng(markerLocation));
    }

    /**
     * Shows error message.
     *
     * @param message Error message.
     */
    private void showError(final String message) {
        if (this.marker != null && this.marker.isVisible()) {
            this.marker.remove();
        }

        Toast.makeText(this.getActivity(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * @param match {@link LocationMatch} to format.
     * @return String representation of {@link LocationMatch}.
     */
    private String formatResult(final LocationMatch match) {
        final String newLine = System.getProperty("line.separator");
        final StringBuilder result = new StringBuilder();
        result.append(this.getString(R.string.geocode_match_result_address, match.getFormattedAddress()));
        result.append(newLine);

        if (match.getFormattedAddress() != null) {
            result.append(this.getString(R.string.geocode_match_result_city, match.getCity()));
            result.append(newLine);
            result.append(this.getString(R.string.geocode_match_result_state, match.getState()));
            result.append(newLine);
            result.append(this.getString(R.string.geocode_match_result_postal_code, match.getStateCode()));
            result.append(newLine);
            result.append(this.getString(R.string.geocode_match_result_country, match.getCountry()));
            result.append(newLine);
        }

        if (match.getLocation() != null) {
            result.append(this.getString(R.string.geocode_match_result_point, match.getLocation().getLatitude(), match.getLocation().getLongitude()));
            result.append(newLine);
        }

        result.append(this.getString(R.string.geocode_match_result_place_id, match.getPlaceId()));
        return result.toString();
    }
}
