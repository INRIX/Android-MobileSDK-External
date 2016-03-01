/**
 * Copyright (c) 2013-2015 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample;

import com.inrix.sdk.model.GeoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class Place.
 */
public class Place implements Cloneable {

    /**
     * The name.
     */
    private String name;

    /**
     * The point.
     */
    private GeoPoint point;

    /**
     * The type.
     */
    private PlaceType type = PlaceType.OTHER;

    /**
     * The destinations.
     */
    private List<Place> destinations = new ArrayList<Place>();

    /**
     * Instantiates a new city.
     *
     * @param name  the name
     * @param point the point
     */
    public Place(String name, GeoPoint point) {
        this.setName(name);
        this.setPoint(point);
    }

    /**
     * Instantiates a new place.
     *
     * @param name  the name
     * @param point the point
     * @param type  the type
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
     * @param name the name to set
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
     * @param point the point to set
     */
    public void setPoint(GeoPoint point) {
        this.point = point;
    }

    /**
     * Adds the destination.
     *
     * @param point the point
     */
    public void addDestination(Place point) {
        destinations.add(point);
    }

    /**
     * Gets the destinations.
     *
     * @return the destination as a list.
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

    /**
     * {@inheritDoc}
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