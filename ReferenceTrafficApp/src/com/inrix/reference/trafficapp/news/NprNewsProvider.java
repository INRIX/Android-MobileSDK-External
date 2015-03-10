/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.news;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * Sample NPR news provder.
 */
public final class NprNewsProvider extends NewsProvider {
	@Root(name = "rss", strict = false)
	public static final class Rss {
		@Element(name = "channel")
		public Channel channel;

		@Root(name = "channel", strict = false)
		public static final class Channel {
			@ElementList(inline = true)
			public List<Item> items;

			@Root(name = "item", strict = false)
			public static final class Item {
				@Element(required = false)
				public String title;

				@Element(required = false)
				public String description;

				@Element(required = false)
				public String link;

				@Element(required = false)
				public String pubDate;
			}
		}
	}

	private static final String RSS_URL = "http://www.npr.org/rss/rss.php?id=1001";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.inrix.reference.trafficapp.news.INewsProvider#getEntries()
	 */
	@Override
	public final List<NewsEntry> getEntries() {
		final List<NewsEntry> entries = new LinkedList<NewsEntry>();

		try {
			final HttpURLConnection connection = (HttpURLConnection) new URL(RSS_URL)
					.openConnection();
			final InputStream contentStream = connection.getInputStream();

			final Serializer serializer = new Persister();
			final Rss rss = serializer.read(Rss.class, contentStream);

			// Wed, 19 Feb 2014 14:18:00 -0500
			final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss z",
					Locale.ENGLISH);
			for (final Rss.Channel.Item nprEntry : rss.channel.items) {
				entries.add(new NewsEntry().setTitle(nprEntry.title)
						.setDescription(nprEntry.description)
						.setLink(nprEntry.link)
						.setPublicationDate(dateFormat.parse(nprEntry.pubDate)));
			}

			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return entries;
	}

	@Override
	public List<NewsEntry> getEntries(String rssUrl, int newsFilter) {
		return null;
	}

	@Override
	public List<NewsEntry> getEntriesFromContent(String rssContent,
			int newsFilter) {
		return null;
	}

	@Override
	public void cancelCurrentRequest() {
		// do nothing here. Ideally would need to cancel request
	}
}
