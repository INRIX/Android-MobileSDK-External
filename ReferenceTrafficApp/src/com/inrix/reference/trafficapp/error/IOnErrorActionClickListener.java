/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.error;

public interface IOnErrorActionClickListener {
	/**
	 * Triggered whenever action button clicked in error view
	 * 
	 * @param errorAction
	 *            - action clicked
	 * @param error
	 *            - error entity which this action belongs to
	 * @return true if action was consumed, false otherwise
	 */
	boolean onErrorActionClicked(ErrorAction errorAction, ErrorEntity error);
}
