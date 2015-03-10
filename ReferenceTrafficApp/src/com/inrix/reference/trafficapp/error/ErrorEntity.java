/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Parcel;
import android.os.Parcelable;

import com.inrix.sdk.Error;

public class ErrorEntity implements Parcelable {
	private static final Logger logger = LoggerFactory.getLogger(ErrorEntity.class);
	
	private long id;
	private String message;
	private int priority = NORMAL_PRIORITY;
	private ErrorType type;
	private static final int HIGH_PRIORITY = 5;
	private static final int NORMAL_PRIORITY = 4;

	public ErrorEntity(Parcel in) {
		String[] data = new String[4];
		in.readStringArray(data);
		this.id = Long.valueOf(data[0]);
		this.message = data[1];
		this.priority = Integer.valueOf(data[2]);
		this.type = ErrorType.values()[Integer.valueOf(data[3])];
	}

	public ErrorEntity(ErrorType type) {
		this.type = type;
		this.priority = getDefaultPriority(type);
	}

	public long getId() {
		return id;
	}

	public ErrorEntity setId(long id) {
		this.id = id;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public ErrorEntity setMessage(String message) {
		this.message = message;
		return this;
	}

	public int getPriority() {
		return priority;
	}

	public ErrorEntity setPriority(int priority) {
		this.priority = priority;
		return this;
	}

	public ErrorType getType() {
		return type;
	}

	public ErrorEntity setType(ErrorType type) {
		this.type = type;
		return this;
	}

	private int getDefaultPriority(ErrorType type) {
		int result = NORMAL_PRIORITY;
		switch (type) {
			case NETWORK_ERROR:
			case LBS_OFF:
				result = HIGH_PRIORITY;
				break;
			default:
				break;
		}
		return result;
	}

	/**
	 * Create ErrorEntity from INRIX error
	 * 
	 * @param inrixError
	 * @return ErrorEntity or null if error cannot be transformed
	 */
	public static ErrorEntity fromInrixError(Error inrixError) {

		if (inrixError == null) {
			return null;
		}

		ErrorType type = null;
		switch (inrixError.getErrorType()) {
			case NETWORK_ERROR:
				type = ErrorType.NETWORK_ERROR;
				break;
            case SERVER_ERROR:
                type = ErrorType.SERVER_ERROR;
                break;
			default:
				// TODO: parse all other errors
				break;
		}
		if (type == null) {
			logger.error("Unable to create ErrorEntity. Unknown error: {}", inrixError.getErrorType());
			return null;
		}
		return new ErrorEntity(type);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		String keyValueFormat = "%s=\"%s\"";
		builder.append('{');
		builder.append(String.format(keyValueFormat, "type", type));
		builder.append(", ");
		builder.append(String.format(keyValueFormat, "custom_message", message));
		builder.append(", ");
		builder.append(String.format(keyValueFormat, "id", id));
		builder.append(", ");
		builder.append(String.format(keyValueFormat, "priority", priority));
		builder.append('}');
		return builder.toString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] { String.valueOf(id), message,
				String.valueOf(priority), String.valueOf(type.ordinal()) });
	}

	public static final Parcelable.Creator<ErrorEntity> CREATOR = new Parcelable.Creator<ErrorEntity>() {
		public ErrorEntity createFromParcel(Parcel in) {
			return new ErrorEntity(in);
		}

		public ErrorEntity[] newArray(int size) {
			return new ErrorEntity[size];
		}
	};
}
