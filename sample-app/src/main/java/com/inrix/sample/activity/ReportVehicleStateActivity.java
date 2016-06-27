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
import android.view.View;
import android.widget.Toast;

import com.inrix.sample.R;
import com.inrix.sdk.Error;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.VehicleStateManager;
import com.inrix.sdk.model.VehicleState;

import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReportVehicleStateActivity extends InrixSdkActivity {
    private static final String VEHICLE_ID = UUID.randomUUID().toString();

    @BindView(R.id.report_vehicle_state_register)
    protected View registerButton;

    @BindView(R.id.report_vehicle_state_unregister)
    protected View unregisterButton;

    @BindView(R.id.report_vehicle_state_send_state)
    protected View reportStateButton;

    private VehicleStateManager manager;

    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_report_vehicle_state;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        if (InrixCore.getAuthenticator().hasAccessToken()) {
            this.onReady();
        } else {
            InrixCore.addOnInrixReadyListener(new InrixCore.OnInrixReadyListener() {
                @Override
                public void onInrixReady() {
                    InrixCore.removeOnInrixReadyListener(this);
                    onReady();
                }
            });
        }

        this.manager = InrixCore.getVehicleStateManager();
    }

    @OnClick(R.id.report_vehicle_state_register)
    protected void onRegisterClick() {
        registerVehicle();
    }

    @OnClick(R.id.report_vehicle_state_unregister)
    protected void onUnregisterClick() {
        unregisterVehicle();
    }

    @OnClick(R.id.report_vehicle_state_send_state)
    protected void onReportStateClick() {
        reportState();
    }

    private void onReady() {
        this.registerButton.setEnabled(true);
        this.unregisterButton.setEnabled(true);
        this.reportStateButton.setEnabled(true);
    }

    private void registerVehicle() {
        this.manager.registerVehicle(
                new VehicleStateManager.RegisterVehicleOptions(VEHICLE_ID),
                new VehicleStateManager.IRegisterVehicleListener() {
                    @Override
                    public void onResult(Boolean data) {
                        onSuccessResult("Vehicle registered successfully.");
                    }

                    @Override
                    public void onError(com.inrix.sdk.Error error) {
                        onErrorResult(error);
                    }
                });
    }

    private void unregisterVehicle() {
        this.manager.unregisterVehicle(
                new VehicleStateManager.UnregisterVehicleOptions(VEHICLE_ID),
                new VehicleStateManager.IUnregisterVehicleListener() {
                    @Override
                    public void onResult(Boolean data) {
                        onSuccessResult("Vehicle unregistered successfully.");
                    }

                    @Override
                    public void onError(com.inrix.sdk.Error error) {
                        onErrorResult(error);
                    }
                });
    }

    private void reportState() {
        this.manager.reportVehicleState(
                new VehicleStateManager.ReportVehicleStateOptions(VEHICLE_ID)
                        .withMeasurements(
                                new Date(),
                                new VehicleState.FuelLevel(25),
                                new VehicleState.FuelTankCapacity(24),
                                new VehicleState.AverageMileage(13)),
                new VehicleStateManager.IReportVehicleStateListener() {
                    @Override
                    public void onResult(Boolean data) {
                        onSuccessResult("Vehicle state reported successfully.");
                    }

                    @Override
                    public void onError(Error error) {
                        onErrorResult(error);
                    }
                });
    }

    public void onSuccessResult(final String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void onErrorResult(final com.inrix.sdk.Error error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
    }
}
