/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.util;

import android.os.CountDownTimer;

/** Helper class to notify the listener to refresh data. */
public class RefreshNotifier {

	/** Listener to get feedback. */
	private OnRefreshListener listener;

	/** Refresh timer. */
	private CountDownTimer autoRefreshTimer = null;

	/** Refresh period, in ms. */
	private long refreshPeriod = Constants.DEFAULT_CONTENT_REFRESH_TIMEOUT_MS;

	/** Time stamp when data was update last time. */
	private long lastUpdateTs = 0;

	/**
	 * Creates new instance of the class.
	 * 
	 * @param listener
	 *            Listener to get notifications.
	 */
	public RefreshNotifier(OnRefreshListener listener) {
		this.listener = listener;
	}

	/**
	 * Sets custom refresh period.
	 * 
	 * @param refreshPeriod
	 *            Refresh period, in ms.
	 */
	public void setRefreshPeriod(long refreshPeriod) {
		this.refreshPeriod = refreshPeriod;
	}

	/**
	 * Sets custom last update time if a data wasn't updated in onRefreshData
	 * method. If data was updated and listener returned true in onRefreshData,
	 * last update time will be updated by the notifier.
	 * 
	 * @param lastUpdateTime
	 *            Last update time, in ms.
	 */
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTs = lastUpdateTime;
	}

	public long getLastUpdateTime() {
		return this.lastUpdateTs;
	}

	/** Resume auto refresh */
	public void resumeAutoRefresh() {
		if (autoRefreshTimer != null) {
			autoRefreshTimer.cancel();
			autoRefreshTimer = null;
		}

		long timeLeft = this.refreshPeriod
				- (System.currentTimeMillis() - lastUpdateTs);

		if (timeLeft < 0) {
			notifyListener();
			return;
		}

		autoRefreshTimer = new CountDownTimer(timeLeft, timeLeft) {

			@Override
			public void onTick(long millisUntilFinished) {

			}

			@Override
			public void onFinish() {
				autoRefreshTimer = null;
				notifyListener();
			}
		};

		autoRefreshTimer.start();
	}

	/** Stops the refresh timer. */
	public void pauseAutoRefresh() {
		if (autoRefreshTimer != null) {
			autoRefreshTimer.cancel();
			autoRefreshTimer = null;
		}
	}

	/**
	 * Notifies listener to refresh data and resets timer if refresh is
	 * successful.
	 */
	private void notifyListener() {
		if (this.listener != null && listener.onRefreshData(this)) {
			this.lastUpdateTs = System.currentTimeMillis();
			resumeAutoRefresh();
		}
	}

	/** Interface to get notifications to refresh data. */
	public interface OnRefreshListener {

		/**
		 * Will be called when the listener should to refresh data.
		 * 
		 * @param notifier
		 *            Initiator of refresh event to detect what data should be
		 *            updated.
		 * @return Whether the refresh is successful.
		 */
		public boolean onRefreshData(RefreshNotifier notifier);
	}
}