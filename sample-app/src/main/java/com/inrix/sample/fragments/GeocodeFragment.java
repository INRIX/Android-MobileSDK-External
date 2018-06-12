/**
 * Copyright (c) 2013-2016 INRIX, Inc.
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.inrix.sample.R;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.SearchManager;
import com.inrix.sdk.model.LocationMatch;
import com.inrix.sdk.model.LocationMatchGoogle;

import java.util.List;

/**
 * Demonstrates geocode functions.
 */
public class GeocodeFragment extends Fragment implements SearchManager.ISearchResponseListener<LocationMatchGoogle> {
    private TextView resultTextView;
    private SearchManager searchManager;
    private ICancellable searchRequest;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_geocode,
                container,
                false);
        final EditText editTextAddress = (EditText) view
                .findViewById(R.id.edt_address);

        final Button buttonGeocode = (Button) view
                .findViewById(R.id.btn_geocode);
        this.resultTextView = (TextView) view
                .findViewById(R.id.txt_address_result);

        this.searchManager = InrixCore.getSearchManager();

        buttonGeocode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                resultTextView.setText(R.string.geocode_status_in_progress);

                if (searchRequest != null) {
                    searchRequest.cancel();
                }

                searchRequest = searchManager.geocode(new SearchManager.GeocodeOptions(editTextAddress.getText().toString()), GeocodeFragment.this);
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        if (this.searchRequest != null) {
            this.searchRequest.cancel();
        }
        super.onDestroyView();
    }

    @Override
    public void onResult(List<LocationMatchGoogle> data) {
        StringBuilder stringBuilder = new StringBuilder();
        if (data != null && !data.isEmpty()) {
            int count = data.size();
            for (int i = 0; i < count; i++) {
                LocationMatch address = data.get(i);
                stringBuilder
                        .append(getString(R.string.geocode_result_latitude))
                        .append(address.getLocation().getLatitude()).append(" ");
                stringBuilder
                        .append(getString(R.string.geocode_result_longitude))
                        .append(address.getLocation().getLongitude()).append("\n");
            }
        } else {
            resultTextView.setText(R.string.geocode_status_no_address_found);
            return;
        }

        resultTextView.setText(getString(R.string.geocode_results, stringBuilder.toString()));
    }

    @Override
    public void onError(com.inrix.sdk.Error error) {
        resultTextView.setText(R.string.geocode_status_network_error);
    }
}