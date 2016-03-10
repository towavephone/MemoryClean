package com.memoryclean.images;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class ImageProcessing {

	/**
	 * Drawable对象转Bitmap对象 并设置高宽
	 * 
	 * @param drawable
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap zoomDrawable(Drawable drawable, int w, int h) {
		Bitmap bmp = ((BitmapDrawable) drawable).getBitmap();
		if (w == 0 && h == 0) {
			w = bmp.getWidth();
			h = bmp.getHeight();
		}
		return zoomBitmap(bmp, w, h);
	}

	/**
	 * Bitmap对象设置高宽
	 * 
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		matrix = null;
		return newbmp;
	}

	/**
	 * 将obj转换为Drawable对象
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Drawable transformToDrawable(Object obj) {
		return new BitmapDrawable((Bitmap) obj);
	}

	// public static Bitmap mergeDrawable(Context context, int src, int dst) {
	// MCanvas mcanvas = new MCanvas(context, src);
	// mcanvas.drawBitmap(context.getResources().getDrawable(dst), 0);
	// return mcanvas.getMbitmap();
	// }

	// public static Bitmap combineDrawable(Context context, Drawable...
	// drawables) {
	// MCanvas canvas = new MCanvas(context, R.drawable.clean_log_background);
	// double l = lenth[drawables.length];
	// for (int i = 0; i < drawables.length; i++) {
	// if (drawables[i] != null)
	// canvas.drawBitmap(drawables[i], l, pl[drawables.length - 1][i]);
	// }
	// return canvas.getMbitmap();
	// }
}
