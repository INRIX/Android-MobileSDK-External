package com.inrix.sdk.client.fragments;

import com.inrix.sdk.client.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * The Class InrixConnectionLauncherFragment.
 */
public class InrixConnectionLauncherFragment extends Fragment implements
		OnCheckedChangeListener {

	/**
	 * The Interface IInrixConection.
	 */
	public interface IInrixConection {
		void onConnect();

		void onDisconnect();
	}

	/** The connection listener. */
	private IInrixConection connectionListener;

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_inrix_connection,
				container,
				false);
		CompoundButton onButton = (CompoundButton) view.findViewById(R.id.connetionSwitch);
		onButton.setOnCheckedChangeListener(this);
		return view;
	}

	/**
	 * Sets the inrix conection listener.
	 *
	 * @param listener the new inrix conection listener
	 */
	public void setInrixConectionListener(IInrixConection listener) {
		this.connectionListener = listener;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof IInrixConection) {
			this.setInrixConectionListener((IInrixConection) activity);
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implemenet InrixConnectionLauncherFragment.IInrixConection");
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
