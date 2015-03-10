package com.inrix.sample;

import java.util.List;

import com.inrix.sdk.model.Incident;

public class InrixIncidentsReceivedEvent {
	private List<Incident> incidents;

	public InrixIncidentsReceivedEvent(List<Incident> incidents) {
		this.incidents = incidents;
	}

	public List<Incident> getIncidents() {
		return this.incidents;
	}
}
