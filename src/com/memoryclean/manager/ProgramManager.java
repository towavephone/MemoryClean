package com.memoryclean.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.memoryclean.cache.ImageSoftCache;
import com.memoryclean.classes.MPrograme;
import com.memoryclean.images.ImageProcessing;

/**
 * 使用单例模式 管理应用的一些函数
 * 
 * @author tianwailaike
 *
 */
public class ProgramManager {
	private List<MPrograme> runing_Program = new ArrayList<MPrograme>();
	private Map<String, List<String>> pkgProcessAppMap = new HashMap<String, List<String>>();
	private ImageSoftCache ic;

	private ReentrantLock lock = new ReentrantLock();
	private static ProgramManager p = new ProgramManager();

	private ProgramManager() {
	}

	public synchronized static ProgramManager getInstance() {
		return p;
	}

	private void getPkgProcessAppMap() {
		String[] pkgNameList;
		String pkgName;
		List<String> appnamelist;
		for (RunningAppProcessInfo ra : ApplicationManager.getRunProcessList()) {
			pkgNameList = ra.pkgList;// 获得运行在该进程里的所有应用程序包
			for (int i = 0; i < pkgNameList.length; i++) {
				pkgName = pkgNameList[i];
				if (!pkgProcessAppMap.containsKey(pkgName)) {
					appnamelist = new ArrayList<String>();
				} else {
					appnamelist = pkgProcessAppMap.get(pkgName);
				}
				appnamelist.add(ra.processName);
				pkgProcessAppMap.put(pkgName, appnamelist);
			}
		}
		pkgNameList = null;
		pkgName = null;
		appnamelist = null;
	}

	/**
	 * 返回正在运行的应用
	 * 
	 * @return
	 */
	public List<MPrograme> getRuning_applications(boolean flag) {
		if (runing_Program.size() == 0) {
			lock.lock();
			if (runing_Program.size() == 0) {
				pkgProcessAppMap.clear();
				getPkgProcessAppMap();
				runing_Program = getAppInfo(flag);
			}
			lock.unlock();
		}
		return runing_Program;
	}

	public List<MPrograme> getRuning_applications(boolean flag1, boolean flag2) {
		return getAppInfo(flag2);

	}

	/**
	 * 通过包名判断应用是否怎在运行
	 * 
	 * @param pkgName
	 * @return
	 */
	public boolean isRunning(String pkgName) {
		ApplicationManager.clearRunProcessList();
		String[] pkgNameList;
		for (ActivityManager.RunningAppProcessInfo ra : ApplicationManager
				.getRunProcessList()) {
			pkgNameList = ra.pkgList;
			for (String s : pkgNameList)
				if (s.equals(pkgName))
					return true;
		}
		pkgNameList = null;
		return false;
	}

	/**
	 * 判断多个应用是否在运行 返回正在运行应用的列表
	 * 
	 * @param list
	 * @return
	 */
	public ArrayList<MPrograme> isRunning(List<MPrograme> list) {
		ArrayList<String> lists = new ArrayList<String>();
		ArrayList<MPrograme> mlist = new ArrayList<MPrograme>();
		String[] pkgNameList;
		ApplicationManager.clearRunProcessList();
		for (ActivityManager.RunningAppProcessInfo ra : ApplicationManager
				.getRunProcessList()) {
			pkgNameList = ra.pkgList;
			lists.addAll(Arrays.asList(pkgNameList));
		}
		for (MPrograme m : list) {
			if (lists.contains(m.getPkgname())) {
				mlist.add(m);
			}
		}
		lists = null;
		pkgNameList = null;
		return mlist;
	}

	public boolean iskilled(String pkgName) {
		return !isRunning(pkgName);
	}

	public ArrayList<MPrograme> iskill(List<MPrograme> list) {
		ArrayList<MPrograme> mlist = (ArrayList<MPrograme>) list;
		mlist.removeAll(isRunning(list));
		return mlist;
	}

	public void clearRunning_application() {
		runing_Program.clear();
	}

	private List<MPrograme> getAppInfo(boolean flag) {
		List<ApplicationInfo> list = null;
		List<MPrograme> mlist = null;
		if (flag)
			list = ApplicationManager.getappList();
		else
			list = ApplicationManager.getInstalled_appList();
		synchronized (list) {
			Iterator<ApplicationInfo> it = list.iterator();
			MPrograme m = null;
			mlist = new ArrayList<MPrograme>();
			while (it.hasNext()) {
				ApplicationInfo appinfo = it.next();
				if (pkgProcessAppMap.containsKey(appinfo.packageName)) {
					m = getMPrograme(appinfo,
							pkgProcessAppMap.get(appinfo.packageName));
					mlist.add(m);
				}
			}
			m = null;
			it = null;
		}
		list = null;
		return mlist;
	}

	private MPrograme getMPrograme(ApplicationInfo appinfo,
			List<String> runappinfos) {
		MPrograme m = new MPrograme();
		m.setName(appinfo.loadLabel(ApplicationManager.pm).toString());
		// m.setPicture(appinfo.loadIcon(ApplicationManager.pm));
		if (ic == null)
			ic = ImageSoftCache.getInstance();
		ic.put(appinfo.loadLabel(ApplicationManager.pm).toString(),
				appinfo.loadIcon(ApplicationManager.pm));
		m.setSystemapp(appinfo.uid < 10000);
		m.setProcessnames((ArrayList<String>) runappinfos);
		m.setPkgname(appinfo.packageName);
		m.setUid(appinfo.uid);
		// if ((appinfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0)
		// m.setSystemapp(true);
		Intent intent = ApplicationManager.pm
				.getLaunchIntentForPackage(appinfo.packageName);
		if (intent != null)
			m.setLaunchIntent(intent);
		intent = null;
		return m;
	}

	/**
	 * 根据名字获取MPrograme对象
	 * 
	 * @param name
	 * @return
	 */
	public MPrograme getMProgramByName(String name) {
		for (MPrograme m : runing_Program)
			if (m.getName().equals(name) || m.getPkgname().equals(name))
				return m;
		return null;
	}

	/**
	 * 根据名字获取图标
	 * 
	 * @param name
	 * @return
	 */
	public Bitmap getBitmap(String name) {
		Drawable drawable = null;
		for (ApplicationInfo ai : ApplicationManager.getappList()) {
			if (name.equals(ai.loadLabel(ApplicationManager.pm).toString())) {
				drawable = ai.loadIcon(ApplicationManager.pm);
				break;
			}
		}
		if (drawable != null) {
			if (ic == null)
				ic = ImageSoftCache.getInstance();
			ic.put(name, drawable);
			return ImageProcessing.zoomDrawable(drawable, 0, 0);
		}
		return null;
	}

	/**
	 * 根据名字列表获取所有图标
	 * 
	 * @param name
	 * @return
	 */
	// public Map<String, Drawable> getDrawable(List<String> names) {
	// Map<String, Drawable> drawables = new HashMap<String, Drawable>();
	// Iterator<ApplicationInfo> it = ApplicationManager.getappList()
	// .iterator();
	// ApplicationInfo ai;
	// String name;
	// while (it.hasNext()) {
	// ai = it.next();
	// name = ai.loadLabel(ApplicationManager.pm).toString();
	// if (names.contains(name))
	// drawables.put(name, ai.loadIcon(ApplicationManager.pm));
	// }
	// ai = null;
	// it = null;
	// name = null;
	// return drawables;
	// }

	public ArrayList<MPrograme> getInstalledMProgram() {
		ArrayList<MPrograme> ms = new ArrayList<MPrograme>();
		MPrograme m;
		for (ApplicationInfo ai : ApplicationManager.getInstalled_appList()) {
			String ss = ai.loadLabel(ApplicationManager.pm).toString();
			m = new MPrograme();
			m.setName(ss);
			if (ic == null)
				ic = ImageSoftCache.getInstance();
			ic.put(ai.loadLabel(ApplicationManager.pm).toString(),
					ai.loadIcon(ApplicationManager.pm));
			ms.add(m);
		}
		m = null;
		return ms;
	}
}
