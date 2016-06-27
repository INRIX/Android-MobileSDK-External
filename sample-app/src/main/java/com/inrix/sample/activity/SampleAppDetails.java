/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

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

    public SampleAppDetails(int titleId, int descriptionId, Class<? extends FragmentActivity> activityClass) {
        this.titleId = titleId;
        this.descriptionId = descriptionId;
        this.activityClass = activityClass;
    }
}
