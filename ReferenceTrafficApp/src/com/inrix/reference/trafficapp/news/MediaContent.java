/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.news;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import android.text.TextUtils;

@Root(name = "content", strict = false)
public class MediaContent {
	@Attribute(required = false)
	private String medium;

	@Attribute(required = false)
	private String url;

	@Attribute(required = false)
	private long fileSize;

	@Attribute(required = false, name = "type")
	private String mediaType;

	@Attribute(required = false)
	private int bitrate;

	@Attribute(required = false)
	private String width;

	@Attribute(required = false)
	private String height;

	public MediaContent() {

	}

	public String getMedium() {
		return this.medium;
	}

	public void setMedium(String medium) {
		this.medium = medium;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getFileSize() {
		return this.fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public String getMediaType() {
		return this.mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public int getBitRate() {
		return this.bitrate;
	}

	public void setBitRate(int bitRate) {
		this.bitrate = bitRate;
	}

	public int getWidth() {
		if (!TextUtils.isEmpty(this.width)) {
			return Integer.valueOf(this.width);
		}
		return 0;
	}

	public void setWidth(int width) {
		this.width = String.valueOf(width);
	}

	public int getHeight() {
		if (!TextUtils.isEmpty(this.height)) {
			return Integer.valueOf(this.height);
		}
		return 0;
	}

	public void setHeight(int height) {
		this.height = String.valueOf(height);
	}
}
