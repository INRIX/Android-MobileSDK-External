package com.inrix.sample.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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

import java.util.ArrayList;
import java.util.List;

import static com.inrix.sample.util.GeoPointHelper.fromLatLng;
import static com.inrix.sample.util.GeoPointHelper.toLatLng;

/**
 * Demonstrates nearby search
 */
public class NearbySearchOnMapFragment extends SupportMapFragment implements
        SearchManager.ISearchResponseListener,
        OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener {

    /**
     * Default search query for all nearby searches in this sample.
     */
    private static final String SEARCH_QUERY = "restaurant";

    private static final GeoPoint START_POSITION = new GeoPoint(47.614496, -122.328758); //Seattle, WA

    private GoogleMap map = null;
    private List<Marker> resultMarkers;
    private Marker searchMarker;
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

        this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(toLatLng(START_POSITION), 12));
        this.map.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        showSearchMarker(getString(R.string.location_search_searching), latLng);

        final GeoPoint point = fromLatLng(latLng);
        SearchManager.NearbySearchOptions options = new SearchManager.
                NearbySearchOptions(SEARCH_QUERY, point).
                setRadius(500);

        searchRequest = searchManager.searchNearby(options, NearbySearchOnMapFragment.this);
    }

    @Override
    public void onResult(final List<LocationMatch> data) {
        if (data != null && data.size() > 0) {
            hideMarker(this.searchMarker);

            this.resultMarkers = new ArrayList<>(data.size());
            for (LocationMatch match : data) {
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

                final LatLng latLng = toLatLng(match.getPoint());
                Marker result = this.map.addMarker(
                        new MarkerOptions()
                                .position(latLng)
                                .title(description.toString()));

                result.showInfoWindow();
                resultMarkers.add(result);
            }
        } else {
            // No matches
            hideMarker(this.searchMarker);
            this.resultMarkers = null;
            Toast.makeText(getActivity(), R.string.location_search_no_matches, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onError(final com.inrix.sdk.Error error) {
        hideMarker(this.searchMarker);
        Toast.makeText(getActivity(), getString(R.string.location_search_error, error.toString()), Toast.LENGTH_LONG).show();
    }

    /**
     * Shows the marker.
     *
     * @param text   Text to show.
     * @param latLng Position to show marker.
     */
    private synchronized void showSearchMarker(String text, LatLng latLng) {
        hideMarkers(this.resultMarkers);
        hideMarker(this.searchMarker);

        this.searchMarker = this.map.addMarker(new MarkerOptions().position(latLng).title(text));
        this.searchMarker.showInfoWindow();
    }

    /**
     * Hide the markers.
     *
     * @param markers The markers to hide.
     */
    private synchronized void hideMarkers(List<Marker> markers) {
        if (markers == null) {
            return;
        }

        for (Marker marker : markers) {
            hideMarker(marker);
        }
    }

    /**
     * Hide the marker.
     *
     * @param marker The marker to hide.
     */
    private synchronized void hideMarker(Marker marker) {
        if (marker != null && marker.isVisible()) {
            marker.remove();
        }
    }
}
