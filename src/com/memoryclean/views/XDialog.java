package com.memoryclean.views;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.wust.memoryclean.R;

/**
 * 自定义dialog 与view配合 适配多种样式
 *
 */
public class XDialog {
	AlertDialog dialog;
	AlertDialog.Builder builder;

	Window window;

	public XDialog(Context context) {
		builder = new AlertDialog.Builder(context, R.style.MyDialog);
		// params = new LayoutParams();
		// params.width = (int) (Screen.screenWidth / 1.2);
		// params.height = (int) (Screen.screenHeight / 1.5);
	}

	public XDialog(Context context, int theme) {
		builder = new AlertDialog.Builder(context, theme);
		// params = new LayoutParams();
		// params.alpha = 0.8f; // 透明度
		// params.width = (int) (Screen.screenWidth / 1.2);
		// params.height = (int) (Screen.screenHeight / 1.5);
	}

	public void setVew(MView view) {
		builder.setView(view.getView());
	}
//
//	public void setSize(int w, int h) {
//		params.width = w;
//		params.height = h;
//		params.alpha = 0.8f;
//	}

	public Window getWindow() {
		if (window == null && dialog != null)
			window = dialog.getWindow();
		return window;
	}

	public void setGravity(int gravity) {
		window.setGravity(Gravity.CENTER);
	}

	public void show() {
		dialog = builder.create();
		getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.alpha = 0.8f;//设置透明度
		// params.x = 100; // 新位置X坐标
		// params.y = 100; // 新位置Y坐标
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		window.setAttributes(params);
	}

	public void dismiss() {
		dialog.dismiss();
	}
}
