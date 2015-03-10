/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowDrawable;
import org.robolectric.shadows.ShadowLog;

import utils.FragmentUtils;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.inrix.reference.trafficapp.fragments.NewsFragment;
import com.inrix.reference.trafficapp.news.MRSSProvider;
import com.inrix.reference.trafficapp.news.NewsEntry;
import com.inrix.reference.trafficapp.news.NewsEntry.MediaType;
import com.inrix.reference.trafficapp.news.NewsProvider;
import com.inrix.reference.trafficapp.news.Thumbnail;
import com.inrix.reference.trafficapp.test.data.MediaParserData;
import com.inrix.reference.trafficapp.view.CardView;
import com.squareup.picasso.MockPicasso;

@RunWith(RobolectricTestRunner.class)
public class NewsFragmentTest {
	private NewsFragment newsFragment = null;
	private ListView newsListView = null;
	MockNewsProvider newsProvider = null;


	@Before
	public void setup() {
		ShadowLog.stream = System.out;
		MockPicasso.init();
	
	}

	@After
	public void teardown() {
		MockPicasso.clear();
		newsFragment = null;
		newsListView = null;
		newsProvider = null;
	}

	@Test
	public void testNewsItemCount() {
		initNewsFragment(MediaParserData.topVideoTwoItems);
		verifyNewsFragmentContent();
	}

	@Test
	public void testAudioNewsList() {
		initNewsFragment(MediaParserData.audioContent);
		verifyNewsFragmentContent();
	}

	@Test
	public void testImageNewsList() {
		initNewsFragment(MediaParserData.imageContent);
		verifyNewsFragmentContent();
	}

	@Test
	public void testVideoNewsList() {
		initNewsFragment(MediaParserData.kaitWeatherVideo);
		verifyNewsFragmentContent();
	}
	
	@Test
	public void testEmptyMRSSfeed()
	{
		initNewsFragment(MediaParserData.EmptyContent);
		verifyNewsFragmentEmptyContent();
	}
	
	@Test
	public void testOnClickLoadsVideo() 
	{
		initNewsFragment(MediaParserData.kaitWeatherVideo);
		verifyNewsFragmentVideoLoad();
	}
	
	/* helper methods */
	

	private void verifyNewsFragmentVideoLoad() 
	{
		ShadowActivity shadowActivity;
		assertNotNull(newsFragment);
		assertNotNull(newsListView);	
		Robolectric.shadowOf(newsListView).populateItems();	
		Robolectric.shadowOf(newsListView).performItemClick(0);
		shadowActivity = Robolectric.shadowOf(FragmentUtils.getFragmentActivity());
	
		//check Intent 
		Intent intent = shadowActivity.peekNextStartedActivity();
	
		assertEquals(intent.getAction(),"android.intent.action.VIEW");
		
		
	}
	
	
	/* helper methods */
		
	
	private void verifyNewsFragmentEmptyContent() {
		assertNotNull(newsFragment);
		assertNotNull(newsListView);
		List<NewsEntry> newsEntryList = newsProvider.getEntries();
		Robolectric.shadowOf(newsListView).populateItems();
		int countOfItems = newsListView.getCount();
		//Verify that there r 0 items 
		assertEquals(newsEntryList.size(), countOfItems);
		//Verify that the view is empty
		View emptyView = newsListView.getEmptyView();
		assertEquals(emptyView.getVisibility(),View.VISIBLE);
	
	}
	


	private void verifyNewsFragmentContent() {
		assertNotNull(newsFragment);
		assertNotNull(newsListView);
		List<NewsEntry> newsEntryList = newsProvider.getEntries();
		Robolectric.shadowOf(newsListView).populateItems();
		int countOfItems = newsListView.getCount();
		assertEquals(newsEntryList.size(), countOfItems);
		for (int i = 0; i < countOfItems; i++) {
			CardView newsCard = (CardView) newsListView.getChildAt(i);
			verifyCardMatchesNews(newsCard, newsEntryList.get(i));
		}
	}

	private void initNewsFragment(String strNewsContent) {
		newsFragment = new NewsFragment();
		newsProvider = new MockNewsProvider(strNewsContent);
		MockNewsLoader newsLoader = new MockNewsLoader(Robolectric
				.getShadowApplication().getApplicationContext(), newsProvider);
		setNewsLoader(newsFragment, newsLoader);
		setNewsProvider(newsFragment, newsProvider);
		FragmentUtils.startv4Fragment(newsFragment);
		newsListView = newsFragment.getListView();
	}

	private void setNewsLoader(NewsFragment nFrag,
			Loader<List<NewsEntry>> loader) {
		assertNotNull(nFrag);
		assertNotNull(loader);
		try {
			Field providerFld = NewsFragment.class
					.getDeclaredField("newsLoader");
			providerFld.setAccessible(true);
			providerFld.set(nFrag, loader);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void setNewsProvider(NewsFragment nFrag,
			NewsProvider provider) {
		assertNotNull(nFrag);
		assertNotNull(provider);
		try {
			Field providerFld = NewsFragment.class
					.getDeclaredField("newsProvider");
			providerFld.setAccessible(true);
			providerFld.set(nFrag, provider);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void verifyCardMatchesNews(CardView card, NewsEntry news) {
		assertNotNull(card);
		assertNotNull(news);
		String title = news.getTitle();
		String footerText = news.getPublicationDateString();
		MediaType mType = news.getMediaType();
		Thumbnail thumbNailImage = null;
		try {
			thumbNailImage = news.getRssItem().getThumbNailList().get(0);
		} catch (Exception ex) {
		}
		TextView titleView = (TextView) card.findViewById(R.id.title);
		TextView footer = (TextView) card.findViewById(R.id.subtitle);
		ImageView thumbnail = (ImageView) card.findViewById(R.id.thumbnail);
		ImageView icon = (ImageView) card.findViewById(R.id.icon);

		if (null != title) {
			assertEquals(title, titleView.getText().toString());
		}
		if (null != footerText) {
			assertEquals(footerText, footer.getText().toString());
		}
		int thumbNailVisibility = (null == thumbNailImage) ? View.GONE
				: View.VISIBLE;
		assertEquals(thumbNailVisibility, thumbnail.getVisibility());
		if (thumbNailVisibility == View.VISIBLE) {
			String imageUrl = MockPicasso.getImagePath(thumbnail);
			assertEquals(thumbNailImage.getUrl(), imageUrl);
		}

		verifyIconResource(icon, mType);
	}

	private void verifyIconResource(ImageView icon, MediaType mediaType) {
		assertNotNull(icon);
		assertNotNull(mediaType);
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
		ShadowDrawable shadowDrawable = Robolectric
				.shadowOf(icon.getDrawable());
		int shadowResource = shadowDrawable.getCreatedFromResId();
		assertEquals(resourceId, shadowResource);
	}

	public static class MockNewsProvider extends NewsProvider {
		private List<NewsEntry> newsList;
	
		public MockNewsProvider(String strContent) {
			this.newsList = MRSSProvider.getEntriesFromString(strContent,
					NEWS_TYPE_ALL);
		}

		@Override
		public List<NewsEntry> getEntries() {
			return newsList;
		}

		@Override
		public List<NewsEntry> getEntries(String rssUrl, int newsFilter) {
			return newsList;
		}

		@Override
		public List<NewsEntry> getEntriesFromContent(String rssContent,
				int newsFilter) {
			return newsList;
		}

		@Override
		public void cancelCurrentRequest() {
			// TODO Auto-generated method stub
			
		}
	}

	public static class MockNewsLoader extends AsyncTaskLoader<List<NewsEntry>> {
		NewsProvider newsProvider = null;

		public MockNewsLoader(Context context, NewsProvider provider) {
			super(context);
			newsProvider = provider;
		}

		@Override
		public List<NewsEntry> loadInBackground() {
			return newsProvider.getEntries();
		}

		@Override
		public final void deliverResult(final List<NewsEntry> result) {
			if (this.isStarted()) {
				super.deliverResult(result);
			}
		}

		@Override
		protected final void onStartLoading() {
			this.deliverResult(newsProvider.getEntries());
		}
	}
}
