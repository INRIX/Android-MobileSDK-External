/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.inrix.sample.R;

/**
 * Utility class that helps check and request permissions needed by the INRIX SDK.
 */
public abstract class PermissionHelper {

    /**
     * ID to identify location permission requests.
     */
    public static final int REQUEST_CODE_LOCATION_PERMISSIONS = 100;

    /**
     * Check if the app has the all of the requested permissions granted.
     *
     * @param context An instance of {@link Context}.
     * @return {@code true} if all permissions are granted, {@code false} otherwise.
     */
    public static boolean hasAllPermissions(final Context context) {
        return hasLocationPermissions(context);
    }

    /**
     * Check if the app has fine and coarse location permissions granted.
     *
     * @param context An instance of {@link Context}.
     * @return {@code true} if location permissions are granted, {@code false} otherwise.
     * @see android.Manifest.permission#ACCESS_COARSE_LOCATION
     * @see android.Manifest.permission#ACCESS_FINE_LOCATION
     */
    public static boolean hasLocationPermissions(final Context context) {
        return isPermissionGranted(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                && isPermissionGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    /**
     * Request location permissions.
     *
     * @param activity The activity to display the request in and receive {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback#onRequestPermissionsResult(int, String[], int[])}.
     * @see #hasLocationPermissions(Context)
     * @see #REQUEST_CODE_LOCATION_PERMISSIONS
     */
    public static void requestLocationPermissions(final Activity activity) {
        if (hasLocationPermissions(activity)) {
            return;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    R.string.permission_rationale_location,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestLocationPermissionExplicit(activity);
                        }
                    })
                    .show();
        } else {
            requestLocationPermissionExplicit(activity);
        }
    }

    /**
     * Show explicit dialog, if possible, to request location permission.
     *
     * @param activity The activity to display the request in and receive {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback#onRequestPermissionsResult(int, String[], int[])}.
     */
    private static void requestLocationPermissionExplicit(final Activity activity) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_CODE_LOCATION_PERMISSIONS);
    }

    /**
     * Check if the permissions requested were granted by the user.
     *
     * @param permissions The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     * @return {@code true} if all permissions requested were granted, {@code false} otherwise.
     */
    public static boolean checkRequestPermissionsResult(@NonNull final String[] permissions, @NonNull final int[] grantResults) {
        if (grantResults.length == 0 || permissions.length != grantResults.length) {
            return false;
        }

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if the app has the provided permission granted.
     *
     * @param context An instance of {@link Context}.
     * @param permission The permission to check.
     * @return {@code true} if granted, {@code false} otherwise.
     */
    private static boolean isPermissionGranted(final Context context, final String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
