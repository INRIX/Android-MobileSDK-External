/**
 * Copyright (c) 2013-2015 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample;

import com.squareup.otto.Bus;

public class BusProvider {

    private static Bus bus = null;

    public static synchronized Bus getBus() {
        if (bus == null) {
            bus = new Bus();
        }
        return bus;
    }

}
