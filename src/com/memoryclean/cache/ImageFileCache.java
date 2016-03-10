package com.memoryclean.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.memoryclean.images.ImageProcessing;
import com.memoryclean.images.Screen;
import com.memoryclean.images.combinedImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;

@SuppressWarnings("deprecation")
public class ImageFileCache implements ImageCache {
	private static final int MAX_CACHE_SIZE = 20 * 1024 * 1024; // 20M
	private File mCacheDir;
	private Context context;

	public ImageFileCache(Context context) {
		this.context = context;
		mCacheDir = new File(context.getCacheDir(), "ImageCache");
		if (!mCacheDir.exists())
			mCacheDir.mkdirs();
	}

	private final LruCache<String, Long> sFileCache = new LruCache<String, Long>(MAX_CACHE_SIZE) {
		@Override
		public int sizeOf(String key, Long value) {
			return value.intValue();
		}

		@Override
		protected void entryRemoved(boolean evicted, String key, Long oldValue, Long newValue) {
			File file = new File(mCacheDir, key);
			if (file.exists())
				file.delete();

		}
	};

	// 缓存bitmap到外部存储
	public boolean put(String key, Bitmap bitmap) {
		File file;
		try {
			file = new File(mCacheDir, key);
			if (file.exists()) {
				return true;
			}
			file.createNewFile();
			FileOutputStream fos = getOutputStream(key);
			boolean saved = bitmap.compress(CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
			if (saved) {
				synchronized (sFileCache) {
					sFileCache.put(key, file.length());
				}
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	// 根据key获取OutputStream
	private FileOutputStream getOutputStream(String key) {
		if (mCacheDir == null)
			return null;
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(mCacheDir.getAbsolutePath() + File.separator + key);
			return fos;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

	}

	// 获取bitmap
	private static BitmapFactory.Options sBitmapOptions;

	static {
		sBitmapOptions = new BitmapFactory.Options();
		sBitmapOptions.inPurgeable = true; // bitmap can be purged to disk
	}

	public Bitmap get(String key) {
		Bitmap bitmap = getBitmapFormSoft(key);
		if (bitmap == null) {
			bitmap = getCombinedImage(key);
			if (bitmap != null) {
				put(key, bitmap);
			}
		}
		return bitmap;
	}

	private Bitmap getCombinedImage(String key) {
		String ss[] = key.split(",");
		return new combinedImage(context).combine(ss);
	}

	private Bitmap getBitmapFormSoft(String key) {
		File bitmapFile = new File(mCacheDir, key);
		Bitmap bitmap = null;
		if (bitmapFile.exists()) {
			try {
				bitmap = BitmapFactory.decodeStream(new FileInputStream(bitmapFile), null, sBitmapOptions);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
		return bitmap;
	}

	@Override
	public boolean put(String key, Drawable drawable) {
		if (drawable != null) {
			int[] size = Screen.getPictureSize();
			return put(key, ImageProcessing.zoomDrawable(drawable, size[0], size[1]));
		}
		return false;
	}
}
