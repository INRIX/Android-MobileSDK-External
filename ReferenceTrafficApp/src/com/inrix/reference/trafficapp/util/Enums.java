/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.util;

public class Enums {
	/**
	 * The Enum RobotoFontTyperface.
	 */
	public enum RobotoFontTyperface {
		Lite(0), Thin(1), Condensed(2), Medium(3), Black(4), Bold_condensed(5), Light_italic(
				6), Unknown(-1);

		private RobotoFontTyperface(final int value) {
			this.fontIndex = value;
		}

		/** The font index. */
		private final int fontIndex;

		/**
		 * Gets the font index.
		 * 
		 * @return the font index
		 */
		public final int getFontIndex() {
			return fontIndex;
		}

		/**
		 * Value of.
		 * 
		 * @param code
		 *            the code
		 * @return the roboto font typerface
		 */
		public static RobotoFontTyperface valueOf(int code) {
			if (code >= 0 && code <= RobotoFontTyperface.values().length - 1) {
				try {
					return RobotoFontTyperface.values()[code];
				} catch (ArrayIndexOutOfBoundsException ex) {
					return RobotoFontTyperface.Unknown;
				}
			}

			return RobotoFontTyperface.Unknown;
		}
	}
}
