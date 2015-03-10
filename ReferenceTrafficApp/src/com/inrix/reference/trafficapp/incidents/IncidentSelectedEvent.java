/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.incidents;

import com.inrix.sdk.model.Incident;

public class IncidentSelectedEvent {
    private int position;

    private Incident incident;

    public IncidentSelectedEvent(int position,  Incident incident) {
        this.position = position;
        this.incident = incident;
    }

    public int getPosition() {
        return position;
    }

    public Incident getIncident() {
        return this.incident;
    }
}
