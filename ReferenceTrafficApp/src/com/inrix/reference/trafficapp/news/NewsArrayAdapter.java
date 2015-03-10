/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.news;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.news.NewsEntry.MediaType;
import com.inrix.reference.trafficapp.view.CardView;

/**
 * Represents an adapter for news list fragment.
 */
public final class NewsArrayAdapter extends BaseAdapter {

	private List<NewsEntry> news = new ArrayList<NewsEntry>();
	private Context context;

	/**
	 * Initializes a new instance of the {@link NewsArrayAdapter}.
	 * 
	 * @param context
	 *            Current context.
	 */
	public NewsArrayAdapter(final Context context) {
		this.context = context;
	}

	/**
	 * Updates the data source for the adapter.
	 * 
	 * @param data
	 *            New data.
	 */
	public final void setData(final List<NewsEntry> data) {
		if (data != null) {
			news = data;
			notifyDataSetChanged();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public final View getView(final int position,
			final View convertView,
			final ViewGroup parent) {
		CardView card = null;

		if (convertView == null) {
			card = new CardView(context);
		} else {
			card = (CardView) convertView;
		}

		final NewsEntry item = this.getItem(position);

		card.setTitle(item.getTitle());
		card.setThumbnail(android.R.color.transparent);
		if (null != item.getPublicationDate()) {
			card.setSubtitle(item.getPublicationDateString());
		} else {
			card.setSubtitle("");
		}
		boolean bShowThumbnail = false;
		if (null != item.getRssItem()) {
			MediaRSSItem rssItem = item.getRssItem();
			List<Thumbnail> thumbNailList = rssItem.getThumbNailList();
			if (null != thumbNailList && !thumbNailList.isEmpty()) {
				Thumbnail tn = thumbNailList.get(0);
				if (!TextUtils.isEmpty(tn.getUrl())) {
					bShowThumbnail = true;
					card.setThumbnail(tn.getUrl());
				}
			}
		}

		if (item.getMediaType() != MediaType.UNKNOWN) {
			card.showIcon(true);
			card.setIcon(resolveIconResourceId(item.getMediaType()));
		} else {
			card.showIcon(false);
		}

		card.showThumbnail(bShowThumbnail);

		return card;
	}

	private int resolveIconResourceId(MediaType mediaType) {
		int resourceId = -1;
		switch (mediaType) {
			case AUDIO:
				resourceId = R.drawable.audio_mrss;
				break;
			case IMAGE:
				resourceId = R.drawable.photo_mrss;
				break;
			case VIDEO:
				resourceId = R.drawable.video_mrss;
				break;
			default:
				break;
		}
		return resourceId;
	}

	@Override
	public int getCount() {
		return news.size();
	}

	@Override
	public NewsEntry getItem(int position) {
		return news.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
}
