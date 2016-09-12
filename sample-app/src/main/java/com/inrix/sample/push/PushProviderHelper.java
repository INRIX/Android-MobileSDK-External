/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */
package com.inrix.sample.push;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;
import com.inrix.sdk.push.IPushChannel;
import com.inrix.sdk.push.PushChannelFactory;

/**
 * Push provider helper.
 */
public class PushProviderHelper {

    /**
     * Push channel name as provided by INRIX.
     */
    public static final String PUSH_CHANNEL_NAME = "Sample,Application,SDK,6";

    /**
     * Sync the push token with INRIX services.
     *
     * @param context An instance of {@link Context}.
     */
    public static void syncPushToken(@NonNull Context context) {
        PushTokenSyncService.syncPushToken(context);
    }

    /**
     * Generates push channel to register with server.
     *
     * @return The push channel.
     */
    public static IPushChannel getPushChannel() {
        return PushChannelFactory.getFirebaseChannel(PUSH_CHANNEL_NAME, getPushToken());
    }

    /**
     * Return push token information from provider.
     *
     * @return push token.
     */
    public static String getPushToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }
}
