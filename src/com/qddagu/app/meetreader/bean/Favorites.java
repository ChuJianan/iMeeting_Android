package com.qddagu.app.meetreader.bean;

import java.util.ArrayList;
import java.util.List;

public class Favorites {
	private List<Meeting> favoriteList = new ArrayList<Meeting>();

	public Favorites(List<Meeting> meetings) {
		this.favoriteList = meetings;
	}

	public List<Meeting> getFavoriteList() {
		return favoriteList;
	}

	public void setFavoriteList(List<Meeting> favoriteList) {
		this.favoriteList = favoriteList;
	}
}
