package com.memoryclean.classes;

import java.io.Serializable;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Debug;

import com.memoryclean.manager.ApplicationManager;
import com.memoryclean.manager.ProcessManager;

/**
 * �Զ���Ӧ����
 *
 * @author tianwailaike
 *
 */
public class MPrograme implements Serializable {
	private static final long serialVersionUID = 1L;
	// private Drawable picture;// Ӧ��ͼ��
	// private SoftReference<Drawable> picture;// Ӧ��ͼ��
	private String pkgname;// ����
	private int uid;
	private int memory = -1;
	private String name;// Ӧ����
	private ArrayList<String> processnames;// ��Ӧ�õĽ�����
	private ArrayList<MProcess> processes = null;// ��Ӧ�õĽ���
	private boolean systemapp = false;// �Ƿ�Ϊϵͳ����
	private Intent launchIntent = null;

	public MPrograme() {
	}

	public MPrograme(String name, int memory) {
		super();
		this.name = name;
		this.memory = memory;
	}

	// public MPrograme(Drawable picture, String name, int memory) {
	// super();
	// this.picture =picture;
	// this.name = name;
	// this.memory = memory;
	// picture = null;
	// }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MPrograme other = (MPrograme) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		other = null;
		return true;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public Intent getLaunchIntent() {
		return launchIntent;
	}

	public void setLaunchIntent(Intent launchIntent) {
		this.launchIntent = launchIntent;
	}

	public boolean isSystemapp() {
		return systemapp;
	}

	public void setSystemapp(boolean systemapp) {
		this.systemapp = systemapp;
	}

	public ArrayList<String> getProcessnames() {
		return processnames;
	}

	public void setProcessnames(ArrayList<String> processnames) {
		this.processnames = processnames;
	}

	// public Drawable getPicture() {
	// return picture;
	// }
	//
	// public void setPicture(Drawable picture) {
	// this.picture = picture;
	// picture = null;
	// }

	public String getPkgname() {
		return pkgname;
	}

	public void setPkgname(String pkgname) {
		this.pkgname = pkgname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * ��ȡ�ý���ռ���ڴ��С
	 * 
	 * @return
	 */
	public int getMemory() {
		if (memory == -1) {
			if (processes == null)
				getProcesses();
			int[] pids = new int[processes.size()];
			/* ��ȡ��ǰӦ�õ����н��̵�PID */
			for (int i = 0; i < processes.size(); i++)
				pids[i] = processes.get(i).getPid();
			/* ����PID��ȡ���������ڴ�ռ����Ϣ */
			Debug.MemoryInfo[] memoryInfo = ApplicationManager.mActivityManager
					.getProcessMemoryInfo(pids);
			pids = null;
			memory = 0;
			for (int i = 0; i < memoryInfo.length; i++) {
				memory += memoryInfo[i].dalvikPrivateDirty;
			}
			memoryInfo = null;
		}
		return memory;
	}

	/**
	 * ������Ӧ��
	 * 
	 * @param context
	 */
	public void launch(Context context) {
		if (launchIntent != null)
			context.startActivity(launchIntent);
	}

	/**
	 * ��ȡ��Ӧ�õ����н���
	 * 
	 * @return
	 */
	public ArrayList<MProcess> getProcesses() {
		ProcessManager p = ProcessManager.getInstance();
		processes = (ArrayList<MProcess>) p.getRunningProcess(this);
		p = null;
		return processes;
	}

	/**
	 * ɱ����Ӧ��
	 */
	public void kill() {
		// if (processes == null)
		// getProcesses();
		// for (MProcess mp : processes)
		// android.os.Process.killProcess(mp.getPid());
		ApplicationManager.mActivityManager.killBackgroundProcesses(pkgname);
	}
}
