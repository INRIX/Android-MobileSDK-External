/**
 * Copyright (c) 2013-2015 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.push;

import android.content.Context;

import com.inrix.sdk.push.IPushChannel;
import com.inrix.sdk.push.PushChannelFactory;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;

/**
 * Push provider helper.
 */
public class PushProviderHelper {

    public interface IPushProviderListener {
        void onTokenAvailable(String token);
        void onError(final String errorMessage);
    }

    public static final String PARSE_CHANNEL_NAME = "Sample,Application,SDK,6";

    /**
     * Initialize push provider.
     *
     * @param context application context.
     */
    public static void initializeProvider(final Context context, final IPushProviderListener listener) {
        Parse.initialize(context);
        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(listener == null){
                    return;
                }

                if (e != null) {
                    listener.onError(e.getMessage());
                } else {
                    listener.onTokenAvailable(getPushToken());
                }
            }
        });
    }

    /**
     * Generates push info to register with server.
     *
     * @return
     */
    public static IPushChannel getIPushChannel() {
        return PushChannelFactory.getParseChannel(PARSE_CHANNEL_NAME, getPushToken());
    }

    /**
     * Return push token information from provider.
     *
     * @return push token.
     */
    public static String getPushToken() {
        return ParseInstallation.getCurrentInstallation().getObjectId();
    }
}
