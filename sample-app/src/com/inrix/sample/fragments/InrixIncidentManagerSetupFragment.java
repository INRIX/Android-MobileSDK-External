/**
 * Copyright (c) 2013-2015 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.inrix.sample.Place;
import com.inrix.sample.Place.PlaceType;
import com.inrix.sample.R;
import com.inrix.sdk.model.GeoPoint;

/**
 * The Class InrixIncidentManagerSetupFragment.
 */
public class InrixIncidentManagerSetupFragment extends Fragment implements OnItemSelectedListener {

    /**
     * The listener interface for receiving IOnGetIncidents events. The class
     * that is interested in processing a IOnGetIncidents event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's
     * <code>addIOnGetIncidentsListener<code> method. When
     * the IOnGetIncidents event occurs, that object's appropriate
     * method is invoked.
     */
    public interface IOnGetIncidentsListener {
        void onGetIncidentsInCity(Place selectedCity);
    }

    private IOnGetIncidentsListener incidentsListener;
    private CityListAdapter adapter;
    private Place selectedCity;
    private Spinner spinner;

    /**
     * Instantiates a new inrix incident manager setup fragment.
     */
    public InrixIncidentManagerSetupFragment() {
        super();
    }

    /**
     * Instantiates a new inrix incident manager setup fragment.
     *
     * @param listener the listener
     */
    public InrixIncidentManagerSetupFragment(IOnGetIncidentsListener listener) {
        super();
        this.setIncidentsListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inrix_incidents_setup, container, false);
        this.spinner = (Spinner) view.findViewById(R.id.cityList);
        this.adapter = new CityListAdapter(this.getActivity());
        this.spinner.setAdapter(this.adapter);

        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.spinner.setOnItemSelectedListener(this);
    }

    public Place getSelectedCity() {
        return selectedCity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.spinner.setOnItemSelectedListener(null);
    }

    /**
     * @return the incidentsListener
     */
    public IOnGetIncidentsListener getIncidentsListener() {
        return incidentsListener;
    }

    /**
     * @param incidentsListener the incidentsListener to set
     */
    public void setIncidentsListener(IOnGetIncidentsListener incidentsListener) {
        this.incidentsListener = incidentsListener;
    }

    /**
     * The Class CityListAdapter.
     */
    class CityListAdapter extends ArrayAdapter<Place> {

        private Place[] citylist;

        public CityListAdapter(Context context) {
            super(context, R.layout.simple_spinner_item);
            this.citylist = this.buildPlacesList();
        }

        public int getCount() {
            return citylist.length;
        }

        public Place getItem(int position) {
            if (position < 0 || position > getCount()) {
                return null;
            }

            return citylist[position];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView result = (TextView) super.getView(position,
                    convertView,
                    parent);
            result.setText(this.getItem(position).getName());
            return result;
        }

        @Override
        public View getDropDownView(int position,
                                    View convertView,
                                    ViewGroup parent) {
            TextView result = (TextView) super.getDropDownView(position,
                    convertView,
                    parent);
            result.setText(this.getItem(position).getName());
            return result;
        }

        /**
         * Builds the places list.
         *
         * @return the place[]
         */
        private Place[] buildPlacesList() {
            Place[] places = new Place[6];
            Place seattle = new Place("Seattle, WA", new GeoPoint(47.614496,
                    -122.328758));
            seattle.addDestination(new Place("Work", new GeoPoint(47.602633,
                    -122.336243), PlaceType.WORK));
            seattle.addDestination(new Place("Home", new GeoPoint(47.616853,
                    -122.193044), PlaceType.HOME));
            places[0] = seattle;

            Place la = new Place("Los Angles, CA", new GeoPoint(34.052659,
                    -118.240585));
            la.addDestination(new Place("Work", new GeoPoint(33.810496,
                    -117.918885), PlaceType.WORK));
            la.addDestination(new Place("Home", new GeoPoint(34.012418,
                    -118.495835), PlaceType.HOME));
            places[1] = la;

            Place london = new Place("London, UK", new GeoPoint(51.510879,
                    -0.118904));
            london.addDestination(new Place("Palace", new GeoPoint(51.512054, -0.119662), PlaceType.WORK));
            london.addDestination(new Place("Home", new GeoPoint(51.560211, -0.279522), PlaceType.HOME));
            places[2] = london;

            Place paris = new Place("Paris, FR", new GeoPoint(48.857035,
                    2.351017));
            paris.addDestination(new Place("Versailes", new GeoPoint(48.805281, 2.124767), PlaceType.WORK));
            paris.addDestination(new Place("Home", new GeoPoint(48.883796, 2.332091), PlaceType.HOME));
            places[3] = paris;

            Place hawai = new Place("Honolulu, HI", new GeoPoint(21.309206,
                    -157.85717));
            hawai.addDestination(new Place("Work", new GeoPoint(21.359414, -157.962484), PlaceType.WORK));
            hawai.addDestination(new Place("Home", new GeoPoint(21.647337, -157.930663), PlaceType.HOME));
            places[4] = hawai;

            Place rioDeJaneiro = new Place("Rio de Janeiro", new GeoPoint(-22.906697, -43.205452));
            rioDeJaneiro.addDestination(new Place("Work", new GeoPoint(-22.922508, -43.182791), PlaceType.WORK));
            rioDeJaneiro.addDestination(new Place("Home", new GeoPoint(-22.823498, -43.37059), PlaceType.HOME));
            places[5] = rioDeJaneiro;

            return places;


        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.selectedCity = this.adapter.getItem(position);
        if (this.getIncidentsListener() != null) {
            this.getIncidentsListener().onGetIncidentsInCity(selectedCity);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

}
