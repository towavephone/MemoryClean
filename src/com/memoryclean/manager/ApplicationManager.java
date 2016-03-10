package com.memoryclean.manager;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * 该类用于获取已经安装的全部程序和用户安装程序，正在运行的进程，服务
 * 
 * 使用方法：首先调用getAll函数 再调用其他函数
 * 
 * @author tianwailaike
 *
 */
public class ApplicationManager {
	public static ActivityManager mActivityManager;
	public static PackageManager pm;

	private static SoftReference<List<Integer>> userInstalled_AppList;
	private static SoftReference<List<ApplicationInfo>> installed_AppList;
	private static SoftReference<List<RunningAppProcessInfo>> runProcessList;
	private static SoftReference<List<RunningServiceInfo>> runServiceList;

	// public static List<Integer> userInstalled_AppList = new
	// ArrayList<Integer>();
	// public static List<ApplicationInfo> installed_AppList = new
	// CopyOnWriteArrayList<ApplicationInfo>();
	// public static List<RunningAppProcessInfo> runProcessList = new
	// CopyOnWriteArrayList<ActivityManager.RunningAppProcessInfo>();
	// public static List<RunningServiceInfo> runServiceList = new
	// CopyOnWriteArrayList<ActivityManager.RunningServiceInfo>();

	private final static int ALLAPP = 1;
	private final static int RUNPROCESS = 2;
	private final static int RUNSERVICE = 3;

	/**
	 * 获取ActivityManager对象给其他函数使用
	 * 
	 * @param context
	 */
	public static void getAll(Context context) {
		pm = context.getPackageManager();
		mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	}

	public synchronized static void clearInstalled_AppList() {
		if (installed_AppList != null)
			installed_AppList.clear();
	}

	public synchronized static void clearRunProcessList() {
		if (runProcessList != null)
			runProcessList.clear();
	}

	public static void clearUserInstalled_AppList() {
		if (userInstalled_AppList != null)
			userInstalled_AppList.clear();
	}

	public synchronized static void clearRunServiceList() {
		if (runServiceList != null)
			runServiceList.clear();
	}

	private static boolean isSystemApp(ApplicationInfo ai) {
		return (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
		// return (ai.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) !=
		// 0;//判断是否为系统程序升级
	}

	// /**
	// * 获取用户安装的程序
	// *
	// * synchronized由于使用多线程，因此每次只有一个线程能调用该函数 在下面不再说明
	// *
	// * @return
	// */
	// public synchronized static List<ApplicationInfo> getInstalled_appList() {
	// int size = 0;
	// if (userInstalled_AppList != null)
	// size = userInstalled_AppList.size();
	// if (size == 0) {
	// userInstalled_AppList = new ArrayList<ApplicationInfo>();
	// getappList();
	// for (int i = installed_AppList.size() - 1; i > 0; i++) {
	// ApplicationInfo ai = installed_AppList.get(i);
	// if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) <= 0)
	// userInstalled_AppList.add(ai);
	//
	// }
	// }
	// return userInstalled_AppList;
	// }
	/**
	 * 获取用户安装的程序
	 * 
	 * synchronized由于使用多线程，因此每次只有一个线程能调用该函数 在下面不再说明
	 * 
	 * @return
	 */
	public static List<ApplicationInfo> getInstalled_appList() {

		List<Integer> lists = get_appList();
		List<ApplicationInfo> temp = getappList();
		ArrayList<ApplicationInfo> list = new ArrayList<ApplicationInfo>();
		list.ensureCapacity(lists.size());
		for (int i : lists) {
			list.add(temp.get(i));
		}
		temp = null;
		lists = null;
		return list;
	}

	// private synchronized static List<Integer> get_appList() {
	// int size = 0;
	// if (userInstalled_AppList != null)
	// size = userInstalled_AppList.size();
	// if (size == 0) {
	// userInstalled_AppList = new ArrayList<Integer>();
	// getappList();
	// for (int i = installed_AppList.size() - 1; i > 0; i--) {
	// ApplicationInfo ai = installed_AppList.get(i);
	// if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) <= 0)
	// userInstalled_AppList.add(i);
	// }
	// }
	// return userInstalled_AppList;
	// }
	private static List<Integer> get_appList() {
		if (userInstalled_AppList == null || userInstalled_AppList.get() == null) {
			ArrayList<Integer> App = new ArrayList<Integer>();
			List<ApplicationInfo> tmp = getappList();
			for (int i = tmp.size() - 1; i > 0; i--) {
				ApplicationInfo ai = tmp.get(i);
				if (!isSystemApp(ai))
					App.add(i);
			}
			userInstalled_AppList = new SoftReference<List<Integer>>(App);
			App = null;
			tmp = null;
		}
		return userInstalled_AppList.get();
	}

	/**
	 * 获取包括系统程序在内的所有程序
	 * 
	 * @return
	 */
	// public synchronized static List<ApplicationInfo> getappList() {
	// int size = 0;
	// if (installed_AppList != null)
	// size = installed_AppList.size();
	// if (size == 0)
	// get(ALLAPP);
	// return installed_AppList;
	// }
	public synchronized static List<ApplicationInfo> getappList() {
		if (installed_AppList == null || installed_AppList.get() == null)
			get(ALLAPP);
		return installed_AppList.get();
	}

	/**
	 * 获取正在运行的进程
	 * 
	 * @return
	 */
	// public static List<ActivityManager.RunningAppProcessInfo>
	// getRunProcessList() {
	// int size = 0;
	// if (runProcessList != null)
	// size = runProcessList.size();
	// if (size == 0)
	// get(RUNPROCESS);
	// return runProcessList;
	// }
	public static List<ActivityManager.RunningAppProcessInfo> getRunProcessList() {
		if (runProcessList == null || runProcessList.get() == null)
			get(RUNPROCESS);
		return runProcessList.get();
	}

	/**
	 * 获取所有正在运行的服务
	 * 
	 * @return
	 */
	// public static List<ActivityManager.RunningServiceInfo>
	// getRunServiceList() {
	// int size = 0;
	// if (runServiceList != null)
	// size = runServiceList.size();
	// if (size == 0)
	// get(RUNSERVICE);
	// return runServiceList;
	// }
	public static List<ActivityManager.RunningServiceInfo> getRunServiceList() {
		if (runServiceList == null || runServiceList.get() == null)
			get(RUNSERVICE);
		return runServiceList.get();
	}

	/**
	 * 根据参数获取想要值
	 * 
	 * @param i
	 */
	private static void get(int i) {
		switch (i) {
		case ALLAPP:
			List<ApplicationInfo> list1 = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
			Collections.sort(list1, new ApplicationInfo.DisplayNameComparator(pm));
			installed_AppList = new SoftReference<List<ApplicationInfo>>(list1);
			list1 = null;
			break;
		case RUNPROCESS:
			// runProcessList = mActivityManager.getRunningAppProcesses();
			runProcessList = new SoftReference<List<RunningAppProcessInfo>>(mActivityManager.getRunningAppProcesses());
		case RUNSERVICE:
			// runServiceList = mActivityManager.getRunningServices(20);
			runServiceList = new SoftReference<List<RunningServiceInfo>>(mActivityManager.getRunningServices(30));
			break;
		}
	}
}