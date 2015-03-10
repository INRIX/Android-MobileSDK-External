package com.inrix.sample;

import java.util.ArrayList;
import java.util.List;

import com.inrix.sdk.model.GeoPoint;

/**
 * The Class Place.
 */
public class Place implements Cloneable {

	/** The name. */
	private String name;

	/** The point. */
	private GeoPoint point;

	/** The type. */
	private PlaceType type = PlaceType.OTHER;

	/** The destinations. */
	private List<Place> destinations = new ArrayList<Place>();

	/**
	 * Instantiates a new city.
	 * 
	 * @param name
	 *            the name
	 * @param point
	 *            the point
	 */
	public Place(String name, GeoPoint point) {
		this.setName(name);
		this.setPoint(point);
	}

	/**
	 * Instantiates a new place.
	 * 
	 * @param name
	 *            the name
	 * @param point
	 *            the point
	 * @param type
	 *            the type
	 */
	public Place(String name, GeoPoint point, PlaceType type) {
		this.setName(name);
		this.setPoint(point);
		this.setType(type);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the point
	 */
	public GeoPoint getPoint() {
		return point;
	}

	/**
	 * @param point
	 *            the point to set
	 */
	public void setPoint(GeoPoint point) {
		this.point = point;
	}

	/**
	 * Adds the destination.
	 * 
	 * @param point
	 *            the point
	 */
	public void addDestination(Place point) {
		destinations.add(point);
	}

	/**
	 * Gets the destination point.
	 * 
	 * @param point
	 *            the point
	 * @return the destination point
	 */
	public List<Place> getDestinationsList() {
		return destinations;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public PlaceType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(PlaceType type) {
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		Place clone = new Place(getName(), getPoint(), getType());
		clone.destinations = new ArrayList<Place>(this.destinations);
		return clone;
	}

	/**
	 * The Enum PlaceType.
	 */
	public enum PlaceType {
		HOME, WORK, OTHER
	}
}