/**
 * Copyright (c) 2013-2017 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */
package com.inrix.sample.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.inrix.sample.R;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.SpeedLimitManager;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.SpeedLimit;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class GetSpeedLimitFragment extends Fragment implements SpeedLimitManager.IGetSpeedLimitResponseListener {

    @BindView(R.id.latitude)
    TextView latitude;

    @BindView(R.id.longitude)
    TextView longitude;

    @BindView(R.id.heading)
    TextView heading;

    @BindView(R.id.results)
    TextView results;

    private SpeedLimitManager speedLimitManager;
    private ICancellable pendingRequest;
    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_get_speed_limit, container, false);

        this.unbinder = ButterKnife.bind(this, view);

        this.speedLimitManager = InrixCore.getSpeedLimitManager();

        return view;
    }

    @Override
    public void onDestroyView() {
        if (this.pendingRequest != null) {
            this.pendingRequest.cancel();
        }

        this.unbinder.unbind();

        super.onDestroyView();
    }

    @OnClick(R.id.btn_get_speed_limit)
    void onGetSpeedLimitClicked() {
        if (this.pendingRequest != null) {
            this.pendingRequest.cancel();
        }

        this.results.setText(R.string.get_speed_limit_pending);

        try {
            final double latitude = Double.valueOf(this.latitude.getText().toString());
            final double longitude = Double.valueOf(this.longitude.getText().toString());
            final int heading = Integer.valueOf(this.heading.getText().toString());
            this.pendingRequest = this.speedLimitManager.getSpeedLimit(new SpeedLimitManager.GetSpeedLimitOptions(
                    new GeoPoint(latitude, longitude),
                    heading),
                    this);
        } catch (Exception exception) {
            this.results.setText(exception.getMessage());
        }
    }

    @Override
    public void onResult(List<SpeedLimit> speedLimits) {
        if (speedLimits.isEmpty()) {
            this.results.setText(R.string.get_speed_limit_no_results);
            return;
        }

        StringBuilder resultBuilder = new StringBuilder();
        for (SpeedLimit speedLimit : speedLimits) {
            resultBuilder
                    .append("\n")
                    .append(getString(R.string.get_speed_limit_result_speed_limit,
                            String.valueOf(speedLimit.getSpeedLimit())))
                    .append("\n")
                    .append(getString(R.string.get_speed_limit_result_road_name,
                            String.valueOf(speedLimit.getRoadName())))
                    .append("\n")
                    .append(getString(R.string.get_speed_limit_result_frc_level,
                            String.valueOf(speedLimit.getFrcLevel())))
                    .append("\n");
        }

        this.results.setText(getString(R.string.get_speed_limit_result_format, resultBuilder.toString()));
    }

    @Override
    public void onError(com.inrix.sdk.Error error) {
        this.results.setText(error.toString());
    }
}