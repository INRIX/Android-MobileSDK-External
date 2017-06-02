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
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.inrix.sample.R;
import com.inrix.sdk.model.SavedLocation;
import com.inrix.sdk.model.SavedLocation.LocationType;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SavedLocationEditFragment extends DialogFragment {
    public interface Callback {
        void onLocationEdited(final SavedLocation location);
    }

    private static final String FRAGMENT_TAG = SavedLocationEditFragment.class.getCanonicalName();
    private static final String ARG_LOCATION = ":location";

    @BindView(R.id.place_name)
    protected TextView textName;

    @BindView(R.id.place_address)
    protected TextView textAddress;

    @BindView(R.id.place_type_group)
    protected RadioGroup radioGroupType;

    protected SavedLocation location;

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

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.dialog_place_edit, container, false);

        ButterKnife.bind(this, root);

        this.location = this.getArguments().getParcelable(ARG_LOCATION);
        if (location == null) {
            return root;
        }

        this.textName.setText(this.location.getName());
        this.textAddress.setText(this.location.getAddress());

        switch (this.location.getType()) {
            case HOME:
                this.radioGroupType.check(R.id.place_type_home_radio);
                break;
            case WORK:
                this.radioGroupType.check(R.id.place_type_work_radio);
                break;
            case OTHER:
                this.radioGroupType.check(R.id.place_type_other_radio);
                break;
        }

        this.radioGroupType.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.place_type_home_radio:
                        location.setType(LocationType.HOME);
                        break;
                    case R.id.place_type_work_radio:
                        location.setType(LocationType.WORK);
                        break;
                    case R.id.place_type_other_radio:
                        location.setType(LocationType.OTHER);
                        break;
                }
            }
        });

        return root;
    }

    /**
     * Called when "Cancel" button is clicked.
     */
    @OnClick(R.id.cancel)
    protected void onCancelButtonClick() {
        this.dismiss();
    }

    /**
     * Called when "OK" button is clicked.
     */
    @OnClick(R.id.ok)
    protected void onSaveButtonClick() {
        this.dismiss();

        if (!(this.getActivity() instanceof Callback)) {
            return;
        }

        this.location.setName(this.textName.getText().toString());
        this.location.setAddress(this.textAddress.getText().toString());

        final Callback callback = (Callback) this.getActivity();
        callback.onLocationEdited(this.location);
    }

    /**
     * Open dialog.
     *
     * @param parentActivity Parent activity instance.
     * @param location       Message to show.
     */
    public static void show(final FragmentActivity parentActivity, final SavedLocation location) {
        final Bundle args = new Bundle();
        args.putParcelable(ARG_LOCATION, location);

        final FragmentManager fragmentManager = parentActivity.getSupportFragmentManager();
        final Fragment existing = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (existing != null && existing instanceof DialogFragment) {
            ((DialogFragment) existing).dismiss();
        }

        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        final SavedLocationEditFragment dialog = (SavedLocationEditFragment) Fragment.instantiate(parentActivity, SavedLocationEditFragment.class.getName(), args);
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
