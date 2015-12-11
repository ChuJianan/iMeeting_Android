package com.qddagu.app.meetreader.bean;

import java.util.Date;

import com.google.gson.JsonSyntaxException;
import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.util.JsonUtils;
/**
 * 会议预告
 * @author SYZ
 */
public class Notice extends Entity{

	private String title;	//会议预告标题
	private String brief;	//会议预告简介
	private String content;	//会议预告内容
	private Date time;	//发布时间
	
	public static Notice parse(String jsonString) throws AppException {
		Notice notice = null;
		try {
			notice = JsonUtils.fromJson(jsonString, Notice.class);
		} catch (JsonSyntaxException e) {
			throw AppException.json(e);
		} 
		return notice;
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

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
}
