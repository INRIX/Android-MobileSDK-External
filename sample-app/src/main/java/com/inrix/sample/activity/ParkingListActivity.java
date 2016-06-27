/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.inrix.sample.R;
import com.inrix.sample.fragments.ProgressFragment;
import com.inrix.sdk.Error;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.ParkingManager;
import com.inrix.sdk.ParkingManager.IParkingInfoResponseListener;
import com.inrix.sdk.ParkingManager.ParkingRadiusOptions;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.ParkingInfo;
import com.inrix.sdk.model.ParkingLot;
import com.inrix.sdk.model.ParkingLot.Address;
import com.inrix.sdk.utils.UserPreferences;
import com.inrix.sdk.utils.UserPreferences.Unit;

import java.util.List;

public class ParkingListActivity extends InrixSdkActivity {
    private final static GeoPoint SEATTLE_POSITION = new GeoPoint(47.614496, -122.328758);

    private int requestRadius = 5;
    private ParkingManager parkingManager;

    // Loading Dialog
    private ICancellable currentOperation = null;

    /**
     * A custom array adapter that shows a {@link SimpleView} containing details
     * about the parking lot
     */
    private static class CustomArrayAdapter extends ArrayAdapter<ParkingLot> {

        /**
         * @param parkingLots An array containing the parking lots to be displayed
         */
        public CustomArrayAdapter(Context context, ParkingLot[] parkingLots) {
            super(context,
                    R.layout.inrix_list_view_item,
                    R.id.inrix_list_description,
                    parkingLots);
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
                    + String.format("%.2f "
                            + ((UserPreferences.getSettingUnits() == Unit.MILES) ? "miles"
                            : "km"),
                    parkingLot.getDistance(SEATTLE_POSITION));
            if ((parkingLot.getStaticContent() != null) &&
                    (parkingLot.getStaticContent().getPricingPayment() != null) &&
                    (parkingLot.getStaticContent().getPricingPayment().size() > 0) &&
                    (parkingLot.getStaticContent().getPricingPayment().get(0).isPriceAvailable())) {
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

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_gas_station_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.parkingManager = InrixCore.getParkingManager();

        // Clear the gas station List
        setParkingLotList(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            refreshData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshData() {
        if (this.parkingManager == null) {
            return;
        }

        if (currentOperation != null) {
            currentOperation.cancel();
            currentOperation = null;
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(new ProgressFragment(), "").addToBackStack("")
                    .commit();
        }

        // Get the parking lots for the selected city and radius
        final ParkingRadiusOptions options = new ParkingRadiusOptions(SEATTLE_POSITION,
                this.requestRadius);
        options.setOutputFields(ParkingManager.PARKING_OUTPUT_FIELD_BASIC | ParkingManager.PARKING_OUTPUT_FIELD_PRICING);

        currentOperation = parkingManager.getParkingInfoInRadius(
                options,
                new IParkingInfoResponseListener() {
                    @Override
                    public void onResult(ParkingInfo data) {
                        getSupportFragmentManager().popBackStack();
                        currentOperation = null;
                        setParkingLotList(data.getParkingLots());
                    }

                    @Override
                    public void onError(Error error) {
                        getSupportFragmentManager().popBackStack();
                        currentOperation = null;
                        setParkingLotList(null);
                    }
                });
    }

    @Override
    protected void onStop() {
        if (this.currentOperation != null) {
            this.currentOperation.cancel();
            this.currentOperation = null;
            getSupportFragmentManager().popBackStack();
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshData();
    }

    private void setParkingLotList(List<ParkingLot> list) {
        if (list != null) {
            ParkingLot parkingLotArray[];
            parkingLotArray = list.toArray(new ParkingLot[list.size()]);
            CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(this, parkingLotArray);
            ((ListView) findViewById(R.id.gas_station_list)).setAdapter(arrayAdapter);
        }
    }
}
