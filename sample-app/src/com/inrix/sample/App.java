/**
 * Copyright (c) 2013-2015 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample;

import android.app.Application;

import com.inrix.sample.push.PushProviderHelper;
import com.inrix.sample.util.PermissionHelper;
import com.inrix.sdk.InrixCore;

/**
 * Application entry point.
 */
public class App extends Application {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        PushProviderHelper.initializeProvider(this, null);
        super.onCreate();

        if (PermissionHelper.hasAllPermissions(this)) {
            //noinspection ResourceType
            InrixCore.initialize(this);
        }
    }
}