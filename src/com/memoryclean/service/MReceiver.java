package com.memoryclean.service;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.memoryclean.properties.SettingManager;
import com.memoryclean.tools.AutoClean;

/**
 * 系统广播接收 --开机广播 关屏广播(动态注册)
 * 
 * @author tianwailaike
 *
 */
public class MReceiver extends BroadcastReceiver {
	// private AutoClean ac = AutoClean.getInstance();
	// private boolean isServiceRunning = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		AutoClean ac = AutoClean.getInstance();
		String action = intent.getAction();
		SettingManager sm = new SettingManager(context);
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)
				&& sm.getBoolean("autostart")) {
			try {
				/* 启动后将应用调到后台 */
				PackageManager pm = context.getPackageManager();
				ResolveInfo homeInfo = pm.resolveActivity(new Intent(
						Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),
						0);
				ActivityInfo ai = homeInfo.activityInfo;
				Intent intent1 = new Intent(Intent.ACTION_MAIN);
				intent1.addCategory(Intent.CATEGORY_LAUNCHER);
				intent1.setComponent(new ComponentName(ai.packageName, ai.name));
				intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent1);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		} else if (action.equals(Intent.ACTION_SCREEN_OFF)
				&& sm.getBoolean("autoclean_after_shutdown_screan")
				&& ac.cankillNow(context)) {
			ac.cleanAfterShutdownScrean(context);
		}

		// } else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
		// ApplicationManager.runServiceList.clear();
		// for (RunningServiceInfo rs :
		// ApplicationManager.getRunServiceList()) {
		// if (rs.process.equals("com.example.Service.AutocleanService"))
		// isServiceRunning = true;
		// }
		// }
		// if (!isServiceRunning) {
		// Intent intent1 = new Intent(context, AutoCleanService.class);
		// context.startService(intent1);

	}
}
