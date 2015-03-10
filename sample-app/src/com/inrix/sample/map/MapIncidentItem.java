package com.inrix.sample.map;

public class MapIncidentItem extends MapClusterItem {
	private long id;
	private int type;

	public MapIncidentItem(double lat, double lng, long id, int type) {
		super(lat, lng);

		this.id = id;
		this.type = type;
	}

	public long getId() {
		return this.id;
	}

	public int getType() {
		return this.type;
	}
}