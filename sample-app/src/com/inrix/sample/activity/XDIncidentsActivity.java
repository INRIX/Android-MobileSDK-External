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

import com.inrix.sample.R;
import com.inrix.sample.fragments.XDIncidentFragmentInBox;
import com.inrix.sample.fragments.XDIncidentFragmentInRadius;

/**
 * Activity for XDIncident Search
 */

public class XDIncidentsActivity extends InrixSdkActivity {

    /**
     * Displays XDIncident search fragments.
     */
    private ViewPager viewPager;

    /**
     * Provides XDIncident search fragments.
     */
    private IncidentListActivity.TabsAdapter tabsAdapter;

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.xdincidents_search;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.viewPager = (ViewPager) findViewById(R.id.pager);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        this.tabsAdapter = new IncidentListActivity.TabsAdapter(this, this.viewPager);

        this.tabsAdapter.addTab(actionBar.newTab().setText(R.string.xdincident_in_box), XDIncidentFragmentInBox.class, null);

        this.tabsAdapter.addTab(actionBar.newTab().setText(R.string.xdincident_in_radius), XDIncidentFragmentInRadius.class, null);
    }
}
