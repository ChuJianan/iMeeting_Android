package com.qddagu.app.meetreader.bean;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonSyntaxException;
import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.util.JsonUtils;

public class Meeting extends Entity{

	private String title;			//会议标题
	private String brief;			//会议简介
	private String place;			//会议地点
	private Date beginTime;		//开始时间
	private Date endTime;			//结束时间
	private Date createTime;		//创建时间
	private String themePicture;	//主题图片
	private boolean isPublic;		//是否公开
	private String agenda;			//会议议程
	private String weibo;			//微博地址
	private Person person;			//当前与会人员
	private URLs urls;				//会议地址信息
	private String url;			//会议地址
	private User user;				//我的用户信息
	private boolean isSend;		//本次会议是否递交了名片
	private int joinNum;			//报名人数
	private boolean isInteraction; //本次会议是否可以提问
	public boolean isInteraction() {
		return isInteraction;
	}

	public void setInteraction(boolean isInteraction) {
		this.isInteraction = isInteraction;
	}

	private List<Guest> guests = Collections.emptyList();				//会议嘉宾列表
	private List<MtFile> files = Collections.emptyList();				//会议文件列表
	private List<Advertising> advertisings = Collections.emptyList();	//会议广告
	
	public static Meeting parse(String jsonString) throws AppException {
		Meeting meeting = null;
		try {
			meeting = JsonUtils.fromJson(jsonString, Meeting.class);
		} catch (JsonSyntaxException e) {
			throw AppException.json(e);
		} 
		return meeting;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getThemePicture() {
		return themePicture;
	}

	public void setThemePicture(String themePicture) {
		this.themePicture = themePicture;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public List<Advertising> getAdvertisings() {
		return advertisings;
	}

	public void setAdvertisings(List<Advertising> advertisings) {
		this.advertisings = advertisings;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public URLs getUrls() {
		return urls;
	}

	public void setUrls(URLs urls) {
		this.urls = urls;
	}
	public List<MtFile> getFiles() {
		return files;
	}
	public void setFiles(List<MtFile> files) {
		this.files = files;
	}

	public List<Guest> getGuests() {
		return guests;
	}

	public void setGuests(List<Guest> guests) {
		this.guests = guests;
	}

	public String getAgenda() {
		return agenda;
	}

	public void setAgenda(String agenda) {
		this.agenda = agenda;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getWeibo() {
		return weibo;
	}

	public void setWeibo(String weibo) {
		this.weibo = weibo;
	}

	public boolean isSend() {
		return isSend;
	}

	public void setSend(boolean isSend) {
		this.isSend = isSend;
	}

	public int getJoinNum() {
		return joinNum;
	}

	public void setJoinNum(int joinNum) {
		this.joinNum = joinNum;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
