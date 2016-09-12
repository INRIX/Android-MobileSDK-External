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

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.gcm.TaskParams;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.UserManager;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A {@link GcmTaskService} implementation that sync's the push token with INRIX
 * services.
 */
public class PushTokenSyncService extends GcmTaskService {

    private static final String TASK_TAG = PushTokenSyncService.class.getName();
    private static final long SYNC_WINDOW_MIN_SECONDS = TimeUnit.SECONDS.toSeconds(30);
    private static final long SYNC_WINDOW_MAX_SECONDS = TimeUnit.MINUTES.toSeconds(5);
    private static final long SYNC_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(1);

    /**
     * Sync the push token with INRIX services.
     *
     * @param context An instance of {@link Context}.
     */
    protected static void syncPushToken(@NonNull Context context) {
        final GcmNetworkManager gcmNetworkManager = GcmNetworkManager.getInstance(context);
        gcmNetworkManager.schedule(new OneoffTask.Builder()
                .setTag(TASK_TAG)
                .setService(PushTokenSyncService.class)
                .setExecutionWindow(SYNC_WINDOW_MIN_SECONDS, SYNC_WINDOW_MAX_SECONDS)
                .setRequiresCharging(false)
                .setUpdateCurrent(true)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .setPersisted(true)
                .build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int onRunTask(TaskParams taskParams) {
        if (!InrixCore.isInitialized()) {
            return GcmNetworkManager.RESULT_RESCHEDULE;
        }

        final UserManager userManager = InrixCore.getUserManager();
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicBoolean requestSuccess = new AtomicBoolean(false);
        final ICancellable syncRequest = userManager.updatePushNotificationInformation(
                new UserManager.UpdatePushNotificationInformationOptions(PushProviderHelper.getPushChannel()),
                new UserManager.UpdatePushNotificationInformationListener() {
                    @Override
                    public void onResult(Boolean data) {
                        requestSuccess.set(true);
                        latch.countDown();
                    }

                    @Override
                    public void onError(com.inrix.sdk.Error error) {
                        latch.countDown();
                    }
                });

        try {
            latch.await(SYNC_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        } finally {
            syncRequest.cancel();
        }

        if (requestSuccess.get()) {
            return GcmNetworkManager.RESULT_SUCCESS;
        } else {
            return GcmNetworkManager.RESULT_RESCHEDULE;
        }
    }
}
