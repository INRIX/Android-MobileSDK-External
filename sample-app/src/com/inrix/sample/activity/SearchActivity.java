package com.inrix.sample.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.inrix.sample.R;
import com.inrix.sample.activity.IncidentListActivity.TabsAdapter;
import com.inrix.sample.fragments.AutocompleteSearchFragment;
import com.inrix.sample.fragments.NearbySearchOnMapFragment;

/**
 * Displays search related fragments.
 */
public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            final TabsAdapter tabsAdapter = new TabsAdapter(this, viewPager);

            tabsAdapter.addTab(actionBar.newTab()
                            .setText(R.string.tab_title_nearby_search),
                    NearbySearchOnMapFragment.class,
                    null);

            tabsAdapter.addTab(actionBar.newTab()
                            .setText(R.string.tab_title_autocomplete_search),
                    AutocompleteSearchFragment.class,
                    null);
        }
    }
}
