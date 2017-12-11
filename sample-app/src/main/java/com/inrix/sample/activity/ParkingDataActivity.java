package com.inrix.sample.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.inrix.sample.R;
import com.inrix.sample.fragments.MapLocationPickerDialog;
import com.inrix.sample.fragments.MapLocationPickerDialog.OnLocationSelectedListener;
import com.inrix.sample.view.ParkingRecyclerView;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.ParkingManager;
import com.inrix.sdk.ParkingManager.IParkingInfoResponseListener;
import com.inrix.sdk.ParkingManager.ParkingRadiusOptions;
import com.inrix.sdk.model.ParkingBlock;
import com.inrix.sdk.model.ParkingInfo;
import com.inrix.sdk.model.ParkingLot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.inrix.sample.activity.ParkingDataActivity.IParkingListItem.TYPE_PARKING_BLOCKS_SECTION;
import static com.inrix.sample.activity.ParkingDataActivity.IParkingListItem.TYPE_PARKING_BLOCK_ITEM;
import static com.inrix.sample.activity.ParkingDataActivity.IParkingListItem.TYPE_PARKING_LOTS_SECTION;
import static com.inrix.sample.activity.ParkingDataActivity.IParkingListItem.TYPE_PARKING_LOT_ITEM;
import static com.inrix.sample.util.GeoPointHelper.fromLatLng;

/**
 * Pepresents sample activity that displays a list of parking data.
 */
public class ParkingDataActivity extends InrixSdkActivity {
    private static final String FRAGMENT_ID_LOCATION_PICKER = "location_picker";
    private static final String STATE_PARKING_INFO = "parking_info";

    @SuppressWarnings("PointlessArithmeticExpression")
    private static final int DEFAULT_RADIUS_METERS = 1 * 1609; // Miles in meters.

    @BindView(R.id.parking_list)
    protected ParkingRecyclerView list;

    @BindView(R.id.empty)
    protected TextView emptyView;

    @BindView(R.id.progress)
    protected View progressView;

    private ParkingAdapter adapter;

    private ParkingManager parkingManager;
    private ICancellable request;
    private ParkingInfo parkingInfo;

    //<editor-fold desc="Lifecycle">

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_parking_list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.parkingInfo = savedInstanceState.getParcelable(STATE_PARKING_INFO);
        }

        ButterKnife.bind(this);

        this.list.setEmptyView(this.emptyView);
        this.list.setProgressView(this.progressView);
        this.list.setLayoutManager(new LinearLayoutManager(this));
        this.list.setItemAnimator(new DefaultItemAnimator());

        if (this.parkingInfo != null) {
            this.adapter = new ParkingAdapter(this.parkingInfo);
            this.list.setAdapter(this.adapter);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        if (this.parkingInfo != null) {
            outState.putParcelable(STATE_PARKING_INFO, this.parkingInfo);
        }
    }

    //</editor-fold>

    //<editor-fold desc="Menu">

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.getMenuInflater().inflate(R.menu.parking, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_parking_select_location:
                this.selectLocation();
                break;
        }

        return true;
    }

    //</editor-fold>

    //<editor-fold desc="Location selection">

    /**
     * Opens a dialog to pick location on the map.
     */
    private void selectLocation() {
        MapLocationPickerDialog fragment = (MapLocationPickerDialog) this.getSupportFragmentManager().findFragmentByTag(FRAGMENT_ID_LOCATION_PICKER);
        if (fragment != null) {
            (fragment).dismiss();
        }

        fragment = new MapLocationPickerDialog();
        fragment.setArguments(new Bundle());
        fragment.setOnLocationSelectedListener(new OnLocationSelectedListener() {
            @Override
            public void onLocationSelected(LatLng location) {
                locationSelected(location);
            }
        });

        fragment.show(this.getSupportFragmentManager(), FRAGMENT_ID_LOCATION_PICKER);
    }

    /**
     * Called when location is selected in the location picker.
     *
     * @param location Selected location.
     */
    void locationSelected(final LatLng location) {
        if (this.parkingManager == null) {
            this.parkingManager = InrixCore.getParkingManager();
        }

        if (this.request != null) {
            this.request.cancel();
        }

        this.requestParkingInformation(location);
    }

    //</editor-fold>

    /**
     * Request parking information for selected location.
     *
     * @param location Location that was selected in location picker.
     */
    private void requestParkingInformation(final LatLng location) {
        if (this.parkingManager == null) {
            this.parkingManager = InrixCore.getParkingManager();
        }

        if (this.request != null) {
            this.request.cancel();
        }

        this.list.showProgress();

        final ParkingManager.ParkingRadiusOptions options = new ParkingRadiusOptions(fromLatLng(location), DEFAULT_RADIUS_METERS)
                .setOutputFields(ParkingManager.PARKING_OUTPUT_FIELD_ALL)
                .setParkingType(ParkingManager.PARKING_TYPE_PARKING_LOT | ParkingManager.PARKING_TYPE_PARKING_BLOCK);

        this.request = this.parkingManager.getParkingInfoInRadius(options, new IParkingInfoResponseListener() {
            @Override
            public void onResult(ParkingInfo data) {
                parkingDataReceived(data);
            }

            @Override
            public void onError(com.inrix.sdk.Error error) {
                parkingDataFailed(error);
            }
        });
    }

    /**
     * Called when parking information received.
     *
     * @param info Parking data.
     */
    void parkingDataReceived(final ParkingInfo info) {
        this.parkingInfo = info;

        this.adapter = new ParkingAdapter(this.parkingInfo);
        this.list.setAdapter(this.adapter);
    }

    /**
     * Called when request to get parking data failed.
     *
     * @param error Error information.
     */
    void parkingDataFailed(final com.inrix.sdk.Error error) {
        this.list.setAdapter(null);
    }

    /**
     * Parking data list adapter.
     */
    public static final class ParkingAdapter extends RecyclerView.Adapter<TextHolder> {
        private final List<IParkingListItem> data;

        public ParkingAdapter(final ParkingInfo data) {
            this.data = this.transform(data);
        }

        @SuppressWarnings("unchecked")
        private List<IParkingListItem> transform(final ParkingInfo data) {
            final List<IParkingListItem> result = new ArrayList<>();

            final List<ParkingLot> lots = data.getParkingLots();
            if (!lots.isEmpty()) {
                result.add(new ParkingListItem("Lots", TYPE_PARKING_LOTS_SECTION));

                for (final ParkingLot lot : lots) {
                    result.add(new ParkingListItem(lot, TYPE_PARKING_LOT_ITEM));
                }
            }

            final List<ParkingBlock> blocks = data.getParkingBlocks();
            if (!blocks.isEmpty()) {
                result.add(new ParkingListItem("Blocks", TYPE_PARKING_BLOCKS_SECTION));

                for (final ParkingBlock block : blocks) {
                    result.add(new ParkingListItem(block, TYPE_PARKING_BLOCK_ITEM));
                }
            }

            return result;
        }

        @Override
        public int getItemViewType(final int position) {
            return this.data.get(position).getItemType();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TextHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int layout = 0;
            switch (viewType) {
                case TYPE_PARKING_LOTS_SECTION:
                case TYPE_PARKING_BLOCKS_SECTION:
                    layout = R.layout.view_parking_list_section;
                    break;
                case TYPE_PARKING_LOT_ITEM:
                case TYPE_PARKING_BLOCK_ITEM:
                    layout = R.layout.view_parking_list_item;
                    break;
            }

            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final View view = inflater.inflate(layout, parent, false);
            return new TextHolder(view);
        }

        @Override
        public void onBindViewHolder(TextHolder holder, int position) {
            String text = null;

            switch (holder.getItemViewType()) {
                case TYPE_PARKING_LOTS_SECTION:
                    text = "Off-street parking";
                    break;
                case TYPE_PARKING_LOT_ITEM:
                    text = ((ParkingLot) this.data.get(position).getData()).getName();
                    break;
                case TYPE_PARKING_BLOCKS_SECTION:
                    text = "On-street parking";
                    break;
                case TYPE_PARKING_BLOCK_ITEM:
                    text = ((ParkingBlock) this.data.get(position).getData()).getName();
                    break;
            }

            holder.setText(text);

        }

        @Override
        public int getItemCount() {
            return this.data.size();
        }
    }

    public interface IParkingListItem<T> {
        int TYPE_PARKING_LOTS_SECTION = 0;
        int TYPE_PARKING_BLOCKS_SECTION = 1;
        int TYPE_PARKING_LOT_ITEM = 2;
        int TYPE_PARKING_BLOCK_ITEM = 3;

        T getData();

        int getItemType();
    }

    public static final class ParkingListItem<T> implements IParkingListItem<T> {
        private final int type;
        private final T data;

        public ParkingListItem(final T data, final int type) {
            this.type = type;
            this.data = data;
        }

        @Override
        public T getData() {
            return this.data;
        }

        @Override
        public int getItemType() {
            return this.type;
        }
    }

    public static final class TextHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text)
        protected TextView textView;

        public TextHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void setText(final String text) {
            this.textView.setText(text);
        }
    }
}
