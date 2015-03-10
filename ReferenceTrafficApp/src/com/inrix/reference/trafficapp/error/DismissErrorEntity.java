/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.error;

/**
 * The Class DismissErrorEntity, notify about dismissing particular error entity
 */
public class DismissErrorEntity {
	private long id;
	private ErrorType type;

	/**
	 * Instantiates a new dismiss error entity.
	 *
	 * @param type the type
	 */
	public DismissErrorEntity(ErrorType type) {
		this.type = type;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public ErrorType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the type
	 * @return the dismiss error entity
	 */
	public DismissErrorEntity setType(ErrorType type) {
		this.type = type;
		return this;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the id
	 * @return the dismiss error entity
	 */
	public DismissErrorEntity setId(long id) {
		this.id = id;
		return this;
	}

}
