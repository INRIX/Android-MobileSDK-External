/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.inrix.sample.R;
import com.inrix.sdk.Error;
import com.inrix.sdk.GasStationManager.GasStationsRadiusOptions;
import com.inrix.sdk.GasStationManager.IGasStationsResponseListener;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.model.GasStation;
import com.inrix.sdk.model.GasStation.Address;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.utils.UserPreferences;

import java.util.List;

public class GasStationsListActivity extends InrixSdkActivity {

    private final GeoPoint SEATTLE_POSITION = new GeoPoint(47.614496, -122.328758);

    // Loading Dialog
    ProgressDialog pd;

    /**
     * A custom array adapter that shows a {@link SimpleView} containing
     * details about the Gas station
     */
    private static class CustomArrayAdapter extends ArrayAdapter<GasStation> {
        /**
         * @param gasStations An array containing the gas stations to be displayed
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

        private String getAddressString(Address gasStationAddress) {
            if (null != gasStationAddress) {
                return gasStationAddress.getStreet() + ", " + gasStationAddress.getCity() + ", " + gasStationAddress.getPhoneNumber();
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

        // Clear the gas station List
        setGasStationList(null);

        pd = new ProgressDialog(this);
        pd.setMessage("loading");
        pd.show();

        // Get the gas stations for the selected city and radius
        int outputOptions = GasStationsRadiusOptions.OUTPUT_FIELDS_BRAND | GasStationsRadiusOptions.OUTPUT_FIELDS_ADDRESS
                | GasStationsRadiusOptions.OUTPUT_FIELDS_CURRENCY_CODE
                | GasStationsRadiusOptions.OUTPUT_FIELDS_PRODUCTS;
        int productTypes = GasStationsRadiusOptions.PRODUCT_TYPE_ALL;
        int REQUEST_RADIUS = 2;
        GasStationsRadiusOptions params = new GasStationsRadiusOptions(SEATTLE_POSITION, REQUEST_RADIUS, UserPreferences.Unit.MILES, outputOptions, productTypes);
        InrixCore.getGasStationManager().getGasStationsInRadius(params, new IGasStationsResponseListener() {

            @Override
            public void onResult(final List<GasStation> data) {
                pd.dismiss();
                if (null != data) {
                    setGasStationList(data);
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
     * @param list station list
     */
    private void setGasStationList(List<GasStation> list) {
        GasStation gasStationArray[];

        if (list == null) {
            return;
        }

        gasStationArray = list.toArray(new GasStation[list.size()]);
        CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(this, gasStationArray);
        ((ListView) findViewById(R.id.gas_station_list)).setAdapter(arrayAdapter);
    }
}
