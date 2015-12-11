package com.qddagu.app.meetreader.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

	// 数据库名
	private final static String DB_NAME = "meetreader.db";
	// 数据库版本号
	private final static int DB_VERSION = 1;
	
	public SQLiteHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE Note (id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "file_md5 TEXT, title TEXT, content TEXT, "
				+ "time DATETIME DEFAULT CURRENT_TIMESTAMP)");
		db.execSQL("CREATE TABLE History (id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+"meeting TEXT, time TEXT, md5 UNIQUE)");
		db.execSQL("CREATE TABLE Favorite (id INTEGER PRIMARY KEY AUTOINCREMENT," 
				+"meeting TEXT, md5 UNIQUE)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
