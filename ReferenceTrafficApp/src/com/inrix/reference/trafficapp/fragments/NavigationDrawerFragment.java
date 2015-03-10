/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.fragments;

import java.util.LinkedList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.inrix.reference.trafficapp.R;

/**
 * Represents a navigation drawer fragment.
 */
public class NavigationDrawerFragment extends Fragment {
	/**
	 * Adapter for navigation drawer list.
	 */
	private final class NavigationDrawerListAdapter extends ArrayAdapter<Pair<String, Integer>> {
		private LayoutInflater inflater;

		/**
		 * Initializes a new instance of {@link NavigationDrawerListAdapter} class.
		 * 
		 * @param context
		 *            Current context.
		 * @param objects
		 *            A list of adapter objects.
		 */
		public NavigationDrawerListAdapter(final Context context, final List<Pair<String, Integer>> objects) {
			super(context, R.layout.drawer_list_item, objects);

			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position < 0) {
				return null;
			}

			View itemView = null;
			ViewHolder itemViewHolder = null;

			if (convertView != null) {
				itemView = convertView;
				itemViewHolder = (ViewHolder) itemView.getTag();
			} else {
				itemView = this.inflater.inflate(R.layout.drawer_list_item, parent, false);
				itemViewHolder = new ViewHolder();
				itemViewHolder.title = (TextView) itemView.findViewById(R.id.drawer_item_text);
				itemViewHolder.icon = (ImageView) itemView.findViewById(R.id.drawer_item_icon);
				itemView.setTag(itemViewHolder);
			}

			final Pair<String, Integer> item = this.getItem(position);
			itemViewHolder.title.setText(item.first);
			itemViewHolder.icon.setImageResource(item.second);

			return itemView;
		}

		private final class ViewHolder {
			public TextView title;
			public ImageView icon;
		}
	}

	/**
	 * Callbacks.
	 */
	public static interface NavigationDrawerCallbacks {
		void onNavigationDrawerItemSelected(final int itemPosition);
	}

	public static final int DRAWER_ITEM_TRAFFIC = 0;
	public static final int DRAWER_ITEM_NEWS = 1;
	public static final int DRAWER_ITEM_ABOUT = 2;

	private static final String KEY_SELECTED_ITEM_INDEX = "selected_item_index";

	private NavigationDrawerCallbacks callback;
	private View fragmentContainerView;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;

	private List<Pair<String, Integer>> drawerItems;
	private int selectedItemIndex;
	private Runnable delayedAction;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.drawerItems = new LinkedList<Pair<String, Integer>>();
		this.drawerItems.add(new Pair<String, Integer>(this.getString(R.string.drawer_text_traffic), R.drawable.ic_action_traffic));
		this.drawerItems.add(new Pair<String, Integer>(this.getString(R.string.drawer_text_news), R.drawable.ic_action_news));
		this.drawerItems.add(new Pair<String, Integer>(this.getString(R.string.drawer_text_about), R.drawable.ic_action_about));

		if (savedInstanceState != null) {
			this.selectedItemIndex = savedInstanceState.getInt(KEY_SELECTED_ITEM_INDEX);
		}

		this.selectItem(this.selectedItemIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public final void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Indicate that we want to contribute in action bar menu.
		this.setHasOptionsMenu(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public final View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		// Inflate list.
		this.drawerList = (ListView) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
		this.drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectItem(position);
			}
		});

		// Setup adapter for it.
		this.drawerList.setAdapter(new NavigationDrawerListAdapter(
				this.getActionBar().getThemedContext(),
				this.drawerItems));
		this.drawerList.setItemChecked(this.selectedItemIndex, true);

		return this.drawerList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public final void onConfigurationChanged(final Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		this.drawerToggle.onConfigurationChanged(newConfig);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu, android.view.MenuInflater)
	 */
	@Override
	public final void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		if (this.drawerLayout != null && this.isDrawerOpen()) {
			this.getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			if ((this.getActionBar().getDisplayOptions() & ActionBar.DISPLAY_SHOW_CUSTOM) == ActionBar.DISPLAY_SHOW_CUSTOM) {
				final View titleView = this.getActivity().findViewById(android.R.id.title);
				((TextView) titleView).setText(R.string.empty);
			} else {
				this.getActionBar().setTitle(R.string.empty);
			}
		}

		super.onCreateOptionsMenu(menu, inflater);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public final boolean onOptionsItemSelected(final MenuItem item) {
		if (this.drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public final void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			this.callback = (NavigationDrawerCallbacks) this.getActivity();
		} catch (final Exception ex) {
			throw new RuntimeException("Parent activity should implement NavigationDrawerCallbacks interface.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onDetach()
	 */
	@Override
	public final void onDetach() {
		super.onDetach();

		this.callback = null;
	}

	/**
	 * Select item in the navigation drawer.
	 * 
	 * @param index
	 *            Item index.
	 */
	private final void selectItem(final int index) {
		if (this.selectedItemIndex == index) {
			if (this.drawerLayout != null) {
				this.drawerLayout.closeDrawer(this.fragmentContainerView);
			}

			return;
		}

		this.selectedItemIndex = index;

		if (this.drawerList != null) {
			this.drawerList.setItemChecked(index, true);
		}

		if (this.drawerLayout != null) {
			this.drawerLayout.closeDrawer(this.fragmentContainerView);
		}

		this.delayedAction = new Runnable() {
			@Override
			public void run() {
				if (callback != null) {
					callback.onNavigationDrawerItemSelected(index);
				}
			}
		};
	}

	/**
	 * Gets a reference to {@link ActionBar}.
	 * 
	 * @return An action bar from parent activity.
	 */
	private final ActionBar getActionBar() {
		return this.getActivity().getActionBar();
	}

	/**
	 * Returns true if the navigation drawer is open now; otherwise false.
	 * 
	 * @return True if drawer open; otherwise false.
	 */
	public final boolean isDrawerOpen() {
		return this.drawerLayout != null && this.drawerLayout.isDrawerOpen(this.fragmentContainerView);
	}

	/**
	 * Initializes navigation drawer.
	 * 
	 * @param fragmentId
	 *            Navigation drawer fragment id.
	 * @param drawerLayout
	 *            Parent layout container.
	 */
	public final void setup(final int fragmentId, final DrawerLayout drawerLayout) {
		this.fragmentContainerView = this.getActivity().findViewById(fragmentId);
		this.drawerLayout = drawerLayout;
		this.drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		this.getActionBar().setHomeButtonEnabled(true);

		this.drawerToggle = new ActionBarDrawerToggle(
				this.getActivity(),
				this.drawerLayout,
				R.drawable.ic_drawer,
				R.string.drawer_open,
				R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);

				if (!isAdded()) {
					return;
				}

				getActivity().invalidateOptionsMenu();

				if (delayedAction != null) {
					drawerLayout.post(delayedAction);
					delayedAction = null;
				}
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);

				if (!isAdded()) {
					return;
				}

				getActivity().invalidateOptionsMenu();
			}
		};

		this.drawerLayout.post(new Runnable() {
			@Override
			public void run() {
				drawerToggle.syncState();
			}
		});

		this.drawerLayout.setDrawerListener(this.drawerToggle);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(KEY_SELECTED_ITEM_INDEX, selectedItemIndex);
		super.onSaveInstanceState(outState);
	}
}
