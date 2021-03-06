/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.inrix.sample.R;

/**
 * A widget that describes an activity that demonstrates a feature.
 */
public final class SimpleView extends FrameLayout {

    /**
     * Constructs a feature view by inflating layout/feature.xml.
     */
    public SimpleView(Context context) {
        super(context);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.simple_view, this);
    }

    /**
     * Set the resource id of the title of the demo.
     *
     * @param titleId the resource id of the title of the demo
     */
    public synchronized void setTitleId(int titleId) {
        ((TextView) (findViewById(R.id.title))).setText(titleId);
    }


    /**
     * Set the resource id of the title of the demo.
     *
     * @param title the resource id of the title of the demo
     */
    public synchronized void setTitle(String title) {
        ((TextView) (findViewById(R.id.title))).setText(title);
    }

    /**
     * Set the description
     *
     * @param description
     */
    public synchronized void setDescription(String description) {
        ((TextView) (findViewById(R.id.description))).setText(description);
    }

    /**
     * Set the resource id of the description of the demo.
     *
     * @param descriptionId the resource id of the description of the demo
     */
    public synchronized void setDescriptionId(int descriptionId) {
        ((TextView) (findViewById(R.id.description))).setText(descriptionId);
    }

}
