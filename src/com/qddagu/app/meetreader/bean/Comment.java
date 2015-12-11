package com.qddagu.app.meetreader.bean;

import java.util.Date;

import com.google.gson.JsonSyntaxException;
import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.util.JsonUtils;

public class Comment extends Entity{

	private int fileId;	//文件ID
	private String title;	//评论标题
	private String content;	//评论内容
	private Date time;		//评论时间
	
	public static Comment parse(String jsonString) throws AppException {
		Comment comment = null;
		try {
			comment = JsonUtils.fromJson(jsonString, Comment.class);
		} catch (JsonSyntaxException e) {
			throw AppException.json(e);
		} 
		return comment;
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

	public int getFileId() {
		return fileId;
	}

	public void setFileId(int fileId) {
		this.fileId = fileId;
	}
}
