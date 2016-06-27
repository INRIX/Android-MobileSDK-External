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
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.inrix.sample.R;
import com.inrix.sdk.Error;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.UserManager;
import com.inrix.sdk.UserManager.ChangePasswordOptions;
import com.inrix.sdk.UserManager.UserCreateOptions;
import com.inrix.sdk.UserManager.UserGetOptions;
import com.inrix.sdk.UserManager.UserSignInOptions;
import com.inrix.sdk.authentication.Account;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountManagementActivity extends InrixSdkActivity {
    @BindViews({
            R.id.buttonCreateAccount,
            R.id.buttonSignIn,
            R.id.buttonGet,
            R.id.buttonSignOut,
            R.id.buttonChangePassword,
            R.id.buttonResetPassword})
    protected List<Button> buttons;

    @BindView(R.id.status)
    protected TextView statusView;

    private UserManager userManager;
    private Gson serializer;

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_account_management;
    }

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.serializer = new GsonBuilder().setPrettyPrinting().create();

        this.userManager = InrixCore.getUserManager();
        ButterKnife.bind(this);
        ButterKnife.apply(this.buttons, ENABLED, true);
    }

    private void showStatus(final String status) {
        this.statusView.setText(status);
    }

    @SuppressWarnings("ConstantConditions")
    private Pair<String, String> getLoginCredentials() {
        final String loginName = ((TextView) this.findViewById(R.id.loginName)).getText().toString();
        final String password = ((TextView) this.findViewById(R.id.password)).getText().toString();
        return new Pair<>(loginName, password);
    }

    @OnClick(R.id.buttonCreateAccount)
    protected void onCreateAccount() {
        final Pair<String, String> credentials = this.getLoginCredentials();
        final UserCreateOptions options = new UserCreateOptions(credentials.first, credentials.second);
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

    @OnClick(R.id.buttonSignIn)
    protected void onSignIn() {
        final Pair<String, String> credentials = this.getLoginCredentials();
        final UserSignInOptions options = new UserSignInOptions(credentials.first, credentials.second);
        this.userManager.signIn(options, new UserManager.UserSignInListener() {
            @Override
            public void onResult(Boolean result) {
                showStatus("Sign in successful: " + result);
            }

            @Override
            public void onError(Error error) {
                showStatus(serializer.toJson(error));
            }
        });
    }

    @OnClick(R.id.buttonGet)
    protected void onGetAccount() {
        if (!this.userManager.isSignedIn()) {
            this.showStatus("Create or sign in first.");
            return;
        }

        this.userManager.getUserInformation(new UserGetOptions(), new UserManager.UserGetListener() {
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

    @OnClick(R.id.buttonSignOut)
    protected void onSignOut() {
        this.userManager.signOut(new UserManager.UserSignOutListener() {
            @Override
            public void onResult(Boolean data) {
                showStatus(serializer.toJson(data));
            }

            @Override
            public void onError(Error error) {
                showStatus(serializer.toJson(error));
            }
        });
    }

    @OnClick(R.id.buttonChangePassword)
    protected void onChangePassword() {
        final Pair<String, String> credentials = this.getLoginCredentials();
        final String oldPassword = credentials.second;
        final String newPassword = "NewP@ssW0RD12#";

        final ChangePasswordOptions options = new ChangePasswordOptions(oldPassword, newPassword);
        this.userManager.changePassword(options, new UserManager.ChangePasswordListener() {
            @Override
            public void onResult(Boolean data) {
                showStatus("Password changed: " + data);
            }

            @Override
            public void onError(Error error) {
                showStatus(serializer.toJson(error));
            }
        });
    }

    @OnClick(R.id.buttonResetPassword)
    protected void onResetPassword() {
        final Pair<String, String> credentials = this.getLoginCredentials();
        this.userManager.resetPassword(new UserManager.ResetPasswordOptions(credentials.first), new UserManager.ResetPasswordListener() {

            @Override
            public void onResult(Boolean data) {
                showStatus("Password reset: " + data);
            }

            @Override
            public void onError(Error error) {
                showStatus(serializer.toJson(error));
            }
        });
    }

    @OnClick(R.id.status)
    protected void onStatusClicked() {
        this.statusView.setText("");
    }
}
