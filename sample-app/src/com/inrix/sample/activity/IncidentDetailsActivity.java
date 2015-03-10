package com.inrix.sample.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import com.inrix.sdk.model.Incident;
import com.inrix.sdk.model.GeoPoint;

/**
 * The Class IncidentDetailsActivity.
 */
public class IncidentDetailsActivity extends FragmentActivity {
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

		this.map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		this.details = (IncidentDetailsFragment) getSupportFragmentManager()
				.findFragmentById(R.id.details);

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

		LatLng incidentLatLang = this.addPointOnMap(incidentPoint,
				R.drawable.incident,
				null);
		builder.include(incidentLatLang);

		LatLng head = this.addPointOnMap(incidentHead,
				R.drawable.incident_head,
				"Head");

		if (head != null) {
			builder.include(head);
		}

		if (tails != null) {
			for (GeoPoint tail : tails) {
				builder.include(this.addPointOnMap(tail,
						R.drawable.incident_tail,
						"Tail"));
			}
		}

		if (detours != null) {
			for (GeoPoint detour : detours) {
				builder.include(this.addPointOnMap(detour,
						R.drawable.incident_detour,
						"Detour"));
			}
		}

		this.map.animateCamera(CameraUpdateFactory
				.newLatLngZoom(incidentLatLang, 14));
	}

	/**
	 * Adds the point on map.
	 * 
	 * @param point
	 *            the point
	 * @param resourcesId
	 *            the resources id
	 * @param title
	 *            the title
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
		float[] results = new float[2];
		Location.distanceBetween(Constants.SEATTLE_POSITION.getLatitude(),
				Constants.SEATTLE_POSITION.getLongitude(),
				currentPoint.getLatitude(),
				currentPoint.getLongitude(),
				results);
		double miles = results[0] / Constants.METERS_PER_MILE;
		return miles;
	}
}
