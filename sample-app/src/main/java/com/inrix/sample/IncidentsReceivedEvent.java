/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample;

import com.inrix.sdk.model.Incident;

import java.util.List;

public class IncidentsReceivedEvent {
    private List<Incident> incidents;

    public IncidentsReceivedEvent(List<Incident> incidents) {
        this.incidents = incidents;
    }

    public List<Incident> getIncidents() {
        return this.incidents;
    }
}
