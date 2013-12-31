package com.inrix.sample;

import java.util.List;

import com.inrix.sdk.model.Incident;

public class IncidentsReceivedEvent {
	private List<Incident> incidents;

	public IncidentsReceivedEvent(List<Incident> incidents) {
		this.incidents = incidents;
	}

	public List<Incident> getIncidents() {
		return this.incidents;
	}
}
