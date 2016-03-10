package com.memoryclean.cache;

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.LruCache;

import com.memoryclean.images.ImageProcessing;
import com.memoryclean.images.Screen;
import com.memoryclean.manager.ProgramManager;

public class ImageSoftCache implements ImageCache {
	private static ImageSoftCache ic;

	// 软引用
	private static final int SOFT_CACHE_CAPACITY = 40;
	private final static LinkedHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache = new LinkedHashMap<String, SoftReference<Bitmap>>(
			SOFT_CACHE_CAPACITY, 0.75f, true) {

		private static final long serialVersionUID = 1L;

		@Override
		public SoftReference<Bitmap> put(String key, SoftReference<Bitmap> value) {
			return super.put(key, value);
		}

		protected boolean removeEldestEntry(
				Map.Entry<String, java.lang.ref.SoftReference<Bitmap>> eldest) {
			if (size() > SOFT_CACHE_CAPACITY) {
				Log.v("tag", "Soft Reference limit , purge one");
				return true;
			}
			return false;
		};
	};

	private final int hardCachedSize = 8 * 1024 * 1024;
	// hard cache
	private final LruCache<String, Bitmap> sHardBitmapCache = new LruCache<String, Bitmap>(
			hardCachedSize) {
		@Override
		public int sizeOf(String key, Bitmap value) {
			return value.getRowBytes() * value.getHeight();
		}

		@Override
		protected void entryRemoved(boolean evicted, String key,
				Bitmap oldValue, Bitmap newValue) {
			Log.v("tag", "hard cache is full , push to soft cache");
			// 硬引用缓存区满，将一个最不经常使用的oldvalue推入到软引用缓存区
			sSoftBitmapCache.put(key, new SoftReference<Bitmap>(oldValue));
		}
	};

	private ImageSoftCache() {
	}

	public synchronized static ImageSoftCache getInstance() {
		if (ic == null)
			ic = new ImageSoftCache();
		return ic;
	}

	// 缓存bitmap
	public boolean put(String key, Bitmap bitmap) {
		if (bitmap != null) {
			synchronized (sHardBitmapCache) {
				sHardBitmapCache.put(key, bitmap);
			}
			return true;
		}
		return false;
	}

	// 缓存bitmap
	public boolean put(String key, Drawable drawable) {
		if (drawable != null) {
			int[] size = Screen.getPictureSize();
			return put(key,
					ImageProcessing.zoomDrawable(drawable, size[0], size[1]));
		}
		return false;
	}

	// 从缓存中获取bitmap
	public Bitmap get(String key) {
		Bitmap bitmap = getBitmapFormSoft(key);
		if (bitmap == null) {
			bitmap = getBitmapFormSystem(key);
			if (bitmap == null)
				put(key, bitmap);
		}
		return bitmap;
	}

	private Bitmap getBitmapFormSoft(String key) {
		synchronized (sHardBitmapCache) {
			final Bitmap bitmap = sHardBitmapCache.get(key);
			if (bitmap != null)
				return bitmap;
		}
		// 硬引用缓存区间中读取失败，从软引用缓存区间读取
		synchronized (sSoftBitmapCache) {
			SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(key);
			if (bitmapReference != null) {
				final Bitmap bitmap2 = bitmapReference.get();
				if (bitmap2 != null)
					return bitmap2;
				else {
					Log.v("tag", "soft reference 已经被回收");
					sSoftBitmapCache.remove(key);
				}
			}
		}
		return null;
	}

	private Bitmap getBitmapFormSystem(String key) {
		return ProgramManager.getInstance().getBitmap(key);
	}

}
