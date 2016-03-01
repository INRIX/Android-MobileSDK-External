/**
 * Copyright (c) 2013-2015 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.map;

public class MapIncidentItem extends MapClusterItem {
    private long id;
    private int type;

    public MapIncidentItem(double lat, double lng, long id, int type) {
        super(lat, lng);

        this.id = id;
        this.type = type;
    }

    public long getId() {
        return this.id;
    }

    public int getType() {
        return this.type;
    }
}