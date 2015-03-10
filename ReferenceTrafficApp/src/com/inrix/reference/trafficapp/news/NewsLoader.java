/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.news;

import java.util.List;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

/**
 * Background news loader.
 */
public final class NewsLoader extends AsyncTaskLoader<List<NewsEntry>> {
	private List<NewsEntry> data;
	private final NewsProvider provider;
	private String rssUrl = null;

	/**
	 * Initializes a new instance of the {@link NewsLoader} class.
	 * 
	 * @param context
	 *            Current context.
	 * @param provider
	 *            News provider.
	 */
	public NewsLoader(final Context context, final NewsProvider provider) {
		super(context);

		this.provider = provider;
	}

	/**
	 * Initializes a new instance of the {@link NewsLoader} class.
	 * 
	 * @param context
	 *            Current context.
	 * @param provider
	 *            News provider.
	 */
	public NewsLoader(final Context context, final NewsProvider provider,
			final String newsUrl) {
		super(context);
		rssUrl = newsUrl;
		this.provider = provider;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.content.AsyncTaskLoader#loadInBackground()
	 */
	@Override
	public final List<NewsEntry> loadInBackground() {
		if (!TextUtils.isEmpty(rssUrl)) {
			return this.provider
					.getEntries(rssUrl,
							(NewsProvider.NEWS_TYPE_AUDIO
									| NewsProvider.NEWS_TYPE_VIDEO | NewsProvider.NEWS_TYPE_IMAGE));
		}
		return this.provider.getEntries();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.content.Loader#deliverResult(java.lang.Object)
	 */
	@Override
	public final void deliverResult(final List<NewsEntry> result) {
		if (this.isStarted()) {
			super.deliverResult(result);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.content.Loader#onStartLoading()
	 */
	@Override
	protected final void onStartLoading() {
		if (this.data != null) {
			this.deliverResult(this.data);
		}

		if (this.takeContentChanged() || this.data == null) {
			this.forceLoad();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.content.Loader#onStopLoading()
	 */
	@Override
	protected final void onStopLoading() {
		this.cancelLoad();
		this.provider.cancelCurrentRequest();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.content.Loader#onReset()
	 */
	@Override
	protected final void onReset() {
		super.onReset();

		// Ensure the loader is stopped.
		this.onStopLoading();
	}
}
