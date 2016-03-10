package com.memoryclean.properties;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private final static String NAME = "memoryclean.db";
	private final static int VERSION = 1;
	private final String CREATE_WHITELIST = "create table whitelist(appname varchar(64))";
	private final String CREATE_MLOG = "create table mlog(time varchar(16),action varchar(16),names varchar(64),memory float)";

	public DBHelper(Context context) {
		super(context, NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_WHITELIST);
		db.execSQL(CREATE_MLOG);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop if table exists whitelist");
		db.execSQL("drop if table exists mlog");
		onCreate(db);
	}
}
