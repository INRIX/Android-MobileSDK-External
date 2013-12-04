package com.inrix.sample;

import java.util.Comparator;

import com.inrix.sdk.IncidentsManager;
import com.inrix.sdk.IncidentUtils;
import com.inrix.sdk.model.Incident;

/**
 * The Class IncidentComparator, sorts incident in default way
 */
public class IncidentComparator implements Comparator<	Incident> {

	@Override
	public int compare(Incident o1, Incident o2) {

		boolean roadClosure = IncidentUtils.isRoadClosure(o1.getEventCode());
		boolean roadClosure2 = IncidentUtils.isRoadClosure(o2.getEventCode());

		if ( (o1.getType() == IncidentsManager.INCIDENT_TYPE_CONSTRUCTION && !roadClosure)
				&& (o2.getType() == IncidentsManager.INCIDENT_TYPE_CONSTRUCTION && !roadClosure2)) {
			Double constructionDistance = o1.getDistance() - o2.getDistance();
			return constructionDistance.intValue();
		}

		if ( (o1.getType() == IncidentsManager.INCIDENT_TYPE_CONSTRUCTION && !roadClosure)
				&& (roadClosure2 || o2.getType() != IncidentsManager.INCIDENT_TYPE_CONSTRUCTION)) {
			return 1;
		}
		if ( (o2.getType() == IncidentsManager.INCIDENT_TYPE_CONSTRUCTION && !roadClosure2)
				&& (o1.getType() != IncidentsManager.INCIDENT_TYPE_CONSTRUCTION || roadClosure)) {
			return -1;
		}

		Double distanceCompare = o1.getDistance() - o2.getDistance();
		if (distanceCompare == 0.0) {
			if (o1.getSeverity() > o2.getSeverity()) {
				return -1;
			}
		} else {
			return distanceCompare.intValue();
		}

		return 0;
	}
}