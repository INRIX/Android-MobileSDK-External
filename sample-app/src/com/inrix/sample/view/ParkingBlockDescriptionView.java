package com.inrix.sample.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inrix.sample.R;
import com.inrix.sdk.model.ParkingBlock;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ParkingBlockDescriptionView extends LinearLayout {
    @Bind(R.id.parking_block_from)
    protected TextView fromText;

    @Bind(R.id.parking_block_to)
    protected TextView toText;

    public ParkingBlockDescriptionView(Context context) {
        super(context);
        this.init(context);
    }

    public ParkingBlockDescriptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public ParkingBlockDescriptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public ParkingBlockDescriptionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context);
    }

    private void init(final Context context) {
        inflate(context, R.layout.view_parking_block_description, this);

        this.setOrientation(VERTICAL);

        ButterKnife.bind(this, this);
    }

    public void bind(final ParkingBlock model) {
        final boolean hasFromStreet = !TextUtils.isEmpty(model.getFromStreetName());
        final boolean hasToStreet = !TextUtils.isEmpty(model.getToStreetName());
        if (hasFromStreet || hasToStreet) {
            if (hasFromStreet) {
                this.bindStart(model);
            } else {
                this.fromText.setVisibility(GONE);
            }

            if (hasToStreet) {
                this.bindEnd(model);
            } else {
                this.toText.setVisibility(GONE);
            }
        } else {
            this.setVisibility(GONE);
        }
    }

    private void bindStart(ParkingBlock model) {
        final String text = String.format(
                Locale.getDefault(),
                this.getContext().getString(R.string.parking_block_from_format),
                model.getFromStreetName(),
                model.getFromPoint());

        this.fromText.setText(text);
    }

    private void bindEnd(ParkingBlock model) {
        final String text = String.format(
                Locale.getDefault(),
                this.getContext().getString(R.string.parking_block_to_format),
                model.getToStreetName(),
                model.getToPoint());

        this.toText.setText(text);
    }
}
