/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.activity;

import android.support.v4.app.FragmentActivity;

import com.inrix.reference.trafficapp.Globals;
import com.inrix.reference.trafficapp.error.ErrorController;
import com.inrix.reference.trafficapp.error.ErrorEntity;
import com.inrix.reference.trafficapp.error.ErrorType;
import com.inrix.reference.trafficapp.error.IErrorPresenter;
import com.squareup.otto.Bus;

/**
 * The Class ErrorControllerActivity, contains ErrorController class
 */
public class ErrorControllerActivity extends FragmentActivity {
	private ErrorController errorController;
	private Bus errorBus;
	private boolean isInitialized;

	/**
	 * Sets the error controller.
	 * 
	 * @param errorPresenter
	 *            the error presenter
	 * @param bus
	 *            the bus
	 */
	public void initializeErrorController(final IErrorPresenter errorPresenter,
			final Bus bus) {
		if (errorPresenter == null || bus == null) {
			return;
		}

		this.isInitialized = true;
		this.errorBus = bus;
		this.errorController = new ErrorController(errorPresenter);
		this.errorController.setEventBus(errorBus);
	}

	/**
	 * Gets the error controller.
	 * 
	 * @return the error controller
	 */
	public ErrorController getErrorController() {
		return this.errorController;
	}

	/**
	 * Check for required services.
	 */
	public void checkForRequiredServices() {
		if (!this.isInitialized) {
			return;
		}

		if (!Globals.isNetworkAvailable()) {
			this.errorBus.post(new ErrorEntity(ErrorType.NETWORK_OFF));
		} else {
			this.errorController.dismissError(ErrorType.NETWORK_OFF);
		}

		if (!Globals.isLocationServicesEnabled(getApplicationContext())) {
			this.errorBus.post(new ErrorEntity(ErrorType.LBS_OFF));
		} else {
			this.errorController.dismissError(ErrorType.LBS_OFF);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (!this.isInitialized) {
			return;
		}

		this.errorBus.register(errorController);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.checkForRequiredServices();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!this.isInitialized) {
			return;
		}

		this.errorBus.unregister(errorController);
	}
}
