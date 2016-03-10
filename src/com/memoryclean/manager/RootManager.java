package com.memoryclean.manager;

import java.io.File;

import com.memoryclean.tools.CommandUtil;

/**
 * ʹ�õ���ģʽ ����ROOT
 * 
 * @author tianwailaike
 *
 */
public class RootManager {
	private static RootManager fs;
	private CommandUtil util;
	private int root;
	private boolean checked = false;

	private final String kSuSearchPaths[] = { "/system/bin/", "/system/xbin/",
			"/system/sbin/", "/sbin/", "/vendor/bin/" };

	private RootManager() {
		util = new CommandUtil();
	}

	public static RootManager getInstance() {
		if (fs == null)
			fs = new RootManager();
		return fs;
	}

	private final static int UNROOT = -1;// û�л�ȡrootȨ��
	private final static int UNROORSYSTEM = 0;// Androidϵͳû��root
	private final static int ROOTED = 1;// �Ѿ���ȡrootȨ��
	private static int systemRootState = UNROOT;

	/**
	 * �ж�ϵͳ�Ƿ�ROOT
	 * 
	 * @return
	 */
	private boolean isRootSystem() {
		if (systemRootState == ROOTED) {
			return true;
		} else if (systemRootState == UNROORSYSTEM) {
			return false;
		}
		File f = null;
		try {
			for (int i = 0; i < kSuSearchPaths.length; i++) {
				f = new File(kSuSearchPaths[i] + "su");
				if (f != null && f.exists()) {
					systemRootState = ROOTED;
					return true;
				}
			}
		} catch (Exception e) {
		}
		f = null;
		systemRootState = UNROOT;
		return false;
	}

	/**
	 * �ж��Ƿ����ROOT��Ȩ
	 * 
	 * @param checked
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	/**
	 * ���ROOT��Ȩ
	 * 
	 * @param checked
	 */
	private void checkRoot() {
		checked = true;
		if (!isRootSystem())
			root = UNROORSYSTEM;
		else {
			try {
				util.getRoot("ls /data/");// ͨ���鿴��Ŀ¼���ļ����
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (util.getStdoutList().size() == 0) {
				root = UNROOT;
			} else {
				root = ROOTED;
			}
		}
	}

	/**
	 * ����ROOT��Ȩ
	 * 
	 * @param checked
	 */
	public int getRoot() {
		if (!checked)
			checkRoot();
		return root;

	}
}
