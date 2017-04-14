/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample;

import android.app.Application;
import android.content.Context;

import com.inrix.sample.activity.ItineraryActivity;
import com.inrix.sample.activity.TripsActivity;
import com.inrix.sample.fragments.ItineraryViewFragment;
import com.inrix.sample.fragments.TripsListFragment;
import com.inrix.sample.util.PermissionHelper;
import com.inrix.sdk.Configuration;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.LocationsManager;

/**
 * Application entry point.
 */
public class App extends Application {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        super.onCreate();

        if (PermissionHelper.hasAllPermissions(this)) {
            initializeInrixSdk();
        }
    }

    /**
     * Initialize {@link InrixCore} using a custom configuration.
     *
     * For the default configuration, call {@link InrixCore#initialize(Context)} instead.
     */
    public void initializeInrixSdk() {
        final Configuration.Builder builder = new Configuration.Builder(this);

        /**
         * Enable calendar syncing so the user's calendar items will be available in their Itinerary.
         * Note that you need to ensure the calendar runtime permission has been granted.
         *
         * See {@link ItineraryActivity} and {@link ItineraryViewFragment} examples.
         */
        builder.calendarSyncEnabled(true);

        /**
         * Enable trip and location learning. If you only need location learning, then enable
         * {@link Configuration.Builder#locationLearningEnabled(boolean)} instead.
         *
         * For interacting with trips, see {@link TripsActivity} and {@link TripsListFragment}.
         * Note that learned trips will also be available in the {@link ItineraryActivity} and {@link ItineraryViewFragment} examples.
         * For interacting with learned locations, see {@link LocationsManager}.
         */
        builder.tripRecordingEnabled(true);

        // This should be enabled when using calendar or trip features.
        builder.monitorUserLocation(true);

        //noinspection ResourceType
        InrixCore.initialize(this, builder.build());
    }
}