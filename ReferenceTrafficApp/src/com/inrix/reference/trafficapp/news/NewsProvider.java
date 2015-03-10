/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.news;

import java.util.HashSet;
import java.util.List;

/**
 * Represents a contract for a news provider.
 */
public abstract class NewsProvider {
	public static final int NEWS_TYPE_IMAGE = 0x01;
	public static final int NEWS_TYPE_AUDIO = 0x02;
	public static final int NEWS_TYPE_VIDEO = 0x04;

	public static final String MEDIUM_IMAGE = "image";
	public static final String MEDIUM_AUDIO = "audio";
	public static final String MEDIUM_VIDEO = "video";
	
	public static final int NEWS_TYPE_ALL   = 0xFF;
	public static final int NEWS_TYPE_NONE   = 0x00;
	
	public static HashSet<String> getMediumSet(int newsFilter){
		HashSet<String> returnSet = new HashSet<String>();
		if( (newsFilter & NEWS_TYPE_IMAGE) == NEWS_TYPE_IMAGE ){
			returnSet.add(MEDIUM_IMAGE);
		}
		if( (newsFilter & NEWS_TYPE_AUDIO) == NEWS_TYPE_AUDIO ){
			returnSet.add(MEDIUM_AUDIO);
		}
		if( (newsFilter & NEWS_TYPE_VIDEO) == NEWS_TYPE_VIDEO ){
			returnSet.add(MEDIUM_VIDEO);
		}
		return returnSet;
	}
	
	/**
	 * Gets the news entries from the provider.
	 * 
	 * @return A set of news entries.
	 */
	public abstract List<NewsEntry> getEntries();
	/**
	 * Get the List of News entries from a URL
	 * @param rssUrl - URL to get the rss feed from
	 * @param newsFilter the filter that has the bit fields set 
	 * @return A set of news entries.
	 */
	public abstract List<NewsEntry> getEntries(String rssUrl, int newsFilter);
	/**
	 * Get the list of News Entries from the content string provided
	 * @param rssContent - string that has the rss content
	 * @param newsFilter - the filter that has the bit fields set 
	 * @return A set of news entries.
	 */
	public abstract List<NewsEntry> getEntriesFromContent(String rssContent, int newsFilter);
	
	public abstract void cancelCurrentRequest();
	
}
