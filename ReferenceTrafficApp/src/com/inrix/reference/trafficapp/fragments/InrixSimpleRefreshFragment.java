/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.fragments;

import java.util.Timer;
import java.util.TimerTask;

import android.support.v4.app.Fragment;

public abstract class InrixSimpleRefreshFragment extends Fragment {
	public final int TIME_STAMP_NOT_INITIALIZED = -1;
	private long lastUpdatedTime = TIME_STAMP_NOT_INITIALIZED;

	Timer refreshTimer = null;

	private boolean isRefreshNeeded() {
		boolean bReturn = false;
		if (getLastUpdatedTime() == TIME_STAMP_NOT_INITIALIZED) {
			bReturn = true;
		} else {
			bReturn = (System.currentTimeMillis() - getLastUpdatedTime()) > getRefreshInterval();
		}
		return bReturn;
	}

	/**
	 * check if we need to refresh and get data if refresh not needed right away
	 * just schedule the timer for the remainder of the refresh interval
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (isRefreshNeeded()) {
			getData();
		} else {
			long nextUpdateScheduleTime = System.currentTimeMillis()
					- getLastUpdatedTime();
			resetRefreshTimer(nextUpdateScheduleTime);
		}
	}

	/**
	 * stop the refresh timer
	 */
	@Override
	public void onPause() {
		stopRefreshTimer();
		super.onPause();
	}

	/**
	 * reset the refresh timer
	 * 
	 * @param refreshInterval
	 *            - the time interval
	 */
	public void resetRefreshTimer(long refreshInterval) {
		stopRefreshTimer();
		refreshTimer = new Timer();
		refreshTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getData();
					}
				});
			}
		}, refreshInterval);
	}

	/**
	 * Stop the refresh timer
	 */
	public void stopRefreshTimer() {
		if (refreshTimer != null) {
			refreshTimer.cancel();
			refreshTimer = null;
		}
	}

	/**
	 * return the last updated time
	 * 
	 * @return
	 */
	private long getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	/**
	 * The derived classes need to override this function to call data fetching
	 * in this function and also call super.getData() to reschedule the timer
	 */
	public void getData() {
		lastUpdatedTime = System.currentTimeMillis();
		resetRefreshTimer(getRefreshInterval());
	}
	
	/**
	 * Reset the last updated time to force an update next time
	 */
	protected void resetUpdatedTime(){
		lastUpdatedTime = TIME_STAMP_NOT_INITIALIZED;
	}

	/**
	 * The derived classes need to override this function and return the refresh
	 * interval
	 * 
	 * @return
	 */
	public abstract long getRefreshInterval();

}
