/*
 * 
 */
package com.inrix.reference.trafficapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * The Class ReferenceAppPreferences.
 */
public class ReferenceAppPreferences {

	protected static SharedPreferences settings;
	private static final String PREFERENCE_NAME = "reference_application";
	private static final String IS_DRIVE_TIME_WARNINGS_ENABLED = "drive_time_warnings_enabled";
	private static Editor editor;
	@SuppressLint("CommitPrefEdits")
	public static void load(Context context) {
		if (settings == null) {
			settings = context.getSharedPreferences(PREFERENCE_NAME,
					Context.MODE_PRIVATE);
			editor = settings.edit();
		}
	}

	public static boolean loaded() {
		return settings != null;
	}

	
	/**
	 * Checks if is drive time warnings enabled.
	 *
	 * @return true, if is drive time warnings enabled
	 */
	public static boolean isDriveTimeWarningsEnabled() {
		return settings.getBoolean(IS_DRIVE_TIME_WARNINGS_ENABLED, true);
	}
	
	/**
	 * Sets the drive time warnings enabled.
	 *
	 * @param enabled the new drive time warnings enabled
	 */
	public static void setDriveTimeWarningsEnabled(boolean enabled){
		if(editor == null){
			return;
		}
		
		editor.putBoolean(IS_DRIVE_TIME_WARNINGS_ENABLED, enabled);
		editor.commit();
	}
}
