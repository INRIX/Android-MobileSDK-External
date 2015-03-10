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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.os.Handler;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.inrix.reference.trafficapp.TrafficApp;
import com.inrix.reference.trafficapp.error.ErrorEntity;
import com.inrix.reference.trafficapp.error.ErrorType;
import com.inrix.reference.trafficapp.news.NewsEntry.MediaType;

/**
 * MRSS provider.
 */
public final class MRSSProvider extends NewsProvider {

	private Handler uiThread = new Handler();
	private RequestQueue requestQueue;
	private CountDownLatch latch;
	private Request<?> currentRequest;

	@Root(name = "rss", strict = false)
	public static final class MRss {
		@Element(name = "channel")
		public Channel channel;

		@Root(name = "channel", strict = false)
		public static final class Channel {
			@ElementList(inline = true)
			public List<MediaRSSItem> items;
		}
	}

	public MRSSProvider() {
		this.requestQueue = Volley.newRequestQueue(TrafficApp.getContext());
	}

	@Override
	public final List<NewsEntry> getEntries(String rssURL, final int newsFilter) {
		final List<NewsEntry> entries = new ArrayList<NewsEntry>();
		latch = new CountDownLatch(1);

		cancelCurrentRequest();
		currentRequest = requestQueue.add(new StringRequest(rssURL,
				new Listener<String>() {

					@Override
					public void onResponse(String response) {
						currentRequest = null;
						final Serializer serializer = new Persister();
						MRss rss = null;
						try {
							rss = serializer.read(MRss.class, response, false);
						} catch (Exception e) {
							// TODO: right now we do not handle parse errors (by
							// design). If we ever need to - handling should be
							// done here
						}

						if (rss != null) {
							entries.addAll(processMRSSItemList(rss.channel.items,
									newsFilter));
						}
						latch.countDown();
					}
				},
				new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						currentRequest = null;
						final ErrorEntity errEntity;
						if (error instanceof NetworkError
								|| error instanceof TimeoutError) {
							errEntity = new ErrorEntity(ErrorType.NETWORK_ERROR);
						} else if (error instanceof ServerError
								|| error instanceof AuthFailureError) {
							errEntity = new ErrorEntity(ErrorType.SERVER_ERROR);
						} else {
							errEntity = null;
						}
						// TODO: should not be thrown from here. think of
						// some logic
						// to pass this error up (CM-6938)
						if (errEntity != null) {
							uiThread.post(new Runnable() {

								@Override
								public void run() {
									TrafficApp.getBus().post(errEntity);
								}
							});
						}
						latch.countDown();
					}
				}));

		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (entries.isEmpty()) {
			// TODO: come up with some better indication there was an error (CM-6938)
			return null;
		}

		return entries;
	}

	@Override
	public List<NewsEntry> getEntries() {
		return null;
	}

	@Override
	public List<NewsEntry> getEntriesFromContent(String rssContent,
			int newsFilter) {
		return getEntriesFromString(rssContent, newsFilter); 
	}
	
	public static  List<NewsEntry> getEntriesFromString(String rssContent,
			int newsFilter) {
		List<NewsEntry> entries = null;

		try {

			final Serializer serializer = new Persister();
			final MRss rss = serializer.read(MRss.class, rssContent);

			entries = processMRSSItemList(rss.channel.items, newsFilter);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return entries;

	}

	/**
	 * Process the MRSS item list and filter the items for content that we are
	 * interested in only
	 * 
	 * @param rssItemsList
	 *            - List of the Media RSS Items
	 * @param filter
	 *            - the filter that has the bit fields set for the content types
	 *            we are interested in
	 * @return
	 */
	private static List<NewsEntry> processMRSSItemList(List<MediaRSSItem> rssItemsList,
			int filter) {
		final List<NewsEntry> entries = new LinkedList<NewsEntry>();
		if (null != rssItemsList && filter != NEWS_TYPE_NONE) {

			HashSet<String> mediumList = getMediumSet(filter);

			for (final MediaRSSItem rssEntry : rssItemsList) {
				MediaContent content = rssEntry.getFirstMediaContent();
				if (isItemAllowed(content, mediumList)) {
					entries.add(new NewsEntry().setTitle(rssEntry.getTitle())
							.setDescription(rssEntry.getDescription())
							.setLink(rssEntry.getLink())
							.setPublicationDate(rssEntry.getPublishedDate())
							.setMediaType(resolveMediaType(content))
							.setRssItem(rssEntry));
				}
			}
		}
		return entries;
	}

	/**
	 * Try to resolve media type. At first, "medium" attribute will be checked.
	 * If it is not available, "type" attribute will be checked
	 * 
	 * @param media
	 * @return
	 */
	private static MediaType resolveMediaType(MediaContent media) {
		MediaType type = MediaType.UNKNOWN;
		if (media == null) {
			return type;
		}

		String mediaType = "";
		// check medium first
		if (!TextUtils.isEmpty(media.getMedium())) {
			mediaType = media.getMedium();
		} else if (!TextUtils.isEmpty(media.getMediaType())) {
			// medium is empty, so check type
			int endIndex = media.getMediaType().indexOf('/');
			if (endIndex > 0) {
				mediaType = media.getMediaType().substring(0, endIndex);
			}
		}

		if (TextUtils.equals(mediaType, NewsProvider.MEDIUM_AUDIO)) {
			type = MediaType.AUDIO;
		} else if (TextUtils.equals(mediaType, NewsProvider.MEDIUM_IMAGE)) {
			type = MediaType.IMAGE;
		} else if (TextUtils.equals(mediaType, NewsProvider.MEDIUM_VIDEO)) {
			type = MediaType.VIDEO;
		}

		return type;
	}

	/**
	 * Method to check if a media content item satisfies the allowed medium list
	 * 
	 * @param content
	 *            - the media content object
	 * @param mediumList
	 *            - the allowed mediums hash set
	 * @return
	 */
	private static boolean isItemAllowed(MediaContent content,
			HashSet<String> mediumList) {
		boolean bReturn = false;
		if (null != content && null != mediumList && !mediumList.isEmpty()) {
			String medium = content.getMedium();
			if (TextUtils.isEmpty(medium)) {
				// the content item did not have medium try the type
				String type = content.getMediaType();
				if (!TextUtils.isEmpty(type)) {
					int endIndex = type.indexOf('/');
					if (endIndex > 0) {
						medium = type.substring(0, endIndex);
					}
				}
			}
			if (!TextUtils.isEmpty(medium) && mediumList.contains(medium)) {
				bReturn = true;
			}
		}
		return bReturn;
	}

	@Override
	public void cancelCurrentRequest() {
		if (currentRequest != null) {
			currentRequest.cancel();
			currentRequest = null;
		}
	}

}
