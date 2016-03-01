package com.example.android.mocklocation;

public enum RouteData {
	unknown(""), // Nothing is selected.
	validSFpoints("San Francisco"), // 53 point in the SF area
	nonvalidPoints("Invalid points"), // points with incorrectly set speed and accuracy
	helicopterTripPoint("Helicopter trip"),
	longtrip("Seattle to Portland"), // 99 points, trip started in Seattle, ending in Portland
	londontrip("London trip"),
	seattle_to_carnation("Seattle to Carnation"),
	invalidSFpoints("Invalid San Francisco"), // //53 points with no accuracy, bearing or speed
	trip_to_vet("Trip to veterinarian"),
	seattleredmondtrip("Seattle to Redmond"), // seattle to redmond trip, 772 points
	tukwilanorthseattle("Tukwila to North Seattle") //tukwila to north seattle 
	;

	private static RouteData[] values;
	private final String description;

	private RouteData(final String description) {
		this.description = description;
	}

	@Override
	public final String toString() {
		return this.description;
	}

	public static final RouteData fromString(final String value) {
		if (values == null) {
			values = RouteData.values();
		}
		
		for (final RouteData current : values) {
			if (current.description.equalsIgnoreCase(value)) {
				return current;
			}
		}

		return RouteData.unknown;
	}

	public static final RouteData fromInteger(final int index) {
		if (values == null) {
			values = RouteData.values();
		}
		
		return values[index];
	}
};
