package com.memoryclean.manager;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager.RunningAppProcessInfo;

import com.memoryclean.classes.MProcess;
import com.memoryclean.classes.MPrograme;

/**
 * 使用单例模式 管理进程的一些函数
 * 
 * @author tianwailaike
 *
 */
public class ProcessManager {
	private static ProcessManager pm;
	private List<MProcess> runging_Processes = new ArrayList<MProcess>();

	private ProcessManager() {
	}

	public synchronized static ProcessManager getInstance() {
		if (pm == null)
			pm = new ProcessManager();
		return pm;
	}

	/**
	 * 通过进程名获取进程的信息
	 * 
	 * @param name
	 * @return
	 */
	public RunningAppProcessInfo getMprocessByName(String name) {
		runging_Processes.clear();// 确保获得的是最新正在运行进程列表
		for (RunningAppProcessInfo ra : ApplicationManager.getRunProcessList()) {
			if (name.contains(ra.processName)) {
				return ra;
			}
		}
		return null;
	}

	/**
	 * 获取一个程序运行时包括的所有进程
	 * 
	 * @param mp
	 * @return
	 */
	public List<MProcess> getRunningProcess(MPrograme mp) {
		runging_Processes.clear();// 确保获得的是最新正在运行进程列表
		String pkgname = mp.getPkgname();
		ArrayList<String> processnames = mp.getProcessnames();
		for (RunningAppProcessInfo ra : ApplicationManager.getRunProcessList()) {
			if (processnames.contains(ra.processName)) {
				runging_Processes.add(getMProcess(ra, pkgname));
			}
		}
		return runging_Processes;
	}

	/**
	 * 通过runappinfo和pkgname组建一个MProcess对象
	 * 
	 * @param runappinfo
	 * @param pkgname
	 * @return
	 */
	private MProcess getMProcess(RunningAppProcessInfo runappinfo,
			String pkgname) {
		MProcess mp = new MProcess(runappinfo.pid, runappinfo.processName);
	//	mp.setUid(runappinfo.uid);
		//mp.setPkgname(pkgname);
		return mp;
	}

	public void Clear() {
	}
}
