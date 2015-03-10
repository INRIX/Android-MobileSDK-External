/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Surface;
import android.widget.ImageView;
import android.widget.Toast;

import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.TrafficApp;
import com.inrix.reference.trafficapp.error.ErrorController;
import com.inrix.reference.trafficapp.error.ErrorEntity;
import com.inrix.reference.trafficapp.fragments.AdsFragment;
import com.inrix.reference.trafficapp.fragments.ReportIncidentChooseSideFragment;
import com.inrix.reference.trafficapp.fragments.ReportIncidentChooseSideFragment.OnRoadSideSelectListener;
import com.inrix.reference.trafficapp.fragments.ReportIncidentChooseTypeFragment;
import com.inrix.reference.trafficapp.fragments.ReportIncidentChooseTypeFragment.OnDismissListener;
import com.inrix.reference.trafficapp.fragments.ReportIncidentChooseTypeFragment.OnIncidentTypeChosen;
import com.inrix.reference.trafficapp.incidents.IncidentsProvider;
import com.inrix.reference.trafficapp.model.GeneratedIncidentTypeModel;
import com.inrix.reference.trafficapp.util.AndroidCompatUtils;
import com.inrix.reference.trafficapp.util.BitmapUtils;
import com.inrix.reference.trafficapp.view.ToastErrorPresenter;
import com.inrix.sdk.IncidentsManager;
import com.inrix.sdk.IncidentsManager.IncidentReportOptions;
import com.inrix.sdk.IncidentsManager.RoadSide;
import com.inrix.sdk.model.Incident;
import com.inrix.sdk.model.ReportWrongTrafficColorConfig;
import com.inrix.sdk.phs.PhsController;

/** Allows to choose incident type and sides to report an incident. */
public class ReportIncidentActivity extends FragmentActivity implements
		OnIncidentTypeChosen, OnRoadSideSelectListener, OnDismissListener {

	/** Intent key to pass incident latitude to the activity (double value). */
	public static final String INTENT_KEY_INCIDENT_LATITUDE = "incident_latitude";

	/** Intent key to pass incident longitude to the activity (double value). */
	public static final String INTENT_KEY_INCIDENT_LONGITUDE = "incident_longitude";

	/** Intent key to pass background to the activity (byte array). */
	public static final String INTENT_KEY_BACKGROUND_DATA = "background_data";

	/** Intent key to pass background rotation to the activity (integer value). */
	public static final String INTENT_KEY_BACKGROUND_ROTATION = "background_rotation";

	/** Tag for fragments. */
	private static final String TAG_FRAGMENT = "fragment_tag";

	/** Key to save incident in the bundle. */
	private static final String BUNDLE_KEY_INCIDENT = "incident_key";

	/** Incident to report. */
	private GeneratedIncidentTypeModel incident;

	/** Incident location: latitude. */
	private Double latitude;

	/** Incident location: longitude. */
	private Double longitude;

	/** Selected road side. */
	private RoadSide roadSide;

	private ErrorController errorController;

	/** Original background bitmap passed to the activity. */
	private Bitmap backgroundBitmap;

	/** Background rotation. Required to handle orientation changes. */
	private int backgroundRotation;

	/** Default polling interval. */
	private static final int TURBO_INTERVAL = 15 * 1000;

	/** Default polling length. */
	private static final int TURBO_DURATION = 120 * 1000;

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidCompatUtils.fixWindowContentOverlay(this);
		this.errorController = new ErrorController(new ToastErrorPresenter(this));
		setContentView(R.layout.report_incident_activity);
		parseIntent();
		initFragments(savedInstanceState);
	}

	/**
	 * Initializes new fragment or restores saved one.
	 * 
	 * @param savedInstanceState
	 *            Bundle to store necessary data if the activity is being re-initialized.
	 */
	private void initFragments(final Bundle savedInstanceState) {
		if (savedInstanceState == null
				|| !savedInstanceState.containsKey(BUNDLE_KEY_INCIDENT)) {
			initIncidentTypeFragment(savedInstanceState);
		} else {
			initIncidentRoadSideFragment(savedInstanceState);
		}

		initAdsFragment(savedInstanceState);
	}

	/**
	 * Initializes incident type fragment.
	 * 
	 * @param savedInstanceState
	 *            Bundle to store necessary data if the activity is being re-initialized.
	 */
	private void initIncidentTypeFragment(final Bundle savedInstanceState) {
		ReportIncidentChooseTypeFragment reportChooseIncidentType;
		if (savedInstanceState == null) {
			reportChooseIncidentType = (ReportIncidentChooseTypeFragment) Fragment
					.instantiate(this,
							ReportIncidentChooseTypeFragment.class.getName());

			this.getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.content_frame,
							reportChooseIncidentType,
							TAG_FRAGMENT).commit();
		} else {
			reportChooseIncidentType = (ReportIncidentChooseTypeFragment) getSupportFragmentManager()
					.findFragmentByTag(TAG_FRAGMENT);
		}
		reportChooseIncidentType.setOnIncidentTypeChosenListener(this);
		reportChooseIncidentType.setOnDismissListener(this);
	}

	/**
	 * Initializes incident road side fragment.
	 * 
	 * @param savedInstanceState
	 *            Bundle to store necessary data if the activity is being re-initialized.
	 */
	private void initIncidentRoadSideFragment(final Bundle savedInstanceState) {
		ReportIncidentChooseSideFragment chooseSideFragment = (ReportIncidentChooseSideFragment) getSupportFragmentManager()
				.findFragmentByTag(TAG_FRAGMENT);
		this.incident = savedInstanceState.getParcelable(BUNDLE_KEY_INCIDENT);
		chooseSideFragment.setIncident(this.incident);
		if (this.latitude != null && this.longitude != null) {
			chooseSideFragment.setLocation(this.latitude, this.longitude);
		}
		chooseSideFragment.setOnRoadSideSelectListener(this);
		chooseSideFragment.setOnDismissListener(this);
	}

	/**
	 * Initializes ADs fragment.
	 * 
	 * @param savedInstanceState
	 *            Bundle to store necessary data if the activity is being re-initialized.
	 */
	private void initAdsFragment(Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.ads_container,
							Fragment.instantiate(this,
									AdsFragment.class.getName())).commit();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (this.incident != null) {
			outState.putParcelable(BUNDLE_KEY_INCIDENT, incident);
		}
	}

	/** Parses intent to get all passed parameters. */
	@SuppressWarnings("deprecation")
	private void parseIntent() {
		Bundle data = getIntent().getExtras();
		if (data != null && data.containsKey(INTENT_KEY_INCIDENT_LATITUDE)
				&& data.containsKey(INTENT_KEY_INCIDENT_LONGITUDE)) {
			this.latitude = data.getDouble(INTENT_KEY_INCIDENT_LATITUDE);
			this.longitude = data.getDouble(INTENT_KEY_INCIDENT_LONGITUDE);
		}

		if (data != null && data.containsKey(INTENT_KEY_BACKGROUND_DATA)) {
			byte[] bytes = data.getByteArray(INTENT_KEY_BACKGROUND_DATA);
			this.backgroundBitmap = BitmapFactory.decodeByteArray(bytes,
					0,
					bytes.length);

			this.backgroundRotation = data
					.getInt(INTENT_KEY_BACKGROUND_ROTATION);

			ImageView backgroundView = (ImageView) findViewById(R.id.img_background);
			backgroundView
					.setBackgroundDrawable(new BitmapDrawable(getResources(),
							getBackground()));
		}
	}

	@Override
	public void onIncidentTypeChosen(GeneratedIncidentTypeModel incidentType) {
		this.incident = incidentType;

		if (incident.getType() == IncidentsManager.INCIDENT_TYPE_FLOW) {
			PhsController
					.getInstance()
					.startWrongTrafficTracking(new ReportWrongTrafficColorConfig(null,
							TURBO_INTERVAL,
							TURBO_DURATION));
			Toast.makeText(ReportIncidentActivity.this,
					R.string.incident_report_success,
					Toast.LENGTH_LONG).show();
			finish();
			return;
		}

		ReportIncidentChooseSideFragment chooseSideFragment = (ReportIncidentChooseSideFragment) Fragment
				.instantiate(this,
						ReportIncidentChooseSideFragment.class.getName());
		chooseSideFragment.setIncident(this.incident);
		if (this.latitude != null && this.longitude != null) {
			chooseSideFragment.setLocation(this.latitude, this.longitude);
		}
		chooseSideFragment.setOnRoadSideSelectListener(this);
		chooseSideFragment.setOnDismissListener(this);

		FragmentTransaction transaction = this.getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.content_frame,
				chooseSideFragment,
				TAG_FRAGMENT);
		transaction.commit();
	}

	/** Reports selected incident. */
	private void reportCurrentIncident() {
		if (incident == null) {
			return;
		}

		if (latitude == null || longitude == null) {
			String text = getString(R.string.incident_report_failed) + "\n"
					+ getString(R.string.incident_repoort_no_location);
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG)
					.show();
			return;
		}

		IncidentReportOptions options = null;
		switch (incident.getType()) {
		case IncidentsManager.INCIDENT_TYPE_ACCIDENT:
			options = IncidentsManager.IncidentReportOptions
					.getReportAccidentOptions();
			break;
		case IncidentsManager.INCIDENT_TYPE_CONSTRUCTION:
			options = IncidentsManager.IncidentReportOptions
					.getReportConstructionOptions();
			break;
		case IncidentsManager.INCIDENT_TYPE_HAZARD:
			options = IncidentsManager.IncidentReportOptions
					.getReportHazardOptions();
			break;
		case IncidentsManager.INCIDENT_TYPE_POLICE:
			options = IncidentsManager.IncidentReportOptions
					.getReportPoliceOptions();
			break;
		}

		if (options == null) {
			return;
		}

		if (roadSide != null) {
			options.setSideOfRoad(roadSide);
		}

		final IncidentsProvider provider = new IncidentsProvider();
		provider.reportIncident(options, new IncidentsManager.IIncidentReportListener() {
			@Override
			public void onResult(Incident data) {
				Toast.makeText(ReportIncidentActivity.this, R.string.incident_report_success, Toast.LENGTH_LONG).show();
			}

			@Override
			public void onError(com.inrix.sdk.Error error) {
				final String message = ReportIncidentActivity.this.getString(R.string.incident_report_failed);
				final ErrorEntity reportError = ErrorEntity.fromInrixError(error).setMessage(message);
				TrafficApp.getBus().post(reportError);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		TrafficApp.getBus().register(this.errorController);
	}

	@Override
	protected void onPause() {
		super.onPause();
		TrafficApp.getBus().unregister(this.errorController);
	}

	@Override
	public void onRoadSideSelect(RoadSide roadSide) {
		this.roadSide = roadSide;
	}

	@Override
	public void onDismiss() {
		reportCurrentIncident();
		finish();
	}

	/**
	 * Retrieves background for current orientation and rotation.
	 * 
	 * @return Background for current orientation and rotation.
	 */
	private Bitmap getBackground() {
		if (this.backgroundBitmap == null) {
			return null;
		}

		int rotation = getWindowManager().getDefaultDisplay().getRotation();

		if ((this.backgroundRotation == Surface.ROTATION_0 || this.backgroundRotation == Surface.ROTATION_180)
				&& (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180)) {
			return this.backgroundBitmap;
		}

		if ((this.backgroundRotation == Surface.ROTATION_90 || this.backgroundRotation == Surface.ROTATION_270)
				&& (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)) {
			return this.backgroundBitmap;
		}

		if ((this.backgroundRotation == Surface.ROTATION_0 && rotation == Surface.ROTATION_90)
				|| (this.backgroundRotation == Surface.ROTATION_90 && rotation == Surface.ROTATION_180)
				|| (this.backgroundRotation == Surface.ROTATION_180 && rotation == Surface.ROTATION_270)
				|| (this.backgroundRotation == Surface.ROTATION_270 && rotation == Surface.ROTATION_0)) {
			return BitmapUtils.rotate(this.backgroundBitmap, -90);
		}

		if ((this.backgroundRotation == Surface.ROTATION_0 && rotation == Surface.ROTATION_270)
				|| (this.backgroundRotation == Surface.ROTATION_90 && rotation == Surface.ROTATION_0)
				|| (this.backgroundRotation == Surface.ROTATION_180 && rotation == Surface.ROTATION_90)
				|| (this.backgroundRotation == Surface.ROTATION_270 && rotation == Surface.ROTATION_180)) {
			return BitmapUtils.rotate(this.backgroundBitmap, 90);
		}

		return this.backgroundBitmap;
	}
}