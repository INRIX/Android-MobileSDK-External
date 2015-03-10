/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.locationpicker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.TrafficApp;
import com.inrix.reference.trafficapp.activity.ErrorControllerActivity;
import com.inrix.reference.trafficapp.activity.MainActivity;
import com.inrix.reference.trafficapp.error.ActionRefreshEvent;
import com.inrix.reference.trafficapp.error.ErrorEntity;
import com.inrix.reference.trafficapp.fragments.ConfirmDialogFragment;
import com.inrix.reference.trafficapp.fragments.ConfirmDialogFragment.IConfirmDialogListener;
import com.inrix.reference.trafficapp.locationpicker.LocationPickerActivity.PLACES_ACTION;
import com.inrix.reference.trafficapp.view.SlidingBarErrorPresenter;
import com.inrix.sdk.Error;
import com.inrix.sdk.Error.Type;
import com.inrix.sdk.ICancellable;
import com.inrix.sdk.InrixCore;
import com.inrix.sdk.LocationsManager;
import com.inrix.sdk.LocationsManager.CreateLocationOptions;
import com.inrix.sdk.LocationsManager.DeleteLocationOptions;
import com.inrix.sdk.LocationsManager.ILocationDeleteResponseListener;
import com.inrix.sdk.LocationsManager.ILocationSaveResponseListener;
import com.inrix.sdk.LocationsManager.LocationType;
import com.inrix.sdk.ServerErrorStatus;
import com.inrix.sdk.model.GeoPoint;
import com.inrix.sdk.model.Location;
import com.squareup.otto.Subscribe;

public class LocationPickerMapActivity extends ErrorControllerActivity
		implements IConfirmDialogListener {

	private static final String LOCATION_TO_SHOW = "location_to_show";
	private static final String LOCATION_PICKER_MODE = "location_picker_mode";
	private static final String LOCATION_PICKER_ADDRESS = "location_picker_address";
	private static final String CURRENT_LOCATION_ID = "current_location_id";
	private static final String LOCATIONS_MAP_FRAGMENT_TAG = "location_picker_map";

	private static final String CONFIRM_DIALOG_TAG = "confirmation_dialog";

	private static final int LOCATION_TYPE_HOME = 1000;
	private static final int LOCATION_TYPE_WORK = 2000;

	private LatLng locationToShow = null;
	private PLACES_ACTION currentMode;
	private String currentAddress = "";
	private long currentLocationID = LocationPickerActivity.INVALID_LOCATION_ID;

	private Button saveButton = null;
	private ProgressBar saveProgress = null;
	private LocationsManager locationsManager = InrixCore.getLocationsManager();
	private boolean showTrafficWhenConfirmReturns = false;

	private boolean locationDeleted = false;

	private ICancellable currentOperation = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_picker_map);

		Drawable emptyLogo = new ColorDrawable(Color.argb(255, 255, 255, 255));

		getActionBar().setDisplayShowCustomEnabled(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setLogo(emptyLogo);
		getActionBar().setTitle(R.string.search_results_label);

		Bundle bundle = getIntent().getExtras();
		locationToShow = (LatLng) bundle.get(LOCATION_TO_SHOW);
		currentLocationID = bundle.getLong(CURRENT_LOCATION_ID);

		int mode = bundle.getInt(LOCATION_PICKER_MODE,
				PLACES_ACTION.NOT_DEFINED.ordinal());

		currentMode = PLACES_ACTION.values()[mode];
		currentAddress = bundle.getString(LOCATION_PICKER_ADDRESS);

		if (locationToShow != null && savedInstanceState == null
				&& !TextUtils.isEmpty(currentAddress)) {
			android.support.v4.app.FragmentTransaction t = getSupportFragmentManager()
					.beginTransaction();
			t.add(R.id.map,
					LocationPickerMapFragment.getInstance(locationToShow,
							currentAddress),
					LOCATIONS_MAP_FRAGMENT_TAG);
			t.commit();
		}

		SlidingBarErrorPresenter presenter = (SlidingBarErrorPresenter) findViewById(R.id.error_bar);
		presenter.setMainContent(findViewById(R.id.content_frame));
		this.initializeErrorController(presenter, TrafficApp.getBus());

		saveButton = (Button) findViewById(R.id.saveLocation);
		if (null != saveButton) {
			saveButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					saveCurrentLocation();
				}
			});
		}
		saveProgress = (ProgressBar) findViewById(R.id.saveProgress);
		showTrafficWhenConfirmReturns = false;
		locationDeleted = false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (null != currentOperation) {
			showConfirmDialog();
		} else {
			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		TrafficApp.getBus().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		TrafficApp.getBus().unregister(this);
	}

	@Subscribe
	public void actionRefreshEvent(ActionRefreshEvent refresh) {
		saveCurrentLocation();
	}

	private void saveCurrentLocation() {
		if (null != locationToShow && null == currentOperation) {
			showProgress();
			if (currentMode == PLACES_ACTION.CREATE_HOME) {
				createHome();
			} else if (currentMode == PLACES_ACTION.CREATE_WORK) {
				createWork();
			} else if (currentMode == PLACES_ACTION.EDIT_HOME) {
				editHome();
			} else if (currentMode == PLACES_ACTION.EDIT_WORK) {
				editWork();
			}
		}
	}

	private void showConfirmDialog() {
		ConfirmDialogFragment cdf = new ConfirmDialogFragment();
		cdf.setListener(this);
		cdf.setContext(this);
		cdf.show(getSupportFragmentManager(), CONFIRM_DIALOG_TAG);
	}

	private boolean isConfirmDialogShown() {
		if (getSupportFragmentManager().findFragmentByTag(CONFIRM_DIALOG_TAG) != null) {
			return true;
		}
		return false;
	}

	public static void launchLocationPickerMap(Context context,
			LatLng locationToFocus,
			int mode,
			long locationID,
			String strAddress) {
		if (null != context) {
			Intent intent = new Intent(context, LocationPickerMapActivity.class);
			intent.putExtra(LOCATION_TO_SHOW, locationToFocus);
			intent.putExtra(LOCATION_PICKER_MODE, mode);
			intent.putExtra(CURRENT_LOCATION_ID, locationID);
			intent.putExtra(LOCATION_PICKER_ADDRESS, strAddress);
			context.startActivity(intent);
		}
	}

	public void hideProgress() {
		saveProgress.setVisibility(View.GONE);
	}

	public void showProgress() {
		saveProgress.setVisibility(View.VISIBLE);
	}

	private void launchTrafficPage() {
		final Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	/**
	 * launch a location request to create home.
	 */
	private void createHome() {
		GeoPoint HOME = new GeoPoint(locationToShow.latitude,
				locationToShow.longitude);

		CreateLocationOptions params = new CreateLocationOptions(HOME,
				"Home",
				LocationType.HOME);
		launchCreateLocationRequest(params, LOCATION_TYPE_HOME);
	}

	/**
	 * launch a location request to create work.
	 */
	private void createWork() {
		GeoPoint WORK = new GeoPoint(locationToShow.latitude,
				locationToShow.longitude);
		CreateLocationOptions params = new CreateLocationOptions(WORK,
				"Work",
				LocationType.WORK);
		launchCreateLocationRequest(params, LOCATION_TYPE_WORK);
	}

	private void launchCreateLocationRequest(CreateLocationOptions params,
			final int locType) {
		ILocationSaveResponseListener saveListener = new ILocationSaveResponseListener() {
			@Override
			public void onError(Error error) {
				ErrorEntity errEntity = ErrorEntity.fromInrixError(error);
				if (errEntity != null) {
					TrafficApp.getBus().post(errEntity);
				}
				hideProgress();
				currentOperation = null;
				if (error.getErrorType() == Type.SERVER_ERROR
						&& error.getErrorId() == ServerErrorStatus.LOCATION_NAME_IN_USE) {
					/*
					 * if the location name is in use it means we already
					 * created the location just launch the traffic page in this
					 * case
					 */
					gotoTrafficPage();
				} else {
					Toast.makeText(LocationPickerMapActivity.this,
							R.string.create_edit_location_error_msg,
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onResult(Location result) {
				hideProgress();
				currentOperation = null;
				gotoTrafficPage();
			}
		};
		currentOperation = locationsManager
				.createLocation(params, saveListener);
	}

	private void gotoTrafficPage() {
		if (!isConfirmDialogShown()) {
			launchTrafficPage();
		} else {
			showTrafficWhenConfirmReturns = true;
		}
	}

	private void editHome() {
		if (locationDeleted) {
			// home is already deleted so go ahead and create it
			createHome();
		} else {
			// first delete current home and then launch new home creation
			DeleteLocationOptions deleteHomeOptions = new DeleteLocationOptions(currentLocationID);
			editPlace(deleteHomeOptions, LocationType.HOME);
		}
	}

	private void editWork() {
		if (locationDeleted) {
			// work is already deleted so go ahead and create it
			createWork();
		} else {
			// first delete current work and then launch new work creation
			DeleteLocationOptions deleteWorkOptions = new DeleteLocationOptions(currentLocationID);
			editPlace(deleteWorkOptions, LocationType.WORK);
		}
	}

	private void editPlace(DeleteLocationOptions options,
			final LocationsManager.LocationType locType) {
		ILocationDeleteResponseListener deleteListener = new ILocationDeleteResponseListener() {

			@Override
			public void onResult(Long arg0) {
				handleDeleteSuccess(locType);
			}

			@Override
			public void onError(Error error) {
				if (error.getErrorType() == Type.SERVER_ERROR
						&& error.getErrorId() == ServerErrorStatus.LOCATION_NOT_IN_DB) {
					/*
					 * if the location is not in the database it means we
					 * already deleted the location just create the new location
					 * in this case
					 */
					handleDeleteSuccess(locType);
				} else {
					locationDeleted = false;
					hideProgress();
					Toast.makeText(LocationPickerMapActivity.this,
							R.string.create_edit_location_error_msg,
							Toast.LENGTH_LONG).show();
					currentOperation = null;
				}
			}
		};
		currentOperation = locationsManager.deleteLocation(options,
				deleteListener);
	}

	private void handleDeleteSuccess(final LocationsManager.LocationType locType) {
		locationDeleted = true;
		if (locType == LocationType.HOME) {
			createHome();
		} else if (locType == LocationType.WORK) {
			createWork();
		}
	}

	@Override
	public void onConfirmAction() {
		if (null != currentOperation) {
			currentOperation.cancel();
			currentOperation = null;
		}
		launchTrafficPage();
	}

	@Override
	public void onCancelAction() {
		if (showTrafficWhenConfirmReturns) {
			launchTrafficPage();
		}
	}
}
