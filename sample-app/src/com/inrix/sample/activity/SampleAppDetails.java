package com.inrix.sample.activity;

import android.support.v4.app.FragmentActivity;

public class SampleAppDetails {
    /**
     * The resource id of the title of the demo.
     */
    public final int titleId;

    /**
     * The resources id of the description of the demo.
     */
    public final int descriptionId;

    /**
     * The demo activity's class.
     */
    public final Class<? extends FragmentActivity> activityClass;

    public SampleAppDetails(
            int titleId, int descriptionId, Class<? extends FragmentActivity> activityClass) {
        this.titleId = titleId;
        this.descriptionId = descriptionId;
        this.activityClass = activityClass;
    }
}
