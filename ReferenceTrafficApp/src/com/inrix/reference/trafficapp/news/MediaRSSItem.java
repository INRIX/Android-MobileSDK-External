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
import java.util.List;
import java.util.Locale;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import android.text.Html;
import android.text.TextUtils;

@Root(name = "item", strict = false)
public class MediaRSSItem {

	@ElementList(inline = true, required = false, entry = "title")
	private List<String> title;

	@Element(required = false)
	private String description;

	@Element(required = false)
	private String link;

	@Element(required = false)
	private String pubDate;

	@ElementList(inline = true, required = false, name = "content")
	@Namespace(prefix = "media")
	@Path(value = "media:group")
	private List<MediaContent> groupContentList;

	@ElementList(inline = true, required = false, name = "content")
	@Namespace(prefix = "media")
	private List<MediaContent> contentList;

	@ElementList(inline = true, required = false, type = Thumbnail.class, name = "thumbnail")
	private List<Thumbnail> thumbnails;

	public String getTitle() {
		if (null != this.title && !title.isEmpty()) {
			return this.title.get(0);
		}
		return null;
	}

	private String fixedDescription = null;

	public String getDescription() {
		if (TextUtils.isEmpty(fixedDescription)
				&& !TextUtils.isEmpty(description)) {
			fixedDescription = Html.fromHtml(description).toString();
			fixedDescription = fixedDescription.trim();
		}
		return fixedDescription;
	}

	public String getLink() {
		return this.link;
	}

	public Date getPublishedDate() {
		Date retDate = null;
		if (this.pubDate != null) {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZZ",
						Locale.US);

				retDate = dateFormat.parse(this.pubDate);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return retDate;
	}

	public List<MediaContent> getContentList() {
		if (null != this.contentList) {
			return this.contentList;
		}
		return this.groupContentList;
	}

	public MediaContent getFirstMediaContent() {
		List<MediaContent> mediaContentList = getContentList();
		if (null != mediaContentList && !mediaContentList.isEmpty()) {
			MediaContent firstContent = mediaContentList.get(0);
			return firstContent;
		}
		return null;
	}

	public List<Thumbnail> getThumbNailList() {
		return this.thumbnails;
	}

}
