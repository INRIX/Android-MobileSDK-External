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
import android.app.NotificationManager;
import android.content.Context;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Implementation of {@link FirebaseMessagingService} that receives push messages.
 */
public class FcmListenerService extends FirebaseMessagingService {
    private static final String ALERT_DATA_KEY = "default";

    /**
     * {@inheritDoc}
     */
    @Override
    public void onMessageReceived(RemoteMessage message) {
        final Map<String, String> data = message.getData();
        final String alertJson = data != null ? data.get(ALERT_DATA_KEY) : null;

        final Notification notification = new PushNotificationBuilder(alertJson).buildNotification(this);
        if (notification == null) {
            return;
        }

        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, notification);
    }
}
