package com.inrix.sample.fragments;

import java.util.List;

import android.location.Address;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inrix.sample.R;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.utils.AddressLocator;
import com.inrix.sdk.utils.AddressLocator.AddressLocatorListCallback;

/** Demonstrates geocoding functions. */
public class GeocodingOnMapFragment extends SupportMapFragment {

	/** The map. */
	private GoogleMap map = null;

	/** Displays geocoded address. */
	private Marker marker;

	/** Default start position. */
	private final GeoPoint SEATTLE_POSITION = new GeoPoint(47.614496,
			-122.328758);

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		setUpMapIfNeeded();
		return view;
	}

	/** Initializes the map if it wasn't initialized yet. */
	private void setUpMapIfNeeded() {
		if (this.map != null) {
			return;
		}
		this.map = getMap();
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
	 * @param text
	 *            Text to show.
	 * @param latLng
	 *            Position to show marker.
	 */
	private synchronized void showMarker(String text, LatLng latLng) {
		hideMarker();
		this.marker = this.map.addMarker(new MarkerOptions().position(latLng)
				.title(text));
		this.marker.showInfoWindow();
	}

	/** Hides the marker. */
	private synchronized void hideMarker() {
		if (this.marker != null && this.marker.isVisible()) {
			this.marker.remove();
		}
	}

	/** Handles long clicks on the map. */
	private class MapLongClickListener implements OnMapLongClickListener {
		@Override
		public void onMapLongClick(LatLng latLng) {
			showMarker(getString(R.string.geocode_status_in_progress), latLng);
			AddressLocator geocoder = new AddressLocator(getActivity(),
					new GeocoderCallbackListener());
			geocoder.getAddress((float) latLng.latitude,
					(float) latLng.longitude);
		}
	}

	/** Listens geocode events. */
	private class GeocoderCallbackListener implements
			AddressLocatorListCallback {

		@Override
		public void onAddressListFound(List<Address> addresses) {
			StringBuilder stringBuilder = new StringBuilder();
			Address address;
			if (addresses != null && addresses.size() > 0
					&& (address = addresses.get(0)) != null) {
				int count = address.getMaxAddressLineIndex();
				for (int i = count; i >= 0; i--) {
					String addressLine = address.getAddressLine(i);
					if (addressLine == null || addressLine.isEmpty()) {
						continue;
					}
					if (stringBuilder.length() > 0) {
						stringBuilder.append(", ");
					}
					stringBuilder.append(addressLine);
				}

				LatLng latLng = new LatLng(address.getLatitude(),
						address.getLongitude());
				showMarker(stringBuilder.toString(), latLng);
			}
		}

		@Override
		public void onGeocoderError() {
			hideMarker();
			Toast.makeText(getActivity(),
					R.string.geocode_status_geocoder_error,
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onNetworkError() {
			hideMarker();
			Toast.makeText(getActivity(),
					R.string.geocode_status_network_error,
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onNoAddressFound() {
			hideMarker();
			Toast.makeText(getActivity(),
					R.string.geocode_status_no_address_found,
					Toast.LENGTH_LONG).show();
		}
	}
}
