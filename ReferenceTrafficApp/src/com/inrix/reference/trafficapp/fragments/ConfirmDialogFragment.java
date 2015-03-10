/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.inrix.reference.trafficapp.R;

public class ConfirmDialogFragment extends DialogFragment implements
		OnClickListener {
	private Context mContext = null;
	private IConfirmDialogListener listener = null;

	public interface IConfirmDialogListener {
		public void onConfirmAction();
		public void onCancelAction();
	}

	public ConfirmDialogFragment() {
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
		alertDialogBuilder.setTitle(R.string.are_you_sure);
		alertDialogBuilder.setMessage(R.string.confirm_cancel_action);

		alertDialogBuilder.setPositiveButton(R.string.ok_string, this);
		alertDialogBuilder.setNegativeButton(R.string.cancel_string, this);
		
		AlertDialog dlg = alertDialogBuilder.create();
		dlg.setCancelable(false);
		dlg.setCanceledOnTouchOutside(false);
		return dlg;
	}

	public void setContext(Context context) {
		this.mContext = context;
	}

	public void setListener(IConfirmDialogListener listener) {
		this.listener = listener;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (null != listener) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				this.listener.onConfirmAction();
			} else if (which == DialogInterface.BUTTON_NEGATIVE) {
				this.listener.onCancelAction();
			}
		}
		dismiss();	}

}
