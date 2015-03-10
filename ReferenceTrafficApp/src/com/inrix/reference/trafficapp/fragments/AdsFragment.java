/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.inrix.reference.trafficapp.R;

/**
 * Fragment to display advertisement in the application.
 */
public final class AdsFragment extends Fragment {
	private AdView adView;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public final View onCreateView(final LayoutInflater inflater,
			final ViewGroup container,
			final Bundle savedInstanceState) {
		final View contentView = inflater.inflate(R.layout.fragment_ads,
				container,
				false);

		this.adView = (AdView) contentView.findViewById(R.id.adView);
		final AdRequest.Builder adRequest = new AdRequest.Builder();

		// Add test devices if any are registered.
		for (final String testDeviceId : this.getResources()
				.getStringArray(R.array.ads_test_devices)) {
			adRequest.addTestDevice(testDeviceId);
		}

		this.adView.loadAd(adRequest.build());

		return contentView;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.adView.resume();
	}

	@Override
	public void onPause() {
		this.adView.pause();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		this.adView.destroy();
		super.onDestroy();
	}
}
