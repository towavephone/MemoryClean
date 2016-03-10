package com.memoryclean.tools;

import android.content.Context;
import android.graphics.Bitmap;

import com.memoryclean.cache.ImageCache;
import com.memoryclean.cache.ImageFileCache;
import com.memoryclean.cache.ImageSoftCache;

public class AsyncImageLoader {

	private ImageCache imageCache;
	private Context context;

	public AsyncImageLoader(Context context) {
		this.context = context;
	}

	private Bitmap getBitmap(boolean flag, String key) {
		if (flag) {
			if (imageCache != null
					|| !ImageFileCache.class.isInstance(imageCache))
				imageCache = new ImageFileCache(context);
		} else {
			imageCache = ImageSoftCache.getInstance();
		}
		return imageCache.get(key);
	}

	// public Bitmap loadDrawable(final String key, boolean flag,
	// final ImageCallback imageCallback) {
	// Bitmap bitmap = getBitmap(flag, key);
	// if (bitmap != null) {
	// return bitmap;
	// }
	// final Handler handler = new Handler() {
	// public void handleMessage(Message message) {
	// imageCallback.imageLoaded((Drawable) message.obj, key);
	// }
	// };
	// new Thread() {
	// @Override
	// public void run() {
	// Bitmap bitmap = loadImageFromUrl(key);
	// imageCache.put(key, bitmap);
	// Message message = handler.obtainMessage(0, bitmap);
	// handler.sendMessage(message);
	// }
	// }.start();
	// return null;
	// }

	public Bitmap loadDrawable(String key, boolean flag) {
		Bitmap bitmap = getBitmap(flag, key);
		if (bitmap != null) {
			return bitmap;
		}
		return null;
	}

	// public static Bitmap loadImageFromUrl(String url) {
	// URL m;
	// InputStream i = null;
	// try {
	// m = new URL(url);
	// i = (InputStream) m.getContent();
	// } catch (MalformedURLException e1) {
	// e1.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// Bitmap d = ImageProcessing.zoomDrawable(
	// Drawable.createFromStream(i, "src"), 0, 0);
	// return d;
	// }

	// public interface ImageCallback {
	// public void imageLoaded(Drawable imageDrawable, String imageUrl);
	// }
}
