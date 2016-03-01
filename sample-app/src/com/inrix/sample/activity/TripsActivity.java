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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.inrix.sample.R;
import com.inrix.sample.fragments.TripsCreateFragment;
import com.inrix.sample.fragments.TripsListFragment;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.TripManager;

import java.util.ArrayList;

@SuppressWarnings("deprecation")
public class TripsActivity extends InrixSdkActivity implements TripsCreateFragment.OnTripCreatedListener {

    private ViewPager viewPager;
    private TripManager tripsManager;
    private TabsAdapter tabsAdapter;

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_trips;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        this.viewPager = (ViewPager) findViewById(R.id.trips_pager);
        this.tabsAdapter = new TabsAdapter(this, viewPager);

        tabsAdapter.addTab(actionBar.newTab().setText("Create"), TripsCreateFragment.class, null);
        tabsAdapter.addTab(actionBar.newTab().setText("List"), TripsListFragment.class, null);

        this.tripsManager = InrixCore.getTripManager();
    }

    public TripManager getTripsManager() {
        return tripsManager;
    }

    @Override
    public void onTripCreated() {
        tabsAdapter.onTripCreated();
    }

    public static class TabsAdapter extends FragmentPagerAdapter implements
            ActionBar.TabListener, ViewPager.OnPageChangeListener, TripsCreateFragment.OnTripCreatedListener {
        private final Context context;
        private final ActionBar actionBar;
        private final ViewPager viewPager;
        private final ArrayList<TabInfo> tabs = new ArrayList<>();
        private Fragment listFragment;

        public TabsAdapter(AppCompatActivity activity, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            context = activity;
            actionBar = activity.getSupportActionBar();
            viewPager = pager;
            viewPager.setAdapter(this);
            viewPager.setOnPageChangeListener(this);
        }

        @Override
        public void onTripCreated() {
            if (listFragment != null) {
                ((TripsListFragment) listFragment).updateList(true);
            }
        }

        public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(clss, args);
            tab.setTag(info);
            tab.setTabListener(this);
            tabs.add(info);
            actionBar.addTab(tab);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return tabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = tabs.get(position);

            Fragment fragment = Fragment
                    .instantiate(context, info.clss.getName(), info.args);

            if (info.clss == TripsListFragment.class) {
                listFragment = fragment;
            }

            return fragment;
        }

        @Override
        public void onPageScrolled(int position,
                                   float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            actionBar.setSelectedNavigationItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            Object tag = tab.getTag();
            for (int i = 0; i < tabs.size(); i++) {
                if (tabs.get(i) == tag) {
                    viewPager.setCurrentItem(i);
                }
            }
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        }

        static final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(Class<?> _class, Bundle _args) {
                clss = _class;
                args = _args;
            }
        }
    }
}
