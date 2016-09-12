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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.inrix.sample.R;
import com.inrix.sample.cameras.picasso.CamerasListAdapter;
import com.inrix.sdk.CameraManager;
import com.inrix.sdk.CameraManager.CamerasInRadiusOptions;
import com.inrix.sdk.CameraManager.CamerasOnRouteOptions;
import com.inrix.sdk.CameraManager.IGetCamerasResponseListener;
import com.inrix.sdk.Error;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.RouteManager;
import com.inrix.sdk.RouteManager.IRouteResponseListener;
import com.inrix.sdk.RouteManager.RequestRouteOptions;
import com.inrix.sdk.model.Camera;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.RequestRouteResults;
import com.inrix.sdk.model.Route;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The Class CamerasActivity, example of cameras APIs.
 */
public class CamerasActivity extends InrixSdkActivity {
    /**
     * The manager.
     */
    protected CameraManager manager;

    /**
     * The cameras list.
     */
    @BindView(R.id.cameras_list)
    protected ListView camerasList;

    /**
     * The progress bar.
     */
    @BindView(R.id.progress_bar)
    protected View progressBar;

    /**
     * Default start position.
     */
    private final GeoPoint SEATTLE = new GeoPoint(47.614496, -122.328758);

    /**
     * Redmond, WA.
     */
    private final GeoPoint REDMOND = new GeoPoint(47.672704, -122.123504);

    /**
     * The radius.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final int RADIUS = 5;

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_cameras;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        this.manager = InrixCore.getCameraManager();

        this.camerasList.setEmptyView(findViewById(android.R.id.empty));
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cameras_menu, menu);
        return true;
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_find_cameras) {
            this.getCamerasInSeattle();
            return true;
        }

        if (item.getItemId() == R.id.action_cameras_on_route) {
            this.getRoute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Gets the cameras in Seattle area.
     */
    private void getCamerasInSeattle() {
        if (this.manager == null) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        manager.getCamerasInRadius(new CamerasInRadiusOptions(SEATTLE, RADIUS),
                new IGetCamerasResponseListener() {

                    @Override
                    public void onResult(final List<Camera> data) {
                        progressBar.setVisibility(View.GONE);
                        camerasList.setAdapter(new CamerasListAdapter(getApplicationContext(), data));
                    }

                    @Override
                    public void onError(Error error) {
                        showError(error);
                    }
                });
    }

    /**
     * Gets the route information, and makes cameras along the route request.
     */
    private void getRoute() {
        if (this.manager == null) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        RouteManager routeManager = InrixCore.getRouteManager();
        routeManager.requestRoutes(new RequestRouteOptions(REDMOND, SEATTLE)
                        .setOutputFields(RouteManager.ROUTE_OUTPUT_FIELD_POINTS),
                new IRouteResponseListener() {

                    @Override
                    public void onResult(RequestRouteResults data) {
                        getCamerasOnRoute(data.getRoutes().get(0));
                    }

                    @Override
                    public void onError(Error error) {
                        showError(error);
                    }
                });
    }

    /**
     * Gets the cameras on route.
     *
     * @param routeWithPoints the route with points.
     */
    private void getCamerasOnRoute(final Route routeWithPoints) {
        manager.getCamerasOnRoute(new CamerasOnRouteOptions(routeWithPoints),
                new IGetCamerasResponseListener() {

                    @Override
                    public void onResult(final List<Camera> data) {
                        progressBar.setVisibility(View.GONE);
                        camerasList.setAdapter(new CamerasListAdapter(getApplicationContext(), data));

                    }

                    @Override
                    public void onError(Error error) {
                        showError(error);
                    }
                });
    }

    /**
     * Show error.
     *
     * @param error the error
     */
    private void showError(Error error) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(CamerasActivity.this, error.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
}
