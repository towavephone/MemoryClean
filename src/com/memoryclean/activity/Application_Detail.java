package com.memoryclean.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.memoryclean.adpter.ExpandableListViewAdapter;
import com.memoryclean.cache.ImageSoftCache;
import com.memoryclean.classes.MProcess;
import com.memoryclean.classes.MPrograme;
import com.memoryclean.manager.ProgramManager;
import com.memoryclean.properties.WhiteList;
import com.memoryclean.tools.AutoClean;
import com.memoryclean.views.NormalView;
import com.memoryclean.views.XDialog;
import com.wust.memoryclean.R;

@SuppressLint("UseSparseArrays")
public class Application_Detail extends Activity implements
		OnChildClickListener, View.OnClickListener {
	private ImageView imageView;
	private TextView name, pkgname, Uid;

	private ExpandableListView expandableListView;

	private Button button;
	private CheckBox cb;

	private WhiteList wl;

	private ArrayList<MProcess> data = new ArrayList<MProcess>();
	private MPrograme mp;
	private Bundle bundle;
	private AutoClean ac = AutoClean.getInstance();

	private Context context;

	private XDialog dialog;

	private boolean clickable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_detail);
		init();
		loadData();
		addListener();
	}

	private void loadData() {
		String appname = bundle.get("application_name").toString();
		imageView.setImageBitmap(ImageSoftCache.getInstance().get(appname));
		name.setText("应用名:" + appname);
		pkgname.setText("包名:" + mp.getPkgname());
		Uid.setText("UID:" + mp.getUid());

		data = mp.getProcesses();
		expandableListView.setGroupIndicator(null);
		expandableListView.setAdapter(new ExpandableListViewAdapter(
				getApplicationContext(), data,mp));
		for (int i = 0; i < expandableListView.getCount(); i++)
			expandableListView.expandGroup(i);
//		wl = WhiteList.getInstance();
//		wl.loadConfig();
		wl=new WhiteList(context);
		cb.setChecked(wl.isJoin(mp.getName()));
		button.setText("杀掉该程序(" + data.size() + "个进程)");
		if (mp.isSystemapp()) {
			button.setClickable(false);
			button.setBackgroundResource(R.drawable.button_unclicked);
			clickable = false;
		} else {
			clickable = true;
		}
	}

	private void addListener() {
		if (clickable) {
			// expandableListView.setOnChildClickListener(this);
			button.setOnClickListener(this);
		} else {
			// expandableListView.setOnChildClickListener(null);
			button.setOnClickListener(null);
		}

	}

	private void killprogram() {
		ac.killProgram(mp,context);
		Intent intent = new Intent();
		intent.setClass(context, Main.class);
		intent.putExtra("currentpager", 1);
		intent.putExtra("refresh", "refresh");
		startActivity(intent);
	}

	private void init() {
		context = getApplicationContext();

		bundle = getIntent().getExtras();
		mp = ProgramManager.getInstance().getMProgramByName(
				bundle.get("application_name").toString());

		cb = (CheckBox) findViewById(R.id.join_whiteList);

		imageView = (ImageView) findViewById(R.id.application_icon);
		name = (TextView) findViewById(R.id.application_name);
		pkgname = (TextView) findViewById(R.id.application_pkgname);
		Uid = (TextView) findViewById(R.id.application_uid);
		button = (Button) findViewById(R.id.kill_programe);

		expandableListView = (ExpandableListView) findViewById(R.id.application_expandablelistview);
	}

	@Override
	protected void onStop() {
		if (cb.isChecked())
			wl.Add(mp.getName());
		else
			wl.Remove(mp.getName());
		super.onStop();
	}

	@Override
	public boolean onChildClick(ExpandableListView arg0, View arg1,
			final int arg2, final int arg3, long arg4) {
		NormalView nv = new NormalView(context, false, true);
		nv.setTitle("提醒");
		nv.setMessage("单独结束进程会影响您的使用,确定要结束？\n(注:本次清理将不被记录到日志 )");
		dialog = new XDialog(Application_Detail.this);
		dialog.setVew(nv);
		nv.setListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				switch (v.getId()) {
				case R.id.dialog_sure:
					// ac.killservice(data.get(arg2).get(arg3), context);
					// ApplicationManager.runServiceList = null;
					// ApplicationManager.runProcessList = null;
					// getDatas();
					// for (int i = 0; i < expandableListView.getCount(); i++)
					// expandableListView.expandGroup(i);
					// expandableListView
					// .setAdapter(new ExpandableListViewAdapter(
					// getApplicationContext(), data));
					break;
				case R.id.dialog_cancel:
					break;
				}
				dialog.dismiss();
			}
		});
		dialog.show();
		return true;
	}

	@Override
	public void onClick(View arg0) {
		NormalView nv = new NormalView(context, false, true);
		nv.setTitle("提醒");
		dialog = new XDialog(Application_Detail.this);
		if (mp.getPkgname().equals(AutoClean.self)) {
			Toast.makeText(context, "请按返回键退出！", Toast.LENGTH_LONG).show();
			// nv.setMessage("您确定要退出本应用？？");
			// dialog.setVew(nv);
			// nv.setListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			//
			// switch (v.getId()) {
			// case R.id.dialog_sure:
			// LogWriter.getInstance().writeLog(MLog.CLEANBYHAND, mp);
			// // Intent intent = new Intent(Intent.ACTION_MAIN);
			// // intent.addCategory(Intent.CATEGORY_HOME);
			// // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// // startActivity(intent);
			// AutoClean.getInstance().killself(context);
			// // android.os.Process
			// // .killProcess(android.os.Process.myPid());
			// // context.stopService(new Intent(context,
			// // NotificationService.class));
			// // System.exit(0);
			// break;
			// case R.id.dialog_cancel:
			// break;
			// }
			// dialog.dismiss();
			// }
			// });
			// dialog.show();
		} else {
			if (ac.ignoreByPkgName(mp.getPkgname())) {
				nv.setMessage("结束该程序可能会对您的使用造成影响，您确定要结束？？");
				dialog.setVew(nv);
				nv.setListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						switch (v.getId()) {
						case R.id.dialog_sure:
							killprogram();
							break;
						case R.id.dialog_cancel:
							break;
						}
						dialog.dismiss();
					}
				});
				dialog.show();
			} else
				killprogram();
		}
		nv = null;
	}
}
