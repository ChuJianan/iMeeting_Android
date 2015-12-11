package com.qddagu.app.meetreader.bean;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.qddagu.app.meetreader.AppException;

public class Notes {
	private List<Note> noteList = new ArrayList<Note>();
	
	public static Notes parse(Cursor cursor) throws AppException {
		Notes notes = new Notes();
		try {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				notes.getNoteList().add(Note.parse(cursor));
				cursor.moveToNext();
			}
			cursor.close();
		} catch (Exception e) {
			throw AppException.sqlite(e);
		} 
		return notes;
	}

	public List<Note> getNoteList() {
		return noteList;
	}

	public void setNoteList(List<Note> noteList) {
		this.noteList = noteList;
	}
}
