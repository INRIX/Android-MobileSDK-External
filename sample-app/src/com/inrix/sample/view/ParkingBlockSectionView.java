package com.inrix.sample.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION_CODES;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inrix.sample.R;
import com.inrix.sdk.model.ParkingBlock.DurationUnit;
import com.inrix.sdk.model.ParkingBlock.ParkingRestriction;
import com.inrix.sdk.model.ParkingBlock.ParkingRestriction.RestrictionType;
import com.inrix.sdk.model.ParkingBlock.ParkingSection;
import com.inrix.sdk.model.ParkingBlock.PricingPayment;
import com.inrix.sdk.utils.CollectionUtils.IFunction;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.inrix.sdk.utils.CollectionUtils.map;

@SuppressWarnings("deprecation")
public class ParkingBlockSectionView extends LinearLayout {
    @Bind(R.id.parking_block_section_capacity)
    protected TextView capacityText;

    @Bind(R.id.parking_block_section_side)
    protected TextView sideText;

    @Bind(R.id.parking_block_section_zone)
    protected TextView zoneText;

    @Bind(R.id.parking_block_section_pricing_title)
    protected TextView pricingTitleText;

    @Bind(R.id.parking_block_section_pricing_list)
    protected BulletListView pricingList;

    @Bind(R.id.parking_block_section_restrictions_title)
    protected TextView restrrictionsTitleText;

    @Bind(R.id.parking_block_section_restrictions_list)
    protected BulletListView restrrictionsList;

    public ParkingBlockSectionView(Context context) {
        super(context);
        this.init(context);
    }

    public ParkingBlockSectionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public ParkingBlockSectionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    public ParkingBlockSectionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context);
    }

    private void init(final Context context) {
        inflate(context, R.layout.view_parking_block_section, this);

        this.setOrientation(VERTICAL);

        ButterKnife.bind(this, this);
    }

    public void bind(final ParkingSection section) {
        this.bindCapacity(section);
        this.bindSide(section);
        this.bindZone(section);
        this.bindPricing(section);
        this.bindRestrictions(section);
    }

    private void bindCapacity(final ParkingSection section) {
        final String text = String.format(
                Locale.getDefault(),
                this.getContext().getString(R.string.parking_block_section_capacity_format),
                section.getCapacity() < 0 ? 0 : section.getCapacity(),
                section.getOccupancy() == null ? -1 : section.getOccupancy().getValue(),
                section.getStartOffset(),
                section.getEndOffset());

        this.capacityText.setText(text);

        if (section.getOccupancy() == null) {
            return;
        }

        switch (section.getOccupancy().getBucket()) {
            case 4:
                this.capacityText.setTextColor(this.getContext().getResources().getColor(R.color.parking_section_occupancy_4));
                break;
            case 3:
                this.capacityText.setTextColor(this.getContext().getResources().getColor(R.color.parking_section_occupancy_3));
                break;
            case 2:
                this.capacityText.setTextColor(this.getContext().getResources().getColor(R.color.parking_section_occupancy_2));
                break;
            case 1:
                this.capacityText.setTextColor(this.getContext().getResources().getColor(R.color.parking_section_occupancy_1));
                break;
            case 0:
                this.capacityText.setTextColor(this.getContext().getResources().getColor(R.color.parking_section_occupancy_0));
                break;
            default:
                this.capacityText.setTextColor(this.getContext().getResources().getColor(R.color.parking_section_occupancy_none));
                break;
        }
    }

    private void bindSide(final ParkingSection section) {
        this.sideText.setText(section.getSide().getValue());
    }

    private void bindZone(final ParkingSection section) {
        switch (section.getParkingZone()) {
            case NO_PARKING:
                this.zoneText.setText(this.getContext().getString(R.string.parking_section_zone_no_parking));
                break;
            case CARPOOL_PARKING:
                this.zoneText.setText(this.getContext().getString(R.string.parking_section_zone_carpool));
                break;
            case NONPAID_PARKING:
                this.zoneText.setText(this.getContext().getString(R.string.parking_section_zone_non_paid));
                break;
            case PAID_PARKING:
                this.zoneText.setText(this.getContext().getString(R.string.parking_section_zone_paid_parking));
                break;
            case RESTRICTED_PARKING:
                this.zoneText.setText(this.getContext().getString(R.string.parking_section_zone_restricted));
                break;
            case TIME_LIMITED_PARKING:
                this.zoneText.setText(this.getContext().getString(R.string.parking_section_zone_time_limited));
                break;
            case UNRESTRICTED_PARKING:
                this.zoneText.setText(this.getContext().getString(R.string.parking_section_zone_unrestricted));
                break;
            case NONE:
            default:
                this.zoneText.setText(this.getContext().getString(R.string.parking_section_zone_none));
                break;
        }
    }

    private void bindPricing(final ParkingSection section) {
        final List<PricingPayment> pricing = section.getPricingPayments();
        if (pricing.isEmpty()) {
            this.pricingTitleText.setVisibility(GONE);
            this.pricingList.setVisibility(GONE);
            return;
        }

        this.pricingTitleText.setVisibility(VISIBLE);
        this.pricingList.setVisibility(VISIBLE);

        final List<String> data = map(pricing, new IFunction<PricingPayment, String>() {
            @Override
            public String apply(PricingPayment item) {
                final Currency currency = TextUtils.isEmpty(item.getCurrencyCode())
                        ? null
                        : Currency.getInstance(item.getCurrencyCode());

                final String duration = item.getDuration() + " " + durationUnitAsString(item.getDurationUnit());
                return String.format(
                        Locale.getDefault(),
                        getContext().getString(R.string.parking_block_section_pricing_format),
                        duration,
                        item.getAmount(),
                        currency == null ? "" : currency.getSymbol());
            }
        });

        this.pricingList.setData(data);
    }

    private void bindRestrictions(final ParkingSection section) {
        final List<ParkingRestriction> restrictions = section.getRestrictions();
        if (restrictions.isEmpty()) {
            this.restrrictionsTitleText.setVisibility(GONE);
            this.restrrictionsList.setVisibility(GONE);
            return;
        }

        this.restrrictionsTitleText.setVisibility(VISIBLE);
        this.restrrictionsList.setVisibility(VISIBLE);

        final List<String> data = map(restrictions, new IFunction<ParkingRestriction, String>() {
            @Override
            public String apply(ParkingRestriction restriction) {
                return String.format(
                        Locale.getDefault(),
                        getContext().getString(R.string.parking_block_section_restriction_format),
                        restrictionTypeAsString(restriction.getType()),
                        restriction.getDuration(),
                        durationUnitAsString(restriction.getDurationUnit()));
            }
        });

        this.restrrictionsList.setData(data);
    }

    private String restrictionTypeAsString(RestrictionType type) {
        switch (type) {
            case MAXIMUM_TIME:
                return "Max. time";
            default:
                return "Other";
        }
    }

    private String durationUnitAsString(DurationUnit durationUnit) {
        switch (durationUnit) {
            case HOUR:
                return "h.";
            case MINUTE:
                return "min.";
            default:
                return "";
        }
    }
}
