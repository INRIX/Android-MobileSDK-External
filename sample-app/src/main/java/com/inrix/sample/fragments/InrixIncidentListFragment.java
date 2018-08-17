/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.inrix.sample.Place;
import com.inrix.sample.Place.PlaceType;
import com.inrix.sample.R;
import com.inrix.sdk.Error;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.IncidentUtils;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Incident;
import com.inrix.sdk.model.LocationMatch;
import com.inrix.sdk.model.LocationMatchGoogle;
import com.inrix.sdk.model.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The Class InrixIncidentListFragment.
 */
public class InrixIncidentListFragment extends ListFragment {
    /**
     * The Class InrixListModel, compose 2 models Place, Route.
     */
    public class InrixListModel {
        private Place place;
        private Route route;

        /**
         * Instantiates a new inrix list model.
         *
         * @param place the place
         * @param route the route
         */
        public InrixListModel(Place place, Route route) {
            this.setPlace(place);
            this.setRoute(route);
        }

        /**
         * Gets the place.
         *
         * @return the place
         */
        public Place getPlace() {
            return place;
        }

        /**
         * Sets the place.
         *
         * @param place the new place
         */
        public void setPlace(Place place) {
            this.place = place;
        }

        /**
         * Gets the Route model.
         *
         * @return the route
         */
        public Route getRoute() {
            return route;
        }

        /**
         * Sets the Route model.
         *
         * @param route the new Route model
         */
        public void setRoute(Route route) {
            this.route = route;
        }
    }

    private InrixModelListAdapter adapter;

    private static final String DELAY_FORMAT = "%s minutes";

    private static final String BACKUP_FORMAT = "%.1f miles";

    /**
     * The Constant MIN_BACK_UP_VALUE.
     */
    public static final double MIN_BACK_UP_VALUE = 0.1;

    /**
     * The Constant MIN_DELAY_VALUE.
     */
    public static final double MIN_DELAY_VALUE = 0.5;

    private ICancellable searchRequest;

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new InrixModelListAdapter();

        this.setListShownNoAnimation(true);
        this.getListView().setDrawSelectorOnTop(true);
        this.getListView().setDivider(this.getActivity().getResources()
                .getDrawable(R.drawable.transparent_divider));
    }

    /**
     * Sets the incidents list.
     *
     * @param currentLocation the current location
     * @param incidents       the incidents
     */
    public void setIncidentsList(Place currentLocation, List<Incident> incidents) {
        setListAdapter(adapter);
        validateList(currentLocation, incidents);
    }

    /**
     * Clear all.
     */
    public void clearAll() {
        adapter.clearRoutes();
    }

    /**
     * Adds the routes list.
     *
     * @param currentLocation the current location
     * @param routes          the routes
     */
    public void addRoutesList(Place currentLocation, List<Route> routes) {
        setListAdapter(adapter);
        this.convertRoutesCollection(currentLocation, routes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onListItemClick(final ListView list,
                                final View selectedView,
                                int position,
                                long id) {
        super.onListItemClick(list, selectedView, position, id);
        // TODO: Show the Incident Details
    }

    /**
     * Validate list.
     *
     * @param incidents the incidents
     */
    private void validateList(Place currentLocation, List<Incident> incidents) {
        if (incidents == null) {
            adapter.setList(new ArrayList<Incident>(0));
            return;
        }

        final List<Incident> validatedIncidents = new ArrayList<>(incidents.size());
        for (Incident incident : incidents) {
            if (incident.getFullDescription() == null) {
                continue;
            } else {
                incident.setDistanceKM(10.9);
            }
            validatedIncidents.add(incident);
        }

        Collections.sort(validatedIncidents, IncidentUtils.getDefaultListComparator());
        adapter.setList(validatedIncidents);
    }

    /**
     * Convert routes collection.
     *
     * @param place  the place
     * @param routes the routes
     */
    private void convertRoutesCollection(final Place place,
                                         final List<Route> routes) {
        List<InrixListModel> convertedRoutes = new ArrayList<InrixListModel>();
        Iterator<Route> routeIterator = routes.iterator();
        while (routeIterator.hasNext()) {
            convertedRoutes
                    .add(new InrixListModel(place, routeIterator.next()));
        }

        adapter.addRoutes(convertedRoutes);
        adapter.notifyDataSetChanged();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        if (this.searchRequest != null) {
            this.searchRequest.cancel();
        }

        super.onDestroy();
    }

    /**
     * The Class InrixModelListAdapter, contains incidents and routes info
     */
    private class InrixModelListAdapter extends BaseAdapter {

        private static final int TYPE_INCIDENT = 0;
        private static final int TYPE_ROUTE = 1;
        private static final int TYPE_MAX_COUNT = TYPE_ROUTE + 1;
        private final LayoutInflater inflater;

        private List<InrixListModel> routes = new ArrayList<InrixListModel>();

        private List<Incident> incidents = new ArrayList<Incident>();

        /**
         * Stores incident description received from geocoder when short and
         * full descriptions are not available.
         */
        @SuppressLint("UseSparseArrays")
        private Map<Long, String> descriptionMap = new HashMap<Long, String>();

        private int height;

        private java.text.DateFormat dateformat;
        private final Context context;

        public InrixModelListAdapter() {
            super();
            this.context = InrixIncidentListFragment.this.getActivity();
            this.inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            height = context.getResources()
                    .getDimensionPixelSize(R.dimen.incident_list_item_height);
            dateformat = android.text.format.DateFormat.getTimeFormat(context);
        }

        @Override
        public int getItemViewType(int position) {
            return routes.size() > position ? TYPE_ROUTE : TYPE_INCIDENT;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }

        /**
         * Clear routes.
         */
        public void clearRoutes() {
            routes.clear();
        }

        /**
         * Sets the list.
         *
         * @param incidents the new list
         */
        public void setList(List<Incident> incidents) {
            this.incidents = incidents;
            notifyDataSetChanged();
        }

        /**
         * Adds the routes.
         *
         * @param routes the routes
         */
        public void addRoutes(List<InrixListModel> routes) {
            this.routes.addAll(routes);
            notifyDataSetChanged();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasStableIds() {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isEmpty() {
            return incidents.isEmpty() && routes.isEmpty();
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return incidents.size() + routes.size();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object getItem(int position) {
            if (position < 0 || position >= this.getCount()) {
                return null;
            }

            if (routes.size() > position) {
                return routes.get(position);
            }

            return this.incidents.get(position - routes.size());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View recycledView = convertView;

            ViewHolder viewHolder = new ViewHolder();
            if (recycledView == null) {
                recycledView = this.inflater
                        .inflate(R.layout.incidents_list_view_item, parent, false);
                viewHolder.description = (TextView) recycledView
                        .findViewById(R.id.incident_description);
                viewHolder.icon = (ImageView) recycledView
                        .findViewById(R.id.incident_icon);

                viewHolder.customProperty1Title = (TextView) recycledView
                        .findViewById(R.id.header1);

                viewHolder.customProperty2Title = (TextView) recycledView
                        .findViewById(R.id.header2);

                viewHolder.customProperty1 = (TextView) recycledView
                        .findViewById(R.id.incident_delay);

                viewHolder.customProperty2 = (TextView) recycledView
                        .findViewById(R.id.incident_back_up);

                recycledView.setTag(viewHolder);
                recycledView
                        .setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                                height));
            }

            viewHolder = (ViewHolder) recycledView.getTag();
            int type = getItemViewType(position);
            Object currentItem = this.getItem(position);
            switch (type) {
                case TYPE_INCIDENT:
                    Incident currentIncident = (Incident) currentItem;
                    if (currentIncident != null) {
                        setIncident(viewHolder, currentIncident);
                    }
                    break;
                case TYPE_ROUTE:
                    InrixListModel currentRoute = (InrixListModel) currentItem;
                    if (currentRoute != null) {
                        setRoute(viewHolder, currentRoute);
                    }
            }

            return recycledView;
        }

        /**
         * Sets the incident display layout
         *
         * @param holder the holder
         * @param model  the model
         */
        private void setIncident(ViewHolder holder, Incident model) {
            int resourceId = R.drawable.accident;

            if (IncidentUtils.isRoadClosure(model.getEventCode())) {
                resourceId = R.drawable.closed_road;
            } else {
                switch (model.getType()) {
                    case CONSTRUCTION:
                        resourceId = R.drawable.construction;
                        break;
                    case EVENT:
                        resourceId = R.drawable.closed_road;
                        break;
                    case CONGESTION:
                        resourceId = R.drawable.congestion;
                        break;
                    case HAZARD:
                        resourceId = R.drawable.hazard;
                        break;
                    case POLICE:
                        resourceId = R.drawable.police;
                        break;
                }
            }

            this.setDescription(holder, model);

            holder.icon.setImageResource(resourceId);
            if (model.getDelayImpact() == null) {
                holder.customProperty1.setText(null);
                holder.customProperty2.setText(null);
                holder.customProperty1Title.setText(null);
                holder.customProperty2Title.setText(null);
                return;
            }

            if (model.getDelayImpact().getFreeFlowMinutes() >= MIN_DELAY_VALUE
                    && model.getDelayImpact().getDistance() >= MIN_BACK_UP_VALUE) {
                holder.customProperty1Title
                        .setText(R.string.incident_delay_header);
                holder.customProperty1.setText(String.format(DELAY_FORMAT,
                        Math.round(model.getDelayImpact().getDistance())));

                holder.customProperty2Title
                        .setText(R.string.incident_backup_header);
                holder.customProperty2.setText(String.format(BACKUP_FORMAT,
                        model.getDelayImpact().getDistance()));

            } else if (model.getDelayImpact().getFreeFlowMinutes() < MIN_DELAY_VALUE
                    && model.getDelayImpact().getDistance() > MIN_BACK_UP_VALUE) {
                holder.customProperty2Title
                        .setText(R.string.incident_backup_header);
                holder.customProperty2.setText(String.format(BACKUP_FORMAT,
                        model.getDelayImpact().getDistance()));

                holder.customProperty1.setText(null);
                holder.customProperty1Title.setText(null);
            } else if (model.getDelayImpact().getFreeFlowMinutes() >= MIN_DELAY_VALUE
                    && model.getDelayImpact().getDistance() <= MIN_BACK_UP_VALUE) {
                holder.customProperty1Title
                        .setText(R.string.incident_delay_header);
                holder.customProperty1.setText(String.format(DELAY_FORMAT,
                        Math.round(model.getDelayImpact().getDistance())));

                holder.customProperty2Title.setText(null);
                holder.customProperty2.setText(null);
            } else {
                holder.customProperty1.setText(null);
                holder.customProperty2.setText(null);

                holder.customProperty1Title.setText(null);
                holder.customProperty2Title.setText(null);
            }
        }

        /**
         * Sets the route display layout.
         *
         * @param holder the holder
         * @param model  the model
         */
        private void setRoute(ViewHolder holder, InrixListModel model) {
            SpannableStringBuilder builder = new SpannableStringBuilder();

            builder.append(getString(R.string.route_list_item_destination_header,
                    model.getPlace().getName()));

            builder.setSpan(new TextAppearanceSpan(this.context,
                            R.style.list_view_text_header),
                    0,
                    builder.length() - 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            builder.append(model.getRoute().getSummary().getText());

            holder.description.setText(builder, TextView.BufferType.SPANNABLE);

            holder.icon
                    .setImageResource(model.getPlace().getType() == PlaceType.HOME ? R.drawable.ic_home
                            : R.drawable.ic_work);

            final int travelTime = model.getRoute().getTravelTimeMinutes();
            final long arrivalTime = System.currentTimeMillis()
                    + (travelTime * 60 * 1000);

            holder.customProperty1Title
                    .setText(getString(R.string.route_list_item_arrival_time));

            holder.customProperty1
                    .setText(getFormattedTimeForDisplay(arrivalTime));

            holder.customProperty2Title
                    .setText(getString(R.string.route_list_item_travel_time));

            holder.customProperty2
                    .setText(getString(R.string.route_list_item_travel_time_format,
                            travelTime));
        }

        private String getFormattedTimeForDisplay(long timeInMS) {
            return (dateformat.format(new Date(timeInMS)));
        }

        /**
         * Displays description for specific incident.
         *
         * @param holder   Holder to display the description.
         * @param incident Incident to take the description.
         */
        private void setDescription(ViewHolder holder, final Incident incident) {
            String description = incident.getFullDescription();

            // Uses full description.
            if (description != null && !description.isEmpty()) {
                holder.description.setText(description);
                return;
            }

            description = incident.getShortDescription();

            // Uses short description.
            if (description != null && !description.isEmpty()) {
                holder.description.setText(description);
                return;
            }

            // Uses cached value from geocoder.
            if (this.descriptionMap.containsKey(incident.getId())) {
                description = this.descriptionMap.get(incident.getId());
                holder.description.setText(description);
                return;
            }

            this.descriptionMap.put(incident.getId(),
                    getString(R.string.incident_description_loading));
            holder.description.setText(description);
        }

        class ViewHolder {
            TextView description;
            TextView customProperty1;
            TextView customProperty2;
            TextView customProperty2Title;
            TextView customProperty1Title;
            ImageView icon;
        }
    }
}
