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
import android.view.Menu;
import android.view.MenuItem;

import com.inrix.sample.BusProvider;
import com.inrix.sample.Constants;
import com.inrix.sample.IncidentsReceivedEvent;
import com.inrix.sample.R;
import com.inrix.sample.fragments.IncidentListFragment;
import com.inrix.sample.fragments.IncidentsMapFragment;
import com.inrix.sample.fragments.ProgressFragment;
import com.inrix.sdk.Error;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.IncidentsManager;
import com.inrix.sdk.IncidentsManager.IIncidentsResponseListener;
import com.inrix.sdk.IncidentsManager.IncidentRadiusOptions;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Incident;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class IncidentListActivity extends InrixSdkActivity implements ActionBar.TabListener {
    private ICancellable currentOperation = null;
    private final int INCIDENT_RADIUS_MILES = 20;
    private GeoPoint lastRequestedPosition = null;
    private IncidentsManager incidentManager;
    private ViewPager viewPager;
    private TabsAdapter tabsAdapter;
    private int selectedTab;

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_incident_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.selectedTab = savedInstanceState.getInt("tab", 0);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        this.viewPager = (ViewPager) findViewById(R.id.pager);
        this.tabsAdapter = new TabsAdapter(this, viewPager);
        tabsAdapter.addTab(actionBar.newTab().setText("List"), IncidentListFragment.class, null);
        tabsAdapter.addTab(actionBar.newTab().setText("Map"), IncidentsMapFragment.class, null);
        actionBar.setSelectedNavigationItem(this.selectedTab);

        this.incidentManager = InrixCore.getIncidentsManager();
        this.refreshData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            refreshData();
            return true;
        }
        return false;
    }

    @Override
    protected void onStop() {
        cancelCurrentRequest();
        super.onStop();
    }

    private void cancelCurrentRequest() {
        if (this.currentOperation != null) {
            this.currentOperation.cancel();
            this.currentOperation = null;
            stopSpinner();
        }
    }

    private void stopSpinner() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshData();
    }

    private void refreshData() {
        if (this.incidentManager == null) {
            return;
        }

        if (currentOperation != null) {
            currentOperation.cancel();
            currentOperation = null;
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(new ProgressFragment(), "")
                    .addToBackStack("")
                    .commitAllowingStateLoss();
        }

        if (lastRequestedPosition == null) {
            lastRequestedPosition = Constants.SEATTLE_POSITION;
        }

        // Get the Incidents for the selected city and radius
        IncidentRadiusOptions params = new IncidentRadiusOptions(lastRequestedPosition, INCIDENT_RADIUS_MILES);
        this.currentOperation = this.incidentManager.getIncidentsInRadius(
                params,
                new IIncidentsResponseListener() {
                    @Override
                    public void onResult(List<Incident> data) {
                        stopSpinner();
                        currentOperation = null;
                        BusProvider.getBus().post(new IncidentsReceivedEvent(data));
                    }

                    @Override
                    public void onError(Error error) {
                        stopSpinner();
                        currentOperation = null;
                        BusProvider.getBus().post(error);
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("tab", getSupportActionBar().getSelectedNavigationIndex());
        cancelCurrentRequest();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        // TODO Auto-generated method stub
    }

    public static class TabsAdapter extends FragmentPagerAdapter
            implements
            ActionBar.TabListener,
            ViewPager.OnPageChangeListener {
        private final Context context;
        private final ActionBar actionBar;
        private final ViewPager viewPager;
        private final ArrayList<TabInfo> tabs = new ArrayList<TabInfo>();

        static final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(Class<?> _class, Bundle _args) {
                clss = _class;
                args = _args;
            }
        }

        public TabsAdapter(AppCompatActivity activity, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            context = activity;
            actionBar = activity.getSupportActionBar();
            viewPager = pager;
            viewPager.setAdapter(this);
            viewPager.setOnPageChangeListener(this);
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
            return Fragment.instantiate(context, info.clss.getName(), info.args);
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
    }
}
