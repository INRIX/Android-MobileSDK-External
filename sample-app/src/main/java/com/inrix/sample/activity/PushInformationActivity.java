/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.activity;

import android.os.Bundle;
import android.util.Pair;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inrix.sample.R;
import com.inrix.sample.push.PushProviderHelper;
import com.inrix.sdk.Error;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.UserManager;
import com.inrix.sdk.authentication.Account;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Sample activity that demonstrates push operation.
 * <p/>
 * You will need to update the google-services.json file with your own credentials in order to get
 * a push token and receive push notifications.
 *
 * @see <a href="https://developers.google.com/cloud-messaging/android/start">Firebase Getting Started</a>
 */
public class PushInformationActivity extends InrixSdkActivity {
    private UserManager userManager;
    private Gson serializer;

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_push_notification;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        this.userManager = InrixCore.getUserManager();
        this.serializer = new GsonBuilder().setPrettyPrinting().create();

        final String pushToken = getString(R.string.push_token_label_format, PushProviderHelper.getPushToken());
        ButterKnife.<TextView>findById(this, R.id.pushTokenId).setText(pushToken);
    }

    @OnClick(R.id.status)
    void onStatusClicked() {
        showStatus("");
    }

    @OnClick(R.id.buttonCreateAccount)
    void onCreateAccount() {
        final Pair<String, String> credentials = this.getLoginCredentials();
        final UserManager.UserCreateOptions options = new UserManager.UserCreateOptions(credentials.first, credentials.second);
        this.userManager.create(options, new UserManager.UserCreateListener() {
            @Override
            public void onResult(Account data) {
                showStatus(serializer.toJson(data));
                updatePushChannel();
            }

            @Override
            public void onError(Error error) {
                showStatus(serializer.toJson(error));
            }
        });
    }

    @OnClick(R.id.buttonUpdatePushChannel)
    void onUpdatePushChannel() {
        updatePushChannel();
    }

    /**
     * Update the push channel information for the user.
     * <p/>
     * This should be called anytime a new user is created or signs in.
     */
    private void updatePushChannel() {
        PushProviderHelper.syncPushToken(this);
    }

    private Pair<String, String> getLoginCredentials() {
        final String loginName = ((TextView) this.findViewById(R.id.loginName)).getText().toString();
        final String password = ((TextView) this.findViewById(R.id.password)).getText().toString();
        return new Pair<>(loginName, password);
    }

    /**
     * Show status text.
     *
     * @param status The text to display.
     */
    private void showStatus(final String status) {
        ((TextView) this.findViewById(R.id.status)).setText(status);
    }
}
