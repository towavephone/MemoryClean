package com.memoryclean.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.memoryclean.classes.MProcess;
import com.memoryclean.classes.MPrograme;
import com.memoryclean.classes.MService;
import com.memoryclean.manager.ApplicationManager;
import com.memoryclean.manager.ProgramManager;
import com.memoryclean.manager.RootManager;
import com.memoryclean.manager.ServiceManager;
import com.memoryclean.properties.MLog;
import com.memoryclean.properties.SettingManager;
import com.memoryclean.properties.WhiteList;
import com.memoryclean.service.NotificationService;

public class AutoClean {
	private Clean c;
	private WhiteList wl = null;
	// private GlobalConfig gc;
	private Date lastCleanTime;
	private boolean iscleaning;

	public static String self = "com.wust.memoryclean";
	private String selfService = "com.wust.notificationservice";
	private static AutoClean ac;

	private AutoClean() {
		// wl = WhiteList.getInstance();
		// gc = new SettingManager(context).loadSetting();
		if (RootManager.getInstance().getRoot() == 1)
			c = new StrongClean();
		else
			c = new NormalClean();
	}

	public synchronized static AutoClean getInstance() {
		if (ac == null)
			ac = new AutoClean();
		return ac;
	}

	public void setLastCleanTime(Date lastCleanTime) {
		this.lastCleanTime = lastCleanTime;
	}

	public Date getLastCleanTime() {
		return lastCleanTime;
	}

	public boolean isCleanning() {
		return iscleaning;
	}

	public void stopClean() {
		iscleaning = false;
	}
	

	/**
	 * ������Ӧ��
	 * 
	 * @param mp
	 */
	public void killProgram(MPrograme mp, Context context) {
		iscleaning = true;
		lastCleanTime = new Date();
		c.killProgram(mp);
		if (ProgramManager.getInstance().iskilled(mp.getPkgname()))
			LogWriter.getInstance().writeLog(MLog.CLEANBYHAND, mp, context);
		iscleaning = false;
	}

	/**
	 * ѡ������
	 */
	public void ChoseClean(ArrayList<MPrograme> programs) {
		iscleaning = true;
		lastCleanTime = new Date();
		for(int i=programs.size()-1;i>=0;i--){
			MPrograme mprograme=programs.get(i);
			if(!canKill(mprograme))
				programs.remove(i);
		}
		c.killPrograms(programs);
	}

	public void cleanAfterShutdownScrean(Context context) {
		lastCleanTime = new Date();
		ArrayList<MPrograme> list = onekeyclean(context);
		LogWriter.getInstance().writeLog(MLog.CLEANAFTERSCREENSHUTDOWN,
				list, context);
	}

	/**
	 * һ������
	 * 
	 * @return ������Ҫ�������Ӧ��
	 */
	public ArrayList<MPrograme> onekeyclean(Context context) {
		iscleaning = true;
		boolean flag = new SettingManager(context)
				.getBoolean("includesystemapp");
		ArrayList<MPrograme> programs = new ArrayList<MPrograme>();
		// Map<String,Integer>maps=new HashMap<String, Integer>();
		ProgramManager pm = ProgramManager.getInstance();
		List<MPrograme> Mlist = pm.getRuning_applications(false, flag);
		String name;
		for (MPrograme m : Mlist) {
			name = m.getPkgname();
			if (canKill(m, context)) {
				programs.add(m);
				// maps.put(name, m.getMemory());
				m.getMemory();
			}
		}
		c.killPrograms(programs);
		iscleaning = false;
		name = null;
		Mlist = null;
		// for(Entry<String, Integer> entry : maps.entrySet()){
		// Log.e("TAG",entry.getKey()+ "====="+entry.getValue());
		// }
		return programs;
	}

	private boolean joinwitelistcankill(Context context, String name) {
		if (wl == null)
			wl = new WhiteList(context);
		if (new SettingManager(context).getBoolean("includewhileList"))
			return true;
		if (wl.isJoin(name))
			return false;
		return true;
	}

	/**
	 * ����һЩϵͳ��Ҫ��Ӧ��
	 * 
	 * @param pkgname
	 * @return
	 */
	public boolean ignoreByPkgName(String pkgname) {
		return pkgname.contains("input") || pkgname.contains("system")
				|| pkgname.contains("launcher");
	}

	/**
	 * �����Լ� ��ֹ�Լ������Լ�
	 * 
	 * @param pkgname
	 * @return
	 */
	private boolean includeSelf(String pkgname) {
		return pkgname.contains(AutoClean.self);
	}

	public void killself(Context context) {
		if (!iscleaning) {
			killSelfService(context);
			android.os.Process.killProcess(android.os.Process.myPid());
		} else
			Toast.makeText(context, "��������,���Եȡ�����", Toast.LENGTH_LONG).show();
	}

	public void killservice(MProcess m, Context context) {
		c.killProcess(m, context);
	}

	public void killSelfService(Context context) {
		if (ApplicationManager.getRunServiceList() != null)
			ApplicationManager.clearRunServiceList();
		List<MService> services = ServiceManager.getInstance()
				.getServiceByName(this.selfService,AutoClean.self);
		if (services != null && services.size() != 0) {
			// context.startService(new Intent(context,
			// NotificationService.class));
			services.get(0).kill(context);
		}
	}

	public void startSelfService(Context context) {
		if (ApplicationManager.getRunServiceList() != null)
			ApplicationManager.clearRunServiceList();
		List<MService> services = ServiceManager.getInstance()
				.getServiceByName(this.selfService,AutoClean.self);
		if (services == null || services.size() == 0) {
			context.startService(new Intent(context, NotificationService.class));
		}
	}
	
	private Boolean canKill(MPrograme m,Context context){
		boolean f = includeSelf(m.getName()) || ignoreByPkgName(m.getName()) || m.isSystemapp();
		
		return !f&&joinwitelistcankill(context, m.getName());
	}
	
	private Boolean canKill(MPrograme m){
		boolean f = includeSelf(m.getName()) || ignoreByPkgName(m.getName()) || m.isSystemapp();
		
		return !f;
	}

	/**
	 * �ж������Ƿ��ܹ���������
	 * 
	 * @return
	 */
	public boolean cankillNow(Context context) {
		if (lastCleanTime == null)
			lastCleanTime = new SettingManager(context)
					.getDate("lastCleanTime");
		Date nowTime = new Date();
		if (iscleaning == true || lastCleanTime == null
				|| nowTime.getTime() - lastCleanTime.getTime() > 10 * 60 * 1000) {
			return true;
		}
		return false;
	}
}
