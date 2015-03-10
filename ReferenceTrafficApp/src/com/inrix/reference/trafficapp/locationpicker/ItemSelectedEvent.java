/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.locationpicker;

public class ItemSelectedEvent {

    private int position;

    private GeocodingResult item;

    public ItemSelectedEvent(int position, GeocodingResult item) {
        this.position = position;
        this.item = item;
    }

    public int getPosition() {
        return position;
    }

    public GeocodingResult getItem() {
        return item;
    }
}
