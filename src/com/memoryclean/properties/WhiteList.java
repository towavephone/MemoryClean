package com.memoryclean.properties;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WhiteList {
	private DBHelper helper;
	private final String table = "whitelist";
	private final int UNSUCCESS = -1;// 操作不成功
	private final int EXIST = 0;// 该数据已存在
	private final int UNEXIST = 0;// 该数据不存在

	public WhiteList(Context context) {
		helper = new DBHelper(context);
	}

	public ArrayList<String> isJoin(ArrayList<String> list) {
		ArrayList<String> Join_list = (ArrayList<String>) query(null);
		for (int i = list.size(); i > 0; i++) {
			if (!Join_list.contains(list.get(i)))
				list.remove(i);
		}
		return list;
	}

	/**
	 * 根据应用名判断是否加入白名单
	 * 
	 * @param name
	 * @return
	 */
	public boolean isJoin(String name) {
		List<String> list = query(name);
		return list != null && list.size() != 0;
	}

	private List<String> query(String value) {
		SQLiteDatabase database = helper.getReadableDatabase();
		String where_value = null;
		if (value != null)
			where_value = "appname='" + value + "'";
		Cursor cursor = database.query(table, null, where_value, null, null,
				null, null);
		if (cursor != null) {
			List<String> list = new ArrayList<String>();
			while (cursor.moveToNext()) {
				list.add(cursor.getString(cursor.getColumnIndex("appname")));
			}
			cursor.close();
			return list;
		}
		return null;
	}

	public void Add(String name) {
		insert(name);
	}

	public void addAll(ArrayList<String> list) {
		for (int i = 0; i < list.size(); i++) {
			String s = list.get(i);
			insert(s);
		}
	}

	private long insert(String value) {
		long id = UNSUCCESS;
		if (value != null) {
			if (query(value) != null) {
				ContentValues values = new ContentValues();
				values.put("appname", value);
				SQLiteDatabase database = helper.getWritableDatabase();
				id = database.insert(table, null, values);
				database.close();
			} else {
				id = EXIST;
			}
		}
		return id;
	}

	public void Remove(String name) {
		delete(name);
	}

	private long delete(String value) {
		SQLiteDatabase database = helper.getWritableDatabase();
		long id = UNSUCCESS;
		if (value != null) {
			if (query(value) != null) {
				String whereClause = "appname='" + value + "'";
				id = database.delete(table, whereClause, null);
				database.close();
			} else
				id = UNEXIST;
		} else {
			id = database.delete(table, null, null);
			database.close();
		}
		return id;

	}

}
