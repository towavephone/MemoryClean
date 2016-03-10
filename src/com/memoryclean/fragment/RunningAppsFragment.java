package com.memoryclean.fragment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.memoryclean.activity.WhiteListActivity;
import com.memoryclean.classes.MPrograme;
import com.memoryclean.manager.ApplicationManager;
import com.memoryclean.manager.ProgramManager;
import com.memoryclean.manager.StateMamager;
import com.memoryclean.properties.MLog;
import com.memoryclean.properties.SettingManager;
import com.memoryclean.properties.WhiteList;
import com.memoryclean.tools.AutoClean;
import com.memoryclean.tools.LogWriter;
import com.memoryclean.tools.ThreadPool;
import com.memoryclean.views.MenuView;
import com.memoryclean.views.NormalView;
import com.memoryclean.views.ViewHolder;
import com.memoryclean.views.XDialog;
import com.memoryclean.views.XListView;
import com.wust.memoryclean.R;

@SuppressLint("HandlerLeak")
public class RunningAppsFragment extends Fragment implements XListView.IXListViewListener, OnClickListener {
	private Context context;

	private ArrayList<Map<String, Object>> infoMapArr;
	private ArrayList<String> checked_items;

	private MPrograme self = null;
	private ProgramManager p;
	private boolean showsystemapp;
	private ViewHolder vh;
	private StateMamager sm;

	private XDialog dialog;

	private static boolean current_show_state;

	private final int ERROR = 0;
	private final int FINISH_REFRESH = 1;
	private final int FINISH_CLEAN = 2;
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case ERROR:
				Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT).show();
				vh.stoprefresh();
				break;
			case FINISH_REFRESH:
				updateView((List<MPrograme>) msg.obj);
				sm.setState(StateMamager.REFRESH_NORMAL);
				break;
			case FINISH_CLEAN:
				AutoClean.getInstance().stopClean();
				Toast.makeText(context, "清理完成", Toast.LENGTH_LONG).show();
				boolean flag = (Boolean) msg.obj;
				if (flag) {
					self = null;
					android.os.Process.killProcess(android.os.Process.myPid());
				}
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.running_apps_list, null);
		context = getActivity();
		infoMapArr = new ArrayList<Map<String, Object>>();
		p = ProgramManager.getInstance();
		vh = new ViewHolder(context, view, infoMapArr);
		vh.setButtonsListener(this);
		sm = new StateMamager(vh, context);
		vh.setListViewRefresh(this);
		vh.setAdapter();
		return view;
	}

	private void updateView(List<MPrograme> programes) {
		infoMapArr.clear();
		Map<String, Object> map;
		String name;
		double memory;
		for (MPrograme m : programes) {
			map = new HashMap<String, Object>();
			memory = KBtoMB(m.getMemory());
			name = m.getName();
			if (memory == 0) {
				continue;
			}
			map.put("application_memory", memory + "M");
			map.put("application_name", name);
			map.put("application_ischecked", false);
			map.put("clean_time", "0000");
			infoMapArr.add(map);
		}
		map = null;
		name = null;
		vh.setData(infoMapArr);
		System.gc();
	}

	@Override
	public void onRefresh() {
		sm.setState(StateMamager.NORMAL_REFRESH);
		ThreadPool.getInstance().AddThread(new Runnable() {
			Message message;
			List<MPrograme> programes;

			@Override
			public void run() {
				message = Message.obtain();
				try {
					ApplicationManager.clearRunProcessList();
					ApplicationManager.clearRunServiceList();
					ApplicationManager.clearUserInstalled_AppList();
					p.clearRunning_application();
					showsystemapp = new SettingManager(context).getBoolean("showsystemapp");
					current_show_state = showsystemapp;
					programes = p.getRuning_applications(showsystemapp);
					message.obj = programes;
					message.what = FINISH_REFRESH;
					handler.sendMessage(message);
				} catch (Exception e) {
					message.obj = e.getClass().getSimpleName() + ":应用清理列表有未知错误！";
					message.what = 0;
					handler.sendMessage(message);
				}
			}
		});
	}

	private double KBtoMB(int kb) {
		double mb = 0;
		float f = (float) (kb / 1024.0);
		BigDecimal b = new BigDecimal(f);
		mb = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return mb;
	}

	@Override
	public void onClick(View arg0) {
		checked_items = vh.getChecked_items();
		switch (arg0.getId()) {
		case R.id.btn_refresh:
			vh.refresh();
			break;
		case R.id.btn_whitelist:
			Intent intent = new Intent(getActivity(), WhiteListActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_edit:
			sm.setState(StateMamager.NORMAL_EDIT);
			break;
		case R.id.btn_clean:// 选择清理
			if (!AutoClean.getInstance().isCleanning()) {
				NormalView nv = new NormalView(context, false, true);
				nv.setTitle("注意");
				nv.setMessage("您确定要清理所选的应用吗？？");
				nv.setListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						switch (v.getId()) {
						case R.id.dialog_sure:
							kill();
							break;
						default:
							break;
						}
						dialog.dismiss();
					}
				});
				dialog = new XDialog(context);
				dialog.setVew(nv);
				dialog.show();
			} else {
				Toast.makeText(getActivity(), "清理时间间隔过短，请稍后再试！！", Toast.LENGTH_LONG).show();
			}

			break;
		case R.id.btn_ignore:
			WhiteList wl = new WhiteList(context);
			int sum = checked_items.size();
			int k = wl.isJoin(checked_items).size();
			wl.addAll(checked_items);
			if (k == 0)
				Toast.makeText(context, "往白名单中添加" + sum + "项.", Toast.LENGTH_LONG).show();
			else if (k == sum)
				Toast.makeText(context, "选中项已在白名单中", Toast.LENGTH_LONG).show();
			else
				Toast.makeText(context, "往白名单中添加" + (sum - k) + "项,已在白名单中有" + k + "项", Toast.LENGTH_LONG).show();
			break;
		case R.id.btn_more:
			MPrograme m = p.getMProgramByName(checked_items.get(0));
			MenuView mv = new MenuView(context, m);
			dialog = new XDialog(context);
			dialog.setVew(mv);
			dialog.show();
			Window window = dialog.getWindow();
			window.setGravity(Gravity.BOTTOM);
			window.setWindowAnimations(R.style.style_dialog);
			break;
		case R.id.btn_finish:
			vh.getChecked_items().clear();
			sm.setState(StateMamager.EDIT_NORMAL);
			break;
		default:
			break;
		}
	}

	private void kill() {
		ArrayList<MPrograme> list = new ArrayList<MPrograme>();
		Collections.reverse(checked_items);
		for (String name : checked_items) {
			MPrograme m = p.getMProgramByName(name);
			if (name.equals("MemoryClean")) {
				self = m;
			} else
				list.add(m);
		}
		for (int i = infoMapArr.size() - 1; i >= 0; i--) {
			Map<String, Object> map = infoMapArr.get(i);
			if (checked_items.contains(map.get("application_name"))) {
				infoMapArr.remove(map);
			}
		}
		vh.getChecked_items().clear();
		vh.setText();
		vh.getAdapter().notifyDataSetChanged();
		vh.setChosedSize(0);
		final ArrayList<MPrograme> list1 = list;
		ThreadPool.getInstance().AddThread(new Runnable() {

			@Override
			public void run() {
				ProgramManager pm = ProgramManager.getInstance();
				ArrayList<MPrograme> list = pm.isRunning(list1);
				if (list != null) {
					AutoClean.getInstance().ChoseClean(list);
				}
				ArrayList<MPrograme> list2 = pm.iskill(list);
				if (self != null) {
					list2.add(self);
				}
				if (list2.size() != 0)
					LogWriter.getInstance().writeLog(MLog.CLEANBYHAND, list2, context);
				Message msg = new Message();
				msg.what = FINISH_CLEAN;
				if (self != null)
					msg.obj = true;
				else {
					msg.obj = false;
				}
				handler.sendMessage(msg);
			}
		});
	}

	@Override
	public void onStop() {
		super.onStop();
		sm.setState(StateMamager.EDIT_NORMAL);
	}

	@Override
	public void onStart() {
		Date lastCleanTime = AutoClean.getInstance().getLastCleanTime();
		Date refreshTime = vh.getRefreshTime();
		boolean flag1 = false;
		boolean flag2 = lastCleanTime != null && refreshTime != null && lastCleanTime.getTime() > refreshTime.getTime();
		boolean flag3 = current_show_state != new SettingManager(context).getBoolean("showsystemapp");
		if (flag1 || flag2 || flag3) {
			vh.refresh();
			sm.setState(StateMamager.NORMAL_REFRESH);
		}
		super.onStart();
	}

}
