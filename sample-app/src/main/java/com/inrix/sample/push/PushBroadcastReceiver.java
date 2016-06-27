/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.push;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

/**
 * Broadcast receiver for push pre-drive alerts from "Parse".
 */
public class PushBroadcastReceiver extends ParsePushBroadcastReceiver {

    /**
     * {@inheritDoc}
     */
    @Override
    public Notification getNotification(Context context, Intent intent) {
        return new PushNotificationBuilder(intent).buildNotification(context);
    }
}

