package com.inrix.reference.trafficapp.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.inrix.reference.trafficapp.R;

public class SettingsFragment extends PreferenceFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}

}
