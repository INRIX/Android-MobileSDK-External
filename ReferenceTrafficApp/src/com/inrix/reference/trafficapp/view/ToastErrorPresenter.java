/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.view;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.error.ErrorEntity;
import com.inrix.reference.trafficapp.error.ErrorType;
import com.inrix.reference.trafficapp.error.IErrorPresenter;
import com.inrix.reference.trafficapp.error.IOnErrorActionClickListener;

public class ToastErrorPresenter implements IErrorPresenter {

	private Context context;

	public ToastErrorPresenter(Context context) {
		this.context = context;
	}

	@Override
	public boolean show(ErrorEntity error) {
		String msg = error.getMessage();
		if (TextUtils.isEmpty(msg)) {
			msg = getDefaultErrorMessage(error.getType());
		}
		if (!TextUtils.isEmpty(msg)) {
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	@Override
	public void dismiss(ErrorEntity error) {
	}

	@Override
	public boolean addOnErrorActionClickListener(IOnErrorActionClickListener l) {
		// No action buttons in toasts
		return false;
	}

	@Override
	public boolean removeOnErrorActionClickListener(IOnErrorActionClickListener l) {
		return false;
	}

	private String getDefaultErrorMessage(ErrorType type) {
		String result = "";
		switch (type) {
			case NETWORK_ERROR:
				result = context.getString(R.string.network_error);
				break;
			default:
				break;
		}
		return result;
	}
}
