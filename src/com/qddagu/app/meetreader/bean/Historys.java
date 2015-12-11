package com.qddagu.app.meetreader.bean;

import java.util.ArrayList;
import java.util.List;

public class Historys {
	private List<Meeting> historyList = new ArrayList<Meeting>();
	
	public Historys(List<Meeting> meetings) {
		this.historyList = meetings;
	}
	
	public List<Meeting> getHistoryList() {
		return historyList;
	}

	public void setHistoryList(List<Meeting> historyList) {
		this.historyList = historyList;
	}
}
