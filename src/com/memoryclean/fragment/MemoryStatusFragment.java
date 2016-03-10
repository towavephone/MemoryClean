package com.memoryclean.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.memoryclean.classes.MPrograme;
import com.memoryclean.manager.MemoryUsedMessage;
import com.memoryclean.manager.ProgramManager;
import com.memoryclean.properties.MLog;
import com.memoryclean.tools.AutoClean;
import com.memoryclean.tools.ChartsTools;
import com.memoryclean.tools.LogWriter;
import com.memoryclean.tools.ThreadPool;
import com.wust.memoryclean.R;

public class MemoryStatusFragment extends Fragment {
	private View view;
	private TextView textView, textView2, textView3;
	private Button button;

	private Context context;

	private int count;
	private long sum, available;
	private float percent;

	private int status = IS_NORMAL;
	public static final int IS_CLEANING = 100;
	public static final int IS_NORMAL = 101;
	public static final int IS_CLEAN_FINISH = 102;

	private ChartsTools chartsTools;

	@SuppressLint("HandlerLeak")
	private Handler mh = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int msgId = msg.what;
			switch (msgId) {
			case IS_NORMAL:
				updateTextViews();
				chartsTools.updateChartsData(count++, percent);
				break;
			case IS_CLEAN_FINISH:
				AutoClean.getInstance().stopClean();
				button.setClickable(true);
				status = IS_CLEAN_FINISH;
				switchStatus();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.memory_status, null);
		context = getActivity();
		initViews();
		setTimeTask();
		addListener();
		if (getActivity().getIntent().getBooleanExtra("refresh", false)) {
			button.performClick();
		}
		return view;
	}

	public void addListener() {
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (AutoClean.getInstance().cankillNow(context))
					switchStatusByClick();
				else {
					Toast.makeText(context, "清理时间过短，请过一段时间在清理",
							Toast.LENGTH_LONG).show();
				}
			}
		});

	}

	private void switchStatusByClick() {
		if (status == IS_NORMAL) {
			status = IS_CLEANING;
			switchStatus();
		} else if (status == IS_CLEAN_FINISH) {
			status = IS_NORMAL;
		}
	}

	private void switchStatus() {
		switch (status) {
		case IS_CLEANING:
			button.setText("正在清理");
			chartsTools.startAnimation();
			button.setClickable(false);
			clearMemory();
			break;
		case IS_NORMAL:
			button.setText(percent + "%\n一键清理");// 已用内存
			break;
		case IS_CLEAN_FINISH:
			button.setText("清理完成");
			chartsTools.clearAnimation();
			status = IS_NORMAL;
			// Main a = (Main) getActivity();
			// a.getAdapter().reLoad();
			break;
		default:
			break;
		}
	}

	private void clearMemory() {
		ThreadPool.getInstance().AddThread(new Runnable() {

			@Override
			public void run() {
				AutoClean.getInstance().setLastCleanTime(new Date());
				ArrayList<MPrograme> list = ProgramManager.getInstance()
						.iskill(AutoClean.getInstance().onekeyclean(context));
				if (list.size() != 0)
					LogWriter.getInstance().writeLog(MLog.ONEKEYCLEAN, list,
							context);
				list = null;
				Message m = Message.obtain();
				m.what = IS_CLEAN_FINISH;
				mh.sendMessage(m);
			}
		});
	}

	public void updateTextViews() {
		textView.setText("已用内存:\n" + (sum - available) + "MB");
		textView2.setText("可用内存:\n" + available + "MB");
		textView3.setText("总内存:\n" + sum + "MB");
		if (status == IS_NORMAL) {
			switchStatus();
		}
	}

	private void setTimeTask() {
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				Message message = new Message();
				try {
					sum = MemoryUsedMessage.getTotalMemory();
					available = MemoryUsedMessage.getAvailMemory(context);
					percent = MemoryUsedMessage.getPercent(context);
					message.what = IS_NORMAL;
					mh.sendMessage(message);
				} catch (Exception e) {
					message.what = 3;
					mh.sendMessage(message);
				}
			}
		}, 0, 1000);
	}

	private void initViews() {
		count = 0;
		button = (Button) view.findViewById(R.id.clear_memory_button);
		textView = (TextView) view.findViewById(R.id.textview1);
		textView2 = (TextView) view.findViewById(R.id.textview2);
		textView3 = (TextView) view.findViewById(R.id.textview3);
		chartsTools = new ChartsTools(context, view);
	}
}
