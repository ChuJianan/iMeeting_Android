package com.qddagu.app.meetreader.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.util.JsonUtils;
import com.qddagu.app.meetreader.util.Md5;

public class HistoryDB {
	public final static String TABLE_NAME = "History";
	public final static String MEETING = "meeting";
	public final static String MD5 = "md5";
	
	private  SQLiteHelper helper;
	public HistoryDB(Context context) {
		helper = new SQLiteHelper(context);
	}
	public List<Meeting> select() {
		SQLiteDatabase db = helper.getReadableDatabase();
		List<Meeting> list = new ArrayList<Meeting>();
		Cursor cursor = db.query(TABLE_NAME, null, null, 
				null,null, null, "id desc");
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String json = cursor.getString(cursor.getColumnIndex(MEETING));
			list.add(JsonUtils.fromJson(json, Meeting.class));
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		return list;
	}
	public List<Meeting> select(String md5){
		SQLiteDatabase db = helper.getReadableDatabase();
		List<Meeting> list = new ArrayList<Meeting>();
		String selection = MD5 + " = ?";
		String[] selectionArgs = new String[] { md5 };
		Cursor cursor = db
				.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String json = cursor.getString(cursor.getColumnIndex(MEETING));
			list.add(JsonUtils.fromJson(json, Meeting.class));
			cursor.moveToNext();
		}
		cursor.close();
		db.close();
		return list;
	}
	// 增加操作
	public long insert(Meeting meeting) {
		String md5 = Md5.encode(meeting.getUrls().Meeting());
		SQLiteDatabase db = helper.getWritableDatabase();
		/* ContentValues */
		ContentValues cv = new ContentValues();
		cv.put(MEETING, JsonUtils.toJson(meeting));
		cv.put(MD5, md5);
		long row = db.insert(TABLE_NAME, null, cv);
		return row;
	}
	
	public long update(Meeting meeting) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String md5 = Md5.encode(meeting.getUrls().Meeting());
		String where = MD5 + "= '" + md5 + "'";
		ContentValues cv = new ContentValues();
		cv.put(MEETING, JsonUtils.toJson(meeting));
		int rows = db.update(TABLE_NAME, cv, where, null);
		db.close();
		return rows;
	}
	
	public long save(Meeting meeting) {
		String md5 = Md5.encode(meeting.getUrls().Meeting());
		if (select(md5).size() > 0) {
			return update(meeting);
		} else {
			return insert(meeting);
		}
	}
	
	// 删除操作
	public void delete(Meeting meeting) {
		String md5 = Md5.encode(meeting.getUrls().Meeting());
		SQLiteDatabase db = helper.getWritableDatabase();
		String where = MD5 + " = ?";
		String[] whereValue = { md5 };
		db.delete(TABLE_NAME, where, whereValue);
	}
}
