/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.inrix.sdk.model.GeoPoint;

public class WeatherAppConfig {
	private static final Logger logger = LoggerFactory.getLogger(WeatherAppConfig.class);
	
	private static WeatherAppConfig currentAppConfig = null;

	private GeoPoint demoHome = null;
	private GeoPoint demoWork = null;
	private GeoPoint demoCurrentLocation = null;
	private boolean demoLocation = false;
	
	/*private constructor*/
	private WeatherAppConfig(){
		
	}
	public GeoPoint getDemoHome(){
		return this.demoHome;
	}
	
	public GeoPoint getDemoWork(){
		return this.demoWork;
	}
	
	public GeoPoint getDemoCurrentLocation(){
		return this.demoCurrentLocation;
	}
	
	public boolean getDemoLocations(){
		return this.demoLocation;
	}
	
	@SuppressWarnings("rawtypes")
	public void loadFromProperties(Context paramContext, String paramString) {
		Resources localResources = paramContext.getResources();
		AssetManager localAssetManager = localResources.getAssets();
		try {
			if (!Arrays.asList(localAssetManager.list(""))
					.contains(paramString)) {
				return;
			}
		} catch (IOException localIOException1) {

			// Logger.error(localIOException1);
			return;
		}

		Properties localProperties = new Properties();
		try {
			InputStream localInputStream = localAssetManager.open(paramString);
			localProperties.load(localInputStream);
			Class localClass = getClass();
			List localList = Arrays.asList(localClass.getDeclaredFields());
			ListIterator localListIterator = localList.listIterator();
			while (localListIterator.hasNext()) {
				Field localField = (Field) localListIterator.next();
				boolean accesible = localField.isAccessible();
				String fieldName = localField.getName();
				
				String str = localProperties.getProperty(fieldName);
				if (str == null)
					continue;
				try {
					str = str.trim();
					localField.setAccessible(true);
					if ( (localField.getType() == GeoPoint.class)){
						localField.set(this, GeoPoint.parse(str));
					}else if ( (localField.getType() == Integer.TYPE)
							|| (localField.getType() == Integer.class)){
						localField.set(this, Integer.valueOf(str));
					}else if ( (localField.getType() == Long.TYPE)
							|| (localField.getType() == Long.class)){
						localField.set(this, Long.valueOf(str));
					}else if ( (localField.getType() == Boolean.TYPE)
							|| (localField.getType() == Boolean.class)){
						localField.set(this, Boolean.valueOf(str));
					}else{
						try {
							localField.set(this, str.trim());
						} catch (IllegalArgumentException localIllegalArgumentException) {
							logger.error("Unable to set field '{}' due to type mismatch.", localField.getName());
						}
					}
					localField.setAccessible(accesible);
				} catch (IllegalAccessException localIllegalAccessException) {
					logger.error("Unable to set field '{}' because the field is not visible.", localField.getName());
				}
			}
		} catch (IOException localIOException2) {
			logger.error("Error loading properties file {}", paramString, localIOException2);
		}
	}
	
	public synchronized static WeatherAppConfig getCurrentAppConfig(Context context){
		if( null == currentAppConfig && null != context){
			currentAppConfig = new WeatherAppConfig();
			currentAppConfig.loadFromProperties(context, "democonfig.properties");
		}
		return currentAppConfig;
	}
}
