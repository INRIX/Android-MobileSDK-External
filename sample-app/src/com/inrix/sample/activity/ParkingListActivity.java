package com.inrix.sample.activity;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.inrix.sample.ClientFactory;
import com.inrix.sample.R;
import com.inrix.sample.interfaces.IClient;
import com.inrix.sdk.Error;
import com.inrix.sdk.InrixDebug;
import com.inrix.sdk.ParkingManager;
import com.inrix.sdk.ParkingManager.IParkingResponseListener;
import com.inrix.sdk.ParkingManager.ParkingInRadiusOptions;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.ParkingLot;
import com.inrix.sdk.model.ParkingLot.Address;
import com.inrix.sdk.utils.UserPreferences;
import com.inrix.sdk.utils.UserPreferences.UNIT;

public class ParkingListActivity extends FragmentActivity {

	private final static GeoPoint SEATTLE_POSITION = new GeoPoint(47.614496,
			-122.328758);

	private int requestRadius = 5;

	// Interface to the Mobile Data
	private IClient client;

	// Loading Dialog
	ProgressDialog pd;

	/**
	 * A custom array adapter that shows a {@link SimpleView} containing details
	 * about the parking lot
	 */
	private static class CustomArrayAdapter extends ArrayAdapter<ParkingLot> {

		/**
		 * @param demos
		 *            An array containing the parking lots to be displayed
		 */
		public CustomArrayAdapter(Context context, ParkingLot[] parkingLots) {
			super(context, R.layout.inrix_list_view_item,
					R.id.inrix_list_description, parkingLots);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			SimpleView featureView;
			if (convertView instanceof SimpleView) {
				featureView = (SimpleView) convertView;
			} else {
				featureView = new SimpleView(getContext());
			}

			ParkingLot parkingLot = getItem(position);

			String title = "UNKNOWN";
			if (parkingLot.getName() != null) {
				title = parkingLot.getName();
			}
			title = title
					+ " "
					+ String.format("%.2f " + ( (UserPreferences.getSettingUnits() == UNIT.MILES) ? "miles" : "km"),
							parkingLot.getDistance(SEATTLE_POSITION));
			if ( ( parkingLot.getStaticContent() != null ) &&
				 ( parkingLot.getStaticContent().getPricingPayment() != null ) &&
				 ( parkingLot.getStaticContent().getPricingPayment().size() > 0 ) ) {
				title = title + String.format(" Amount = %.2f ", parkingLot.getStaticContent().getPricingPayment().get(0).getAmount());				
			}
			featureView.setTitle(title);

			if (null != parkingLot
					&& null != parkingLot.getStaticContent()
					&& null != parkingLot.getStaticContent().getInformation()
					&& null != parkingLot.getStaticContent().getInformation()
							.getAddress()) {
				Address parkingLotAddress = parkingLot.getStaticContent()
						.getInformation().getAddress();
				featureView.setDescription(getAddressString(parkingLotAddress));
			} else {
				featureView.setDescription("UNKNOWN ADDRESS");
			}

			return featureView;
		}

		private String getAddressString(Address parkingLotAddress) {
			if (null != parkingLotAddress) {
				String strReturn = parkingLotAddress.getStreet() + ", "
						+ parkingLotAddress.getCity() + ", "
						+ parkingLotAddress.getPhoneNumber();
				return strReturn;
			}
			return "Address not found";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
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
		setParkingLotList(null);

		pd = new ProgressDialog(this);
		pd.setMessage("loading");
		pd.show();

		// Get the parking lots for the selected city and radius

		final ParkingInRadiusOptions options = new ParkingInRadiusOptions(SEATTLE_POSITION,
				this.requestRadius);
		options.setOutputFields(ParkingManager.PARKING_OUTPUT_FIELD_BASIC | ParkingManager.PARKING_OUTPUT_FIELD_PRICING);

		this.client.getParkingManager().getParkingLotsInRadius(
				new IParkingResponseListener() {

					@Override
					public void onResult(List<ParkingLot> data) {
						pd.dismiss();
						setParkingLotList(data);
					}

					@Override
					public void onError(Error error) {
						pd.dismiss();
						setParkingLotList(null);
						InrixDebug.LogD(error.getErrorMessage());
					}

				}, options);

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
	 * @param gas
	 *            station list
	 */
	private void setParkingLotList(List<ParkingLot> list) {
		ParkingLot parkingLotArray[];

		if (list == null) {
			parkingLotArray = new ParkingLot[1];
			parkingLotArray[0] = new ParkingLot();
		} else {
			parkingLotArray = list.toArray(new ParkingLot[list.size()]);
		}

		CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(this,
				parkingLotArray);
		((ListView) findViewById(R.id.gas_station_list))
				.setAdapter(arrayAdapter);
	}

}
