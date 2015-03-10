package com.inrix.sample.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by paveld on 2/10/14.
 */
public class GradientView extends View {

	private Paint paint = null;
	private int[] colors = null;
	private float[] positions = null;

	public GradientView(Context context) {
		super(context);
		init();
	}

	public GradientView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GradientView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		setWillNotDraw(false);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (this.colors != null) {
			setGradient(this.colors, this.positions);
		}
	}

	/**
	 * Set gradient configuration
	 * 
	 * @param colors
	 *            - The colors to be distributed along the gradient line
	 * @param positions
	 *            - May be null. The relative positions [0..1] of each
	 *            corresponding color in the colors array. If this is null, the
	 *            the colors are distributed evenly along the gradient line.
	 * @throws IllegalStateException
	 *             when input values are invalid
	 */
	public void setGradient(int[] colors, float[] positions)
			throws IllegalStateException {
		validateInput(colors, positions);
		this.colors = colors;
		this.positions = positions;

		if (getWidth() == 0 || getHeight() == 0) {
			// we are not measured yet
		} else {
			setGradient(colors, positions, getWidth(), getHeight());
		}
	}

	private void setGradient(int[] colors, float[] positions, int w, int h) {
		LinearGradient gradient = new LinearGradient(0,
				h / 2,
				w,
				h / 2,
				colors,
				positions,
				Shader.TileMode.CLAMP);
		paint = new Paint();
		paint.setDither(true);
		paint.setShader(gradient);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (getWidth() == 0 || getHeight() == 0 || paint == null) {
			return;
		}

		canvas.drawPaint(paint);
	}

	private void validateInput(int[] colors, float[] positions) {
		if (colors == null) {
			throw new IllegalStateException("Colors should not be null");
		}

		if (positions != null && colors.length != positions.length) {
			throw new IllegalStateException("Positions length should not be equal to colors length");
		}
	}
}
