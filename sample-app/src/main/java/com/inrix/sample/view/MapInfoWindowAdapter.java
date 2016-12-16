/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

public final class MapInfoWindowAdapter implements InfoWindowAdapter {
    private final Context context;

    /**
     * Initializes a new instance of the {@link MapInfoWindowAdapter}.
     *
     * @param context Current context.
     */
    public MapInfoWindowAdapter(final Context context) {
        if (!(context instanceof Activity)) {
            throw new IllegalArgumentException("Context must be an instance of Activity");
        }

        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getInfoWindow(final Marker marker) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getInfoContents(final Marker marker) {
        final LinearLayout info = new LinearLayout(this.context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(params);

        final TextView title = new TextView(this.context);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);
        title.setText(marker.getTitle());

        final TextView snippet = new TextView(this.context);
        snippet.setTextColor(Color.GRAY);
        snippet.setText(marker.getSnippet());

        info.addView(title);
        info.addView(snippet);

        return info;
    }
}
