/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.view;

import java.util.concurrent.CopyOnWriteArraySet;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.error.ErrorAction;
import com.inrix.reference.trafficapp.error.ErrorEntity;
import com.inrix.reference.trafficapp.error.IErrorPresenter;
import com.inrix.reference.trafficapp.error.IOnErrorActionClickListener;

public class SlidingBarErrorPresenter extends LinearLayout implements
		IErrorPresenter, View.OnClickListener {

	private static final long ANIMATION_DURATION_MS = 300;

	private View mainContent;
	private View actionClose;
	private ImageView actionButton;
	private TextView text;
	private ErrorEntity errorEntity;
	private ErrorEntity nextErrorEntity;
	private ErrorAction actionButtonAction;
	private Interpolator animationInterpolator = new AccelerateDecelerateInterpolator();

	private AnimatorSet inAnimation;
	private AnimatorSet outAnimation;

	private CopyOnWriteArraySet<IOnErrorActionClickListener> actionClickListeners = new CopyOnWriteArraySet<IOnErrorActionClickListener>();

	public SlidingBarErrorPresenter(Context context) {
		this(context, null);
	}

	public SlidingBarErrorPresenter(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		this.actionClose = findViewById(R.id.actionClose);
		this.actionButton = (ImageView) findViewById(R.id.actionButton);
		this.text = (TextView) findViewById(R.id.errorText);

		this.actionClose.setOnClickListener(this);
		this.actionButton.setOnClickListener(this);

	}

	@Override
	public boolean show(final ErrorEntity error) {
		if (error == null) {
			return false;
		}

		if (outAnimation != null && outAnimation.isStarted()) {
			// out animation is in progress
			nextErrorEntity = error;
			return true;
		}

		if (errorEntity != null) {
			// there is an error already on the screen. Hide it
			nextErrorEntity = error;
			dismiss(errorEntity);
			return true;
		}

		this.errorEntity = error;
		initBackground(errorEntity);
		initActions(errorEntity);
		initText(errorEntity);
		setVisibility(View.VISIBLE);
		getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {

			@Override
			public boolean onPreDraw() {
				getViewTreeObserver().removeOnPreDrawListener(this);
				if (errorEntity == null) {
					// Current error has been dismissed while we were waiting
					// for the first frame. Try to show next error if any
					show(nextErrorEntity);
					return true;
				}
				inAnimation = new AnimatorSet();
				setTranslationY(-getHeight());
				mainContent.setTranslationY(-getHeight());
				Animator anim1 = ObjectAnimator.ofFloat(SlidingBarErrorPresenter.this,
						View.TRANSLATION_Y,
						0f);
				Animator anim2 = ObjectAnimator.ofFloat(mainContent,
						View.TRANSLATION_Y,
						0f);
				inAnimation.setDuration(ANIMATION_DURATION_MS);
				inAnimation.setInterpolator(animationInterpolator);
				inAnimation.playTogether(anim1, anim2);
				inAnimation.start();
				return true;
			}
		});
		return true;
	}

	@Override
	public void dismiss(final ErrorEntity error) {
		if (error == nextErrorEntity) {
			// dismiss scheduled error entity;
			nextErrorEntity = null;
			return;
		}
		if (errorEntity == null) {
			// not showing anything
			return;
		}
		errorEntity = null;

		if (inAnimation != null && inAnimation.isStarted()) {
			inAnimation.cancel();
			inAnimation = null;
		}
		Animator anim1 = ObjectAnimator.ofFloat(this,
				View.TRANSLATION_Y,
				-getHeight());
		Animator anim2 = ObjectAnimator.ofFloat(mainContent,
				View.TRANSLATION_Y,
				-getHeight());
		outAnimation = new AnimatorSet();
		outAnimation.setDuration(ANIMATION_DURATION_MS);
		outAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		outAnimation.playTogether(anim1, anim2);
		outAnimation.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {
				outAnimation = null;
				setVisibility(View.GONE);
				mainContent.setTranslationY(0);
				// show next scheduled error (if any)
				ErrorEntity next = nextErrorEntity;
				nextErrorEntity = null;
				show(next);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});
		outAnimation.start();
	}

	private void initBackground(ErrorEntity error) {
		switch (error.getType()) {
			case NETWORK_ERROR:
			case LBS_OFF:
			case NETWORK_OFF:
			case SERVER_ERROR:
				setBackgroundColor(getResources()
						.getColor(R.color.error_bar_color_red));
				break;

			default:
				break;
		}
	}

	private void initActions(ErrorEntity error) {
		switch (error.getType()) {
			case NETWORK_ERROR:
			case SERVER_ERROR:
				actionClose.setVisibility(View.VISIBLE);
				actionButton.setImageResource(R.drawable.ic_action_refresh);
				actionButtonAction = ErrorAction.ACTION_REFRESH;
				actionButton.setVisibility(View.VISIBLE);
				break;
			case LBS_OFF:
				actionClose.setVisibility(View.VISIBLE);
				actionButton.setImageResource(R.drawable.ic_action_settings);
				actionButtonAction = ErrorAction.ACTION_LBS_SETTINGS;
				actionButton.setVisibility(View.VISIBLE);
				break;
			case NETWORK_OFF:
				actionClose.setVisibility(View.VISIBLE);
				actionButton.setImageResource(R.drawable.ic_action_settings);
				actionButtonAction = ErrorAction.ACTION_NETWORK_SETTINGS;
				actionButton.setVisibility(View.VISIBLE);
				break;

			default:
				break;
		}
	}

	private void initText(ErrorEntity error) {
		if (!TextUtils.isEmpty(error.getMessage())) {
			text.setText(error.getMessage());
			return;
		}
		switch (error.getType()) {
			case NETWORK_ERROR:
				text.setText(getContext().getString(R.string.network_error));
				break;
			case LBS_OFF:
				text.setText(getContext().getString(R.string.error_location_services_off));
				break;
			case NETWORK_OFF:
				text.setText(getContext().getString(R.string.error_network_unavailable));
				break;
			case SERVER_ERROR:
				text.setText(getContext().getString(R.string.error_server_error));
				break;
			default:
				break;
		}
	}

	public void setMainContent(View mainContent) {
		this.mainContent = mainContent;
	}

	@Override
	public boolean addOnErrorActionClickListener(IOnErrorActionClickListener l) {
		return this.actionClickListeners.add(l);
	}

	@Override
	public boolean removeOnErrorActionClickListener(IOnErrorActionClickListener l) {
		return this.actionClickListeners.remove(l);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.actionClose) {
			notifyActionButtonClicked(ErrorAction.ACTION_CLOSE);
		} else if (v.getId() == R.id.actionButton) {
			notifyActionButtonClicked(actionButtonAction);
		}
	}

	private void notifyActionButtonClicked(ErrorAction action) {
		for (IOnErrorActionClickListener l : actionClickListeners) {
			l.onErrorActionClicked(action, errorEntity);
		}
	}
}
