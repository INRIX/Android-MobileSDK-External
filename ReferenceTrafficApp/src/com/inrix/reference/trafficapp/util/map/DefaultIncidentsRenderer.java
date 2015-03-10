/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.util.map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.ui.SquareTextView;
import com.inrix.reference.trafficapp.R;
import com.inrix.sdk.IncidentUtils;
import com.inrix.sdk.IncidentsManager;
import com.inrix.sdk.model.Incident;

public class DefaultIncidentsRenderer extends DefaultClusterRenderer<ClusteredIncident> {
	private static final Logger logger = LoggerFactory.getLogger(DefaultIncidentsRenderer.class);
	
	IconGenerator coalescedIconsGenerator;

	public DefaultIncidentsRenderer(Context context, GoogleMap map,
			ClusterManager<ClusteredIncident> clusterManager) {
		super(context, map, clusterManager);
		coalescedIconsGenerator = new IconGenerator(context);
		coalescedIconsGenerator.setContentView(makeSquareTextView(context));
		coalescedIconsGenerator.setContentPadding(
				context.getResources().getDimensionPixelSize(R.dimen.coallesced_icon_padding_left),
				context.getResources().getDimensionPixelSize(R.dimen.coallesced_icon_padding_top),
				context.getResources().getDimensionPixelSize(R.dimen.coallesced_icon_padding_right),
				context.getResources().getDimensionPixelSize(R.dimen.coallesced_icon_padding_bottom));
		coalescedIconsGenerator
				.setTextAppearance(R.style.CoallescedIcon_TextAppearance);
		coalescedIconsGenerator.setBackground(context.getResources()
				.getDrawable(R.drawable.map_icon_pin_coalesced));
	}

	@Override
	protected void onBeforeClusterItemRendered(ClusteredIncident item,
			MarkerOptions markerOptions) {
		super.onBeforeClusterItemRendered(item, markerOptions);
		markerOptions.icon(getIcon(item.getIncident()));
	}

	@Override
	protected void onBeforeClusterRendered(Cluster<ClusteredIncident> cluster,
			MarkerOptions markerOptions) {
		super.onBeforeClusterRendered(cluster, markerOptions);
		int bucket = getBucket(cluster);
		markerOptions.icon(BitmapDescriptorFactory
				.fromBitmap(coalescedIconsGenerator
						.makeIcon(getClusterText(bucket))));
	}

	/**
	 * Make square text view.
	 *
	 * @param context the context
	 * @param density the density
	 * @return the square text view
	 */
	private SquareTextView makeSquareTextView(Context context) {
		SquareTextView squareTextView = new SquareTextView(context);
		ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		squareTextView.setLayoutParams(layoutParams);
		squareTextView.setId(R.id.text);
		return squareTextView;
	}

	public BitmapDescriptor getIcon(Incident incident) {
		BitmapDescriptor result = null;

		// Add road closure logic
		if (IncidentUtils.isRoadClosure(incident.getEventCode())) {
			return BitmapDescriptorFactory
					.fromResource(R.drawable.map_icon_pin_road_closure);
		}

		switch (incident.getType()) {
			case IncidentsManager.INCIDENT_TYPE_CONSTRUCTION:
				result = BitmapDescriptorFactory
						.fromResource(R.drawable.map_icon_pin_construction);
				break;
			case IncidentsManager.INCIDENT_TYPE_FLOW:
				result = BitmapDescriptorFactory
						.fromResource(R.drawable.map_icon_pin_congestion);
				break;
			case IncidentsManager.INCIDENT_TYPE_HAZARD:
				result = BitmapDescriptorFactory
						.fromResource(R.drawable.map_icon_pin_hazard);
				break;
			case IncidentsManager.INCIDENT_TYPE_ACCIDENT:
				result = BitmapDescriptorFactory
						.fromResource(R.drawable.map_icon_pin_accident);
				break;
			case IncidentsManager.INCIDENT_TYPE_POLICE:
				result = BitmapDescriptorFactory
						.fromResource(R.drawable.map_icon_pin_police);
				break;
			case IncidentsManager.INCIDENT_TYPE_EVENT:
				result = BitmapDescriptorFactory
						.fromResource(R.drawable.map_icon_pin_event);
				break;
			default:
				logger.debug("Cannot find icon for incident: {}", incident.getType());
				break;
		}

		return result;
	}

	@Override
	protected boolean shouldRenderAsCluster(Cluster<ClusteredIncident> cluster) {
		return cluster.getSize() > 1;
	}
}
