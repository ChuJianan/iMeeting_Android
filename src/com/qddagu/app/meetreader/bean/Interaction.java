package com.qddagu.app.meetreader.bean;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.qddagu.app.meetreader.AppException;

public class Interaction extends Entity{
private String askQuestion;//提问内容
private String responseQuestion;//回答内容
private String askTime;//提问时间
private String responseTime;//回答时间
private String logo;//提问者logo
private String name;//提问者姓名
private int meetId;
private int userId;



public int getMeetId() {
	return meetId;
}
public void setMeetId(int meetId) {
	this.meetId = meetId;
}
public int getUserId() {
	return userId;
}
public void setUserId(int userId) {
	this.userId = userId;
}
public static Interaction parse(String jsonString) throws AppException {
	Interaction interaction = null;
	try {
		interaction = new Gson().fromJson(jsonString, Interaction.class);
	} catch (JsonSyntaxException e) {
		throw AppException.json(e);
	} 
	return interaction;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}

public String getLogo() {
	return logo;
}
public void setLogo(String logo) {
	this.logo = logo;
}
public String getAskQuestion() {
	return askQuestion;
}
public void setAskQuestion(String askQuestion) {
	this.askQuestion = askQuestion;
}
public String getResponseQuestion() {
	return responseQuestion;
}
public void setResponseQuestion(String responseQuestion) {
	this.responseQuestion = responseQuestion;
}
public String getAskTime() {
	return askTime;
}
public void setAskTime(String askTime) {
	this.askTime = askTime;
}
public String getResponseTime() {
	return responseTime;
}
public void setResponseTime(String responseTime) {
	this.responseTime = responseTime;
}

}
