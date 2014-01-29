package com.inrix.sample.activity;

import com.inrix.sample.R;

/**
 * A list of the Sample Apps
 */
public final class SampleAppDetailsList {

	private SampleAppDetailsList() {
	}

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
					ParkingListActivity.class),
			// Alerts
			new SampleAppDetails(R.string.sample_alerts,
					R.string.sample_alerts_description,
					IncidentAlertsActivity.class),
			// Traffic Tiles
			new SampleAppDetails(R.string.sample_traffic_tiles,
					R.string.sample_traffic_tiles_description,
					TrafficTilesActivity.class),
			// Travel Times
			new SampleAppDetails(R.string.sample_travel_times,
					R.string.sample_travel_times_description,
					TravelTimesActivity.class),

			// Route on map
			new SampleAppDetails(R.string.sample_route_on_map,
					R.string.sample_route_on_map_description,
					RouteOnMapActivity.class),

			// Geocoding and reverse geocoding
			new SampleAppDetails(R.string.sample_geocoding,
					R.string.sample_geocoding_description,
					GeocodingActivity.class) };

}
