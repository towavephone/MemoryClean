package com.memoryclean.properties;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MLogProvider extends ContentProvider {
	private final String table = "mlog";
	private DBHelper helper = null;

	private static final UriMatcher URI_MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);

	private static final int MLOG = 1;// 操作单条记录
	private static final int MLOGS = 2;// 操作多条记录

	static {
		URI_MATCHER.addURI("com.memoryclean.properties.MLogProvider", "mlog",
				MLOGS);
		URI_MATCHER.addURI("com.memoryclean.properties.MLogProvider", "mlog/#",
				MLOG);
	}

	public MLogProvider() {
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = -1;// 影响数据库的行数
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			switch (URI_MATCHER.match(uri)) {
			case MLOG:
				long id = ContentUris.parseId(uri);
				String where_value = "id=" + id;
				if (selection != null && !selection.equals("")) {
					where_value += "and" + selection;
				}
				count = database.delete(table, where_value, selectionArgs);
				break;
			case MLOGS:
				count = database.delete(table, selection, selectionArgs);
				break;
			}
		} catch (Exception e) {
		}
		return count;
	}

	@Override
	public String getType(Uri arg0) {
		switch (URI_MATCHER.match(arg0)) {
		case MLOG:
			return "vnd.android.cursor.item/mlog";
		case MLOGS:
			return "vnd.android.cursor.dir/mlogs";
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri resultUri = null;
		int flag = URI_MATCHER.match(uri);
		SQLiteDatabase database = helper.getWritableDatabase();
		long id = -1;
		switch (flag) {
		case MLOGS:
			id = database.insert(table, null, values);// 当前行的行号
			resultUri = ContentUris.withAppendedId(uri, id);
			break;
		case MLOG:
			id = database.insert(table, null, values);// 当前行的行号
			resultUri = ContentUris.withAppendedId(uri, id);
			break;
		}
		return resultUri;
	}

	@Override
	public boolean onCreate() {
		helper = new DBHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String arg4) {
		Cursor cursor = null;
		try {
			SQLiteDatabase database = helper.getReadableDatabase();
			switch (URI_MATCHER.match(uri)) {
			case MLOG:
				long id = ContentUris.parseId(uri);
				String where_value = "id=" + id;
				if (selection != null && !selection.equals("")) {
					where_value += " and" + selection;
				}
				cursor = database.query(table, null, where_value,
						selectionArgs, null, null, null, null);
				break;

			case MLOGS:
				cursor = database.query(table, null, selection, selectionArgs,
						null, null, null, null);
				break;
			}
		} catch (Exception e) {
		}
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count = -1;
		try {
			SQLiteDatabase database = helper.getWritableDatabase();
			long id = ContentUris.parseId(uri);
			switch (URI_MATCHER.match(uri)) {
			case MLOG:
				String where_value = "id=" + id;
				if (selection != null && !selection.equals("")) {
					where_value += " and" + selection;
				}
				count = database.delete(table, where_value, selectionArgs);
				break;
			case MLOGS:
				break;
			}
		} catch (Exception e) {
		}
		return count;
	}
}
