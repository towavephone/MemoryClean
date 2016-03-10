package com.memoryclean.classes;

import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * �Զ��������
 *
 * @author tianwailaike
 *
 */
public class MService {
	private String name;
	private String label;
	private Intent intent;// Ϊ��ɱ���÷�����Ҫ��ȡһ��Intent

	public MService() {
	}

	public MService(RunningServiceInfo rs) {
		ComponentName serviceCMP = rs.service;
		intent = new Intent();
		intent.setComponent(serviceCMP);
		name = serviceCMP.getShortClassName();
		//label=rs.lo
		// processname = rs.process;
		// pkgname = serviceCMP.getPackageName();
		serviceCMP = null;
	}
	public MService(ComponentName serviceCMP) {
		intent = new Intent();
		intent.setComponent(serviceCMP);
		name = serviceCMP.getShortClassName();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * ɱ���÷���
	 * 
	 * @param context
	 * @return
	 */
	public boolean kill(Context context) {
		try {
			context.stopService(intent);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean start(Context context) {
		try {
			context.startService(intent);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
