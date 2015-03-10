/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.news;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Represents a single news entry for a news feed.
 */
public final class NewsEntry {
	private String title;
	private String link;
	private Date pubDate;
	private String description;
	private MediaRSSItem newsItem;
	private MediaType mediaType = MediaType.UNKNOWN;

	public enum MediaType {
		AUDIO, VIDEO, IMAGE, UNKNOWN
	}

	/**
	 * Gets the news title.
	 * 
	 * @return News entry title.
	 */
	public final String getTitle() {
		return this.title;
	}

	/**
	 * Sets the new news entry title.
	 * 
	 * @param value
	 *            New title value.
	 * @return Current instance.
	 */
	public final NewsEntry setTitle(final String value) {
		this.title = value;
		return this;
	}

	/**
	 * Gets the link to the full news article.
	 * 
	 * @return News article link.
	 */
	public final String getLink() {
		return this.link;
	}

	/**
	 * Sets the new news article link.
	 * 
	 * @param value
	 *            New link value.
	 * @return Current instance.
	 */
	public final NewsEntry setLink(final String value) {
		this.link = value;
		return this;
	}

	/**
	 * Gets the news publication date. If you need formatted Date string please
	 * use {@link #getPublicationDateString()}
	 * 
	 * @return News publication date.
	 */
	public final Date getPublicationDate() {
		return this.pubDate;
	}

	/**
	 * Get formatted Date string
	 * 
	 * @return formatted date
	 */
	public String getPublicationDateString() {
		if (this.pubDate == null) {
			return null;
		}

		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy H:mm a",
				Locale.getDefault());
		return format.format(pubDate);
	}

	/**
	 * Sets the news publication date.
	 * 
	 * @param value
	 *            New publication date value.
	 * @return Current instance.
	 */
	public final NewsEntry setPublicationDate(final Date value) {
		this.pubDate = value;
		return this;
	}

	/**
	 * Gets the news description.
	 * 
	 * @return Description value.
	 */
	public final String getDescription() {
		return this.description;
	}

	/**
	 * Sets the news desciption.
	 * 
	 * @param value
	 *            New description value.
	 * @return Current instance.
	 */
	public final NewsEntry setDescription(final String value) {
		this.description = value;
		return this;
	}

	/**
	 * Get the media RSS item descriptor (with the content and thumb nail and
	 * other information)
	 * 
	 * @return - the news item
	 */
	public final MediaRSSItem getRssItem() {
		return this.newsItem;
	}

	/**
	 * Set media type to this entry
	 * 
	 * @param type
	 * @return
	 */
	public final NewsEntry setMediaType(MediaType type) {
		this.mediaType = type;
		return this;
	}

	/**
	 * Get media type of this item
	 * 
	 * @return
	 */
	public final MediaType getMediaType() {
		return this.mediaType;
	}

	/**
	 * Set the media RSS item for this news entry
	 * 
	 * @param rssItem
	 *            - the media rss item
	 */
	public final NewsEntry setRssItem(MediaRSSItem rssItem) {
		this.newsItem = rssItem;
		return this;
	}
}
