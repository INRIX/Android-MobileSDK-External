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
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.inrix.sample.R;
import com.inrix.sdk.model.PreDriveNotification;
import com.inrix.sdk.push.IPushNotification;
import com.inrix.sdk.push.PushNotificationParser;


/**
 * Pre-drive alert display builder.
 */
public class PushNotificationBuilder {

    private final IPushNotification alert;

    /**
     * Initialize {@link PushNotificationBuilder} with the given alert json data.
     *
     * @param alertJson Alert json data that contains Pre-drive alert information.
     */
    public PushNotificationBuilder(String alertJson) {
        this.alert = PushNotificationParser.parse(alertJson);
    }

    /**
     * Build notification for system tray.
     *
     * @param context application context to build notification message
     * @return Notification for display in tray.
     */
    public Notification buildNotification(Context context) {
        if (this.alert == null) {
            return null;
        }

        if (this.alert instanceof PreDriveNotification) {
            String contentText = ((PreDriveNotification) (this.alert)).getDestinationName();
            final Resources resources = context.getResources();

            return new NotificationCompat.Builder(context)
                    .setContentTitle(resources.getString(R.string.app_name))
                    .setContentText(contentText)
                    .setSmallIcon(android.R.drawable.ic_dialog_map)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher))
                    .build();
        }

        return null;
    }
}
