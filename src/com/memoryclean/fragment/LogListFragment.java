package com.memoryclean.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.memoryclean.adpter.ListViewAdapter;
import com.memoryclean.properties.LogFile;
import com.memoryclean.properties.MLog;
import com.memoryclean.tools.AutoClean;
import com.memoryclean.tools.GetLogTotal;
import com.memoryclean.tools.ThreadPool;
import com.memoryclean.views.AnimationTranslate;
import com.memoryclean.views.NormalView;
import com.memoryclean.views.XDialog;
import com.memoryclean.views.XListView;
import com.wust.memoryclean.R;

@SuppressLint("InflateParams")
public class LogListFragment extends Fragment implements
		XListView.IXListViewListener, OnClickListener {
	private Button clear_log;
	private XListView loglist;
	private ArrayList<Map<String, Object>> logs;
	private View view;
	private TextView textView;
	private XDialog dialog;
	private Context context;
	private ListViewAdapter adapter;
	private int state;

	private static final int NORMAL_STATE = 101;
	private static final int REFRESH_STATE = 102;

	private final static int GETLOGSSUCCESS = 0;
	private final static int GETLOGSERROR = 1;
	
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GETLOGSERROR:
				Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT)
						.show();
				loglist.stopRefresh();
				break;
			case GETLOGSSUCCESS:
				updateViews((ArrayList<MLog>) msg.obj);
				break;
			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		view = inflater.inflate(R.layout.loglists, null);
		Init();
		addListener();
		return view;

	}

	private void Init() {
		clear_log = (Button) view.findViewById(R.id.clear_log);
		textView = (TextView) view.findViewById(R.id.hint_clean_total);
		loglist = (XListView) view.findViewById(R.id.logs);
		loglist.setPullRefreshEnable(true, true);
		loglist.setXListViewListener(this);
		logs = new ArrayList<Map<String, Object>>();
		adapter = new ListViewAdapter(context, logs, loglist);
		adapter.setLogs(true);
		loglist.setAdapter(adapter);
		loglist.setTyzRefreshed();
		adapter.setVisibleTextViewCleanTime(true);
		adapter.setVisibleTextViewMemory(true);
		loglist.setLayoutAnimation(AnimationTranslate.translate_item(context));
	}

	private void addListener() {
		switch (state) {
		case REFRESH_STATE:
			setButtonSate(false);
			loglist.setOnItemClickListener(null);
			break;
		case NORMAL_STATE:
			loglist.setPullRefreshEnable(true, true);
			adapter.notifyDataSetChanged();
			setButtonSate(!(logs == null || logs.size() == 0));
			loglist.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					Map<String, Object> map = logs.get(position - 1);

					NormalView m = new NormalView(context, false, false);

					String Message = map.get("application_name") + "\n";
					String typeString[] = map.get("application_memory")
							.toString().split("\t");
					m.setTitle("清理详情");
					Message += "清理类型:" + typeString[0] + "\n";
					Message += "清理应用:" + typeString[1] + "\n";
					Message += "清理时间:"
							+ map.get("clean_time").toString()
									.replace("\n", " ");
					m.setMessage(Message);
					dialog = new XDialog(context);
					dialog.setVew(m);
					dialog.show();
					map = null;
					m = null;
					Message = null;
				}
			});
			break;
		default:
			break;
		}

	}

	@Override
	public void onClick(View v) {
		NormalView m = new NormalView(context, false, true);
		m.setTitle("注意");
		m.setMessage("您确定要删除所有日志吗？？");
		m.setListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.dialog_sure:
					LogFile.getInstance().delete(context);
					logs.clear();
					textView.setText(String.format("本日释放:0.00 M,全部释放:0.00 M"));
					adapter.notifyDataSetChanged();
					setButtonSate(false);
					break;
				case R.id.dialog_cancel:
					break;
				}
				dialog.dismiss();
			}
		});
		dialog = new XDialog(context);
		dialog.setVew(m);
		dialog.show();
	}

	private void setButtonSate(boolean flag) {
		if (!flag) {
			clear_log.setTextColor(Color.GRAY);
			clear_log.setBackgroundResource(R.drawable.button_unclicked);
			clear_log.setOnClickListener(null);
		} else {
			clear_log.setTextColor(Color.WHITE);
			clear_log.setBackgroundResource(R.drawable.purplebuttonstyle);
			clear_log.setOnClickListener(this);
		}
	}

	/**
	 * 更新Log列表
	 * 
	 * @param list
	 */
	private void updateViews(ArrayList<MLog> list) {
		logs.clear();
		if (list != null && list.size() != 0) {
			/* 根据时间倒序 */
			Comparator<MLog> comp = new Comparator<MLog>() {
				@Override
				public int compare(MLog lhs, MLog rhs) {
					long l = lhs.getTime().getTime();
					long r = rhs.getTime().getTime();
					if (l == r)
						return 0;
					else {
						long lr = l - r;
						return lr > 0 ? -1 : 1;
					}
				}
			};
			Collections.sort(list, comp);
			List<String> namelist = new ArrayList<String>();
			for (MLog ml : list)
				namelist.addAll(Arrays.asList(ml.getNames()));
			HashMap<String, Object> map;
			Date d;
			String[] names;
			StringBuffer nameString;
			String[] dateString;
			for (MLog ml : list) {
				map = new HashMap<String, Object>();
				d = ml.getTime();
				names = ml.getNames();
				nameString = new StringBuffer(ml.getAction() + "\t");
				for (String s : names) {
					nameString.append(s + ',');
				}
				nameString.delete(nameString.length() - 1, nameString.length());
				dateString = MLog.DateToString(d).split(" ");
				map.put("application_name", "释放内存:" + ml.getMemory() + "M");
				map.put("application_memory", nameString);
				map.put("application_ischecked", false);
				map.put("clean_time", dateString[0] + "\n" + dateString[1]);
				logs.add(map);
			}
			namelist = null;
			map = null;
			d = null;
			names = null;
			nameString = null;
			dateString = null;
		}
		String[] total = GetLogTotal.getLogTotal(context);
		textView.setText(String.format("本日释放:%s M,全部释放:%s M", total[0],
				total[1]));
		state = NORMAL_STATE;
		addListener();
		loglist.startLayoutAnimation();
		loglist.stopRefresh();
		total = null;
	}

	@Override
	public void onRefresh() {
		state = REFRESH_STATE;
		addListener();
		ThreadPool tp = ThreadPool.getInstance();
		loglist.setPullRefreshEnable(false, true);
		tp.AddThread(new Runnable() {
			Message message;

			@Override
			public void run() {
				message = Message.obtain();
				try {
					ArrayList<MLog> list = (ArrayList<MLog>) LogFile.getInstance().query(context);
					message.obj = list;
					message.what = GETLOGSSUCCESS;
					list = null;
					handler.sendMessage(message);
				} catch (Exception e) {
					message.obj = e.getClass().getSimpleName() + ":日志列表出现未知错误!";
					message.what = GETLOGSERROR;
					handler.sendMessage(message);
				}
			}
		});
		tp = null;
	}

	@Override
	public void onStart() {
		Date LastCleanTime = AutoClean.getInstance().getLastCleanTime();
		Date refreshTime = loglist.getRefreshTime();
		if (LastCleanTime != null && refreshTime != null
				&& LastCleanTime.getTime() > refreshTime.getTime())
			loglist.setTyzRefreshed();
		super.onStart();
	}
}
