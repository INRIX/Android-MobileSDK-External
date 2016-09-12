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
import android.widget.Toast;

import com.inrix.sample.R;
import com.inrix.sdk.Error;
import com.inrix.sdk.GasStationManager;
import com.inrix.sdk.GasStationManager.GasStationsRadiusOptions;
import com.inrix.sdk.GasStationManager.IGasStationsResponseListener;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.RouteManager;
import com.inrix.sdk.model.GasStation;
import com.inrix.sdk.model.GasStation.Address;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.RequestRouteResults;
import com.inrix.sdk.utils.UserPreferences;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GasStationsListActivity extends InrixSdkActivity implements RouteManager.IRouteResponseListener {

    /**
     * The gas stations list.
     */
    @BindView(R.id.gas_station_list)
    protected ListView gasStationsList;

    /**
     * The progress bar.
     */
    @BindView(R.id.progress_bar)
    protected View progressBar;

    /**
     * Default start position.
     */
    private final GeoPoint SEATTLE = new GeoPoint(47.614496, -122.328758);

    /**
     * Redmond, WA.
     */
    private final GeoPoint REDMOND = new GeoPoint(47.672704, -122.123504);

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
                featureView.setTitle(gasStation.getBrand().toString());
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_gas_station_list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        this.gasStationsList.setEmptyView(findViewById(android.R.id.empty));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gas_stations_menu, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_find_gas_stations_in_radius) {
            this.loadGasStationInRadius();
            return true;
        }

        if (item.getItemId() == R.id.action_find_gas_stations_on_route) {
            this.loadGasStationsOnRoute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResult(RequestRouteResults requestRouteResults) {
        final GasStationManager.GasStationsOnRouteOptions options = new GasStationManager.GasStationsOnRouteOptions(requestRouteResults.getRoutes().get(0));
        InrixCore.getGasStationManager().getGasStationsOnRoute(options, new IGasStationsResponseListener() {
            @Override
            public void onResult(final List<GasStation> gasStations) {
                progressBar.setVisibility(View.GONE);
                if (null != gasStations) {
                    setGasStationList(gasStations);
                }
            }

            @Override
            public void onError(Error error) {
                progressBar.setVisibility(View.GONE);
                showError(error);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onError(Error error) {
        progressBar.setVisibility(View.GONE);
        showError(error);
    }

    private void loadGasStationsOnRoute() {
        this.progressBar.setVisibility(View.VISIBLE);
        // Get a route between origin/destination
        final RouteManager.RequestRouteOptions routeOptions = new RouteManager.RequestRouteOptions(SEATTLE, REDMOND);
        InrixCore.getRouteManager().requestRoutes(routeOptions, this);
    }

    private void loadGasStationInRadius() {
        this.progressBar.setVisibility(View.VISIBLE);

        // Get the gas stations for the selected city and radius
        int outputOptions = GasStationsRadiusOptions.OUTPUT_FIELDS_BRAND | GasStationsRadiusOptions.OUTPUT_FIELDS_ADDRESS
                | GasStationsRadiusOptions.OUTPUT_FIELDS_CURRENCY_CODE
                | GasStationsRadiusOptions.OUTPUT_FIELDS_PRODUCTS;
        int productTypes = GasStationsRadiusOptions.PRODUCT_TYPE_ALL;
        int REQUEST_RADIUS = 2;
        GasStationsRadiusOptions params = new GasStationsRadiusOptions(SEATTLE, REQUEST_RADIUS, UserPreferences.Unit.MILES, outputOptions, productTypes);
        InrixCore.getGasStationManager().getGasStationsInRadius(params, new IGasStationsResponseListener() {

            @Override
            public void onResult(final List<GasStation> data) {
                progressBar.setVisibility(View.GONE);
                if (null != data) {
                    setGasStationList(data);
                }
            }

            @Override
            public void onError(Error error) {
                showError(error);
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

    /**
     * Show error.
     *
     * @param error the error
     */
    private void showError(Error error) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, error.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
}
