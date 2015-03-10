/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package utils;

import org.robolectric.Robolectric;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;

public class FragmentUtils {
	
	private static FragmentActivity fragActivity;
	
	public static void startFragment(Fragment fragment) {
		fragActivity = Robolectric.buildActivity(FragmentActivity.class)
				.create().start().resume().get();
		FragmentManager fragmentManager = fragActivity.getFragmentManager();
			//	.getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.add(fragment, null);
		fragmentTransaction.commit();
		
	}
	public static void startv4Fragment(android.support.v4.app.Fragment fragment) {
		fragActivity = Robolectric.buildActivity(FragmentActivity.class)
				.create().start().resume().get();
		android.support.v4.app.FragmentManager fragmentManager = fragActivity.getSupportFragmentManager();
		android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.add(fragment, null);
		fragmentTransaction.commit();
	}
	
	public static void startFragmentInAppActivity(Fragment locationsListFragment, Class<Activity> locationPickerActivityClass) {

		fragActivity = (FragmentActivity) Robolectric.buildActivity(locationPickerActivityClass)
				.create().start().resume().get();
		FragmentManager fragmentManager = fragActivity.getFragmentManager();
				//.getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		fragmentTransaction.add(locationsListFragment, null);
		fragmentTransaction.commit();
	}


	public static FragmentActivity getFragmentActivity() {
		return fragActivity;
	}

}
