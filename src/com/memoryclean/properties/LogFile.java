package com.memoryclean.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * 日志文件存放位置和读取保存日志
 * 
 * @author tianwailaike
 *
 */
public class LogFile {

	private ReentrantLock lock=new ReentrantLock();
	private static LogFile lf;
	private final Uri url = Uri
			.parse("content://com.memoryclean.properties.MLogProvider/mlog");

	private LogFile() {
	}

	public synchronized static LogFile getInstance() {
		if (lf == null)
			lf = new LogFile();
		return lf;
	}

	public void insert(Context context, MLog mlog) {
		lock.lock();
		ContentResolver contentResolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put("time", MLog.DateToString(mlog.getTime()));
		values.put("action", mlog.getAction());
		StringBuilder ss = new StringBuilder();
		for (String s : mlog.getNames()) {
			ss.append(s + ",");
		}
		values.put("names", ss.toString());
		values.put("memory", mlog.getMemory());
		contentResolver.insert(url, values);
		lock.unlock();
	}

	public void delete(Context context) {
		lock.lock();
		ContentResolver contentResolver = context.getContentResolver();
		contentResolver.delete(url, null, null);
		lock.unlock();
	}

	public List<MLog> query(Context context) {
		lock.lock();
		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = contentResolver.query(url, null, null, null, null);
		List<MLog> list = null;
		if (cursor != null) {
			list = new ArrayList<MLog>();
			while (cursor.moveToNext()) {
				MLog mlog = new MLog();
				mlog.setAction(cursor.getString(cursor.getColumnIndex("action")));
				mlog.setMemory(Float.parseFloat(cursor.getString(cursor
						.getColumnIndex("memory"))));
				mlog.setTime(MLog.StringToDate(cursor.getString(cursor
						.getColumnIndex("time"))));
				mlog.setNames(cursor.getString(cursor.getColumnIndex("names"))
						.split(","));
				list.add(mlog);
			}
		}
		lock.unlock();
		return list;
	}
}
