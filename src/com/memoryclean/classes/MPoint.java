package com.memoryclean.classes;

/**
 * 该类用于记录图片左上角点的位置
 * 
 * @author tianwailaike
 *
 */
public class MPoint {
	public double x, y;

	// public int left, top;

	public MPoint() {
		x = y = 0;
		// left = top = 0;
	}

	public MPoint(double x, double y) {
		this.x = x;
		this.y = y;
		// left = top = 0;
	}

	// public mpoint(int left, int top) {
	// x = y = 0;
	// this.left = left;
	// this.top = top;
	// }
	//
	// public mpoint(double x, double y, int left, int top) {
	// this.x = x;
	// this.y = y;
	// this.left = left;
	// this.top = top;
	// }
}
