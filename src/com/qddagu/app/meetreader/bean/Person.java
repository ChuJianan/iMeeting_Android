package com.qddagu.app.meetreader.bean;

import com.google.gson.JsonSyntaxException;
import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.util.JsonUtils;

public class Person extends Entity{

	private String name;	//姓名
	private String post;	//职位
	private String mac;		//MAC地址
	
	public static Person parse(String jsonString) throws AppException {
		Person person = null;
		try {
			person = JsonUtils.fromJson(jsonString, Person.class);
		} catch (JsonSyntaxException e) {
			throw AppException.json(e);
		} 
		return person;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPost() {
		return post;
	}
	public void setPost(String post) {
		this.post = post;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}
}
