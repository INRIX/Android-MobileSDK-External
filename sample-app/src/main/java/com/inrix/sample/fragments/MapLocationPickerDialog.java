/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inrix.sample.R;

public class MapLocationPickerDialog extends DialogFragment {
    public interface OnLocationSelectedListener {
        void onLocationSelected(final LatLng location);
    }

    protected static final String MAP_TAG = MapLocationPickerDialog.class.getCanonicalName() + ":map";

    protected static final LatLng DEFAULT_CENTER = new LatLng(47.620405, -122.349363);
    protected static final float DEFAULT_ZOOM = 11f;

    protected Marker marker;

    protected OnLocationSelectedListener listener;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();

        final Dialog dialog = this.getDialog();
        if (dialog == null) {
            return;
        }

        this.setStyle(STYLE_NO_TITLE, R.style.Dialog);

        //noinspection ConstantConditions
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.dialog_map_location_picker, container, false);

        final Fragment fragment = this.getChildFragmentManager().findFragmentByTag(MAP_TAG);

        final SupportMapFragment mapFragment;
        if (fragment == null) {
            mapFragment = new SupportMapFragment();
            this.getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.map, mapFragment, MAP_TAG)
                    .commit();
        } else {
            mapFragment = (SupportMapFragment) fragment;
        }

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mapReady(googleMap);
            }
        });

        root.findViewById(R.id.select).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final LatLng location = marker == null ? DEFAULT_CENTER : marker.getPosition();
                if (listener != null) {
                    listener.onLocationSelected(location);
                }

                dismiss();
            }
        });

        return root;
    }

    public void setOnLocationSelectedListener(final OnLocationSelectedListener listener) {
        this.listener = listener;
    }

    protected void mapReady(final GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_CENTER, DEFAULT_ZOOM));

        createMarkerAtLocation(googleMap, DEFAULT_CENTER);

        googleMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (marker != null) {
                    marker.remove();
                }

                createMarkerAtLocation(googleMap, latLng);
            }
        });
    }

    protected void createMarkerAtLocation(GoogleMap googleMap, final LatLng location) {
        final MarkerOptions opts = new MarkerOptions();
        opts.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        opts.position(location);
        this.marker = googleMap.addMarker(opts);
    }
}
