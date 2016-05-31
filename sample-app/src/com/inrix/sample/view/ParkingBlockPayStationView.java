package com.inrix.sample.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inrix.sample.R;
import com.inrix.sdk.model.ParkingBlock.PayStation;
import com.inrix.sdk.model.ParkingBlock.PaymentMethod;
import com.inrix.sdk.utils.CollectionUtils.IFunction;

import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.inrix.sdk.utils.CollectionUtils.map;

public class ParkingBlockPayStationView extends LinearLayout {
    @Bind(R.id.parking_block_paystation_title)
    protected TextView payStationTitle;

    @Bind(R.id.parking_block_paystation_payment)
    protected BulletListView paymentMethodList;

    public ParkingBlockPayStationView(Context context) {
        super(context);
        this.init(context);
    }

    public ParkingBlockPayStationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public ParkingBlockPayStationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public ParkingBlockPayStationView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context);
    }

    private void init(final Context context) {
        inflate(context, R.layout.view_parking_block_pay_station, this);

        this.setOrientation(VERTICAL);

        ButterKnife.bind(this, this);
    }

    public void bind(final PayStation payStation) {
        final String title = String.format(
                Locale.getDefault(),
                this.getContext().getString(R.string.parking_block_pay_station_format),
                payStation.getId(),
                payStation.getLocation());

        this.payStationTitle.setText(title);

        final List<PaymentMethod> paymentMethods = payStation.getPaymentMethods();
        if (paymentMethods.isEmpty()) {
            this.paymentMethodList.setVisibility(GONE);
        } else {
            this.paymentMethodList.setVisibility(VISIBLE);

            final List<String> methodsInfo = map(paymentMethods, new IFunction<PaymentMethod, String>() {
                @Override
                public String apply(PaymentMethod method) {
                    return method.getMethod() + " (" + method.getDetails() + ")";
                }
            });

            this.paymentMethodList.setData(methodsInfo);
        }
    }
}
