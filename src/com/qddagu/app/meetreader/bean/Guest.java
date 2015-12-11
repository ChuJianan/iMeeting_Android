package com.qddagu.app.meetreader.bean;

import com.google.gson.JsonSyntaxException;
import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.util.JsonUtils;

public class Guest extends Entity {
	private String name;            //嘉宾姓名
	private String guestPicture;   //嘉宾头像图片
	private String post;            //嘉宾职务
	private String brief;           //嘉宾简介
	
	public static Guest parse(String jsonString) throws AppException {
		Guest guest = null;
		try {
			guest = JsonUtils.fromJson(jsonString, Guest.class);
		} catch (JsonSyntaxException e) {
			throw AppException.json(e);
		} 
		return guest;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGuestPicture() {
		return guestPicture;
	}
	public void setGuestPicture(String guestPicture) {
		this.guestPicture = guestPicture;
	}
	public String getPost() {
		return post;
	}
	public void setPost(String post) {
		this.post = post;
	}
	public String getBrief() {
		return brief;
	}
	public void setBrief(String brief) {
		this.brief = brief;
	}
}
