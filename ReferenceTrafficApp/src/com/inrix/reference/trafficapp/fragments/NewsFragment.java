/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.fragments;

import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.TrafficApp;
import com.inrix.reference.trafficapp.error.ActionRefreshEvent;
import com.inrix.reference.trafficapp.news.MRSSProvider;
import com.inrix.reference.trafficapp.news.MediaContent;
import com.inrix.reference.trafficapp.news.MediaRSSItem;
import com.inrix.reference.trafficapp.news.NewsArrayAdapter;
import com.inrix.reference.trafficapp.news.NewsEntry;
import com.inrix.reference.trafficapp.news.NewsLoader;
import com.inrix.reference.trafficapp.news.NewsProvider;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.PicassoTools;

/**
 * News fragment view.
 */
public final class NewsFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<List<NewsEntry>> {
	private NewsProvider newsProvider;
	private NewsArrayAdapter adapter;

	// private final String NBC_RSS_URL =
	// "http://www.nbc-2.com/category/97711/topvideo?clienttype=rssmedia";
	private final String YAHOO_RSS_URL = "http://pipes.yahoo.com/pipes/pipe.run?_id=8e0e274bff2ac36c7564693b634552e3&_render=rss";

	private Loader<List<NewsEntry>> newsLoader = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.initialize();
	}

	/**
	 * Initialize.
	 */
	private void initialize() {
		this.getNewsProvider();
		this.setEmptyText("No news :(");

		this.adapter = new NewsArrayAdapter(this.getActivity());
		setListAdapter(adapter);

		this.setListShown(false);
		this.getLoaderManager().initLoader(0, null, this);
	}

	/**
	 * Gets the news provider.
	 * 
	 * @return the news provider
	 */
	private NewsProvider getNewsProvider() {
		if (this.newsProvider == null) {
			this.newsProvider = new MRSSProvider();
		}

		return this.newsProvider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.ListFragment#onViewCreated(android.view.View,
	 * android.os.Bundle)
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		this.getListView().setSelector(R.drawable.selectable_item_background);
		this.getListView().setDrawSelectorOnTop(true);
		this.getListView().setDivider(
				new ColorDrawable(getResources().getColor(
						android.R.color.transparent)));
		this.getListView().setScrollBarStyle(
				ListView.SCROLLBARS_OUTSIDE_OVERLAY);
		this.getListView().setDividerHeight(
				getResources().getDimensionPixelSize(
						R.dimen.news_cards_list_separator_height));
		this.getListView().setClipChildren(false);

		int padding = getResources().getDimensionPixelSize(
				R.dimen.news_cards_list_padding);
		this.getListView().setPadding(padding, padding, padding, padding);
		this.getListView().setClipToPadding(false);

		this.setHasOptionsMenu(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu,
	 * android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.news, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView
	 * , android.view.View, int, long)
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		NewsEntry ne = adapter.getItem(position);
		MediaRSSItem rssItem = ne.getRssItem();
		if (null != rssItem && null != rssItem.getFirstMediaContent()) {
			MediaContent mc = rssItem.getFirstMediaContent();
			String mediaUrl = mc.getUrl();
			if (!TextUtils.isEmpty(mediaUrl)) {
				try {
					Intent myIntent = new Intent(Intent.ACTION_VIEW,
							Uri.parse(mediaUrl));
					startActivity(myIntent);
				} catch (ActivityNotFoundException e) {
					Toast.makeText(getActivity(), R.string.app_not_found,
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int,
	 * android.os.Bundle)
	 */
	@Override
	public Loader<List<NewsEntry>> onCreateLoader(int arg0, Bundle arg1) {
		setListShown(false);
		if (newsLoader == null) {
			return new NewsLoader(this.getActivity(), this.newsProvider,
					YAHOO_RSS_URL);
		}
		return newsLoader;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android
	 * .support.v4.content.Loader, java.lang.Object)
	 */
	@Override
	public void onLoadFinished(Loader<List<NewsEntry>> loader,
			List<NewsEntry> data) {
		this.adapter.setData(data);

		if (this.isResumed()) {
			this.setListShown(true);
		} else {
			this.setListShownNoAnimation(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android
	 * .support.v4.content.Loader)
	 */
	@Override
	public void onLoaderReset(Loader<List<NewsEntry>> loader) {
		this.adapter.setData(null);
	}

	@Override
	public void onStart() {
		super.onStart();
		TrafficApp.getBus().register(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		TrafficApp.getBus().unregister(this);
		
		// Remove it when Picasso with cache management is released.
		PicassoTools.clearCache();
	}

	@Subscribe
	public void actionRefreshEvent(ActionRefreshEvent refresh) {
		getLoaderManager().restartLoader(0, null, this);
	}
}
