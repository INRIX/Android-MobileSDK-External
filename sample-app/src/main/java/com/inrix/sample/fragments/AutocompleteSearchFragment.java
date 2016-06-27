/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.inrix.sample.R;
import com.inrix.sdk.Error;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.SearchManager;
import com.inrix.sdk.SearchManager.AutocompleteSearchOptions;
import com.inrix.sdk.model.AutocompleteMatch;
import com.inrix.sdk.model.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Demonstrates search autocomplete feature.
 */
public class AutocompleteSearchFragment extends Fragment implements
        TextWatcher,
        SearchManager.IAutocompleteResponseListener {

    private static final GeoPoint SEARCH_POSITION = new GeoPoint(47.614496, -122.328758); //Seattle, WA
    private static final long INPUT_SEARCH_DELAY = ViewConfiguration.getKeyRepeatTimeout();

    @BindView(android.R.id.text1)
    protected TextView searchBox;

    @BindView(android.R.id.list)
    protected ListView resultsList;

    private SearchManager searchManager;
    private ICancellable searchRequest;
    private Handler handler;
    private ArrayAdapter<String> resultsAdapter;

    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_autocomplete_search,
                container,
                false);

        this.unbinder = ButterKnife.bind(this, view);

        this.searchManager = InrixCore.getSearchManager();
        this.handler = new Handler(Looper.getMainLooper());

        this.searchBox.addTextChangedListener(this);

        this.resultsAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>());
        this.resultsList.setAdapter(this.resultsAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        this.searchBox.removeTextChangedListener(this);

        if (this.searchRequest != null) {
            this.searchRequest.cancel();
            this.searchRequest = null;
        }

        this.unbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        final String query = s.toString();
        final int queryLength = query.length();

        //remove pending searches
        this.handler.removeCallbacksAndMessages(null);
        if (queryLength < AutocompleteSearchOptions.MINIMUM_QUERY_CHARACTERS) {
            // minimum character requirement not met, so clear results and pending requests
            if (this.searchRequest != null) {
                this.searchRequest.cancel();
                this.searchRequest = null;
            }
            this.resultsAdapter.clear();
            this.resultsAdapter.notifyDataSetChanged();
            return;
        }

        if (queryLength == AutocompleteSearchOptions.MINIMUM_QUERY_CHARACTERS) {
            // Search immediately for first term to start showing results
            this.handler.post(new AutocompleteSearchRunnable(query));
        } else {
            this.handler.postDelayed(new AutocompleteSearchRunnable(query), INPUT_SEARCH_DELAY);
        }
    }

    @Override
    public void onResult(List<AutocompleteMatch> data) {
        final List<String> resultDescriptions = new ArrayList<>(data.size());
        for (AutocompleteMatch match : data) {
            resultDescriptions.add(match.getDescription());
        }
        this.resultsAdapter.clear();
        this.resultsAdapter.addAll(resultDescriptions);
        this.resultsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(Error error) {
        //No need to surface errors to the users.
        this.resultsAdapter.clear();
        this.resultsAdapter.notifyDataSetChanged();
    }

    private class AutocompleteSearchRunnable implements Runnable {
        private String query;

        private AutocompleteSearchRunnable(@NonNull String query) {
            this.query = query;
        }

        @Override
        public void run() {
            if (searchRequest != null) {
                searchRequest.cancel();
                searchRequest = null;
            }

            final AutocompleteSearchOptions options =
                    new AutocompleteSearchOptions(this.query, SEARCH_POSITION);
            searchRequest = searchManager.autocompleteSearch(options, AutocompleteSearchFragment.this);
        }
    }
}
