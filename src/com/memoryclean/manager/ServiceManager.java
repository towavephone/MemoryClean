package com.memoryclean.manager;

import java.util.ArrayList;
import java.util.List;

import com.memoryclean.classes.MService;

import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;

/**
 * ʹ�õ���ģʽ ��������һЩ����
 * 
 * @author tianwailaike
 *
 */
public class ServiceManager {
	private static ServiceManager sm;

	private  ServiceManager() {
	}

	public synchronized static ServiceManager getInstance() {
		if (sm == null)
			sm = new ServiceManager();
		return sm;
	}

	/**
	 * ͨ����������ȡ����
	 * 
	 * @param name
	 * @return
	 */
	public List<MService> getServiceByName(String processName,String packegeName) {
		List<MService>list=new ArrayList<MService>();
		for (RunningServiceInfo rs : ApplicationManager.getRunServiceList()) {
			ComponentName serviceCMP = rs.service;
			String pkgname=serviceCMP.getPackageName();
			if (rs.process.equals(processName)&&packegeName.equals(pkgname))
				list.add(new MService(serviceCMP));
		}
		return list;
	}
}
