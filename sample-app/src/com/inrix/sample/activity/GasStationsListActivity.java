package com.inrix.sample.activity;

import java.util.List;

import com.inrix.sample.ClientFactory;
import com.inrix.sample.R;
import com.inrix.sample.interfaces.IClient;
import com.inrix.sdk.Error;
import com.inrix.sdk.GasStationManager.GasStationsOptions;
import com.inrix.sdk.GasStationManager.GasStationsRadiusOptions;
import com.inrix.sdk.GasStationManager.IGasStationResponseListener;
import com.inrix.sdk.model.GasStation;
import com.inrix.sdk.model.GasStation.Address;
import com.inrix.sdk.model.GasStationCollection;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.utils.UserPreferences;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class GasStationsListActivity extends FragmentActivity {

	private final GeoPoint SEATTLE_POSITION = new GeoPoint(47.614496,
			-122.328758);

	private final int REQUEST_RADIUS = 5;

	// Interface to the Mobile Data
	private IClient client;

	// Loading Dialog
	ProgressDialog pd;

	/**
	 * A custom array adapter that shows a {@link SimpleView} containing
	 * details about the Gas station
	 */
	private static class CustomArrayAdapter extends ArrayAdapter<GasStation> {

		/**
		 * @param demos
		 *            An array containing the gas stations to be displayed
		 */
		public CustomArrayAdapter(Context context, GasStation[] gasStations) {
			super(context, R.layout.inrix_list_view_item,
					R.id.inrix_list_description, gasStations);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SimpleView featureView;
			if (convertView instanceof SimpleView) {
				featureView = (SimpleView) convertView;
			} else {
				featureView = new SimpleView(getContext());
			}

			GasStation gasStation = getItem(position);

			if (gasStation.getBrand() == null) {
				featureView.setTitle("UNKNOWN");
			} else {
				featureView.setTitle(gasStation.getBrand());
			}
			featureView.setDescription(getAddressString(gasStation.getAddress()));

			return featureView;
		}
		
		private String getAddressString( Address gasStationAddress ){
			if( null != gasStationAddress ){
				String strReturn = gasStationAddress.getStreet() + ", " + gasStationAddress.getCity() + ", " + gasStationAddress.getPhoneNumber();
				return strReturn;
			}
			return "Address not found";
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_ACTION_BAR);
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_gas_station_list);

		// Initialize INRIX
		initializeINRIX();

		// Clear the gas station List
		setGasStationList( null );

		pd = new ProgressDialog(this);
		pd.setMessage("loading");
		pd.show();

		// Get the gas stations for the selected city and radius
		int outputOptions = GasStationsOptions.OUTPUT_FIELDS_BRAND | GasStationsOptions.OUTPUT_FIELDS_ADDRESS
				            | GasStationsOptions.OUTPUT_FIELDS_LOCATION | GasStationsOptions.OUTPUT_FIELDS_CURRENCY_CODE
				            | GasStationsOptions.OUTPUT_FIELDS_PRODUCTS;
		int productTypes = GasStationsOptions.PRODUCT_TYPE_ALL;
		GasStationsRadiusOptions params = new GasStationsRadiusOptions(SEATTLE_POSITION, REQUEST_RADIUS, UserPreferences.UNIT.MILES, outputOptions, productTypes);
		this.client.getGasStationManager().getGasStationsInRadius(params,new IGasStationResponseListener() {

			@Override
			public void onResult(GasStationCollection data) {
				pd.dismiss();
				if( null != data && null != data.getGasStations() ){
					setGasStationList(data.getGasStations());
				}
			}

			@Override
			public void onError(Error error) {
				pd.dismiss();
				setGasStationList(null);
			}

		});

	}
	
	/**
	 * Initialize the INRIX SDK
	 */
	private void initializeINRIX() {
		this.client = ClientFactory.getInstance().getClient();
		this.client.connect(getApplicationContext());
	}

	/**
	 * 
	 * @param gas station list
	 */
	private void setGasStationList(List<GasStation> list) {
		GasStation gasStationArray[];

		if (list == null) {
			gasStationArray = new GasStation[1];
			gasStationArray[0] = new GasStation("45261", "UNKNOWN", 0.0,0.0);
		} else {
			gasStationArray = list.toArray(new GasStation[list.size()]);
		}

		CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(this, gasStationArray);
		((ListView)findViewById(R.id.gas_station_list)).setAdapter(arrayAdapter);
	}

}
