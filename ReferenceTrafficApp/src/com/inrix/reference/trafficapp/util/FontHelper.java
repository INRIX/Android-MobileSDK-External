/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.util;

import android.content.Context;
import android.graphics.Typeface;

/**
 * The Class FontHelper.
 */
public class FontHelper {

	/** The roboto regular. */
	private static Typeface robotoRegular = null;

	/** The roboto bold. */
	private static Typeface robotoBold = null;

	/** The roboto condensed. */
	private static Typeface robotoCondensed = null;

	/** The roboto bold condensed. */
	private static Typeface robotoBoldCondensed = null;

	/** The roboto italic. */
	private static Typeface robotoItalic = null;

	/** The roboto lite. */
	private static Typeface robotoLite = null;

	/** The roboto thin. */
	private static Typeface robotoThin = null;

	/** The roboto medium. */
	private static Typeface robotoMedium = null;

	/** The roboto black. */
	private static Typeface robotoBlack = null;

	/** The roboto italic. */
	private static Typeface robotoLightItalic = null;

	/**
	 * Gets the normal typeface.
	 * 
	 * @param context
	 *            the context
	 * @return the normal typeface
	 */
	public static final Typeface getNormalTypeface(final Context context) {
		if (robotoRegular == null) {
			robotoRegular = Typeface.createFromAsset(context.getAssets(),
					"Roboto-Regular.ttf");
		}
		return robotoRegular;
	}

	/**
	 * Gets the bold typeface.
	 * 
	 * @param context
	 *            the context
	 * @return the bold typeface
	 */
	public static final Typeface getBoldTypeface(final Context context) {
		if (robotoBold == null) {
			robotoBold = Typeface.createFromAsset(context.getAssets(), "Roboto-Bold.ttf");
		}
		return robotoBold;
	}

	/**
	 * Gets the condensed typeface.
	 * 
	 * @param context
	 *            the context
	 * @return the condensed typeface
	 */
	public static final Typeface getCondensedTypeface(final Context context) {
		if (robotoCondensed == null) {
			robotoCondensed = Typeface.createFromAsset(context.getAssets(),
					"Roboto-Condensed.ttf");
		}
		return robotoCondensed;
	}

	/**
	 * Gets the bold condensed typeface.
	 * 
	 * @param context
	 *            the context
	 * @return the bold condensed typeface
	 */
	public static final Typeface getBoldCondensedTypeface(final Context context) {
		if (robotoBoldCondensed == null) {
			robotoBoldCondensed = Typeface.createFromAsset(context.getAssets(),
					"Roboto-BoldCondensed.ttf");
		}
		return robotoBoldCondensed;
	}

	/**
	 * Gets the lite typeface.
	 * 
	 * @param context
	 *            the context
	 * @return the lite typeface
	 */
	public static final Typeface getLiteTypeface(final Context context) {
		if (robotoLite == null) {
			robotoLite = Typeface.createFromAsset(context.getAssets(),
					"Roboto-Light.ttf");
		}

		return robotoLite;
	}

	/**
	 * Gets the black typeface.
	 * 
	 * @param context
	 *            the context
	 * @return the black typeface
	 */
	public static final Typeface getBlackTypeface(final Context context) {
		if (robotoBlack == null) {
			robotoBlack = Typeface.createFromAsset(context.getAssets(),
					"Roboto-Black.ttf");
		}

		return robotoBlack;
	}

	/**
	 * Gets the thin typeface.
	 * 
	 * @param context
	 *            the context
	 * @return the thin typeface
	 */
	public static final Typeface getThinTypeface(final Context context) {
		if (robotoThin == null) {
			robotoThin = Typeface.createFromAsset(context.getAssets(),
					"Roboto-Thin.ttf");
		}

		return robotoThin;
	}

	/**
	 * Gets the medium typeface.
	 * 
	 * @param context
	 *            the context
	 * @return the medium typeface
	 */
	public static final Typeface getMediumTypeface(final Context context) {
		if (robotoMedium == null) {
			robotoMedium = Typeface.createFromAsset(context.getAssets(),
					"Roboto-Medium.ttf");
		}

		return robotoMedium;
	}

	/**
	 * Gets the italic typeface.
	 * 
	 * @param context
	 *            the context
	 * @return the italic typeface
	 */
	public static final Typeface getItalicTypeface(final Context context) {
		if (robotoItalic == null) {
			robotoItalic = Typeface.createFromAsset(context.getAssets(),
					"Roboto-Italic.ttf");
		}

		return robotoItalic;
	}

	/**
	 * Gets the italic typeface.
	 * 
	 * @param context
	 *            the context
	 * @return the italic typeface
	 */
	public static final Typeface getLightItalicTypeface(final Context context) {
		if (robotoLightItalic == null) {
			robotoLightItalic = Typeface.createFromAsset(context.getAssets(),
					"Roboto-LightItalic.ttf");
		}

		return robotoLightItalic;
	}

}
