/**
 * Copyright (c) 2013-2014 INRIX, Inc.
 * 
 * INRIX is a registered trademark of INRIX, Inc. Any copyright, patent and
 * trademark notice(s) contained herein or in related code, files or
 * documentation shall not be altered and shall be included in all copies and
 * substantial portions of the software.
 */

package com.inrix.reference.trafficapp.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

import com.inrix.reference.trafficapp.TrafficApp;

public class BitmapUtils {

	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(),
				Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	public static Bitmap blur(Bitmap bitmap, int radius, Context context) {
		RenderScript renderScript = RenderScript.create(TrafficApp.getContext());
		ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur
				.create(renderScript, Element.U8_4(renderScript));
		Allocation tmpIn = Allocation.createFromBitmap(renderScript, bitmap);
		Bitmap result = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(),
				Bitmap.Config.ARGB_8888);
		Allocation tmpOut = Allocation.createFromBitmap(renderScript, result);
		theIntrinsic.setRadius(radius);
		theIntrinsic.setInput(tmpIn);
		theIntrinsic.forEach(tmpOut);
		tmpOut.copyTo(result);

		return result;
	}

	public static Bitmap rotate(Bitmap bitmap, int degrees) {
		Matrix matrix = new Matrix();
		matrix.postRotate(degrees);
		return Bitmap.createBitmap(bitmap,
				0,
				0,
				bitmap.getWidth(),
				bitmap.getHeight(),
				matrix,
				true);
	}
}
