/**
 * Copyright (c) 2013-2015 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.inrix.sample.R;
import com.inrix.sample.activity.IncidentListActivity.TabsAdapter;
import com.inrix.sample.fragments.GeocodingOnMapFragment;
import com.inrix.sample.fragments.ReverseGeocodingOnMapFragment;

/**
 * Displays geocoding fragments.
 */
public class GeocodingActivity extends AppCompatActivity {

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geocoding_activity);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            final TabsAdapter tabsAdapter = new TabsAdapter(this, viewPager);

            tabsAdapter.addTab(actionBar.newTab()
                            .setText(R.string.tab_title_geocoding_reverse),
                    ReverseGeocodingOnMapFragment.class,
                    null);

            tabsAdapter.addTab(actionBar.newTab()
                            .setText(R.string.tab_title_geocoding_on_map),
                    GeocodingOnMapFragment.class,
                    null);
        }
    }
}
