package com.memoryclean.properties;

public class GlobalConfig {
	private boolean autostart = true;// ��������
	private boolean showsystemapp;// ��������Ӧ���Ƿ���ʾϵͳӦ��
	// private boolean autoclean = true;
	private boolean autoclean_after_shutdown_screan;// ��������
	// private boolean autoclean_morethan = false;//
	// private int cleantime = 0;
	private boolean includewhileList;// һ���������Ƿ����������
	private boolean includesystemapp;// һ���������Ƿ����ϵͳӦ��
	private boolean shownotification = true;// ��ʾ״̬��
	private int exit;// �˳�ʱ״̬ 0 ����ס -1��ס���˳� 1 ��ס�����˳�

	public GlobalConfig() {
	}

	public GlobalConfig(GlobalConfig s) {
		this.autostart = s.isAutostart();
		this.showsystemapp = s.isShownotification();
		this.autoclean_after_shutdown_screan = s
				.isAutoclean_after_shutdown_screan();
		this.includewhileList = s.isIncludewhileList();
		this.includesystemapp = s.isIncludesystemapp();
		this.shownotification = s.isShownotification();
		this.exit = s.getExit();
	}

	@Override
	public GlobalConfig clone() {
		return new GlobalConfig(this);
	}

	/**
	 * true û�иı� false �ı�
	 * 
	 * @param showsystemapp
	 * @return
	 */
	public boolean isShowChanged(boolean showsystemapp) {
		return this.showsystemapp != showsystemapp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (autoclean_after_shutdown_screan ? 1231 : 1237);
		result = prime * result + (autostart ? 1231 : 1237);
		result = prime * result + exit;
		result = prime * result + (includesystemapp ? 1231 : 1237);
		result = prime * result + (includewhileList ? 1231 : 1237);
		result = prime * result + (shownotification ? 1231 : 1237);
		result = prime * result + (showsystemapp ? 1231 : 1237);
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
		GlobalConfig other = (GlobalConfig) obj;
		if (autoclean_after_shutdown_screan != other.autoclean_after_shutdown_screan)
			return false;
		if (autostart != other.autostart)
			return false;
		if (exit != other.exit)
			return false;
		if (includesystemapp != other.includesystemapp)
			return false;
		if (includewhileList != other.includewhileList)
			return false;
		if (shownotification != other.shownotification)
			return false;
		if (showsystemapp != other.showsystemapp)
			return false;
		return true;
	}

	public boolean isAutostart() {
		return autostart;
	}

	public void setAutostart(boolean autostart) {
		this.autostart = autostart;
	}

	public boolean isShowsystemapp() {
		return showsystemapp;
	}

	public void setShowsystemapp(boolean showsystemapp) {
		this.showsystemapp = showsystemapp;
	}

	public boolean isAutoclean_after_shutdown_screan() {
		return autoclean_after_shutdown_screan;
	}

	public void setAutoclean_after_shutdown_screan(
			boolean autoclean_after_shutdown_screan) {
		this.autoclean_after_shutdown_screan = autoclean_after_shutdown_screan;
	}

	public boolean isIncludewhileList() {
		return includewhileList;
	}

	public void setIncludewhileList(boolean includewhileList) {
		this.includewhileList = includewhileList;
	}

	public boolean isIncludesystemapp() {
		return includesystemapp;
	}

	public void setIncludesystemapp(boolean includesystemapp) {
		this.includesystemapp = includesystemapp;
	}

	public boolean isShownotification() {
		return shownotification;
	}

	public void setShownotification(boolean shownotification) {
		this.shownotification = shownotification;
	}

	public int getExit() {
		return exit;
	}

	public void setExit(int exit) {
		this.exit = exit;
	}
}
