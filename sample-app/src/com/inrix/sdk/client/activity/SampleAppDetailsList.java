package com.inrix.sdk.client.activity;

import com.inrix.sdk.client.R;

/**
 * A list of the Sample Apps
 */
public final class SampleAppDetailsList {

    private SampleAppDetailsList() {}

    public static final SampleAppDetails[] SAMPLES = {
    	// Incidents Basic
        new SampleAppDetails(R.string.sample_incidents_basic_tile,
                		R.string.sample_incidents_basic_description,
                		IncidentListActivity.class), 
        // Busy Commuter
    	new SampleAppDetails(R.string.sample_busy_commuter_tile,
                        R.string.sample_busy_commuter_description,
                        BusyCommuterActivity.class),
        // Gas Stations
    	new SampleAppDetails(R.string.sample_gas_stations,
                        R.string.sample_gas_stations_description,
                        GasStationsListActivity.class),
        // Parking Lots
    	new SampleAppDetails(R.string.sample_parking,
                        R.string.sample_parking_description,
                        ParkingListActivity.class)
   };
}
