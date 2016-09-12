/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */
package com.inrix.sample.push;

import com.google.firebase.iid.FirebaseInstanceIdService;
import com.inrix.sdk.InrixCore;

/**
 * Implementation of {@link FirebaseInstanceIdService} that listens for push token updates.
 */
public class InstanceIdListenerService extends FirebaseInstanceIdService {
    /**
     * {@inheritDoc}
     */
    @Override
    public void onTokenRefresh() {
        updatePushChannel();
    }

    /**
     * Update the user's push channel information.
     */
    private void updatePushChannel() {
        if (!InrixCore.isInitialized()) {
            return;
        }

        PushProviderHelper.syncPushToken(this);
    }
}
