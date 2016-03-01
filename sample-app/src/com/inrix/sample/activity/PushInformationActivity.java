/**
 * Copyright (c) 2013-2015 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.activity;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inrix.sample.R;
import com.inrix.sample.push.PushProviderHelper;
import com.inrix.sdk.Error;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.UserManager;
import com.inrix.sdk.authentication.Account;

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

        try {
            PushProviderHelper.initializeProvider(getApplicationContext(), new PushProviderHelper.IPushProviderListener() {
                @Override
                public void onTokenAvailable(String token) {
                    ((TextView) findViewById(R.id.parseObjectId)).setText(
                            getString(R.string.parse_installation_id_label_format, token));
                }

                @Override
                public void onError(String errorMessage) {
                    showStatus(errorMessage);
                }
            });
        } catch (Exception ex) {
            this.showStatus(ex.getMessage());
        }

        this.userManager = InrixCore.getUserManager();
        this.serializer = new GsonBuilder().setPrettyPrinting().create();

        // Attach handlers.
        this.findViewById(R.id.status).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof TextView) {
                    ((TextView) v).setText("");
                }
            }
        });

        // Attach handlers.
        this.findViewById(R.id.buttonCreateAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateAccount();
            }
        });

        this.findViewById(R.id.buttonUpdatePushChannel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePushChannel();
            }
        });
    }

    private Pair<String, String> getLoginCredentials() {
        final String loginName = ((TextView) this.findViewById(R.id.loginName)).getText().toString();
        final String password = ((TextView) this.findViewById(R.id.password)).getText().toString();
        return new Pair<String, String>(loginName, password);
    }

    private void onCreateAccount() {
        final Pair<String, String> credentials = this.getLoginCredentials();
        final UserManager.UserCreateOptions options = new UserManager.UserCreateOptions(credentials.first, credentials.second);
        this.userManager.create(options, new UserManager.UserCreateListener() {
            @Override
            public void onResult(Account data) {
                showStatus(serializer.toJson(data));
            }

            @Override
            public void onError(Error error) {
                showStatus(serializer.toJson(error));
            }
        });
    }

    private void updatePushChannel() {
        this.userManager.updatePushNotificationInformation(
                new UserManager.UpdatePushNotificationInformationOptions(PushProviderHelper.getIPushChannel()),
                new UserManager.UpdatePushNotificationInformationListener() {
                    @Override
                    public void onResult(Boolean data) {
                        showStatus("Push Channel Information Updated " + (data ? "Success" : "Failure"));
                    }

                    @Override
                    public void onError(com.inrix.sdk.Error error) {
                        showStatus(error.getErrorMessage());
                    }
                });
    }

    private void showStatus(final String status) {
        ((TextView) this.findViewById(R.id.status)).setText(status);
    }
}
