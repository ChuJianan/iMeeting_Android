package com.qddagu.app.meetreader.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import com.qddagu.app.meetreader.AppException;

public class Notices {
	private List<Meeting> noticeList = new ArrayList<Meeting>();
	
	public static Notices parse(String jsonString) throws AppException {
		Notices notices = new Notices();
		try {
			JSONArray jsonNotices = new JSONArray(jsonString);
			for (int i = 0; i < jsonNotices.length(); i++) {
				notices.getNoticeList().add(Meeting.parse(jsonNotices.getString(i)));
			}
		} catch (JSONException e) {
			throw AppException.json(e);
		} 
		return notices;
	}

	public List<Meeting> getNoticeList() {
		return noticeList;
	}

	public void setNoticeList(List<Meeting> noticeList) {
		this.noticeList = noticeList;
	}
}
