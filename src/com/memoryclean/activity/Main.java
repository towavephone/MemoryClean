package com.memoryclean.activity;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.memoryclean.adpter.FragementAdapter;
import com.memoryclean.fragment.LogListFragment;
import com.memoryclean.fragment.MemoryStatusFragment;
import com.memoryclean.fragment.RunningAppsFragment;
import com.memoryclean.images.Screen;
import com.memoryclean.manager.ApplicationManager;
import com.memoryclean.manager.RootManager;
import com.memoryclean.properties.SettingManager;
import com.memoryclean.service.MReceiver;
import com.memoryclean.tools.AutoClean;
import com.memoryclean.views.NormalView;
import com.memoryclean.views.XDialog;
import com.wust.memoryclean.R;

public class Main extends FragmentActivity {

	private Context context;
	private ViewPager viewPager;
	private ArrayList<Fragment> items;
	private TextView[] textView;
	private int ids[] = { R.id.textview1, R.id.textview2, R.id.textview3 };
	private int drawables[] = { R.drawable.viewpager_head1,
			R.drawable.viewpager_head2, R.drawable.viewpager_head3 };
	private int selecteds[] = { R.drawable.selected_head1,
			R.drawable.selected_head2, R.drawable.selected_head3 };
	private ShareActionProvider shareActionProvider;
	private int currentpager = 0;
	private XDialog dialog;
	private long exitTime;

	private MReceiver receiver = new MReceiver();

	private SettingManager sm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = getApplicationContext();
		ApplicationManager.getAll(context);
		sm = new SettingManager(context);
		startservice();
		if (RootManager.getInstance().getRoot() != 1)
			Toast.makeText(getApplicationContext(),
					"本应用未获取root权限，部分应用可能无法停止!!!", Toast.LENGTH_LONG).show();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(receiver, filter);
		Screen.getDisplay(context);
		InitTextView();
		InitViewPager();
	}

	private void startservice() {
		if (sm.getBoolean("shownotification"))
			AutoClean.getInstance().startSelfService(context);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void InitViewPager() {
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		items = new ArrayList<Fragment>();
		RunningAppsFragment RunningApps = new RunningAppsFragment();
		MemoryStatusFragment memorystatus = new MemoryStatusFragment();
		LogListFragment logs = new LogListFragment();
		items.add(memorystatus);
		items.add(RunningApps);
		items.add(logs);
		viewPager.setAdapter(new FragementAdapter(getSupportFragmentManager(),
				items));
		viewPager.setOffscreenPageLimit(3);
		Intent intent = getIntent();
		if (intent != null)
			currentpager = intent.getIntExtra("currentpager", 0);
		setTextViewBg(textView[currentpager]);
		viewPager.setCurrentItem(currentpager);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				currentpager = arg0;
				setTextViewBg(textView[arg0]);
				items.get(arg0).onStart();
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
		});
	}

	private void InitTextView() {
		textView = new TextView[ids.length];
		for (int i = 0; i < ids.length; i++) {
			textView[i] = (TextView) findViewById(ids[i]);
			textView[i].setOnClickListener(new TextViewListener(i));
		}
		setTextViewBg(textView[0]);
	}

	public class TextViewListener implements OnClickListener {
		private int index = 0;

		public TextViewListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			viewPager.setCurrentItem(index);
			currentpager = index;
			setTextViewBg(v);
		}
	}

	private void setTextViewBg(View v) {
		for (int i = 0; i < textView.length; i++) {
			textView[i].setBackgroundResource(drawables[i]);
			if (v.equals(textView[i]))
				v.setBackgroundResource(selecteds[i]);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		MenuItem shareItem = menu.findItem(R.id.action_share);
		shareActionProvider = (ShareActionProvider) shareItem
				.getActionProvider();
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		shareIntent.putExtra(Intent.EXTRA_TEXT,
				"来自MemoryClean for Android  :我已成功清理100M的内存,羡慕我吧 详情请点击");
		shareActionProvider.setShareIntent(shareIntent);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.action_settings:
			intent.setClass(Main.this, Global_Config.class);
			startActivity(intent);
			break;
		// case R.id.action_about_us:
		// intent.setClass(Main.this, About_us.class);
		// startActivity(intent);
		// break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			int exitstate = sm.getInt("exit");
			if (exitstate == 0) {
				dialog = new XDialog(Main.this);
				NormalView nv = new NormalView(context, true, true);
				nv.setTitle("确定");
				nv.setMessage("您确定要退出？？");
				nv.setListener(new listener(nv.getcheckbox()));
				dialog.setVew(nv);
				dialog.show();
			} else {
				if (exitTime == 0
						|| (System.currentTimeMillis() - exitTime) > 2000) {
					Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
					exitTime = System.currentTimeMillis();
				} else {
					if (exitstate == 1) {
						runbackground();
					} else {
						exit();
					}
				}

			}
		}
		return false;
	}

	private void runbackground() {
		PackageManager pm = context.getPackageManager();
		ResolveInfo homeInfo = pm.resolveActivity(
				new Intent(Intent.ACTION_MAIN)
						.addCategory(Intent.CATEGORY_HOME), 0);
		ActivityInfo ai = homeInfo.activityInfo;
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setComponent(new ComponentName(ai.packageName, ai.name));
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	private void exit() {
		unregisterReceiver(receiver);
		AutoClean.getInstance().killself(context);
	}

	private class listener implements OnClickListener {
		private CheckBox cb1, cb2;

		public listener(CheckBox[] cb) {
			cb1 = cb[0];
			cb2 = cb[1];
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.dialog_sure:
				int flag = 0;
				if (cb1.isChecked()) {
					if (cb2.isChecked()) {
						flag = 1;
					} else {
						flag = 0;
					}
					sm.saveInt("exit", flag);
					exit();
				} else {
					if (cb2.isChecked()) {
						flag = -1;
					} else {
						flag = 0;
					}
					sm.saveInt("exit", flag);
					runbackground();
				}
				dialog.dismiss();
				break;

			case R.id.dialog_cancel:
				dialog.dismiss();
				break;
			}

		}

	}

}
