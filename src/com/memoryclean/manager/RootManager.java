package com.memoryclean.manager;

import java.io.File;

import com.memoryclean.tools.CommandUtil;

/**
 * 使用单例模式 管理ROOT
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

	private final static int UNROOT = -1;// 没有获取root权限
	private final static int UNROORSYSTEM = 0;// Android系统没有root
	private final static int ROOTED = 1;// 已经获取root权限
	private static int systemRootState = UNROOT;

	/**
	 * 判断系统是否ROOT
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
	 * 判断是否检查过ROOT授权
	 * 
	 * @param checked
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	/**
	 * 检查ROOT授权
	 * 
	 * @param checked
	 */
	private void checkRoot() {
		checked = true;
		if (!isRootSystem())
			root = UNROORSYSTEM;
		else {
			try {
				util.getRoot("ls /data/");// 通过查看该目录下文件检查
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
	 * 申请ROOT授权
	 * 
	 * @param checked
	 */
	public int getRoot() {
		if (!checked)
			checkRoot();
		return root;

	}
}
