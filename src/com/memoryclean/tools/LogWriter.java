package com.memoryclean.tools;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import android.content.Context;

import com.memoryclean.classes.MPrograme;
import com.memoryclean.properties.LogFile;
import com.memoryclean.properties.MLog;

public class LogWriter {
	private MLog ml = new MLog(new Date(), "", 0, null);
	private LogFile lf ;//= LogFile.getInstance();
	private static LogWriter lw;

	private LogWriter() {
		lf=LogFile.getInstance();
	}

	public synchronized static LogWriter getInstance() {
		if (lw == null)
			lw = new LogWriter();
		return lw;
	}

	private double KBtoMB(int kb) {
		double mb = 0;
		float f = (float) (kb / 1024.0);
		BigDecimal b = new BigDecimal(f);
		mb = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		b = null;
		return mb;
	}

	/**
	 * 将多个清理的程序写入日志
	 * 
	 * @param action
	 * @param list
	 */
	public void writeLog(String action, List<MPrograme> list,Context context) {
		ml.setTime(new Date());
		StringBuffer str = new StringBuffer(action);
		String[] names = new String[list.size()];
		int memory = 0;
		MPrograme m;
		for (int i = 0; i < list.size(); i++) {
			m = list.get(i);
			memory += m.getMemory();
			names[i] = m.getName();
		}
		ml.setAction(str.toString());
		ml.setMemory((float) KBtoMB(memory));
		ml.setNames(names);
		lf.insert(context, ml);
		str = null;
		names = null;
		m = null;
	}

	/**
	 * 清理的程序写入日志
	 * 
	 * @param action
	 * @param m
	 */
	public void writeLog(String action, MPrograme m,Context context) {
		ml.setTime(new Date());
		ml.setAction(action);
		ml.setMemory((float) KBtoMB(m.getMemory()));
		ml.setNames(new String[] { m.getName() });
		lf.insert(context,ml);
	}
}
