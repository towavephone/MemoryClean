package com.memoryclean.manager;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager.RunningAppProcessInfo;

import com.memoryclean.classes.MProcess;
import com.memoryclean.classes.MPrograme;

/**
 * ʹ�õ���ģʽ ������̵�һЩ����
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
	 * ͨ����������ȡ���̵���Ϣ
	 * 
	 * @param name
	 * @return
	 */
	public RunningAppProcessInfo getMprocessByName(String name) {
		runging_Processes.clear();// ȷ����õ��������������н����б�
		for (RunningAppProcessInfo ra : ApplicationManager.getRunProcessList()) {
			if (name.contains(ra.processName)) {
				return ra;
			}
		}
		return null;
	}

	/**
	 * ��ȡһ����������ʱ���������н���
	 * 
	 * @param mp
	 * @return
	 */
	public List<MProcess> getRunningProcess(MPrograme mp) {
		runging_Processes.clear();// ȷ����õ��������������н����б�
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
	 * ͨ��runappinfo��pkgname�齨һ��MProcess����
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
