package com.memoryclean.classes;

import com.memoryclean.images.ImageProcessing;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

/**
 * 自定义画板类
 *  处理图片 	实现多个图片合成一张图片
 * 
 * @author tianwailaike
 *
 */
public class MCanvas {
	/* 图片大小---相对于背景的 */
	private final static double lenth[] = new double[] { 1, 0.75, 0.5, 0.5 };
	/* 图片左上角位置---相对于背景的 */
	public final static MPoint pl[][] = new MPoint[][] {
			{ new MPoint() },
			{ new MPoint(), new MPoint(0.25, 0.25) },
			{ new MPoint(0.25, 0.0), new MPoint(0, 0.5), new MPoint(0.5, 0.5) },
			{ new MPoint(), new MPoint(0.5, 0), new MPoint(0, 0.5),
					new MPoint(0.5, 0.5) } };
	private int W, H;// 图片的宽度高度
	private Paint paint;
	private Canvas canvas;
	private Bitmap Mbitmap;
	private double kl = 3;

	private Resources rs;

	public MCanvas(Context context, int id) {
		/* 获取背景图片并把其转换为相应大小的Bitmap */
		rs = context.getResources();
		Bitmap bitmap = BitmapFactory.decodeResource(rs, id);
		W = bitmap.getWidth();
		H = bitmap.getHeight();
		Mbitmap = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888);

		/* 初始化画板 */
		canvas = new Canvas(Mbitmap);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setDither(true);
		paint.setFilterBitmap(true);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		bitmap = null;
	}

	/**
	 * 将图片画在画板上
	 * 
	 * @param drawable
	 * @param l
	 *            图片总数 1~4
	 * @param k
	 *            第几个图片
	 */
	public void drawBitmap(Bitmap bitmap, int l, int k) {
		MPoint point = pl[l][k];
		double length = lenth[l];
		int width = (int) (W * length - 2 * kl);
		int high = (int) (H * length - 2 * kl);
		Bitmap bitmap1 = ImageProcessing.zoomBitmap(bitmap, width, high);
		float left = (float) (W * point.x + kl);
		float top = (float) (H * point.y + kl);
		point = null;
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));// 设置图片的显示模式
		canvas.drawBitmap(bitmap1, left, top, paint);
		bitmap1 = null;
	}

	public Bitmap getMbitmap() {
		canvas.save();
		return Mbitmap;
	}

	@Override
	protected void finalize() throws Throwable {
		destroy();
		super.finalize();
	}

	private void destroy() {
		paint = null;
		canvas = null;
		Mbitmap = null;

	}
}
