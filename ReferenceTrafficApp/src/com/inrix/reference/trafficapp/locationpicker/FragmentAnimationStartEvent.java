/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.locationpicker;

import android.support.v4.app.Fragment;



public class FragmentAnimationStartEvent {

    private Fragment fragment;

    private boolean enter;

    public FragmentAnimationStartEvent(Fragment f, boolean enter) {
        this.fragment = f;
        this.enter = enter;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public boolean isEnter() {
        return enter;
    }
}
