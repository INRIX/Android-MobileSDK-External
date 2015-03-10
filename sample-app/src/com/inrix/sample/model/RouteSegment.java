package com.inrix.sample.model;

import android.graphics.Color;

public class RouteSegment implements Comparable<RouteSegment> {
	public float segmentStart = 0;
	public int segmentColor = Color.BLACK;

	@Override
	public int compareTo(RouteSegment another) {
		if (another == null) {
			return -1;
		}

		return (int) ( (segmentStart - another.segmentStart) * 1000);
	}

	@Override
	public String toString() {
		return segmentStart + ": " + resolveColor(segmentColor);
	}

	private String resolveColor(int color) {
		if (color == Color.GREEN) {
			return "green";
		} else if (color == Color.RED) {
			return "red";
		} else if (color == Color.YELLOW) {
			return "yellow";
		}

		return String.valueOf(color);
	}
}
