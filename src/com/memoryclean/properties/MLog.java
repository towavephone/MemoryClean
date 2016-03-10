package com.memoryclean.properties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;

/**
 * ��־������
 * 
 * @author tianwailaike
 *
 */
@SuppressLint("SimpleDateFormat")
public class MLog {
	public final static String CLEANBYHAND = "�ֶ�����";
	public final static String CLEANAFTERSCREENSHUTDOWN = "��������";
	public final static String ONEKEYCLEAN = "һ������";

	private Date time;
	private String action;// ����������
	private String[] names;// ������Ӧ������
	private float memory;// �ͷŵ��ڴ�
	private static SimpleDateFormat formatter = new SimpleDateFormat(
			"MM-dd HH:mm:ss");

	public MLog() {
		time = new Date();
		action = CLEANBYHAND;
		names = null;
		memory = 0;
	}

	public MLog(Date time, String action, float memory, ArrayList<String> names) {
		this.time = DateToDate(time);
		this.action = action;
		this.memory = memory;

		if (names == null)
			this.names = null;
		else {
			this.names = new String[names.size()];
			for (int i = 0; i < names.size(); i++) {
				this.names[i] = names.get(i);
			}
		}
	}

	public static Date DateToDate(Date date) {
		return StringToDate(DateToString(date));
	}

	/**
	 * ������ת��Ϊ�ַ��� MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String DateToString(Date date) {
		return formatter.format(date).toString();
	}

	/**
	 * ���ַ��� MM-dd HH:mm:ssת��Ϊ����
	 * 
	 * @param sdate
	 * @return
	 */
	public static Date StringToDate(String sdate) {
		Date date = null;
		try {
			date = formatter.parse(sdate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public float getMemory() {
		return memory;
	}

	public void setMemory(float memory) {
		this.memory = memory;
	}

	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}
}
