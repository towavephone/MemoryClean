package com.memoryclean.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.memoryclean.manager.RootManager;
import com.memoryclean.properties.GlobalConfig;
import com.memoryclean.properties.SettingManager;
import com.memoryclean.tools.AutoClean;
import com.wust.memoryclean.R;

public class Global_Config extends Activity implements View.OnClickListener {
	private ToggleButton autostart, showsystemapp, root, shd, shownotification;
	private CheckBox cb3, cb4, cb5;
	private SettingManager sm;
	private TextView aboutus;
	private TextView showWhiteList;
	private View view1, view2, view3;

//	private NormalView nv;
//	private XDialog dialog;

	private boolean lastshowsystemapp;

	private int exit;
	//private int setExit = 0;

	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_global_config);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		Init();
		addListener();
	}

	@Override
	protected void onStart() {
		loadData();
		super.onStart();
	}

	private void Init() {
		context = Global_Config.this;
		View v1 = findViewById(R.id.normal_setting);
		autostart = (ToggleButton) v1.findViewById(R.id.autostart_switch);
		showsystemapp = (ToggleButton) v1.findViewById(R.id.showsystemapp);
		root = (ToggleButton) v1.findViewById(R.id.root_giving);
		shd = (ToggleButton) v1.findViewById(R.id.clean_after_shutdownscreen);
		shownotification = (ToggleButton) v1
				.findViewById(R.id.shownotification);
		View v2 = findViewById(R.id.onkeyclean_item);
		view1 = v2.findViewById(R.id.includewhitelist_view);
		cb3 = (CheckBox) v2.findViewById(R.id.includewhitelist);
		view2 = v2.findViewById(R.id.includesystemapp_view);
		cb4 = (CheckBox) v2.findViewById(R.id.includesystemapp);
		View v3 = findViewById(R.id.other_setting);
		view3 = v3.findViewById(R.id.remenberview);
		cb5 = (CheckBox) v3.findViewById(R.id.remenber);
		aboutus = (TextView) v3.findViewById(R.id.aboutus);
		showWhiteList = (TextView) v3.findViewById(R.id.showwhitelist);
		sm = new SettingManager(context);
	}

	private void loadData() {
		GlobalConfig gc = sm.loadSetting();
		autostart.setChecked(gc.isAutostart());
		lastshowsystemapp = gc.isShowsystemapp();
		showsystemapp.setChecked(lastshowsystemapp);
		shd.setChecked(gc.isAutoclean_after_shutdown_screan());
		shownotification.setChecked(gc.isShownotification());
		switch (RootManager.getInstance().getRoot()) {
		case -1:
			root.setBackgroundColor(Color.GRAY);
			root.setClickable(false);
			break;
		case 0:
			root.setClickable(true);
			root.setChecked(false);
			break;
		case 1:
			root.setClickable(true);
			root.setChecked(true);
			break;
		}
		cb3.setChecked(gc.isIncludewhileList());
		cb4.setChecked(gc.isIncludesystemapp());
		exit = gc.getExit();
		cb5.setChecked(exit == 0 ? false : true);
	}

	private void addListener() {
		autostart.setOnCheckedChangeListener(new CheckedListener(autostart));
		showsystemapp.setOnCheckedChangeListener(new CheckedListener(
				showsystemapp));
		shd.setOnCheckedChangeListener(new CheckedListener(shd));
		shownotification.setOnCheckedChangeListener(new CheckedListener(
				shownotification));
		cb3.setOnCheckedChangeListener(new CheckedListener(cb3));
		cb4.setOnCheckedChangeListener(new CheckedListener(cb4));
		cb5.setOnCheckedChangeListener(new CheckedListener(cb5));
		view1.setOnClickListener(this);
		view2.setOnClickListener(this);
		view3.setOnClickListener(this);
		aboutus.setOnClickListener(this);
		showWhiteList.setOnClickListener(this);
		root.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (arg1) {
					int flag = RootManager.getInstance().getRoot();
					if (flag == -1) {
						root.setClickable(false);
						root.setBackgroundResource(R.drawable.switch_unchecked);
						Toast.makeText(context, "没有root权限", Toast.LENGTH_LONG)
								.show();
					} else if (flag == 0) {
						root.setChecked(false);
						Toast.makeText(context,
								"授权失败，可能是您在权限管理软件中禁止了本软件的root权限",
								Toast.LENGTH_LONG).show();
					} else {
						root.setChecked(true);
						Toast.makeText(context, "授权成功", Toast.LENGTH_LONG)
								.show();
					}
				} else {
					Toast.makeText(context, "请在权限管理软件中关闭root授权！！",
							Toast.LENGTH_LONG).show();
				}
			}
		});
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

	private class CheckedListener implements OnCheckedChangeListener {
		private View v;

		public CheckedListener(View v) {
			this.v = v;
		}

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			switch (v.getId()) {
			case R.id.autostart_switch:
				sm.saveBoolean("autostart", arg1);
				break;
			case R.id.showsystemapp:
				sm.saveBoolean("showsystemapp", arg1);
				break;
			case R.id.clean_after_shutdownscreen:
				sm.saveBoolean("autoclean_after_shutdown_screan", arg1);
				break;
			case R.id.shownotification:
				sm.saveBoolean("shownotification", arg1);
				AutoClean ac = AutoClean.getInstance();
				if (arg1) {
					ac.startSelfService(context);
				} else {
					ac.killSelfService(context);
				}
				break;
			case R.id.includesystemapp:
				sm.saveBoolean("includesystemapp", arg1);
				break;
			case R.id.includewhitelist:
				sm.saveBoolean("includewhileList", arg1);
				break;
			case R.id.remenber:
				// if (arg1)
				// selectExitedItem();
				// sm.saveInt("exit", setExit);
				sm.saveInt("exit", arg1 ? 1 : 0);
				break;
			default:
				break;
			}
		}
	}

//	private void selectExitedItem() {
//		nv = new NormalView(context, true, true);
//		nv.setTitle("提示");
//		nv.setMessage("选择退出类型:");
//		nv.setBothSelected(false);
//		nv.setListener(this);
//		nv.setCheckBoxMessage("完全关闭", "后台运行");
//		dialog = new XDialog(context);
//		dialog.setVew(nv);
//		dialog.show();
//	}

	@Override
	public void onClick(View arg0) {
		Intent intent;
		switch (arg0.getId()) {
		case R.id.showwhitelist:
			intent = new Intent(Global_Config.this, WhiteListActivity.class);
			startActivity(intent);
			break;
		case R.id.aboutus:
			intent = new Intent(Global_Config.this, About_us.class);
			startActivity(intent);
			break;
		case R.id.includewhitelist_view:
			cb3.setChecked(!cb3.isChecked());
			break;
		case R.id.includesystemapp_view:
			cb4.setChecked(!cb4.isChecked());
			break;
		case R.id.remenberview:
			cb5.setChecked(!cb5.isChecked());
			break;
		// case R.id.dialog_sure:
		// CheckBox[] cbs = nv.getcheckbox();
		// if (cbs[0].isChecked())
		// setExit = 1;
		// if (cbs[1].isChecked())
		// setExit = -1;
		// if(setExit==0)
		// cb5.setChecked(false);
		// dialog.dismiss();
		// break;
		// case R.id.dialog_cancel:
		// cb5.setChecked(false);
		// setExit=0;
		// dialog.dismiss();
		// break;
		}
	}

}
