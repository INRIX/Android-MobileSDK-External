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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.SimpleArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.inrix.sample.BusProvider;
import com.inrix.sample.IncidentsReceivedEvent;
import com.inrix.sample.R;
import com.inrix.sample.activity.IncidentListActivity.TabsAdapter;
import com.inrix.sample.fragments.IncidentAlertsMapFragment;
import com.inrix.sample.fragments.IncidentListFragment;
import com.inrix.sdk.AlertsManager;
import com.inrix.sdk.AlertsManager.IIncidentsAlertListener;
import com.inrix.sdk.AlertsManager.IncidentAlertOptions;
import com.inrix.sdk.Error;
import com.inrix.sdk.IncidentAlert;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.model.Incident;

import java.util.Date;
import java.util.List;

@SuppressWarnings("deprecation")
public class IncidentAlertsActivity extends InrixSdkActivity implements IIncidentsAlertListener, ActionBar.TabListener {
    private TextView timestamp;
    private IncidentAlert alert;
    private ProgressBar progressBar;
    private AlertsManager alertManager;

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_alerts;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.timestamp = (TextView) findViewById(R.id.timestamp);
        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        TabsAdapter tabsAdapter = new TabsAdapter(this, viewPager);

        tabsAdapter.addTab(actionBar.newTab().setText("List"), IncidentListFragment.class, null);
        tabsAdapter.addTab(actionBar.newTab().setText("Map"), IncidentAlertsMapFragment.class, null);

        this.alertManager = InrixCore.getAlertsManager();

        this.progressBar.setVisibility(View.VISIBLE);
        this.timestamp.setText("Loading incidents around you based on your speed.");

        IncidentAlertOptions alertOptions = new IncidentAlertOptions(15);
        alertOptions.setSpeedFactor(1f);
        alertOptions.setForwardConeAngle(120);

        this.alert = this.alertManager.createIncidentAlert(alertOptions, this, false);
    }

    @Override
    protected void onStop() {
        if (this.alert != null) {
            this.alert.cancel();
            this.alert = null;
        }
        super.onStop();
    }

    @Override
    public void onResult(List<Incident> data) {
        getSupportFragmentManager().popBackStack();
        BusProvider.getBus().post(new IncidentsReceivedEvent(data));
        Date date = new Date(System.currentTimeMillis());
        this.timestamp.setText("Last update: " + date.toString() + " \n Last requested distance:" + this.alert.getLastRequestedDistance());
        this.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onError(Error error) {
        getSupportFragmentManager().popBackStack();
        this.timestamp.setText("Unable to retrieve data: " + error.toString());
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    /**
     * Call for when the route tracker has finished processing the route and mapped all the incidents to a route point.
     *
     * @param incidentToDistanceMap The map of {@link Incident} to their driving distance from origin.
     */
    @Override
    public void onIncidentWithDistanceResult(SimpleArrayMap<Incident, Double> incidentToDistanceMap) {

    }
}