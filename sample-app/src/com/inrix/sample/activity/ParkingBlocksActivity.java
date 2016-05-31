package com.inrix.sample.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.inrix.sample.R;
import com.inrix.sample.fragments.MapLocationPickerDialog;
import com.inrix.sample.fragments.MapLocationPickerDialog.OnLocationSelectedListener;
import com.inrix.sample.view.ParkingBlockRecyclerAdapter;
import com.inrix.sample.view.ParkingBlockRecyclerView;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.ParkingManager;
import com.inrix.sdk.ParkingManager.IParkingInfoResponseListener;
import com.inrix.sdk.ParkingManager.ParkingRadiusOptions;
import com.inrix.sdk.model.ParkingBlock;
import com.inrix.sdk.model.ParkingInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.inrix.sample.util.GeoPointHelper.fromLatLng;

/**
 * Parking blocks sample.
 */
public final class ParkingBlocksActivity extends InrixSdkActivity {
    private static final String FRAGMENT_ID_LOCATION_PICKER = "location_picker";
    private static final String STATE_PARKING_BLOCKS = ":parking_blocks";

    private static final int DEFAULT_RADIUS = 5;

    @Bind(android.R.id.widget_frame)
    protected View root;

    @Bind(R.id.parking_blocks_list)
    protected ParkingBlockRecyclerView list;

    @Bind(R.id.empty)
    protected TextView emptyView;

    @Bind(R.id.progress)
    protected View progressView;

    private ParkingManager parkingManager;
    private ICancellable request;

    private ArrayList<ParkingBlock> parkingBlocks;
    private ParkingBlockRecyclerAdapter adapter;

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_parking_blocks;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.parkingBlocks = savedInstanceState.getParcelableArrayList(STATE_PARKING_BLOCKS);
        }

        ButterKnife.bind(this);

        this.list.setEmptyView(this.emptyView);
        this.list.setProgressView(this.progressView);
        this.list.setLayoutManager(new LinearLayoutManager(this));
        this.list.setItemAnimator(new DefaultItemAnimator());

        if (this.parkingBlocks != null) {
            this.adapter = new ParkingBlockRecyclerAdapter(this.parkingBlocks);
            this.list.setAdapter(this.adapter);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);

        if (this.parkingBlocks != null) {
            outState.putParcelableArrayList(STATE_PARKING_BLOCKS, this.parkingBlocks);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        this.getMenuInflater().inflate(R.menu.parking_blocks, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_parking_blocks_select_location:
                this.selectLocation();
                break;
        }

        return true;
    }

    /**
     * Opens a dialog to pick location on the map.
     */
    private void selectLocation() {
        MapLocationPickerDialog fragment =
                (MapLocationPickerDialog) this.getSupportFragmentManager().findFragmentByTag(FRAGMENT_ID_LOCATION_PICKER);
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
    private void locationSelected(final LatLng location) {
        if (this.parkingManager == null) {
            this.parkingManager = InrixCore.getParkingManager();
        }

        if (this.request != null) {
            this.request.cancel();
        }

        this.list.showProgress();

        final ParkingManager.ParkingRadiusOptions options = new ParkingRadiusOptions(fromLatLng(location), DEFAULT_RADIUS)
                .setOutputFields(ParkingManager.PARKING_OUTPUT_FIELD_ALL)
                .setParkingType(ParkingManager.PARKING_TYPE_PARKING_BLOCK);

        this.request = this.parkingManager.getParkingInfoInRadius(options, new IParkingInfoResponseListener() {
            @Override
            public void onResult(ParkingInfo data) {
                parkingBlocksReceived(data.getParkingBlocks());
            }

            @Override
            public void onError(com.inrix.sdk.Error error) {
                Snackbar.make(root, error.toString(), Snackbar.LENGTH_LONG).show();
                parkingBlocks = null;
                list.setAdapter(null);
            }
        });
    }

    /**
     * Called when new set of on-street parking data received.
     *
     * @param parkingBlocks Parking blocks.
     */
    private void parkingBlocksReceived(final List<ParkingBlock> parkingBlocks) {
        this.parkingBlocks = new ArrayList<>(parkingBlocks);
        this.adapter = new ParkingBlockRecyclerAdapter(this.parkingBlocks);
        this.list.setAdapter(this.adapter);
    }
}
