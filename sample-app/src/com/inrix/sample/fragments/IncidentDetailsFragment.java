/**
 * Copyright (c) 2013-2015 INRIX, Inc.
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

/**
 * The Class IncidentDetailsFragment.
 */
public class IncidentDetailsFragment extends Fragment {
    private TextView distanceTextView;
    private TextView descriptionTextView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_incidents_details,
                container,
                false);

        descriptionTextView = (TextView) rootView
                .findViewById(R.id.description);
        distanceTextView = (TextView) rootView.findViewById(R.id.distance);
        return rootView;
    }

    /**
     * Sets the incident details.
     *
     * @param description
     *            the new incident details
     */
    public void setIncidentDetails(final String description) {
        this.descriptionTextView.setText(description);
    }

    /**
     * Sets the incident distance.
     *
     * @param distance
     *            the new incident distance
     */
    public void setIncidentDistance(final double distance) {
        this.distanceTextView.setText(getString(R.string.incident_distance,
                distance));
    }

}
