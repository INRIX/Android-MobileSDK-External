/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.locationpicker;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.TrafficApp;

public class LocationsListFragment extends ListFragment {

	private final static String CONTENT_EXTRA = "content_extra";

	private final double METERS_IN_MILE = 1609.34;

	private List<GeocodingResult> locations;

	public static LocationsListFragment getInstance(List<GeocodingResult> content) {
		Bundle args = new Bundle();
		args.putParcelableArray(CONTENT_EXTRA,
				content.toArray(new GeocodingResult[content.size()]));
		LocationsListFragment f = new LocationsListFragment();
		f.setArguments(args);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.locations_list_fragment,
				container,
				false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (getArguments() != null) {
			this.locations = Arrays.asList((GeocodingResult[]) (getArguments()
					.getParcelableArray(CONTENT_EXTRA)));
		}

		setListAdapter(new LocationsListAdapter(locations));
		this.getListView().requestFocus();
		this.getListView().setEmptyView(view.findViewById(android.R.id.empty));
	}

	@Override
	public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
		int animResId = enter ? R.animator.slide_up : R.animator.slide_down;
		TrafficApp.getBus().post(new FragmentAnimationStartEvent(this, enter));
		return AnimationUtils.loadAnimation(getActivity(), animResId);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		TrafficApp.getBus().post(new ListItemSelectedEvent(position,
				locations.get(position)));
		getFragmentManager().popBackStack();
	}

	class LocationsListAdapter extends BaseAdapter {

		private List<GeocodingResult> content;

		LocationsListAdapter(List<GeocodingResult> results) {
			this.content = results;
		}

		@Override
		public int getCount() {
			return content.size();
		}

		@Override
		public GeocodingResult getItem(int position) {
			return content.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View result = convertView;
			if (result == null) {
				result = LayoutInflater.from(parent.getContext())
						.inflate(R.layout.location_search_result_item_layout,
								parent,
								false);
				ViewHolder vh = new ViewHolder();
				vh.title = (TextView) result.findViewById(R.id.title);
				vh.description = (TextView) result
						.findViewById(R.id.description);
				vh.distance = (TextView) result.findViewById(R.id.distance);
				vh.distanceContainer = result
						.findViewById(R.id.distanceContainer);
				result.setTag(vh);
			}

			ViewHolder vh = (ViewHolder) result.getTag();
			vh.title.setText(getItem(position).getTitle());
			vh.description.setText(getItem(position).getDescription());

			if (!Float.isNaN(getItem(position).getDistance())) {
				vh.distanceContainer.setVisibility(View.VISIBLE);
				NumberFormat format = NumberFormat.getNumberInstance();
				format.setMinimumFractionDigits(0);
				format.setMaximumFractionDigits(1);
				String distanceStr = format.format(getItem(position)
						.getDistance() / METERS_IN_MILE);
				vh.distance.setText(distanceStr);
			} else {
				vh.distanceContainer.setVisibility(View.GONE);
			}
			return result;
		}

		class ViewHolder {
			TextView title;
			TextView description;
			TextView distance;
			View distanceContainer;
		}
	}
}
