package com.inrix.sample.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.inrix.sample.BusProvider;
import com.inrix.sample.IncidentsReceivedEvent;
import com.inrix.sample.R;
import com.inrix.sample.fragments.IncidentListFragment;
import com.inrix.sample.fragments.IncidentsMapFragment;
import com.inrix.sample.fragments.ProgressFragment;
import com.inrix.sdk.Error;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.IncidentsManager;
import com.inrix.sdk.IncidentsManager.IIncidentsResponseListener;
import com.inrix.sdk.IncidentsManager.IncidentRadiusOptions;
import com.inrix.sdk.Inrix;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Incident;

public class IncidentListActivity extends FragmentActivity implements
		TabListener {

	private final GeoPoint SEATTLE_POSITION = new GeoPoint(47.614496,
			-122.328758);

	private ICancellable currentOperation = null;
	private final int INCIDENT_RADIUS_MILES = 20;
	private GeoPoint lastRequestedPosition = null;
	private IncidentsManager incidentManager;
	private ViewPager viewPager;
	private TabsAdapter tabsAdapter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_incident_list);

		// Initialize INRIX
		initializeINRIX();
		this.incidentManager = new IncidentsManager();

		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		this.viewPager = (ViewPager) findViewById(R.id.pager);
		this.tabsAdapter = new TabsAdapter(this, viewPager);
		tabsAdapter.addTab(actionBar.newTab().setText("List"),
				IncidentListFragment.class,
				null);
		tabsAdapter.addTab(actionBar.newTab().setText("Map"),
				IncidentsMapFragment.class,
				null);

		// Clear the Incident List
		setIncidentList(null);

		if (savedInstanceState != null) {
			actionBar.setSelectedNavigationItem(savedInstanceState
					.getInt("tab", 0));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_refresh) {
			refreshData();
			return true;
		}
		return false;
	}

	/**
	 * Initialize the INRIX SDK
	 */
	private void initializeINRIX() {
		Inrix.initialize(getApplicationContext());
	}

	/**
	 * 
	 * @param incident
	 *            list
	 */
	private void setIncidentList(List<Incident> list) {
		BusProvider.getBus().post(new IncidentsReceivedEvent(list));
	}

	@Override
	protected void onStop() {
		if (this.currentOperation != null) {
			this.currentOperation.cancel();
			this.currentOperation = null;
			getSupportFragmentManager().popBackStack();
		}
		super.onStop();
	}

	@Override
	protected void onStart() {
		super.onStart();
		refreshData();
	}

	private void refreshData() {
		if (currentOperation != null) {
			currentOperation.cancel();
			currentOperation = null;
		} else {
			getSupportFragmentManager().beginTransaction()
					.add(new ProgressFragment(), "").addToBackStack("")
					.commitAllowingStateLoss();
		}

		if (lastRequestedPosition == null) {
			lastRequestedPosition = SEATTLE_POSITION;
		}

		// Get the Incidents for the selected city and radius
		IncidentRadiusOptions params = new IncidentRadiusOptions(lastRequestedPosition,
				INCIDENT_RADIUS_MILES);
		this.currentOperation = this.incidentManager
				.getIncidentsInRadius(new IIncidentsResponseListener() {

					@Override
					public void onResult(List<Incident> data) {
						getSupportFragmentManager().popBackStack();
						currentOperation = null;
						setIncidentList(data);
					}

					@Override
					public void onError(Error error) {
						getSupportFragmentManager().popBackStack();
						currentOperation = null;
						setIncidentList(null);
					}

				}, params);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	public static class TabsAdapter extends FragmentPagerAdapter implements
			ActionBar.TabListener, ViewPager.OnPageChangeListener {
		private final Context context;
		private final ActionBar actionBar;
		private final ViewPager viewPager;
		private final ArrayList<TabInfo> tabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(Class<?> _class, Bundle _args) {
				clss = _class;
				args = _args;
			}
		}

		public TabsAdapter(FragmentActivity activity, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			context = activity;
			actionBar = activity.getActionBar();
			viewPager = pager;
			viewPager.setAdapter(this);
			viewPager.setOnPageChangeListener(this);
		}

		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
			TabInfo info = new TabInfo(clss, args);
			tab.setTag(info);
			tab.setTabListener(this);
			tabs.add(info);
			actionBar.addTab(tab);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return tabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = tabs.get(position);
			return Fragment
					.instantiate(context, info.clss.getName(), info.args);
		}

		@Override
		public void onPageScrolled(int position,
				float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			actionBar.setSelectedNavigationItem(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i = 0; i < tabs.size(); i++) {
				if (tabs.get(i) == tag) {
					viewPager.setCurrentItem(i);
				}
			}
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}
}
