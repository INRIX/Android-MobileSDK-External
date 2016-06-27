/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inrix.sample.R;
import com.inrix.sdk.model.ParkingBlock;
import com.inrix.sdk.model.ParkingBlock.ParkingSection;
import com.inrix.sdk.model.ParkingBlock.PayStation;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ParkingBlockView extends LinearLayout {
    @BindView(R.id.parking_block_title)
    protected TextView title;

    @BindView(R.id.parking_block_description)
    protected ParkingBlockDescriptionView descriptionView;

    @BindView(R.id.parking_block_paystations_list_separator)
    protected View payStationsSeparator;

    @BindView(R.id.parking_block_paystations_list)
    protected ParkingBlockPayStationList payStationsList;

    @BindView(R.id.parking_block_sections_list_separator)
    protected View parkingSectionsSeparator;

    @BindView(R.id.parking_block_sections_list)
    protected ParkingBlockSectionsList parkingSections;

    public ParkingBlockView(Context context) {
        super(context);
        this.init(context);
    }

    public ParkingBlockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public ParkingBlockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public ParkingBlockView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context);
    }

    private void init(final Context context) {
        inflate(context, R.layout.view_parking_block, this);

        ButterKnife.bind(this, this);

        // Show parking icon.
        //noinspection ConstantConditions,deprecation
        final Drawable icon = context.getResources().getDrawable(R.drawable.ic_local_parking_black_48dp).mutate();
        icon.setColorFilter(Color.BLUE, Mode.SRC_ATOP);
        this.title.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
    }

    public void bind(final ParkingBlock model) {
        final String text = String.format(
                Locale.getDefault(),
                this.getContext().getString(R.string.parking_block_title_format),
                model.getStreetName(),
                model.getId());
        this.title.setText(text);

        this.descriptionView.bind(model);

        final List<PayStation> payStations = model.getPayStations();
        if (payStations.isEmpty()) {
            this.payStationsSeparator.setVisibility(GONE);
            this.payStationsList.setVisibility(GONE);
        } else {
            this.payStationsSeparator.setVisibility(VISIBLE);
            this.payStationsList.setVisibility(VISIBLE);
            this.payStationsList.bind(payStations);
        }

        final List<ParkingSection> sections = model.getParkingSections();
        if (sections.isEmpty()) {
            this.parkingSectionsSeparator.setVisibility(GONE);
            this.parkingSections.setVisibility(GONE);
        } else {
            this.parkingSectionsSeparator.setVisibility(VISIBLE);
            this.parkingSections.setVisibility(VISIBLE);
            this.parkingSections.bind(sections);
        }
    }
}
