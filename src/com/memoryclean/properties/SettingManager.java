package com.memoryclean.properties;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SettingManager {
	private SharedPreferences sp;

	public SettingManager(Context context) {
		sp = context.getSharedPreferences("setting", 0);
		// 第一个参数是preferece的名称(比如：MyPref)
		// 第二个参数是打开的方式（一般选择private方式）
	}

	public GlobalConfig loadSetting() {
		GlobalConfig setting = new GlobalConfig();
		setting.setAutostart(sp.getBoolean("autostart", true));
		setting.setShowsystemapp(sp.getBoolean("showsystemapp", false));
		setting.setAutoclean_after_shutdown_screan(sp.getBoolean(
				"autoclean_after_shutdown_screan", false));
		setting.setIncludewhileList(sp.getBoolean("includewhileList", false));
		setting.setIncludesystemapp(sp.getBoolean("includesystemapp", false));
		setting.setShownotification(sp.getBoolean("shownotification", true));
		setting.setExit(sp.getInt("exit", 0));
		return setting;
	}

	public boolean getBoolean(String s) {
		return sp.getBoolean(s, false);
	}

	public int getInt(String s) {
		return sp.getInt(s, 0);
	}

	public Date getDate(String s) {
		return MLog.StringToDate(sp.getString(s,
				new Date().toString()));
	}

	// public void save(GlobalConfig newsetting, GlobalConfig oldsetting) {
	// if (!newsetting.equals(oldsetting)) {
	// save(newsetting);
	// }
	// }
	//
	// private void save(GlobalConfig newsetting) {
	// Editor editor = sp.edit();
	// editor.putBoolean("autostart", newsetting.isAutostart());
	// editor.putBoolean("showsystemapp", newsetting.isShowsystemapp());
	// editor.putBoolean("autoclean_after_shutdown_screan",
	// newsetting.isAutoclean_after_shutdown_screan());
	// editor.putBoolean("includewhileList", newsetting.isIncludewhileList());
	// editor.putBoolean("includesystemapp", newsetting.isIncludesystemapp());
	// editor.putBoolean("shownotification", newsetting.isShownotification());
	// editor.putInt("exit", newsetting.getExit());
	// editor.commit();
	// }

	public void saveBoolean(String name, boolean flag) {
		Editor editor = sp.edit();
		editor.putBoolean(name, flag);
		editor.commit();
	}

	public void saveInt(String name, int flag) {
		Editor editor = sp.edit();
		editor.putInt(name, flag);
		editor.commit();
	}

	public void saveString(String name, String string) {
		Editor editor = sp.edit();
		editor.putString(name,string);
		editor.commit();
	}
}
