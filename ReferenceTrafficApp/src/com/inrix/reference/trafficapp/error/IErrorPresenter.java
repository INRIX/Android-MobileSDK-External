/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.error;

public interface IErrorPresenter {
	/**
	 * Show UI representing this error.
	 * 
	 * @param error
	 *            to show
	 * @return - true if {@link #dismiss()} needs to be called in order to
	 *         remove this error from screen (for instance sliding bar which
	 *         needs to be closed manually). False can be returned if there is
	 *         no need to call {@link #dismiss()} (f.i. our error is Toast - so
	 *         it will be removed automatically)
	 */
	boolean show(ErrorEntity error);

	void dismiss(ErrorEntity error);

	boolean addOnErrorActionClickListener(IOnErrorActionClickListener l);

	boolean removeOnErrorActionClickListener(IOnErrorActionClickListener l);
}
