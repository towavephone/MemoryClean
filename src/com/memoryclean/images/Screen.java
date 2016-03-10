package com.memoryclean.images;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

public class Screen {
	public static int screenWidth;
	public static int screenHeight;
	public static Display dm;

	/**
	 * ��ȡ��Ļ�ĸ߿� ���
	 * 
	 * @param context
	 */
	@SuppressWarnings("deprecation")
	public static void getDisplay(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		dm = wm.getDefaultDisplay();
		screenHeight = dm.getHeight();
		screenWidth = dm.getWidth();
	}

	/**
	 * ͨ����Ļ��С����ͼƬ�Ĵ�С
	 * 
	 * @return
	 */
	public static int[] getPictureSize() {
		int[] size = new int[2];
		size[0] = screenWidth / 8;
		size[1] = screenHeight / 13;
		return size;
	}
}
