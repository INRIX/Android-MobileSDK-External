/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountManagementActivity extends InrixSdkActivity {

    private static final String FACEBOOK_OAUTH_PROVIDER = "facebook";
    private static final String FACEBOOK_GRAPH_FIELDS = "fields";
    private static final String FACEBOOK_EMAIL = "email";
    private static final String FACEBOOK_GENDER = "gender";

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
    private CallbackManager facebookCallbackManager;
    private AccessTokenTracker facebookTokenTracker;

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_account_management;
    }

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        if (!FacebookSdk.isInitialized()) {
            // If using Facebook's LoginButton, then the SDK needs to be initialized before setting the view
            FacebookSdk.sdkInitialize(getApplicationContext());
        }

        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        this.serializer = new GsonBuilder().setPrettyPrinting().create();

        this.userManager = InrixCore.getUserManager();
        ButterKnife.bind(this);
        ButterKnife.apply(this.buttons, ENABLED, true);

        this.facebookCallbackManager = CallbackManager.Factory.create();
        this.facebookTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    // Capture log out via Facebook LoginButton
                    onSignOut();
                }
            }
        };

        // TODO: Facebook Test Email: sample_futrdtp_test@tfbnw.net
        //       Test Password: sampleapp

        final LoginButton loginButton = ButterKnife.findById(this, R.id.buttonFacebookLogin);
        loginButton.setReadPermissions(FACEBOOK_EMAIL);
        loginButton.registerCallback(this.facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final AccessToken accessToken = loginResult.getAccessToken();
                final String userId = accessToken.getUserId();
                final String token = accessToken.getToken();
                final String provider = FACEBOOK_OAUTH_PROVIDER;
                final UserSignInOptions options = new UserSignInOptions(null, userId, token, provider);
                userManager.signIn(options, new UserManager.UserSignInListener() {
                    @Override
                    public void onResult(Boolean result) {
                        showStatus("Sign in successful: " + result);
                        requestFacebookEmail();
                    }

                    @Override
                    public void onError(Error error) {
                        showStatus(serializer.toJson(error));
                    }
                });
            }

            @Override
            public void onCancel() {
                showStatus("Facebook login cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                showStatus(error.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        this.facebookTokenTracker.stopTracking();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.facebookCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
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
                showStatus("Log out successful");
                logOutOfFacebook();
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

    /**
     * Show status text on screen.
     *
     * @param status The text to display.
     */
    private void showStatus(final String status) {
        this.statusView.setText(status);
    }

    /**
     * Get the login credentials from the {@link android.widget.EditText} boxes.
     *
     * @return The login credentials.
     */
    @SuppressWarnings("ConstantConditions")
    private Pair<String, String> getLoginCredentials() {
        final String loginName = ((TextView) this.findViewById(R.id.loginName)).getText().toString();
        final String password = ((TextView) this.findViewById(R.id.password)).getText().toString();
        return new Pair<>(loginName, password);
    }

    /**
     * Request Facebook email and update the user's account.
     */
    private void requestFacebookEmail() {
        final AccessToken facebookToken = AccessToken.getCurrentAccessToken();
        if (facebookToken == null) {
            // Not logged in
            return;
        }

        final GraphRequest emailRequest = GraphRequest.newMeRequest(
                facebookToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        final UserManager.UserUpdateOptions options = new UserManager.UserUpdateOptions();

                        try {
                            options.setSignInName(object.getString(FACEBOOK_EMAIL));
                            options.setGender(object.getString(FACEBOOK_GENDER));
                        } catch (JSONException ignored) {
                        }

                        final Profile profile = Profile.getCurrentProfile();
                        if (profile != null) {
                            options.setGivenName(profile.getFirstName());
                            options.setSurname(profile.getLastName());
                        }

                        userManager.update(options, new UserManager.UserUpdateListener() {
                            @Override
                            public void onResult(Boolean result) {
                                showStatus(statusView.getText() + "\n\n"
                                        + "User updated");
                            }

                            @Override
                            public void onError(Error error) {
                                showStatus(statusView.getText() + "\n\n"
                                        + "Error updating user: " + error);
                            }
                        });
                    }
                });

        final Bundle params = new Bundle();
        params.putString(FACEBOOK_GRAPH_FIELDS, TextUtils.join(",", Arrays.asList(FACEBOOK_EMAIL, FACEBOOK_GENDER)));
        emailRequest.setParameters(params);
        emailRequest.executeAsync();
    }

    /**
     * Log out of Facebook.
     */
    private void logOutOfFacebook() {
        final AccessToken facebookToken = AccessToken.getCurrentAccessToken();
        if (facebookToken == null) {
            // Not logged in
            return;
        }

        LoginManager.getInstance().logOut();
    }
}
