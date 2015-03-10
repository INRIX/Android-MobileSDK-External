/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.squareup.picasso;

import android.widget.ImageView;

import java.util.HashMap;

/**
 * Mock the picasso image loader so that the actual image loading does not
 * happen
 * 
 */
public class MockPicasso extends Picasso {

	private static HashMap<ImageView, String> viewUrlMap;

	MockPicasso() {
		super(null, null, Cache.NONE, null, null, new MockStats(), false);
	}

	/** Initializes new {@code MockPicasso} and replaces production instance. */
	public static void init() {
		viewUrlMap = new HashMap<ImageView, String>();
		singleton = new MockPicasso();
	}

	/** clear the hash map with imageview controls and the image urls */
	public static void clear() {
		viewUrlMap.clear();
	}

	/** Returns the URL path for the view passed in */
	public static String getImagePath(ImageView view) {
		return viewUrlMap.get(view);
	}

	@Override
	public RequestCreator load(String path) {
		return new MockRequestCreator().setPath(path);
	}

	static class MockRequestCreator extends RequestCreator {
		private String path;

		public MockRequestCreator setPath(String path) {
			this.path = path;
			return this;
		}

		@Override
		public void into(ImageView target) {
			viewUrlMap.put(target, path);
		}
	}

	static class MockStats extends Stats {
		MockStats() {
			super(Cache.NONE);
		}
	}
}