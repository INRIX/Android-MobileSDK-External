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
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;


public class SlidingRelativeLayout extends RelativeLayout {

    public SlidingRelativeLayout(Context context) {
        super(context);
    }

    public SlidingRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlidingRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private ViewTreeObserver.OnPreDrawListener preDrawListener = null;

    public void setYFraction(final float fraction) {
        if (getHeight() == 0) {
            if (preDrawListener != null) {
                getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
            }
            preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
                    setYFraction(fraction);
                    return true;
                }
            };
            getViewTreeObserver().addOnPreDrawListener(preDrawListener);
            return;
        }
        float translationY = getHeight() * fraction;
        setTranslationY(translationY);
    }

    public float getYFraction() {
        if (getHeight() == 0) {
            return 0;
        }
        return getTranslationY() / getHeight();
    }
}
