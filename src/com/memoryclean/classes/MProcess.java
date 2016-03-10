package com.memoryclean.classes;

import java.util.List;

import android.content.Context;

import com.memoryclean.manager.ServiceManager;

/**
 * 自定义进程类
 * 
 * @author tianwailaike
 *
 */
public class MProcess {
	private int pid;
	private String name;
	public List<MService> services = null;

	public MProcess() {
	}

	public MProcess(int id, String name) {
		super();
		this.pid = id;
		this.name = name;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取运行在该进程下的服务
	 * 
	 * @return
	 */
	public List<MService> getServices(String packageName) {
		services = ServiceManager.getInstance().getServiceByName(this.name,packageName);
		return services;
	}

	public void setServices(List<MService> services) {
		this.services = services;
	}

	public void kill(Context context,String packageName) {
		if (services == null)
			getServices(packageName);
		if (services != null && services.size() != 0)
			for (MService service : services)
				service.kill(context);
		android.os.Process.killProcess(pid);
	}
}
