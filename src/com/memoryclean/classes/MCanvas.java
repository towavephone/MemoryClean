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
 * �Զ��廭����
 *  ����ͼƬ 	ʵ�ֶ��ͼƬ�ϳ�һ��ͼƬ
 * 
 * @author tianwailaike
 *
 */
public class MCanvas {
	/* ͼƬ��С---����ڱ����� */
	private final static double lenth[] = new double[] { 1, 0.75, 0.5, 0.5 };
	/* ͼƬ���Ͻ�λ��---����ڱ����� */
	public final static MPoint pl[][] = new MPoint[][] {
			{ new MPoint() },
			{ new MPoint(), new MPoint(0.25, 0.25) },
			{ new MPoint(0.25, 0.0), new MPoint(0, 0.5), new MPoint(0.5, 0.5) },
			{ new MPoint(), new MPoint(0.5, 0), new MPoint(0, 0.5),
					new MPoint(0.5, 0.5) } };
	private int W, H;// ͼƬ�Ŀ�ȸ߶�
	private Paint paint;
	private Canvas canvas;
	private Bitmap Mbitmap;
	private double kl = 3;

	private Resources rs;

	public MCanvas(Context context, int id) {
		/* ��ȡ����ͼƬ������ת��Ϊ��Ӧ��С��Bitmap */
		rs = context.getResources();
		Bitmap bitmap = BitmapFactory.decodeResource(rs, id);
		W = bitmap.getWidth();
		H = bitmap.getHeight();
		Mbitmap = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888);

		/* ��ʼ������ */
		canvas = new Canvas(Mbitmap);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setDither(true);
		paint.setFilterBitmap(true);
		canvas.drawBitmap(bitmap, 0, 0, paint);
		bitmap = null;
	}

	/**
	 * ��ͼƬ���ڻ�����
	 * 
	 * @param drawable
	 * @param l
	 *            ͼƬ���� 1~4
	 * @param k
	 *            �ڼ���ͼƬ
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
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));// ����ͼƬ����ʾģʽ
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
