/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import com.inrix.reference.trafficapp.TrafficApp;
import com.inrix.sdk.IncidentsManager;

/** Incident type model. */
public class GeneratedIncidentTypeModel implements Parcelable {

	/** Incident type. */
	private int type;

	/** Friendly incident name. */
	private String name;

	/** Incident image resource id. */
	private int smallDrawableResId;

	/** Incident image. */
	private Drawable smallDrawable;

	/** Incident image resource id (for select side screen). */
	private int largeDrawableResId;

	/** Incident image (for select side screen). */
	private Drawable largeDrawable;

	/**
	 * Creates new instance of the class.
	 * 
	 * @param type
	 *            Incident type.
	 * @param nameRes
	 *            Incident name (Resource Id).
	 * @param drawableRes
	 *            Incident image (Resource Id).
	 */
	public GeneratedIncidentTypeModel(int type, int nameRes,
			int smallDrawableRes, int largeDrawableRes) {
		this.type = type;
		this.name = TrafficApp.getContext().getString(nameRes);
		this.smallDrawableResId = smallDrawableRes;
		this.smallDrawable = TrafficApp.getContext().getResources()
				.getDrawable(this.smallDrawableResId);

		this.largeDrawableResId = largeDrawableRes;
		this.largeDrawable = TrafficApp.getContext().getResources()
				.getDrawable(this.largeDrawableResId);
	}

	/**
	 * Creates new instance of the class from the parcel.
	 * 
	 * @param in
	 *            The parcel.
	 */
	public GeneratedIncidentTypeModel(Parcel in) {
		this.type = in.readInt();
		this.name = in.readString();
		this.smallDrawableResId = in.readInt();
		this.smallDrawable = TrafficApp.getContext().getResources()
				.getDrawable(this.smallDrawableResId);
		this.largeDrawableResId = in.readInt();
		this.largeDrawable = TrafficApp.getContext().getResources()
				.getDrawable(this.largeDrawableResId);
	}

	/**
	 * Retrieves type. See {@link IncidentsManager}.
	 * 
	 * @return Incident type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Retrieves friendly name.
	 * 
	 * @return Incident name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves small incident image.
	 * 
	 * @return Incident image.
	 */
	public Drawable getSmallDrawable() {
		return smallDrawable;
	}

	/**
	 * Retrieves large incident image.
	 * 
	 * @return Incident image.
	 */
	public Drawable getLargeDrawable() {
		return largeDrawable;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.type);
		dest.writeString(this.name);
		dest.writeInt(this.smallDrawableResId);
		dest.writeInt(this.largeDrawableResId);
	}

	/** Field to support parcelable functionality. */
	public static final Parcelable.Creator<GeneratedIncidentTypeModel> CREATOR = new Parcelable.Creator<GeneratedIncidentTypeModel>() {

		@Override
		public GeneratedIncidentTypeModel createFromParcel(Parcel in) {
			return new GeneratedIncidentTypeModel(in);
		}

		@Override
		public GeneratedIncidentTypeModel[] newArray(int size) {
			return new GeneratedIncidentTypeModel[size];
		}
	};
}