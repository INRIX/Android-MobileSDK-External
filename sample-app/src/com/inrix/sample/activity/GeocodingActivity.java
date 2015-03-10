package com.inrix.sample.activity;

import com.inrix.sample.R;
import com.inrix.sample.activity.IncidentListActivity.TabsAdapter;
import com.inrix.sample.fragments.GeocodingOnMapFragment;
import com.inrix.sample.fragments.GeocodingReverseGeocodingFragment;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

/** Displays geocoding fragments. */
public class GeocodingActivity extends FragmentActivity {

	/** Displays geocode fragments. */
	private ViewPager viewPager;

	/** Provides geocode fragments. */
	private TabsAdapter tabsAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.geocoding_activity);
		this.viewPager = (ViewPager) findViewById(R.id.pager);

		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		this.tabsAdapter = new TabsAdapter(this, this.viewPager);
		this.tabsAdapter.addTab(actionBar.newTab()
				.setText(R.string.tab_title_geocoding_on_map),
				GeocodingOnMapFragment.class,
				null);
		this.tabsAdapter.addTab(actionBar.newTab()
				.setText(R.string.tab_title_geocoding_reverse),
				GeocodingReverseGeocodingFragment.class,
				null);
	}
}
