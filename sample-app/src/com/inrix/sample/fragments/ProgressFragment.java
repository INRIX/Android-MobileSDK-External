package com.inrix.sample.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressFragment extends DialogFragment {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog pd = new ProgressDialog(getActivity());
		pd.setMessage("Loading");
		pd.setCancelable(false);
		pd.setCanceledOnTouchOutside(false);
		return pd;
	}
}
