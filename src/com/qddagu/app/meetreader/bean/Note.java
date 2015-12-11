package com.qddagu.app.meetreader.bean;

import java.util.Date;

import android.database.Cursor;

import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.db.NoteDB;
import com.qddagu.app.meetreader.util.DateUtils;

public class Note extends Entity {
	private int id;				//笔记ID
	private String fileMd5;			//文件地址MD5
	private String title; 			//笔记标题
	private String content;			//笔记内容
	private Date time;				//记录时间
 
	public static Note parse(Cursor cursor) throws AppException {
		Note note = new Note();
		try {
			note.setId(cursor.getInt(cursor.getColumnIndex(NoteDB.ID)));
			note.setFileMd5(cursor.getString(cursor.getColumnIndex(NoteDB.FILE_MD5)));
			note.setTitle(cursor.getString(cursor.getColumnIndex(NoteDB.TITLE)));
			note.setContent(cursor.getString(cursor.getColumnIndex(NoteDB.CONTENT)));
			note.setTime(DateUtils.parse(cursor.getString(cursor.getColumnIndex(NoteDB.TIME))));
		} catch (Exception e) {
			throw AppException.sqlite(e);
		}
		return note;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFileMd5() {
		return fileMd5;
	}

	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
	}
}
