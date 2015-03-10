/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Intent;
import android.provider.Settings;

import com.inrix.reference.trafficapp.TrafficApp;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class ErrorController implements IOnErrorActionClickListener {
	private static final Logger logger = LoggerFactory.getLogger(ErrorController.class);
	private IErrorPresenter presenter;
	private ErrorEntity activeError = null;
	private Bus eventBus;

	/**
	 * @param errorPresenter
	 *            - responsible for displaying errors. Should not be null
	 */
	public ErrorController(IErrorPresenter errorPresenter) {
		if (errorPresenter == null) {
			throw new IllegalArgumentException("Error presenter should not be null");
		}
		this.presenter = errorPresenter;
		this.presenter.addOnErrorActionClickListener(this);
	}

	/**
	 * On dismiss error, handles cases when error needs to be removed, without direct access to controller
	 *
	 * @param dismissEntity the dismiss entity
	 */
	@Subscribe
	public void onDismissError(DismissErrorEntity dismissEntity) {
		if (dismissEntity == null) {
			return;
		}
		
		this.dismissError(dismissEntity.getType());
	}
	
	@Subscribe
	public void onError(ErrorEntity error) {
		if (error != null) {
			if (activeError != null) {
				if (error.getPriority() >= activeError.getPriority()) {
					// Show errors only with higher priority
					logger.debug("Error '{}'  skipped. There is a more important error already displayed: {}", error.getType(), activeError);
					return;
				} else {
					dismissCurrentError();
				}
			}

			logger.debug("Show error: {}", error);
			if (presenter.show(error)) {
				activeError = error;
			}
		}
	}

	/**
	 * Dismiss currently displayed error
	 */
	public void dismissCurrentError() {
		if (activeError != null) {
			presenter.dismiss(activeError);
			activeError = null;
		}
	}

	public void setEventBus(Bus bus) {
		this.eventBus = bus;
	}

	/**
	 * Dismiss error by type. If there is no active error, or active error has a
	 * different type - this method does nothing
	 * 
	 * @param type
	 */
	public void dismissError(ErrorType type) {
		if (type == null || activeError == null
				|| activeError.getType() != type) {
			return;
		} else {
			dismissCurrentError();
		}
	}

	IErrorPresenter getErrorPresenter() {
		return this.presenter;
	}

	public ErrorEntity getActiveError() {
		return this.activeError;
	}

	@Override
	public boolean onErrorActionClicked(ErrorAction errorAction,
			ErrorEntity error) {
		if (errorAction == null) {
			return false;
		}

		logger.debug("Error action clicked: {}", errorAction);

		if (eventBus == null) {
			logger.warn("Event bus is not set for error controller. Action will not be broadcasted!");
		}

		switch (errorAction) {
			case ACTION_REFRESH:
				if (eventBus != null) {
					eventBus.post(new ActionRefreshEvent());
				}
				break;
			case ACTION_LBS_SETTINGS: // Location services are off, navigate to settings
				Intent lbsSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				lbsSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				TrafficApp.getContext().startActivity(lbsSettings);
				break;
			case ACTION_NETWORK_SETTINGS: // Network is off, navigate to settings
				Intent networkSettings = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				networkSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				TrafficApp.getContext().startActivity(networkSettings);
				break;
			default:
				break;
		}

		// Right now dismiss error no matter what action is clicked. We might
		// need to change this behavior later on, but for now - just dismiss it
		dismissCurrentError();

		return false;
	}
}
