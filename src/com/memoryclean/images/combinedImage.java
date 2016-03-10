package com.memoryclean.images;

import android.content.Context;
import android.graphics.Bitmap;

import com.memoryclean.cache.ImageSoftCache;
import com.memoryclean.classes.MCanvas;
import com.memoryclean.manager.ProgramManager;
import com.wust.memoryclean.R;

public class combinedImage {
	private MCanvas canvas;

	public combinedImage(Context context) {
		canvas = new MCanvas(context, R.drawable.clean_log_background);
	}

	public Bitmap combine(String[] names) {
		ImageSoftCache ic = ImageSoftCache.getInstance();
		int length = names.length;
		if (length > 4)
			length = 4;
		for (int i = 0; i < length; i++) {
			Bitmap bitmap = ic.get(names[i]);
			if (bitmap == null)
				bitmap = ProgramManager.getInstance().getBitmap(names[i]);
			if (bitmap != null && i <= length)
				canvas.drawBitmap(bitmap, length - 1, i);
		}
		return canvas.getMbitmap();
	}
}
