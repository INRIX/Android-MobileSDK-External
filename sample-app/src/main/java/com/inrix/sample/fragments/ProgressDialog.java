/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.inrix.sample.R;

public class ProgressDialog extends DialogFragment {
    private static final String FRAGMENT_TAG = ProgressDialog.class.getCanonicalName();
    private static final String ARG_MESSAGE = ":message";

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
        super.onStart();

        final Dialog dialog = this.getDialog();
        if (dialog == null) {
            return;
        }

        this.setStyle(STYLE_NO_TITLE, R.style.Dialog);

        //noinspection ConstantConditions
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.dialog_progress, container, false);

        final TextView message = (TextView) root.findViewById(R.id.loading_message);
        message.setText(this.getArguments().getString(ARG_MESSAGE, this.getString(R.string.loading)));

        return root;
    }

    /**
     * Open dialog.
     *
     * @param parentActivity Parent activity instance.
     * @param message        Message to show.
     */
    public static void show(final FragmentActivity parentActivity, @StringRes final int message) {
        show(parentActivity, parentActivity.getString(message));
    }

    /**
     * Open dialog.
     *
     * @param parentActivity Parent activity instance.
     * @param message        Message to show.
     */
    public static void show(final FragmentActivity parentActivity, final String message) {
        final Bundle args = new Bundle();

        final FragmentManager fragmentManager = parentActivity.getSupportFragmentManager();
        final Fragment existing = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (existing != null && existing instanceof DialogFragment) {
            ((DialogFragment) existing).dismiss();
        }

        if (!TextUtils.isEmpty(message)) {
            args.putString(ARG_MESSAGE, message);
        }

        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        final ProgressDialog dialog = (ProgressDialog) Fragment.instantiate(parentActivity, ProgressDialog.class.getName(), args);
        dialog.setCancelable(false);
        dialog.show(transaction, FRAGMENT_TAG);
    }

    /**
     * Dismiss current dialog.
     *
     * @param parentActivity Parent activity instance.
     */
    public static void dismiss(final FragmentActivity parentActivity) {
        final FragmentManager fragmentManager = parentActivity.getSupportFragmentManager();
        final Fragment existing = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (existing != null && existing instanceof DialogFragment) {
            ((DialogFragment) existing).dismiss();
        }
    }
}
