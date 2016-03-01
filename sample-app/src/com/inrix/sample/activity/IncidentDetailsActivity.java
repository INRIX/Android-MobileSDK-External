/**
 * Copyright (c) 2013-2015 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inrix.sample.Constants;
import com.inrix.sample.R;
import com.inrix.sample.fragments.IncidentDetailsFragment;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Incident;
import com.inrix.sdk.utils.GeoUtils;

import java.util.List;

/**
 * The Class IncidentDetailsActivity.
 */
public class IncidentDetailsActivity extends AppCompatActivity {
    private static final String INCIDENT = "incident";

    private GoogleMap map;
    private IncidentDetailsFragment details;

    public static Intent generateIncidentDetailsActivity(final Context context, Incident incident) {
        Intent incidentDetailsIntent = new Intent(context, IncidentDetailsActivity.class);
        incidentDetailsIntent.putExtra(INCIDENT, incident);
        return incidentDetailsIntent;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_incident_details);

        this.map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

        this.details = (IncidentDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.details);

        this.bindIncidentDetails();
    }

    /**
     * Bind incident details.
     */
    private void bindIncidentDetails() {
        Intent launchIntent = this.getIntent();
        Incident incident = launchIntent.getParcelableExtra(INCIDENT);
        final String description = incident.getDescription() == null ? "Incident" : incident.getDescription();
        final GeoPoint incidentPoint = new GeoPoint(incident.getLatitude(), incident.getLongitude());
        final GeoPoint incidentHead = incident.getHead();
        final List<GeoPoint> tails = incident.getTails();
        final List<GeoPoint> detours = incident.getLastDetourPoints();

        this.details.setIncidentDetails(description);
        this.details.setIncidentDistance(getDistance(incidentPoint));

        LatLngBounds.Builder builder = LatLngBounds.builder();

        LatLng incidentLatLang = this.addPointOnMap(incidentPoint, R.drawable.incident, null);
        builder.include(incidentLatLang);

        LatLng head = this.addPointOnMap(incidentHead, R.drawable.incident_head, "Head");

        if (head != null) {
            builder.include(head);
        }

        if (tails != null) {
            for (GeoPoint tail : tails) {
                builder.include(
                        this.addPointOnMap(tail, R.drawable.incident_tail, "Tail"));
            }
        }

        if (detours != null) {
            for (GeoPoint detour : detours) {
                builder.include(
                        this.addPointOnMap(detour, R.drawable.incident_detour, "Detour"));
            }
        }

        this.map.animateCamera(CameraUpdateFactory.newLatLngZoom(incidentLatLang, 14));
    }

    /**
     * Adds the point on map.
     *
     * @param point       the point
     * @param resourcesId the resources id
     * @param title       the title
     */
    private LatLng addPointOnMap(final GeoPoint point,
                                 int resourcesId,
                                 final String title) {
        if (point == null) {
            return null;
        }

        LatLng position = new LatLng(point.getLatitude(), point.getLongitude());

        final MarkerOptions options = new MarkerOptions().position(position)
                .icon(BitmapDescriptorFactory.fromResource(resourcesId));
        if (!TextUtils.isEmpty(title)) {
            options.title(title);
        }

        this.map.addMarker(options);
        return position;
    }

    /**
     * Gets the distance.
     *
     * @param currentPoint the current point
     * @return the distance
     */
    private double getDistance(final GeoPoint currentPoint) {
        double distance = GeoUtils.distanceKM(Constants.SEATTLE_POSITION, currentPoint);
        return GeoUtils.kmToMI(distance);
    }
}
