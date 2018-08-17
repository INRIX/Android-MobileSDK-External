package com.inrix.sample.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.inrix.sample.R;
import com.inrix.sample.fragments.MapLocationPickerDialog;
import com.inrix.sample.fragments.MapLocationPickerDialog.OnLocationSelectedListener;
import com.inrix.sample.fragments.ProgressDialog;
import com.inrix.sample.fragments.SavedLocationEditFragment;
import com.inrix.sdk.Error;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.LocationsManager;
import com.inrix.sdk.LocationsManager.DeleteLocationOptions;
import com.inrix.sdk.LocationsManager.GetLearnedLocationsOptions;
import com.inrix.sdk.LocationsManager.IDeleteSavedLocationResponseListener;
import com.inrix.sdk.LocationsManager.IGetLearnedLocationsListener;
import com.inrix.sdk.LocationsManager.IGetSavedLocationsResponseListener;
import com.inrix.sdk.LocationsManager.ISaveLocationResponseListener;
import com.inrix.sdk.LocationsManager.IUpdateLearnedLocationsListener;
import com.inrix.sdk.LocationsManager.SaveLocationOptions;
import com.inrix.sdk.LocationsManager.UpdateLearnedLocationsOptions;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.LearnedLocation;
import com.inrix.sdk.model.LocationMatch;
import com.inrix.sdk.model.LocationMatchGoogle;
import com.inrix.sdk.model.SavedLocation;
import com.inrix.sdk.model.SavedLocation.LocationType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Shows how to load, create, modify and delete {@link SavedLocation}s and hot to load, hide {@link LearnedLocation}s.
 */
public class PlacesActivity extends InrixSdkActivity implements SavedLocationEditFragment.Callback {
    private static final String FRAGMENT_ID_LOCATION_PICKER = "_location_picker";

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.places_list)
    protected RecyclerView placesList;

    protected List<ListModel<?>> places;

    protected ICancellable currentRequest;

    /**
     * {@inheritDoc}
     */
    @Override
    protected int getActivityLayoutResource() {
        return R.layout.activity_places;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        this.setSupportActionBar(this.toolbar);
        this.getSupportActionBar().setTitle(R.string.places_title);

        this.placesList.setLayoutManager(new LinearLayoutManager(this));
        this.placesList.setAdapter(new Adapter());

        this.loadPlaces();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        super.onPause();

        if (this.currentRequest != null) {
            ProgressDialog.dismiss(this);

            this.currentRequest.cancel();
            this.currentRequest = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        if (this.places == null) {
            return false;
        }

        this.getMenuInflater().inflate(R.menu.places_view_menu, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.places_add:
                this.selectLocation();
                break;
        }

        return true;
    }

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
     * Called when location selected on the map.
     *
     * @param location Selected location.
     */
    protected void locationSelected(final LatLng location) {
        ProgressDialog.show(this, R.string.places_resolving_address_message);

        final GeoPoint coordinates = new GeoPoint(location.latitude, location.longitude);
    }

    /**
     * Called when location details are received for selected point on map.
     *
     * @param coordinates Coordinates of the selected location.
     * @param match       Location information.
     */
    protected void locationAddressResolved(final GeoPoint coordinates, final LocationMatch match) {
        if (this.isFinishing()) {
            return;
        }

        ProgressDialog.dismiss(this);

        final String name = match == null ? "New Location" : match.getLocationName();
        final LocationType type = LocationType.OTHER;
        final String address = match == null ? "" : match.getFormattedAddress();

        final SavedLocation newLocation = new SavedLocation(name, type, address, coordinates);
        SavedLocationEditFragment.show(this, newLocation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLocationEdited(final SavedLocation location) {
        ProgressDialog.show(this, R.string.places_saving_location_message);

        final LocationsManager locationsManager = InrixCore.getLocationsManager();
        locationsManager.saveLocation(
                new SaveLocationOptions(location),
                new ISaveLocationResponseListener() {
                    @Override
                    public void onResult(final SavedLocation location) {
                        locationSaved();
                    }

                    @Override
                    public void onError(final Error error) {
                        handleError(error);
                    }
                });
    }

    /**
     * Called when location changes are saved.
     */
    protected void locationSaved() {
        ProgressDialog.dismiss(this);
        this.loadPlaces();
    }

    /**
     * Loads both saved and learned locations.
     */
    protected void loadPlaces() {
        ProgressDialog.show(this, R.string.places_loading_places_message);

        this.places = null;

        final LocationsManager locationsManager = InrixCore.getLocationsManager();
        this.currentRequest = locationsManager.getSavedLocations(
                null,
                new IGetSavedLocationsResponseListener() {
                    @Override
                    public void onResult(final List<SavedLocation> savedLocations) {
                        currentRequest = locationsManager.getLearnedLocations(
                                new GetLearnedLocationsOptions(),
                                new IGetLearnedLocationsListener() {
                                    @Override
                                    public void onResult(final List<LearnedLocation> learnedLocations) {
                                        placesLoaded(savedLocations, learnedLocations);
                                    }

                                    @Override
                                    public void onError(Error error) {
                                        handleError(error);
                                    }
                                });
                    }

                    @Override
                    public void onError(Error error) {
                        handleError(error);
                    }
                });
    }

    /**
     * Called when both saved and learned locations are loaded.
     *
     * @param savedLocations   Saved places list.
     * @param learnedLocations Learned places list.
     */
    protected void placesLoaded(final List<SavedLocation> savedLocations, final List<LearnedLocation> learnedLocations) {
        if (this.isFinishing()) {
            return;
        }

        ProgressDialog.dismiss(this);

        this.invalidateOptionsMenu();

        this.places = new ArrayList<>();
        if (savedLocations != null && !savedLocations.isEmpty()) {
            this.places.add(new SavedHeaderListModel());

            for (final SavedLocation location : savedLocations) {
                this.places.add(new SavedItemListModel(location));
            }
        }

        if (learnedLocations != null && !learnedLocations.isEmpty()) {
            this.places.add(new LearnedHeaderListModel());

            for (final LearnedLocation location : learnedLocations) {
                this.places.add(new LearnedItemListModel(location));
            }
        }

        this.placesList.getAdapter().notifyDataSetChanged();
    }

    /**
     * Called when {@link SavedLocation} is clicked in the list.
     *
     * @param savedLocation Selected {@link SavedLocation}.
     */
    protected void onSavedLocationListItemClick(final SavedLocation savedLocation) {
        SavedLocationEditFragment.show(this, savedLocation);
    }

    /**
     * Called when {@link LearnedLocation} is clicked in the list. Learned location is
     * converted to {@link SavedLocation} and dialog to save new location is opened.
     *
     * @param learnedLocation An instance of {@link LearnedLocation}.
     */
    protected void onLearnedLocationListItemClick(final LearnedLocation learnedLocation) {
        final SavedLocation newLocation = new SavedLocation(
                learnedLocation.getName(),
                LocationType.OTHER,
                learnedLocation.getAddress(),
                learnedLocation.getGeoPoint());

        SavedLocationEditFragment.show(this, newLocation);
    }

    /**
     * Called when delete button is clicked for {@link SavedLocation} in the list.
     *
     * @param savedLocation An instance of {@link SavedLocation}.
     */
    protected void onDeleteSavedLocationClick(final SavedLocation savedLocation) {
        final DeleteLocationOptions options = new DeleteLocationOptions(savedLocation);
        this.currentRequest = InrixCore.getLocationsManager().deleteLocation(
                options,
                new IDeleteSavedLocationResponseListener() {
                    @Override
                    public void onResult(List<SavedLocation> savedLocations) {
                        savedLocationDeleted();
                    }

                    @Override
                    public void onError(Error error) {
                        handleError(error);
                    }
                });
    }

    /**
     * Called when saved location successfully deleted.
     */
    protected void savedLocationDeleted() {
        if (this.isFinishing()) {
            return;
        }

        this.loadPlaces();
    }

    /**
     * Called when hide button is clicked for {@link LearnedLocation} in the list.
     *
     * @param learnedLocation An instance of {@link LearnedLocation}.
     */
    protected void onHideLearnedLocationClick(final LearnedLocation learnedLocation) {
        final UpdateLearnedLocationsOptions options = new UpdateLearnedLocationsOptions(
                Collections.singletonList(learnedLocation),
                true);
        this.currentRequest = InrixCore.getLocationsManager().updateLearnedLocations(
                options,
                new IUpdateLearnedLocationsListener() {
                    @Override
                    public void onResult(Boolean result) {
                        learnedLocationHidden();
                    }

                    @Override
                    public void onError(Error error) {
                        handleError(error);
                    }
                });
    }

    /**
     * Called when {@link LearnedLocation} was hidden successfully.
     */
    protected void learnedLocationHidden() {
        if (this.isFinishing()) {
            return;
        }

        this.loadPlaces();
    }

    /**
     * Handles errors.
     *
     * @param error Error instance.
     */
    protected void handleError(final Error error) {
        ProgressDialog.dismiss(this);
        Toast.makeText(this, error.getErrorMessage(), Toast.LENGTH_LONG).show();
    }

    /**
     * Places list adapter.
     */
    protected class Adapter extends RecyclerView.Adapter {
        protected static final int TYPE_SAVED_HEADER = 1;
        protected static final int TYPE_SAVED_ITEM = 2;
        protected static final int TYPE_LEARNED_HEADER = 3;
        protected static final int TYPE_LEARNED_ITEM = 4;

        /**
         * Initializes a new instance of {@link Adapter}.
         */
        public Adapter() {
            this.setHasStableIds(true);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getItemViewType(int position) {
            return places.get(position).getType();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            ViewHolder result = null;

            switch (viewType) {
                case TYPE_SAVED_HEADER:
                    result = new SavedHeaderViewHolder(inflater.inflate(R.layout.view_places_saved_header, parent, false));
                    break;
                case TYPE_SAVED_ITEM:
                    result = new SavedItemViewHolder(inflater.inflate(R.layout.view_places_saved_item, parent, false));
                    break;
                case TYPE_LEARNED_HEADER:
                    result = new LearnedHeaderViewHolder(inflater.inflate(R.layout.view_places_learned_header, parent, false));
                    break;
                case TYPE_LEARNED_ITEM:
                    result = new LearnedItemViewHolder(inflater.inflate(R.layout.view_places_learned_item, parent, false));
                    break;
            }

            return result;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final ListModel<?> listModel = places.get(position);
            switch (listModel.getType()) {
                case TYPE_SAVED_HEADER:
                    ((SavedHeaderViewHolder) holder).bind(listModel.getName());
                    break;
                case TYPE_SAVED_ITEM:
                    final SavedLocation savedLocation = (SavedLocation) listModel.model;
                    ((SavedItemViewHolder) holder).bind(savedLocation);
                    holder.itemView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final SavedLocation clicked = (SavedLocation) places.get(holder.getAdapterPosition()).getModel();
                            onSavedLocationListItemClick(clicked);
                        }
                    });
                    ((SavedItemViewHolder) holder).deleteButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final SavedLocation clicked = (SavedLocation) places.get(holder.getAdapterPosition()).getModel();
                            onDeleteSavedLocationClick(clicked);
                        }
                    });
                    break;
                case TYPE_LEARNED_HEADER:
                    ((LearnedHeaderViewHolder) holder).bind(listModel.getName());
                    break;
                case TYPE_LEARNED_ITEM:
                    ((LearnedItemViewHolder) holder).bind((LearnedLocation) listModel.model);
                    holder.itemView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final LearnedLocation clicked = (LearnedLocation) places.get(holder.getAdapterPosition()).getModel();
                            onLearnedLocationListItemClick(clicked);
                        }
                    });
                    ((LearnedItemViewHolder) holder).hideButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final LearnedLocation clicked = (LearnedLocation) places.get(holder.getAdapterPosition()).getModel();
                            onHideLearnedLocationClick(clicked);
                        }
                    });
                    break;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getItemCount() {
            return places == null ? 0 : places.size();
        }
    }

    /**
     * Wrapper for adapter data.
     *
     * @param <T> Type of wrapped model.
     */
    public abstract class ListModel<T> {
        protected final int type;
        protected final T model;

        /**
         * Initializes a new instance of {@link ListModel}.
         *
         * @param type  Model type.
         * @param model Model instance.
         */
        public ListModel(final int type, final T model) {
            this.type = type;
            this.model = model;
        }

        /**
         * Gets model type.
         *
         * @return Type value.
         */
        public int getType() {
            return this.type;
        }

        /**
         * Gets wrapped model instance.
         *
         * @return Wrapped model instance.
         */
        public T getModel() {
            return this.model;
        }

        /**
         * Gets common name value.
         *
         * @return Name value.
         */
        public abstract String getName();
    }

    /**
     * List model for {@link SavedLocation}s section header.
     */
    public class SavedHeaderListModel extends ListModel<Object> {
        /**
         * Initializes a new instance of {@link SavedHeaderListModel}.
         */
        public SavedHeaderListModel() {
            super(Adapter.TYPE_SAVED_HEADER, null);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() {
            return PlacesActivity.this.getString(R.string.places_header_saved);
        }
    }

    /**
     * List model for {@link SavedLocation}s list item.
     */
    public class SavedItemListModel extends ListModel<SavedLocation> {
        /**
         * Initializes a new instance of {@link SavedItemListModel}.
         *
         * @param model An instance of {@link SavedLocation}.
         */
        public SavedItemListModel(final SavedLocation model) {
            super(Adapter.TYPE_SAVED_ITEM, model);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() {
            return this.model.getName();
        }
    }

    /**
     * List model for {@link LearnedLocation}s header item.
     */
    public class LearnedHeaderListModel extends ListModel<Object> {
        /**
         * Initializes a new instance of {@link LearnedHeaderListModel}.
         */
        public LearnedHeaderListModel() {
            super(Adapter.TYPE_LEARNED_HEADER, null);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() {
            return PlacesActivity.this.getString(R.string.places_header_learned);
        }
    }

    /**
     * List model for {@link LearnedLocation} item.
     */
    public class LearnedItemListModel extends ListModel<LearnedLocation> {
        /**
         * Initializes a new instance of {@link LearnedItemListModel}.
         *
         * @param model An instance of {@link LearnedLocation}.
         */
        public LearnedItemListModel(final LearnedLocation model) {
            super(Adapter.TYPE_LEARNED_ITEM, model);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() {
            return this.model.getName();
        }
    }

    /**
     * View holder for {@link SavedLocation}s section header.
     */
    public class SavedHeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.places_saved_header_view_title)
        protected TextView title;

        /**
         * Initializes a new instance of {@link SavedHeaderViewHolder}.
         *
         * @param itemView Target view.
         */
        public SavedHeaderViewHolder(final View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        /**
         * Bind header.
         *
         * @param title Header title.
         */
        public void bind(final String title) {
            this.title.setText(title);
        }
    }

    /**
     * {@link SavedLocation}'s item view holder.
     */
    public class SavedItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.places_saved_item_view_name)
        protected TextView nameText;

        @BindView(R.id.places_saved_item_view_address)
        protected TextView addressText;

        @BindView(R.id.places_saved_item_view_delete)
        protected ImageButton deleteButton;

        /**
         * Initializes a new instance of {@link SavedItemViewHolder}.
         *
         * @param itemView Target view.
         */
        public SavedItemViewHolder(final View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        /**
         * Bind view to {@link SavedLocation}.
         *
         * @param model Saved location instance.
         */
        protected void bind(final SavedLocation model) {
            this.nameText.setText(model.getName());
            int icon = R.drawable.ic_star;
            switch (model.getType()) {
                case HOME:
                    icon = R.drawable.ic_home;
                    break;
                case WORK:
                    icon = R.drawable.ic_work;
                    break;
            }

            this.nameText.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(this.itemView.getContext(), icon),
                    null,
                    null,
                    null);

            this.addressText.setText(model.getAddress());
        }
    }

    /**
     * Learned locations section header view holder.
     */
    public class LearnedHeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.places_learned_header_view_title)
        protected TextView title;

        /**
         * Initializes a new instance of {@link LearnedHeaderViewHolder}.
         *
         * @param itemView Target view.
         */
        public LearnedHeaderViewHolder(final View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        /**
         * Bind holder.
         *
         * @param title Title string.
         */
        public void bind(final String title) {
            this.title.setText(title);
        }
    }

    /**
     * {@link LearnedLocation} view holder.
     */
    public class LearnedItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.places_learned_item_view_name)
        protected TextView nameText;

        @BindView(R.id.places_learned_item_view_address)
        protected TextView addressText;

        @BindView(R.id.places_learned_item_view_hide)
        protected ImageButton hideButton;

        /**
         * Initializes a new instance of {@link LearnedItemViewHolder}.
         *
         * @param itemView Target view.
         */
        public LearnedItemViewHolder(final View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        /**
         * Bind view holder to model.
         *
         * @param model An instance of {@link LearnedLocation}.
         */
        protected void bind(final LearnedLocation model) {
            this.nameText.setText(model.getName());
            this.nameText.setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(this.itemView.getContext(), R.drawable.ic_star),
                    null,
                    null,
                    null);

            this.addressText.setText(model.getAddress());
        }
    }
}
