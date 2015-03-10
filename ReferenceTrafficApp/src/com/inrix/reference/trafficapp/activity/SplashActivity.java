/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.build.Build;

/**
 * Application splash activity.
 */
public final class SplashActivity extends FragmentActivity {
	private static final int DEFAULT_SPLASH_TIMEOUT = 2000;
	private static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;

	private final Runnable nextActivityLauncher = new Runnable() {
		@Override
		public void run() {
			startNextActivity();
		}
	};

	final Handler handler = new Handler();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.splash_activity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();

		if (this.checkPlayServices()) {
			this.handler.postDelayed(this.nextActivityLauncher, DEFAULT_SPLASH_TIMEOUT);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		this.handler.removeCallbacks(this.nextActivityLauncher);
	}

	/**
	 * Starts main application activity.
	 */
	protected void startNextActivity() {
		if (Build.hasTimebombExpired()) {
			BetaExpiredDialogFragment betaDialog = new BetaExpiredDialogFragment();
			betaDialog.show(this.getSupportFragmentManager(), null);
			return;
		}

		Intent intent = new Intent(this, MainActivity.class);
		this.startActivity(intent);
		this.finish();
	}

	/**
	 * Checks if Google Play Services are enabled and device has correct version installed.
	 * 
	 * @return True if everything is fine; otherwise false.
	 */
	private final boolean checkPlayServices() {
		final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (status != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
				GooglePlayServicesUtil.showErrorDialogFragment(status, this, REQUEST_CODE_RECOVER_PLAY_SERVICES, new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						finish();
					}
				});
			} else {
				this.finish();
			}

			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected final void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_RECOVER_PLAY_SERVICES:
			if (resultCode == RESULT_CANCELED) {
				finish();
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public class BetaExpiredDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.beta_expired_title);
			builder.setMessage(R.string.beta_expired_message);

			builder.setNeutralButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
							SplashActivity.this.finish();
						}
					});
			// Create the AlertDialog object and return it
			AlertDialog dialog = builder.create();
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			return dialog;
		}
	}
}
