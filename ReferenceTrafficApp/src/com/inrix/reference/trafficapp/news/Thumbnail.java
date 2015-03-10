/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.news;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import android.text.TextUtils;


@Root(name = "thumbnail", strict=false)
public class Thumbnail {
	@Attribute(name="width", required=false)
	private String width;

	@Attribute(name="height", required=false)
	private String height;
	
	@Attribute(name="url", required=false)
	private String url;
	
	@Attribute(name="size", required=false)
	private String size;
	
	public Thumbnail(){
		
	}
	
	/**
	 * getWidth
	 * @return width
	 */
	public int getWidth(){
		if (!TextUtils.isEmpty(this.width)) {
			return Integer.valueOf(this.width);
		}
		return 0;	
	}
	
	/**
	 * setWidth
	 * @param width
	 */
	public void setWidth( int width ){
		this.width = String.valueOf(width);
	}	
	
	/**
	 * getHeight
	 * @return height
	 */
	public int getHeight(){
		if (!TextUtils.isEmpty(this.height)) {
			return Integer.valueOf(this.height);
		}
		return 0;
	}
	
	/**
	 * setHeight
	 * @param height
	 */
	public void setHeight( int height ){
		this.height = String.valueOf(height);
	}
	
	public String getUrl(){
		return this.url;
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	
	public String getSize(){
		return this.size;
	}
	
	public void setSize(String size){
		this.size = size;
	}
	
}
