/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.TrafficApp;
import com.inrix.reference.trafficapp.fragments.AboutFragment;
import com.inrix.reference.trafficapp.fragments.AdsFragment;
import com.inrix.reference.trafficapp.fragments.NavigationDrawerFragment;
import com.inrix.reference.trafficapp.fragments.NavigationDrawerFragment.NavigationDrawerCallbacks;
import com.inrix.reference.trafficapp.fragments.NewsFragment;
import com.inrix.reference.trafficapp.fragments.TrafficFragment;
import com.inrix.reference.trafficapp.util.AndroidCompatUtils;
import com.inrix.reference.trafficapp.view.SlidingBarErrorPresenter;

/**
 * The main activity of the Weather Channel application 
 */
public final class MainActivity extends ErrorControllerActivity implements
		NavigationDrawerCallbacks {
	private static final String ADS_FRAGMENT_LABEL = "inrix_ads_fragment";
	private static final String CURRENT_DRAWER_TITLE = "drawer_title";

	private String title;
	private NavigationDrawerFragment navigationDrawer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main_activity);
		AndroidCompatUtils.fixWindowContentOverlay(this);

		final TextView customView = (TextView) LayoutInflater.from(this)
				.inflate(R.layout.main_custom_action_bar, null);
		final ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
				ActionBar.LayoutParams.WRAP_CONTENT,
				Gravity.CENTER);

		this.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);
		this.getActionBar().setCustomView(customView, params);

		this.title = this.getString(R.string.drawer_text_traffic);
		this.navigationDrawer = (NavigationDrawerFragment) this
				.getSupportFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		this.navigationDrawer.setup(R.id.navigation_drawer,
				(DrawerLayout) this.findViewById(R.id.drawer_layout));

		SlidingBarErrorPresenter presenter = (SlidingBarErrorPresenter) findViewById(R.id.error_bar);
		presenter.setMainContent(findViewById(R.id.content_frame));
		this.initializeErrorController(presenter, TrafficApp.getBus());

		if (savedInstanceState == null) {
			loadAds();
			final Fragment contentFragment = Fragment.instantiate(this,
					TrafficFragment.class.getName());
			this.getSupportFragmentManager().beginTransaction()
					.add(R.id.content_frame, contentFragment).commit();
		} else {
			this.title = savedInstanceState.getString(CURRENT_DRAWER_TITLE);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.inrix.reference.trafficapp.fragments.NavigationDrawerFragment.NavigationDrawerCallbacks
	 * #onNavigationDrawerItemSelected(int)
	 */
	@Override
	public final void onNavigationDrawerItemSelected(final int itemPosition) {
		switch (itemPosition) {
			case NavigationDrawerFragment.DRAWER_ITEM_TRAFFIC:
				this.title = this.getString(R.string.drawer_text_traffic);
				this.switchActiveView(Fragment.instantiate(this,
						TrafficFragment.class.getName()));
				this.checkForRequiredServices();
				break;
			case NavigationDrawerFragment.DRAWER_ITEM_NEWS:
				this.title = this.getString(R.string.drawer_text_news);
				this.switchActiveView(Fragment.instantiate(this,
						NewsFragment.class.getName()));
				this.checkForRequiredServices();
				break;
			case NavigationDrawerFragment.DRAWER_ITEM_ABOUT:
				this.title = this.getString(R.string.drawer_text_about);
				this.switchActiveView(Fragment.instantiate(this,
						AboutFragment.class.getName()));
				break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public final boolean onCreateOptionsMenu(final Menu menu) {
		if (!this.navigationDrawer.isDrawerOpen()) {
			this.getMenuInflater().inflate(R.menu.main, menu);

			// Restore action bar.
			this.getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			if ( (this.getActionBar().getDisplayOptions() & ActionBar.DISPLAY_SHOW_CUSTOM) == ActionBar.DISPLAY_SHOW_CUSTOM) {
				final View titleView = this.findViewById(android.R.id.title);
				if (titleView != null) {
					((TextView) titleView).setText(this.title);
				}
			} else {
				this.getActionBar().setTitle(this.title);
			}
			
			return true;
		} else {
			return super.onCreateOptionsMenu(menu);
		}
	}

	/**
	 * Load ads fragment.
	 */
	private void loadAds() {
		getSupportFragmentManager()
				.beginTransaction()
				.add(R.id.ads_container,
						Fragment.instantiate(this, AdsFragment.class.getName()),
						ADS_FRAGMENT_LABEL).commit();
	}

	/**
	 * Switch to a specified fragment.
	 * 
	 * @param fragment
	 *            New content fragment.
	 */
	private final void switchActiveView(final Fragment fragment) {
		final FragmentTransaction transaction = this
				.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.content_frame, fragment);
		transaction.commit();
		this.getErrorController().dismissCurrentError();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString(CURRENT_DRAWER_TITLE, this.title);
		super.onSaveInstanceState(outState);
	}
}
