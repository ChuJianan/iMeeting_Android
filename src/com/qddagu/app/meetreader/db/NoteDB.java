package com.qddagu.app.meetreader.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qddagu.app.meetreader.bean.Note;
import com.qddagu.app.meetreader.util.DateUtils;

public class NoteDB  {
	
	// 表名
	public final static String TABLE_NAME = "note";
	public final static String ID = "id";
	public final static String FILE_MD5 = "file_md5";	//使用MD5判断笔记是哪个文件的
	public final static String TITLE = "title";
	public final static String CONTENT = "content";
	public final static String TIME = "time";
	
	private SQLiteHelper helper;
	
	public NoteDB(Context context) {
		helper = new SQLiteHelper(context);
	}
	
	public Cursor select(int id) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String selection = ID + " = ?";
		String[] selectionArgs = { String.valueOf(id) };
		Cursor cursor = db
				.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
		return cursor;
	}
	
	public Cursor select(String fileMd5) {
		SQLiteDatabase db = helper.getReadableDatabase();
		String selection = FILE_MD5 + " = ?";
		String[] selectionArgs = { fileMd5 };
		Cursor cursor = db
				.query(TABLE_NAME, null, selection, selectionArgs, null, null, null);
		return cursor;
	}

	// 增加操作
	public long insert(Note note) {
		SQLiteDatabase db = helper.getWritableDatabase();
		/* ContentValues */
		ContentValues cv = new ContentValues();
		cv.put(FILE_MD5, note.getFileMd5());
		cv.put(TITLE, note.getTitle());
		cv.put(CONTENT, note.getContent());
		cv.put(TIME, DateUtils.format(note.getTime()));
		long row = db.insert(TABLE_NAME, null, cv);
		return row;
	}

	// 删除操作
	public void delete(int id) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String where = ID + " = ?";
		String[] whereValue = { Integer.toString(id) };
		db.delete(TABLE_NAME, where, whereValue);
	}

	// 修改操作
	public int update(Note note) {
		SQLiteDatabase db = helper.getWritableDatabase();
		String where = ID + " = ?";
		String[] whereValue = { Integer.toString(note.getId()) };

		ContentValues cv = new ContentValues();
		cv.put(FILE_MD5, note.getFileMd5());
		cv.put(TITLE, note.getTitle());
		cv.put(CONTENT, note.getContent());
		cv.put(TIME, DateUtils.format(note.getTime()));
		return db.update(TABLE_NAME, cv, where, whereValue);
	}
	
	//保存
	public long save(Note note) {
		Cursor cursor = select(note.getId());
		int count = cursor.getCount();
		cursor.close();
		if (count > 0) {
			return update(note);
		} else {
			return insert(note);
		}
	}
}
