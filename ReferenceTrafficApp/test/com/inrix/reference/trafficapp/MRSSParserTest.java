/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import com.inrix.reference.trafficapp.news.MRSSProvider;
import com.inrix.reference.trafficapp.news.NewsEntry;
import com.inrix.reference.trafficapp.news.NewsProvider;
import com.inrix.reference.trafficapp.test.data.MediaParserData;

@RunWith(RobolectricTestRunner.class)
public class MRSSParserTest {

	//MRSSProvider MRSSProvider = null;
	private long testTime = 0;
	private long testStartTime = 0;

	@Before
	public void setup() {
//		MRSSProvider = new MRSSProvider();
//		Assert.assertNotNull(MRSSProvider);
	}

	@Test
	public void testAverageSizeYahoo_VideoContent() {

		testStartTime = System.currentTimeMillis();

		String strContent = MediaParserData.kaitWeatherVideo;
		List<NewsEntry> newsEntryList = null;
		newsEntryList = MRSSProvider.getEntriesFromString(strContent,
				NewsProvider.NEWS_TYPE_ALL);
		Assert.assertFalse(newsEntryList.isEmpty());

		testTime = System.currentTimeMillis() - testStartTime;
		Assert.assertTrue("testWeatherVideo time " + testTime, testTime < 3000);

	}

	@Test
	public void testLongSize_VideoContent() throws Exception {
		testStartTime = System.currentTimeMillis();

		String strContent = MediaParserData.topVideoP1
				+ MediaParserData.topVideoP2 + MediaParserData.topVideoP3;
		List<NewsEntry> newsEntryList = MRSSProvider.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_ALL);
		Assert.assertFalse(newsEntryList.isEmpty());

		testTime = System.currentTimeMillis() - testStartTime;
		Assert.assertTrue("testLongTopVideo time " + testTime, testTime < 3000);

	}

	@Test
	public void testAverageSizeCorruptedVideo() throws Exception {
		testStartTime = System.currentTimeMillis();

		String strContent = MediaParserData.topVideoP1
				+ MediaParserData.topVideoP2;
		List<NewsEntry> newsEntryList = MRSSProvider.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_ALL);
		Assert.assertNull(newsEntryList);

		testTime = System.currentTimeMillis() - testStartTime;
		Assert.assertTrue("testAverageTopVideo time " + testTime,
				testTime < 3000);

	}

	@Test
	public void testBasicInfoParsingYahoo_SomeItemsMissing_VideoContent() {
		testStartTime = System.currentTimeMillis();
		String strContent = MediaParserData.kaitWeatherVideo;
		List<NewsEntry> newsEntryList = MRSSProvider
				.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_ALL);
		Assert.assertEquals(24, newsEntryList.size());
		Assert.assertEquals("KAIT Live - Available during regular newscast times and during breaking news",
				newsEntryList.get(0).getDescription());
		Assert.assertEquals("KAIT Live - Available during regular newscast times and during breaking news",
				newsEntryList.get(0).getTitle());
		Assert.assertNull(newsEntryList.get(0).getLink());
		Assert.assertEquals("http: kait-lh.akamaihd.net/i/KAIT_842@14629/master.m3u8?bkup=off",
				newsEntryList.get(0).getRssItem().getFirstMediaContent()
						.getUrl());
		Assert.assertEquals("video/mp4", newsEntryList.get(0).getRssItem()
				.getFirstMediaContent().getMediaType());
		Assert.assertEquals(360, newsEntryList.get(0).getRssItem()
				.getFirstMediaContent().getHeight());
		Assert.assertEquals(640, newsEntryList.get(0).getRssItem()
				.getFirstMediaContent().getWidth());
		Assert.assertNull(newsEntryList.get(0).getRssItem().getThumbNailList());
		Assert.assertNull(newsEntryList.get(0).getPublicationDate());

		testTime = System.currentTimeMillis() - testStartTime;
		System.out.println("testBasicInfoParsing time " + testTime);
	}

	@Test
	public void testBasicInfoParsingYahoo_VideoContent() {
		testStartTime = System.currentTimeMillis();
		String strContent = MediaParserData.kaitWeatherVideo;
		List<NewsEntry> newsEntryList = MRSSProvider
				.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_VIDEO);
		Assert.assertEquals(24, newsEntryList.size());
		Assert.assertEquals("Midday Tuesday News & Weather",
				newsEntryList.get(1).getDescription());
		Assert.assertEquals("Midday Tuesday News & Weather",
				newsEntryList.get(1).getTitle());
		Assert.assertEquals("http: www.kait8.com/category/240189/video-landing-page?clipId=9908819&clipFormat=&topVideoCatNo=201116",
				newsEntryList.get(1).getLink());
		Assert.assertEquals("Tue Mar 04 09:59:00 PST 2014", newsEntryList
				.get(1).getPublicationDate().toString());
		Assert.assertEquals("http: kait.videodownload.worldnow.com/kait_20140304124932570AB.mp4",
				newsEntryList.get(1).getRssItem().getFirstMediaContent()
						.getUrl());
		Assert.assertEquals("video/mp4", newsEntryList.get(1).getRssItem()
				.getFirstMediaContent().getMediaType());
		Assert.assertEquals(360, newsEntryList.get(1).getRssItem()
				.getFirstMediaContent().getHeight());
		Assert.assertEquals(640, newsEntryList.get(1).getRssItem()
				.getFirstMediaContent().getWidth());
		Assert.assertEquals(0, newsEntryList.get(1).getRssItem()
				.getFirstMediaContent().getBitRate());
		Assert.assertEquals(0, newsEntryList.get(1).getRssItem()
				.getFirstMediaContent().getFileSize());
		Assert.assertEquals("http: kait.images.worldnow.com/images/9908819_vt.jpg",
				newsEntryList.get(1).getRssItem().getThumbNailList().get(0)
						.getUrl());
		Assert.assertEquals(105, newsEntryList.get(1).getRssItem()
				.getThumbNailList().get(0).getWidth());
		Assert.assertEquals(60, newsEntryList.get(1).getRssItem()
				.getThumbNailList().get(0).getHeight());

		testTime = System.currentTimeMillis() - testStartTime;
		System.out.println("testBasicInfoParsing time " + testTime);
	}

	@Test
	public void testBasicInfoParsingNBC_VideoContent() {
		String strContent = MediaParserData.topVideoTwoItems;
		List<NewsEntry> newsEntryList = MRSSProvider
				.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_VIDEO);
		Assert.assertEquals("http://www.nbc-2.com/video?clipId=9909268&clipFormat=&topVideoCatNo=97711",
				newsEntryList.get(0).getLink());
		Assert.assertEquals("NBC2 WeatherNow: Tuesday", newsEntryList.get(0)
				.getTitle());
		Assert.assertEquals("Tue Mar 04 12:42:00 PST 2014", newsEntryList
				.get(0).getPublicationDate().toString());
		Assert.assertNull(newsEntryList.get(0).getDescription());

		Assert.assertEquals("video/mp4", newsEntryList.get(0).getRssItem()
				.getFirstMediaContent().getMediaType());
		Assert.assertEquals(360, newsEntryList.get(0).getRssItem()
				.getFirstMediaContent().getHeight());
		Assert.assertEquals(640, newsEntryList.get(0).getRssItem()
				.getFirstMediaContent().getWidth());

		Assert.assertEquals("video", newsEntryList.get(0).getRssItem()
				.getFirstMediaContent().getMedium());
		Assert.assertEquals(861, newsEntryList.get(0).getRssItem()
				.getFirstMediaContent().getBitRate());
		Assert.assertEquals(20686531, newsEntryList.get(0).getRssItem()
				.getFirstMediaContent().getFileSize());
		Assert.assertEquals("http://wbbh.videodownload.worldnow.com/WBBH_20140304153923447AA.mp4",
				newsEntryList.get(0).getRssItem().getFirstMediaContent()
						.getUrl());
		Assert.assertEquals(640, newsEntryList.get(0).getRssItem()
				.getFirstMediaContent().getWidth());
		Assert.assertEquals(360, newsEntryList.get(0).getRssItem()
				.getFirstMediaContent().getHeight());
		Assert.assertEquals("video", newsEntryList.get(0).getRssItem()
				.getFirstMediaContent().getMedium());

		Assert.assertEquals("video/3gpp", newsEntryList.get(0).getRssItem()
				.getContentList().get(1).getMediaType());
		Assert.assertEquals(58, newsEntryList.get(0).getRssItem()
				.getContentList().get(1).getBitRate());
		Assert.assertEquals(1426380, newsEntryList.get(0).getRssItem()
				.getContentList().get(1).getFileSize());
		Assert.assertEquals("http://wbbh.videodownload.worldnow.com/WBBH_20140304153923447AB.3gp",
				newsEntryList.get(0).getRssItem().getContentList().get(1)
						.getUrl());
		Assert.assertEquals(144, newsEntryList.get(0).getRssItem()
				.getContentList().get(1).getHeight());
		Assert.assertEquals(176, newsEntryList.get(0).getRssItem()
				.getContentList().get(1).getWidth());
		Assert.assertEquals("video", newsEntryList.get(0).getRssItem()
				.getContentList().get(1).getMedium());

		Assert.assertEquals("http://wbbh.images.worldnow.com/images/9909268_vt.jpg",
				newsEntryList.get(0).getRssItem().getThumbNailList().get(0)
						.getUrl());
		Assert.assertEquals(60, newsEntryList.get(0).getRssItem()
				.getThumbNailList().get(0).getHeight());
		Assert.assertEquals(105, newsEntryList.get(0).getRssItem()
				.getThumbNailList().get(0).getWidth());
		Assert.assertEquals("small", newsEntryList.get(0).getRssItem()
				.getThumbNailList().get(0).getSize());

		Assert.assertEquals("http://wbbh.images.worldnow.com/images/9909268_vk.jpg",
				newsEntryList.get(0).getRssItem().getThumbNailList().get(1)
						.getUrl());
		Assert.assertEquals(640, newsEntryList.get(0).getRssItem()
				.getThumbNailList().get(1).getHeight());
		Assert.assertEquals(360, newsEntryList.get(0).getRssItem()
				.getThumbNailList().get(1).getWidth());
		Assert.assertEquals("large", newsEntryList.get(0).getRssItem()
				.getThumbNailList().get(1).getSize());
	}

	@Test
	public void testBasicInfoParsing_ImageContent() {
		String strContent = MediaParserData.imageContent;
		List<NewsEntry> newsEntryList = MRSSProvider
				.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_IMAGE);
		/*String expectedDescription = "By Dan Levine SAN FRANCISCO (Reuters) - A U.S. judge on Thursday rejected Apple's request for a permanent sales ban in the United States against some older Samsung smartphones, a key setback for the iPhone maker in its global patent battle. U.S. District Judge Lucy Koh in San Jose, California, ruled that Apple Inc had not presented enough evidence to show that its patented features were a significant enough driver of consumer demand to warrant an injunction. Apple and Samsung Electronics Co Ltd have been litigating for nearly three years over various smartphone features patented by Apple, such as the use of fingers to pinch and zoom on the screen, as well as design elements such as the phone's flat, black glass screen. Apple was awarded more than $900 million by U.S. juries but the iPhone maker has failed to sustain a permanent sales ban against its rival, a far more serious threat to Samsung, which earned $7.7 billion last quarter.";
		Assert.assertEquals( newsEntryList.get(0)
				.getDescription(),expectedDescription, newsEntryList.get(0)
				.getDescription().trim());*/
		Assert.assertEquals("Thu Mar 06 10:54:29 PST 2014", newsEntryList
				.get(0).getPublicationDate().toString());
		Assert.assertEquals("Apple loses bid for U.S. ban on Samsung smartphone sales",
				newsEntryList.get(0).getTitle());
		Assert.assertEquals("image/jpeg", newsEntryList.get(0).getRssItem()
				.getContentList().get(0).getMediaType());
		Assert.assertEquals("http://l.yimg.com/bt/api/res/1.2/clYChDbg9inX90BaXBdwxQ--/YXBwaWQ9eW5ld3M7Zmk9ZmlsbDtoPTg2O3E9NzU7dz0xMzA-/http://media.zenfs.com/en_us/News/Reuters/2014-03-06T185429Z_3_CBREA2518TD00_RTROPTP_2_CTECH-US-APPLE-SAMSUNG-INJUNCTION.JPG",
				newsEntryList.get(0).getRssItem().getContentList().get(0)
						.getUrl());
		Assert.assertEquals(130, newsEntryList.get(0).getRssItem()
				.getContentList().get(0).getWidth());
		Assert.assertEquals(86, newsEntryList.get(0).getRssItem()
				.getContentList().get(0).getHeight());

		Assert.assertEquals(0, newsEntryList.get(0).getRssItem()
				.getContentList().get(0).getFileSize());
		Assert.assertEquals(0, newsEntryList.get(0).getRssItem()
				.getContentList().get(0).getBitRate());
		Assert.assertEquals(null, newsEntryList.get(0).getRssItem()
				.getContentList().get(0).getMedium());
		Assert.assertEquals(null, newsEntryList.get(0).getRssItem()
				.getThumbNailList());

	}

	@Test
	public void testBasicInfoParsing_AudioContent() {
		String strContent = MediaParserData.audioContent;
		List<NewsEntry> newsEntryList = MRSSProvider
				.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_AUDIO);
		Assert.assertEquals("audio/mp3", newsEntryList.get(0).getRssItem()
				.getContentList().get(0).getMediaType());
		Assert.assertEquals(8567787, newsEntryList.get(0).getRssItem()
				.getContentList().get(0).getFileSize());
		Assert.assertEquals("http://download.ted.com/talks/AnnetteHeuser_2013G.mp3?apikey=172BB350-0207",
				newsEntryList.get(0).getRssItem().getContentList().get(0)
						.getUrl());
		Assert.assertEquals("http://images.ted.com/images/ted/ae4840677d9d05de0c928eedcc9850150b21288f_480x360.jpg",
				newsEntryList.get(0).getRssItem().getThumbNailList().get(0)
						.getUrl());
		Assert.assertEquals(360, newsEntryList.get(0).getRssItem()
				.getThumbNailList().get(0).getHeight());
		Assert.assertEquals(480, newsEntryList.get(0).getRssItem()
				.getThumbNailList().get(0).getWidth());
	}

	@Test
	public void testEmptyCollection() {
		testStartTime = System.currentTimeMillis();
		testTime = System.currentTimeMillis() - testStartTime;
		String strContent = MediaParserData.emptyCollection;
		List<NewsEntry> newsEntryList = MRSSProvider
				.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_ALL);
		Assert.assertTrue(newsEntryList.isEmpty());
		System.out.println("testEmptyCollection time " + testTime);
	}

	@Test
	public void testAudioTypeOnlyFiltered() {
		testStartTime = System.currentTimeMillis();
		String strContent = MediaParserData.audioContent;
		List<NewsEntry> newsEntryList = MRSSProvider
				.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_AUDIO);
		Assert.assertEquals(10, newsEntryList.size());
		testTime = System.currentTimeMillis() - testStartTime;
		Assert.assertTrue("testWeatherVideo time " + testTime, testTime < 3000);

	}

	@Test
	public void testUnsupportedXMLFormat_ReturnsEmptyList() {
		testStartTime = System.currentTimeMillis();
		String strContent = MediaParserData.imageKaitDataP1;
		List<NewsEntry> newsEntryList = MRSSProvider
				.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_AUDIO);
		Assert.assertTrue(newsEntryList.isEmpty());
		testTime = System.currentTimeMillis() - testStartTime;
		System.out.println("testAudioTypeOnlyFiltered time " + testTime);

	}

	@Test
	public void testAudioMediaTypeOnlyFiltered() {
		testStartTime = System.currentTimeMillis();
		String strContent = MediaParserData.kaitWeatherVideo;
		List<NewsEntry> newsEntryList = MRSSProvider
				.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_AUDIO);
		Assert.assertTrue(newsEntryList.isEmpty());
		testTime = System.currentTimeMillis() - testStartTime;
		System.out.println("testAudioMediaTypeOnlyFiltered time " + testTime);
	}

	@Test
	public void testImageTypeOnlyFiltered_EmptyListReturned() {
		testStartTime = System.currentTimeMillis();
		String strContent = MediaParserData.topVideoP1
				+ MediaParserData.topVideoP2 + MediaParserData.topVideoP3;
		List<NewsEntry> newsEntryList = MRSSProvider
				.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_IMAGE);
		Assert.assertTrue(newsEntryList.isEmpty());
		testTime = System.currentTimeMillis() - testStartTime;
		System.out.println("testImageTypeOnlyFiltered time " + testTime);
	}

	@Test
	public void testImageTypeOnlyFiltered_ListReturned() {
		testStartTime = System.currentTimeMillis();
		String strContent = MediaParserData.imageContent;
		List<NewsEntry> newsEntryList = MRSSProvider
				.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_IMAGE);
		Assert.assertEquals(1, newsEntryList.size());
		testTime = System.currentTimeMillis() - testStartTime;
		System.out.println("testImageTypeOnlyFiltered time " + testTime);
	}

	@Test
	public void testImageMediaTypeOnlyFiltered() {
		testStartTime = System.currentTimeMillis();
		String strContent = MediaParserData.imageContent;
		List<NewsEntry> newsEntryList = MRSSProvider
				.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_IMAGE);
		Assert.assertEquals(1, newsEntryList.size());
		testTime = System.currentTimeMillis() - testStartTime;
		System.out.println("testImageMediaTypeOnlyFiltered time " + testTime);
	}

	@Test
	public void testVideoTypeOnlyFiltered() {
		testStartTime = System.currentTimeMillis();
		String strContent = MediaParserData.topVideoP1
				+ MediaParserData.topVideoP2 + MediaParserData.topVideoP3;
		List<NewsEntry> newsEntryList = MRSSProvider
				.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_VIDEO);
		Assert.assertEquals(49, newsEntryList.size());
		testTime = System.currentTimeMillis() - testStartTime;
		System.out.println("testVideoTypeOnlyFiltered time " + testTime);
	}

	@Test
	public void testVideoMediaTypeOnlyFiltered() {
		testStartTime = System.currentTimeMillis();
		String strContent = MediaParserData.kaitWeatherVideo;
		List<NewsEntry> newsEntryList = MRSSProvider
				.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_VIDEO);
		Assert.assertEquals(24, newsEntryList.size());
		testTime = System.currentTimeMillis() - testStartTime;
		System.out.println("testVideoMediaTypeOnlyFiltered time " + testTime);
	}

	@Test
	public void testNoneTypeOnlyFiltered() {
		testStartTime = System.currentTimeMillis();
		String strContent = MediaParserData.topVideoP1
				+ MediaParserData.topVideoP2 + MediaParserData.topVideoP3;
		List<NewsEntry> newsEntryList = MRSSProvider
				.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_NONE);
		Assert.assertTrue(newsEntryList.isEmpty());
		testTime = System.currentTimeMillis() - testStartTime;
		System.out.println("testNoneTypeOnlyFiltered time " + testTime);
	}

	@Test
	public void testNotSupportedTypeFormat() {
		testStartTime = System.currentTimeMillis();
		String strContent = MediaParserData.notSupportedTypeContent;
		List<NewsEntry> newsEntryList = MRSSProvider
				.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_ALL);
		Assert.assertTrue(newsEntryList.isEmpty());
		testTime = System.currentTimeMillis() - testStartTime;
		System.out.println("testNoneTypeOnlyFiltered time " + testTime);
	}

	@Test
	public void testAllTypesInOneXml_VerifiesOnlySupportedItemsReturned() {
		testStartTime = System.currentTimeMillis();
		String strContent = MediaParserData.complexXML;
		List<NewsEntry> newsEntryList = MRSSProvider
				.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_ALL);
		Assert.assertEquals(3, newsEntryList.size());
		testTime = System.currentTimeMillis() - testStartTime;
		System.out.println("testNoneTypeOnlyFiltered time " + testTime);
	}
	
	@Test
	public void testEmptyElementInContent() {
		
		String strContent = MediaParserData.emptyElement;
		List<NewsEntry> newsEntryList = MRSSProvider
				.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_ALL);
		Assert.assertEquals(3, newsEntryList.size());
		Assert.assertEquals("KAIT Live - Available during regular newscast times and during breaking news", newsEntryList.get(0).getTitle());
		Assert.assertEquals("GMR8 Friday News & Weather", newsEntryList.get(1).getTitle());
		Assert.assertEquals("Midday Thursday Threads of Life Interview", newsEntryList.get(2).getTitle());
	}
	
	@Test
	public void testEmptyWidthInContent() {
		
		String strContent = MediaParserData.emptyWidthElement;
		List<NewsEntry> newsEntryList = MRSSProvider
				.getEntriesFromString(strContent, NewsProvider.NEWS_TYPE_ALL);
		Assert.assertEquals(4, newsEntryList.size());
		Assert.assertEquals("KAIT Live - Available during regular newscast times and during breaking news", newsEntryList.get(0).getTitle());
		Assert.assertEquals("GMR8 Friday News & Weather", newsEntryList.get(1).getTitle());
		Assert.assertEquals("Midday Thursday Threads of Life Interview", newsEntryList.get(2).getTitle());
		Assert.assertEquals("Midday Thursday University Heights Lions Club Interview", newsEntryList.get(3).getTitle());
		Assert.assertEquals(0, newsEntryList.get(3).getRssItem().getFirstMediaContent().getHeight());
		Assert.assertEquals(0, newsEntryList.get(3).getRssItem().getFirstMediaContent().getWidth());
		Assert.assertEquals("http://kait.videodownload.worldnow.com/kait_20140327124221967AB.mp4", newsEntryList.get(3).getRssItem().getFirstMediaContent().getUrl());
		Assert.assertEquals("video/mp4", newsEntryList.get(3).getRssItem().getFirstMediaContent().getMediaType());
	}

}
