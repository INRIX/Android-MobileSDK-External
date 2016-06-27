/**
 * Copyright (c) 2013-2016 INRIX, Inc.
 * <p/>
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and trademark notice(s)
 * contained herein or in related code, files or documentation shall not be altered and shall be
 * included in all copies and substantial portions of the software. This software is "Sample Code".
 * Refer to the License.pdf file for your rights to use this software.
 */

package com.inrix.sample.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.inrix.sample.R;

/**
 * The Class InrixConnectionLauncherFragment.
 */
public class InrixConnectionLauncherFragment extends Fragment implements OnCheckedChangeListener {

    /**
     * The Interface IInrixConnection.
     */
    public interface IInrixConnection {
        void onConnect();

        void onDisconnect();
    }

    /**
     * The connection listener.
     */
    private IInrixConnection connectionListener;

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inrix_connection, container, false);
        CompoundButton onButton = (CompoundButton) view.findViewById(R.id.connetionSwitch);
        onButton.setOnCheckedChangeListener(this);
        return view;
    }

    /**
     * Sets the inrix conection listener.
     *
     * @param listener the new inrix conection listener
     */
    public void setInrixConectionListener(IInrixConnection listener) {
        this.connectionListener = listener;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IInrixConnection) {
            this.setInrixConectionListener((IInrixConnection) activity);
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implemenet InrixConnectionLauncherFragment.IInrixConnection");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (this.connectionListener == null) {
            return;
        }

        if (isChecked) {
            this.connectionListener.onConnect();
        } else {
            this.connectionListener.onDisconnect();
        }
    }
}
