package com.inrix.sample.model;

import java.util.ArrayList;

public class RouteSegmentsCollection extends ArrayList<RouteSegment>{
	private static final long serialVersionUID = 1L;

	public void addSegment(RouteSegment segment) {
		if (segment != null) {
			super.add(segment);
		}
	}

	public int[] getColors() {
		int[] colors = new int[size()];
		for (int i = 0; i < size(); i++) {
			colors[i] = get(i).segmentColor;
		}
		return colors;
	}

	public float[] getPositions() {
		float[] positions = new float[size()];
		for (int i = 0; i < size(); i++) {
			positions[i] = get(i).segmentStart;
		}
		return positions;
	}
}
