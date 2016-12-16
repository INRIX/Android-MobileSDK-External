/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

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
            new SampleAppDetails(R.string.sample_parking_off_street,
                    R.string.sample_parking_off_street_description,
                    ParkingListActivity.class),
            // Parking blocks - on-street parking.
            new SampleAppDetails(
                    R.string.sample_parking_on_street,
                    R.string.sample_parking_on_street_description,
                    ParkingBlocksActivity.class),
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
            // Geocode and reverse geocode
            new SampleAppDetails(R.string.sample_geocode,
                    R.string.sample_geocode_description,
                    GeocodeActivity.class),
            // Search
            new SampleAppDetails(R.string.sample_search,
                    R.string.sample_search_description,
                    SearchActivity.class),
            // Report incidents
            new SampleAppDetails(R.string.sample_report_incident,
                    R.string.sample_report_incident_description,
                    IncidentManagementActivity.class),
            // Travel Times
            new SampleAppDetails(R.string.sample_travel_times_sync,
                    R.string.sample_travel_times_description,
                    TravelTimesSyncActivity.class),
            // Service Availability
            new SampleAppDetails(R.string.sample_service_availability,
                    R.string.sample_service_availability_description,
                    ServiceAvailabilityActivity.class),
            // Account management
            new SampleAppDetails(R.string.sample_account_management,
                    R.string.sample_account_management_description,
                    AccountManagementActivity.class),
            // Trips
            new SampleAppDetails(R.string.sample_trips,
                    R.string.sample_trips_description,
                    TripsActivity.class),
            // Cameras
            new SampleAppDetails(R.string.sample_cameras,
                    R.string.sample_cameras_description,
                    CamerasActivity.class),
            // XdIncidents
            new SampleAppDetails(R.string.sample_xdincidents,
                    R.string.sample_xd_incidents,
                    XDIncidentsActivity.class),
            // Itinerary
            new SampleAppDetails(R.string.sample_itinerary,
                    R.string.sample_itinerary_description,
                    ItineraryActivity.class),
            // Report Vehicle State
            new SampleAppDetails(
                    R.string.report_vehicle_state,
                    R.string.report_vehicle_state,
                    ReportVehicleStateActivity.class),
            // Service invocation.
            new SampleAppDetails(
                    R.string.service_invocation_title,
                    R.string.service_invocation_title,
                    ServiceInvocationActivity.class),
            // Push information.
            new SampleAppDetails(R.string.sample_push_information,
                    R.string.sample_push_information_description,
                    PushInformationActivity.class),
            // Dangerous Slowdowns.
            new SampleAppDetails(R.string.sample_dangerous_slowdowns,
                    R.string.sample_dangerous_slowdowns_description,
                    DangerousSlowdownsActivity.class)
    };
}
