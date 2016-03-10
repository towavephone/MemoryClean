package com.memoryclean.tools;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.wust.memoryclean.R;
public class ChartsTools {
    private List<PointValue> mPointValues;
    private List<AxisValue> mAxisValues;
    private List<SliceValue> sliceValues;
    private List<Line> lines;
    private LineChartView mLineChartView;
    private PieChartView mpieChartView;
    final static int MAX_COUNT = 2 * 60;
    final static int REAL_MAX_COUNT = MAX_COUNT + MAX_COUNT / 15;
    private Animation animation;
    
    public ChartsTools(Context context,View view) {
	mLineChartView = (LineChartView) view.findViewById(R.id.linechartview);
	mpieChartView = (PieChartView) view.findViewById(R.id.piechartview);
	mPointValues = new ArrayList<PointValue>();
	mAxisValues = new ArrayList<AxisValue>();
	sliceValues = new ArrayList<SliceValue>();
	animation = AnimationUtils.loadAnimation(context, R.anim.my_rotate);
    }

    public void updateChartsData(int count, float percent) {
	Date date = new Date();
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
	String time = simpleDateFormat.format(date);

	sliceValues.clear();
	SliceValue sliceValue = new SliceValue(percent,
		Color.parseColor("#0000FF"));
	sliceValues.add(sliceValue);

	sliceValue = new SliceValue(100 - percent, Color.parseColor("#00FFFF"));
	sliceValues.add(sliceValue);
	PieChartData pieChartData = new PieChartData(sliceValues)
		.setHasCenterCircle(true);
	mpieChartView.setPieChartData(pieChartData);
	mpieChartView.setVisibility(View.VISIBLE);

	mPointValues.add(new PointValue(count, percent));
	mAxisValues.add(new AxisValue(count).setLabel(time));
	if (count > REAL_MAX_COUNT) {
	    mPointValues.remove(0);
	    mAxisValues.remove(0);
	}
	Line line = new Line(mPointValues)
		.setColor(Color.parseColor("#bf3399ff")).setCubic(true)
		.setFilled(true).setHasPoints(false);
	lines = new ArrayList<Line>();
	lines.add(line);
	LineChartData data = new LineChartData();
	data.setLines(lines);
	// 坐标轴
	Axis axisX = new Axis(mAxisValues).setTextColor(Color.WHITE)
		.setHasLines(true).setLineColor(Color.WHITE)
		.setMaxLabelChars(8); // X轴
	data.setAxisXBottom(axisX);
	Axis axisY = new Axis().setHasLines(true).setTextColor(Color.WHITE)
		.setLineColor(Color.WHITE).setMaxLabelChars(6); // Y轴
	// Float num[]=new Float[]{(float) 0,(float) 20,(float)
	// 40,(float) 60,(float) 80,(float) 100};
	//
	// axisY=Axis.generateAxisFromRange(0, 100, (float) 0.1);
	// axisY.setFormatter(new SimpleAxisValueFormatter(1));
	// 默认是3，只能看最后三个数字
	data.setAxisYLeft(axisY);

	// 设置行为属性，支持缩放、滑动以及平移
	mLineChartView.setInteractive(false);
	// mLineChartView.setZoomType(ZoomType.HORIZONTAL);
	// mLineChartView.setContainerScrollEnabled(true,
	// ContainerScrollType.HORIZONTAL);
	mLineChartView.setLineChartData(data);
	mLineChartView.setVisibility(View.VISIBLE);
    }
    public void startAnimation(){
	mpieChartView.startAnimation(animation);
    }
    public void clearAnimation(){
	mpieChartView.clearAnimation();
    }
}
