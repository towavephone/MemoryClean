package com.memoryclean.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.memoryclean.adpter.ListViewAdapter;
import com.memoryclean.classes.MPrograme;
import com.memoryclean.manager.ApplicationManager;
import com.memoryclean.manager.ProgramManager;
import com.memoryclean.properties.WhiteList;
import com.memoryclean.tools.ThreadPool;
import com.memoryclean.views.AnimationTranslate;
import com.memoryclean.views.XListView;
import com.wust.memoryclean.R;

public class WhiteListActivity extends Activity implements
		XListView.IXListViewListener {
	private ArrayList<Map<String, Object>> infoMapArr;
	private ListViewAdapter adapter;
	private XListView listView;
	private Context context;
	WhiteList wl;
	SearchView searchView;
	String currentSearchTip;

	private final int FINISH = 1;
	private final int ERROR = 0;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ERROR:
				Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_SHORT)
						.show();
				break;
			case FINISH:
				infoMapArr.clear();
				ArrayList<MPrograme> mProgrames = (ArrayList<MPrograme>) msg.obj;
				for (MPrograme mPrograme : mProgrames) {
					Map<String, Object> map = new HashMap<String, Object>();
					// if (!wl.isJoin(mPrograme.getName())) {
					map.put("application_name", mPrograme.getName());
					map.put("application_memory", "0");
					map.put("application_ischecked",
							wl.isJoin(mPrograme.getName()));
					infoMapArr.add(map);
					// }
				}
				adapter.setInfoMapArr(infoMapArr);
				adapter.notifyDataSetChanged();
				listView.stopRefresh();
				listView.startLayoutAnimation();
				listView.setPullRefreshEnable(true, true);
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_white_list);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		context = getApplicationContext();
		init();
		addListener();
	}

	private void addListener() {
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				infoMapArr = adapter.getInfoMapArr();
				boolean ischecked = (Boolean) infoMapArr.get(arg2 - 1).get(
						"application_ischecked");
				infoMapArr.get(arg2 - 1).put("application_ischecked",
						!ischecked);
				String name = infoMapArr.get(arg2 - 1).get("application_name")
						.toString();
				if (ischecked) {
					wl.Remove(name);
				} else {
					wl.Add(name);
				}
				adapter.notifyDataSetChanged();
			}
		});
	}

	private void init() {
		wl =new WhiteList(context);// WhiteList.getInstance();
		listView = (XListView) findViewById(R.id.white_list_listview);
		listView.setPullRefreshEnable(true, true);
		listView.setXListViewListener(this);
		infoMapArr = new ArrayList<Map<String, Object>>();
		adapter = new ListViewAdapter(context, infoMapArr, listView);
		listView.setAdapter(adapter);
		listView.setTyzRefreshed();
		adapter.setLocked(true);
		listView.setTextFilterEnabled(true);
		listView.setLayoutAnimation(AnimationTranslate.translate_item(context));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.white_list_menu, menu);
		searchView = (SearchView) menu.findItem(R.id.action_search)
				.getActionView();
		searchView.setNextFocusForwardId(R.id.white_list_listview);// 这句话特别重要，没有这句话第一次点击listview时没效果
		searchView.setQueryHint("输入应用名称搜索");
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String arg0) {
				if (arg0 != null && arg0.length() > 0) {
					hideSoftInput();
				}
				return true;
			}

			@Override
			public boolean onQueryTextChange(String arg0) {
				if (arg0 != null && arg0.length() > 0) {
					listView.setFilterText(arg0);
					listView.setPullRefreshEnable(false, false);
				} else {
					listView.clearTextFilter();
					listView.setPullRefreshEnable(true, true);
				}
				adapter.getFilter().filter(arg0);
				return true;
			}
		});
		return true;
	}

	private void hideSoftInput() {
		InputMethodManager inputMethodManager;
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		if (inputMethodManager != null) {
			View v = this.getCurrentFocus();
			if (v == null) {
				return;
			}
			inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			searchView.clearFocus();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRefresh() {

		listView.setPullRefreshEnable(false, true);
		ThreadPool.getInstance().AddThread(new Runnable() {

			Message message = Message.obtain();

			@Override
			public void run() {
				try {
					//wl.loadConfig();
					ApplicationManager.clearInstalled_AppList();
					ApplicationManager.clearUserInstalled_AppList();
					ProgramManager programManager = ProgramManager
							.getInstance();
					ArrayList<MPrograme> mProgrames = programManager
							.getInstalledMProgram();
					message.obj = mProgrames;
					message.what = FINISH;
					handler.sendMessage(message);
				} catch (Exception e) {
					message.obj = e.getClass().getSimpleName() + ":未知错误！";
					message.what = ERROR;
					handler.sendMessage(message);
				}

			}
		});
	}
}
