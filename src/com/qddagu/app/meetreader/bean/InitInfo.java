package com.qddagu.app.meetreader.bean;

import java.util.Collections;
import java.util.List;

import com.google.gson.JsonSyntaxException;
import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.util.JsonUtils;

@SuppressWarnings("serial")
public class InitInfo extends Entity {
	private User user;				//我的用户信息
	private List<Advertising> advertisings = Collections.emptyList();	//首页广告
	
	public static InitInfo parse(String jsonString) throws AppException {
		InitInfo info = null;
		try {
			info = JsonUtils.fromJson(jsonString, InitInfo.class);
		} catch (JsonSyntaxException e) {
			throw AppException.json(e);
		} 
		return info;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<Advertising> getAdvertisings() {
		return advertisings;
	}
	public void setAdvertisings(List<Advertising> advertisings) {
		this.advertisings = advertisings;
	}
}
