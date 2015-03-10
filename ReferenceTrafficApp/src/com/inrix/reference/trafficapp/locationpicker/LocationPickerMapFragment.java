/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.locationpicker;

import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.fragments.BaseMapFragment;

public class LocationPickerMapFragment extends BaseMapFragment {

	private final static String SELECTED_LOCATION = "selected_location";
	private final static String LOCATION_ADDRESS = "location_address";

	Location selectedLocation = null;
	String description = null;

	public static LocationPickerMapFragment getInstance(LatLng location,
			String address) {
		Bundle args = new Bundle();
		args.putParcelable(SELECTED_LOCATION, location);
		args.putString(LOCATION_ADDRESS, address);
		LocationPickerMapFragment f = new LocationPickerMapFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (getArguments() != null) {
			enableCurrentLocationTracking(false);
			LatLng locationToShow = getArguments()
					.getParcelable(SELECTED_LOCATION);
			selectedLocation = new Location("");
			selectedLocation.setLatitude(locationToShow.latitude);
			selectedLocation.setLongitude(locationToShow.longitude);
			addAddressMarker(locationToShow,
					getArguments().getString(LOCATION_ADDRESS));
			animateToLocation(selectedLocation);
		}
	}

	private void addAddressMarker(LatLng location, String text) {
		IconGenerator gen = new IconGenerator(getActivity());
		gen.setBackground(getActivity().getResources()
				.getDrawable(R.drawable.map_bubble_marker));
		gen.setTextAppearance(getActivity(), R.style.MapBubbleTextStyle);
		MarkerOptions options = new MarkerOptions().position(location)
				.icon(BitmapDescriptorFactory.fromBitmap(gen.makeIcon(text)));
		map.addMarker(options);
	}

}