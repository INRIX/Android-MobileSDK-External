package com.inrix.sample.fragments;

import java.util.List;

import com.inrix.sample.R;
import com.inrix.sdk.utils.AddressLocator;
import com.inrix.sdk.utils.AddressLocator.AddressLocatorListCallback;

import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/** Demonstrates reverse geocoding functions. */
public class GeocodingReverseGeocodingFragment extends Fragment {

	/** Displays current value of max results. */
	private TextView maxResultsTextView;

	/** Allows to select max results value. */
	private SeekBar maxResultsSeekBar;

	/** Displays reverse geocoding result. */
	private TextView resultTextView;

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_reverse_geocoding,
				container,
				false);
		final EditText editTextAddress = (EditText) view
				.findViewById(R.id.edt_address);
		this.maxResultsTextView = (TextView) view
				.findViewById(R.id.txt_max_results);
		this.maxResultsSeekBar = (SeekBar) view
				.findViewById(R.id.seek_bar_max_results);
		this.maxResultsSeekBar
				.setOnSeekBarChangeListener(new SeekBarChangeListener());

		this.maxResultsTextView.setText(getString(R.string.geocode_max_results)
				+ (this.maxResultsSeekBar.getProgress() + 1));

		final Button buttonGeocodeReverse = (Button) view
				.findViewById(R.id.btn_geocode_reverse);
		this.resultTextView = (TextView) view
				.findViewById(R.id.txt_address_result);

		buttonGeocodeReverse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				resultTextView.setText(R.string.geocode_status_in_progress);
				AddressLocator geocoder = new AddressLocator(getActivity(),
						new GeocoderCallbackListener());
				geocoder.getLocations(editTextAddress.getText().toString(),
						maxResultsSeekBar.getProgress() + 1);
			}
		});
		return view;
	}

	/** Handles seek bar progress changes. */
	private class SeekBarChangeListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar,
				int progress,
				boolean fromUser) {
			maxResultsTextView.setText(getString(R.string.geocode_max_results)
					+ (maxResultsSeekBar.getProgress() + 1));
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

	}

	/** Listens geocode events. */
	private class GeocoderCallbackListener implements
			AddressLocatorListCallback {

		@Override
		public void onAddressListFound(List<Address> addresses) {
			StringBuilder stringBuilder = new StringBuilder();
			if (addresses != null) {
				int count = addresses.size();
				for (int i = 0; i < count; i++) {
					Address address = addresses.get(i);
					stringBuilder
							.append(getString(R.string.geocode_result_latitude))
							.append(address.getLatitude()).append(" ");
					stringBuilder
							.append(getString(R.string.geocode_result_longitude))
							.append(address.getLongitude()).append("\n");
				}
			}

			resultTextView.setText(getString(R.string.geocode_results)
					+ stringBuilder.toString());
		}

		@Override
		public void onGeocoderError() {
			resultTextView.setText(R.string.geocode_status_geocoder_error);
		}

		@Override
		public void onNetworkError() {
			resultTextView.setText(R.string.geocode_status_network_error);
		}

		@Override
		public void onNoAddressFound() {
			resultTextView.setText(R.string.geocode_status_no_address_found);
		}
	}
}