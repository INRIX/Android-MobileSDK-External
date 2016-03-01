/**
 * Copyright (c) 2013-2015 INRIX, Inc.
 * <p>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.activity;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.inrix.sample.R;
import com.inrix.sample.util.PermissionHelper;
import com.inrix.sdk.InrixCore;

/**
 * The main activity of the Sample App Gallery.
 */
public final class SampleAppsActivity extends ListActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {

    /**
     * A custom array adapter that shows a {@link SimpleView} containing details about the Sample Apps.
     */
    private static class CustomArrayAdapter extends ArrayAdapter<SampleAppDetails> {

        /**
         * @param demos An array containing the details of the demos to be displayed.
         */
        public CustomArrayAdapter(Context context, SampleAppDetails[] demos) {
            super(context, R.layout.simple_view, R.id.title, demos);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SimpleView featureView;
            if (convertView instanceof SimpleView) {
                featureView = (SimpleView) convertView;
            } else {
                featureView = new SimpleView(getContext());
            }

            SampleAppDetails demo = getItem(position);

            featureView.setTitleId(demo.titleId);
            featureView.setDescriptionId(demo.descriptionId);

            return featureView;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_apps);

        ListAdapter adapter = new CustomArrayAdapter(this, SampleAppDetailsList.SAMPLES);
        setListAdapter(adapter);

        this.checkPermissions(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionHelper.REQUEST_CODE_LOCATION_PERMISSIONS:
                if (PermissionHelper.checkRequestPermissionsResult(permissions, grantResults)) {
                    this.checkPermissions(true);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (PermissionHelper.hasAllPermissions(this)) {
            SampleAppDetails demo = (SampleAppDetails) getListAdapter().getItem(position);
            startActivity(new Intent(this, demo.activityClass));
        } else {
            checkPermissions(false);
        }
    }

    /**
     * Check if permissions are granted and request any permissions that are not granted.
     *
     * @param initializeSdk Provide {@code true} to initialize {@link InrixCore} if all permissions
     *     are granted.
     * @see PermissionHelper#hasAllPermissions(Context)
     * @see InrixCore#initialize(Context)
     */
    private void checkPermissions(boolean initializeSdk) {
        if (!PermissionHelper.hasAllPermissions(this)) {
            if (!PermissionHelper.hasLocationPermissions(this)) {
                PermissionHelper.requestLocationPermissions(this);
            }
        } else if (initializeSdk) {
            //noinspection ResourceType
            InrixCore.initialize(getApplicationContext());
        }
    }
}
