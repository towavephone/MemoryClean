package com.memoryclean.cache;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public interface ImageCache {
	public boolean put(String key, Bitmap bitmap);

	public boolean put(String key, Drawable drawable);

	public Bitmap get(String key);
}
