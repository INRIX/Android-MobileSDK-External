/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.fragments;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Xml.Encoding;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.inrix.reference.trafficapp.R;

public class AboutFragment extends Fragment implements OnClickListener {

	private final static String URL_CONTACT_SUPPORT = "http://inrixtraffic.zendesk.com";
	private final static String URL_PRIVACY_POLICY = "http://www.inrixtraffic.com/privacy-policy/";
	private final static String URL_TERMS_CONDITIONS = "http://www.inrixtraffic.com/terms/";
	private AlertDialog.Builder licenseDialog;
	private AlertDialog shownDialog;
	private WebView webView;

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_about,
				container,
				false);
		View supportView = rootView.findViewById(R.id.card_support);
		View googleTcView = rootView.findViewById(R.id.card_google_tc);
		View privacyPolicyView = rootView
				.findViewById(R.id.card_privacy_policy);
		View termsConditionsView = rootView
				.findViewById(R.id.card_terms_conditions);

		supportView.setOnClickListener(this);
		googleTcView.setOnClickListener(this);
		privacyPolicyView.setOnClickListener(this);
		termsConditionsView.setOnClickListener(this);

		return rootView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu,
	 * android.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.about, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.card_support) {
			openBrowser(URL_CONTACT_SUPPORT);
		} else if (v.getId() == R.id.card_google_tc) {
			if (null == this.licenseDialog) {
				this.licenseDialog = new AlertDialog.Builder(getActivity());
				this.licenseDialog.setTitle(R.string.google_tc);

				this.webView = new WebView(getActivity());
				String licenseInfo = GooglePlayServicesUtil
						.getOpenSourceSoftwareLicenseInfo(getActivity());
				try {
					this.webView.loadData(URLEncoder.encode(licenseInfo,
							Encoding.UTF_8.toString()).replaceAll("\\+", " "),
							"text/plain",
							Encoding.UTF_8.toString());
				} catch (UnsupportedEncodingException e) {
				}

				this.licenseDialog.setView(this.webView);
			}

			if (null == this.shownDialog) {
				this.shownDialog = this.licenseDialog.create();
			}

			if (!this.shownDialog.isShowing()) {
				this.shownDialog.show();
			}
		} else if (v.getId() == R.id.card_privacy_policy) {
			openBrowser(URL_PRIVACY_POLICY);
		} else if (v.getId() == R.id.card_terms_conditions) {
			openBrowser(URL_TERMS_CONDITIONS);
		}
	}

	/**
	 * Opens specific URL in browser.
	 * 
	 * @param url
	 *            URL to open.
	 */
	private void openBrowser(String url) {
		try {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(url));
			startActivity(browserIntent);
		} catch (ActivityNotFoundException exception) {
			String errorMessage = getString(R.string.warn_no_browser);
			Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG)
					.show();
		}
	}
}
