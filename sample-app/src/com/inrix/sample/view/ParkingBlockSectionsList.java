package com.inrix.sample.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.inrix.sample.R;
import com.inrix.sdk.model.ParkingBlock.ParkingSection;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ParkingBlockSectionsList extends LinearLayout {
    @Bind(R.id.parking_block_sections_list_items)
    protected LinearLayout itemsList;

    public ParkingBlockSectionsList(Context context) {
        super(context);
        this.init(context);
    }

    public ParkingBlockSectionsList(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public ParkingBlockSectionsList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public ParkingBlockSectionsList(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context);
    }

    private void init(final Context context) {
        inflate(context, R.layout.view_parking_block_sections_list, this);

        this.setOrientation(VERTICAL);

        ButterKnife.bind(this, this);
    }

    public void bind(final List<ParkingSection> sections) {
        this.itemsList.removeAllViews();

        for (final ParkingSection section : sections) {
            final ParkingBlockSectionView item = new ParkingBlockSectionView(this.getContext());
            this.itemsList.addView(item);

            item.bind(section);
        }
    }
}
