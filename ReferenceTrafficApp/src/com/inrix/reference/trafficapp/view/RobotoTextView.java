/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.view;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.inrix.reference.trafficapp.R;
import com.inrix.reference.trafficapp.util.Enums.RobotoFontTyperface;
import com.inrix.reference.trafficapp.util.FontHelper;

/**
 * The Class RobotoTextView.
 */
public class RobotoTextView extends TextView {

	/**
	 * Shrink to fit flag. When set, text size will be changed to fit container
	 * size
	 */
	private boolean shrinkToFit = false;

	/**
	 * Flag to minimize view width.
	 * For example textView has params layout_width="50dp" and text="One two three".
	 * The text will be separated on two lines "One two" and "three".
	 * The view will have width 50 dip.
	 * When set, the view height will be minimized depending on real line width.
	 */
	private boolean minimizeWidth = false;

	/** The all caps flag. */
	private boolean allCaps;

	/** Text paint */
	private Paint textPaint;

	private float originalTextSize = -1;

	/**
	 * Instantiates a new roboto text view.
	 * 
	 * @param context
	 *            the context
	 */
	public RobotoTextView(Context context) {
		super(context, null);
		init(context, null);
	}

	/**
	 * Instantiates a new roboto text view.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	public RobotoTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	/**
	 * Instantiates a new roboto text view.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 * @param defStyle
	 *            the def style
	 */
	@SuppressLint("NewApi")
	public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	/**
	 * Inits the.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	private final void init(final Context context, final AttributeSet attrs) {
		if (this.isInEditMode() == false) {
			textPaint = new Paint();
			textPaint.set(this.getPaint());
			originalTextSize = this.getTextSize();
			boolean typefaceSet = false;

			if (attrs != null) {
				TypedArray fontStyle = context.obtainStyledAttributes(attrs,
						R.styleable.RobotoTextView);

				int fontTyperface = fontStyle
						.getInt(R.styleable.RobotoTextView_robotoFont,
								RobotoFontTyperface.Unknown.getFontIndex());
				this.shrinkToFit = fontStyle
						.getBoolean(R.styleable.RobotoTextView_shrink_to_fit,
								false);

				this.minimizeWidth = fontStyle
						.getBoolean(R.styleable.RobotoTextView_minimize_width,
								false);

				typefaceSet = true;
				switch (RobotoFontTyperface.valueOf(fontTyperface)) {
				case Thin:
					this.setTypeface(FontHelper.getThinTypeface(context));
					break;
				case Black:
					this.setTypeface(FontHelper.getBlackTypeface(context));
					break;
				case Bold_condensed:
					this.setTypeface(FontHelper
							.getBoldCondensedTypeface(context));
					break;
				case Condensed:
					this.setTypeface(FontHelper
							.getCondensedTypeface(context));
					break;
				case Lite:
					this.setTypeface(FontHelper.getLiteTypeface(context));
					break;
				case Medium:
					this.setTypeface(FontHelper.getMediumTypeface(context));
					break;
				case Light_italic:
					this.setTypeface(FontHelper
							.getLightItalicTypeface(context));
					break;
				case Unknown:
					typefaceSet = false;
					break;
				default:
					typefaceSet = false;
					break;
				}

				fontStyle.recycle();

				this.allCaps = attrs
						.getAttributeBooleanValue("http://schemas.android.com/apk/res/android",
								"textAllCaps",
								false);
				if (allCaps) {
					setText(getText().toString()
							.toUpperCase(Locale.getDefault()));
				}
			}

			if (typefaceSet == false) {
				Typeface typeface = this.getTypeface();
				if (typeface == null) {
					this.setTypeface(FontHelper.getNormalTypeface(context));
				} else if (typeface.isBold()) {
					this.setTypeface(FontHelper.getBoldTypeface(context));
				} else if (typeface.isItalic()) {
					this.setTypeface(FontHelper.getItalicTypeface(context));
				}
			}
		}
	}

	/*
	 * Re size the font so the specified text fits in the text box assuming the
	 * text box is the specified width.
	 */
	private void refitText(String text, int textWidth, int textHeight) {
		if (!shrinkToFit) {
			return;
		}
		final float densityMultiplier = getContext().getResources().getDisplayMetrics().density;
		if (textWidth <= 0 || textHeight <= 0)
			return;
		int targetWidth = textWidth - this.getPaddingLeft()
				- this.getPaddingRight();
		float targetHeight = textHeight - this.getPaddingTop()
				- this.getPaddingBottom()
				- 3 * densityMultiplier; // Also subtract the 3dip font padding
		float hi = 100;
		float lo = 2;
		final float threshold = 0.5f; // How close we have to be

		textPaint.set(this.getPaint());

		while ((hi - lo) > threshold) {
			float size = (hi + lo) / 2;
			textPaint.setTextSize(size);
			if (textPaint.measureText(text) >= targetWidth || size >= targetHeight)
				hi = size; // too big
			else
				lo = size; // too small
		}
		// Use lo so that we undershoot rather than overshoot
		if (lo < originalTextSize) {
			this.setTextSize(TypedValue.COMPLEX_UNIT_PX, lo);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		if (minimizeWidth) {
			Layout layout = getLayout();
			if (layout != null) {
				int width = (int) Math.ceil(getMaxLineWidth(layout))
						+ getCompoundPaddingLeft() + getCompoundPaddingRight();
				int height = getMeasuredHeight();
				setMeasuredDimension(width, height);
			}
			return;
		}

		if (!shrinkToFit) {
			return;
		}

		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		int height = getMeasuredHeight();
		refitText(this.getText().toString(), parentWidth, height);
		this.setMeasuredDimension(parentWidth, height);
	}

	/**
	 * Calculates lines width and returns max line width.
	 * 
	 * @param layout
	 *            Text layout with lines.
	 * 
	 * @return Max width of the layout lines.
	 */
	private float getMaxLineWidth(Layout layout) {
		float maxLineWidth = 0;
		int lines = layout.getLineCount();
		for (int i = 0; i < lines; i++) {
			if (layout.getLineWidth(i) > maxLineWidth) {
				maxLineWidth = layout.getLineWidth(i);
			}
		}
		return maxLineWidth;
	}

	@Override
	protected void onTextChanged(final CharSequence text,
			final int start,
			final int before,
			final int after) {
		if (shrinkToFit) {
			refitText(text.toString(), this.getWidth(), this.getHeight());
		}
	}

	/**
	 * Sets the text all caps.
	 * 
	 * @param resourceId
	 *            the new text all caps
	 */
	public void setTextAllCaps(int resourceId) {
		if (!allCaps) {
			setTextAllCaps(getContext().getText(resourceId));
		} else {
			setText(resourceId);
		}
	}

	/**
	 * Sets the text all caps.
	 * 
	 * @param text
	 *            the new text all caps
	 */
	public void setTextAllCaps(CharSequence text) {
		if (!allCaps) {
			this.setText(text.toString().toUpperCase(Locale.getDefault()));
		} else {
			setText(text);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if ((w != oldw || h != oldh) && shrinkToFit) {
			refitText(this.getText().toString(), w, h);
		}
	}
}
