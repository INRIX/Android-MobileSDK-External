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
