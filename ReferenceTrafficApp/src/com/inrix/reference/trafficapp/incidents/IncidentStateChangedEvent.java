/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.incidents;

import com.inrix.reference.trafficapp.incidents.IncidentsProvider.IncidentState;
import com.inrix.sdk.model.Incident;

/**
 * An event for event bus to notify subscribers that incident state has changed.
 */
public final class IncidentStateChangedEvent {
	private final IncidentState state;
	private final Incident incident;

	/**
	 * Initializes a new instance of the {@link IncidentStateChangedEvent} class.
	 * 
	 * @param incident
	 *            Target incident instance whose state has changed.
	 * @param state
	 *            Incident state.
	 */
	public IncidentStateChangedEvent(final Incident incident, final IncidentState state) {
		this.incident = incident;
		this.state = state;
	}

	/**
	 * Gets the incident instance whose state has changed.
	 * 
	 * @return Incident instance.
	 */
	public final Incident getIncident() {
		return this.incident;
	}

	/**
	 * Gets the new incident state.
	 * 
	 * @return Incident state.
	 */
	public final IncidentState getState() {
		return this.state;
	}
}
