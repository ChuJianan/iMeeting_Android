package com.qddagu.app.meetreader.bean;


/**
 * 接口URL实体类
 * @author SYZ
 * @version 1.0
 * @created 2013
 */
public class URLs extends Entity {
	
	public static final String HOST = "http://huiyi.yunruiinfo.com";
	
	public static final String APP_INIT = HOST + "/dagumeet/phone/init";		//应用初始化地址
	public static final String HOME_ADS = HOST + "/dagumeet/phone/homeads";		//首页广告地址
	
	public static final String NOTICE = HOST + "/dagu_manage/phone/meet/notices";	//预告地址
	public static final String CLOUD = HOST + "/dagu_manage/phone/meet/list?pageNo=";	//会议云地址
	public static final String UPDATE = HOST + "/dagumeet/phone/update";			//更新地址
	public static final String INTERACTION=HOST+"/dagumeet/phone/reQuestion?";//获得互动信息列表
	public static final String SAVE_GUEST=HOST+"/dagumeet/phone/saveQuestion?";//提交问题
	private String meeting;	//会议地址
	private String files;		//文件列表
	private String guests;		//嘉宾列表
	private String comments;	//评论列表
	private String comment;	//发表评论
	private String cards;		//会议名片墙
	private String user;		//保存用户信息
	private String signs;		//签到列表
	private String sign;		//我要签到
	private String join;		//我要报名
	private String send;		//递交名片
	private String wps;		//wps下载地址
	private String html;		//html转换地址
	
	
	public String Meeting() {
		return this.meeting;
	}
	public String Files() {
		return this.files;
	}
	public String Comments() {
		return this.comments;
	}
	public String Comment() {
		return this.comment;
	}
	public String Wps() {
		return this.wps;
	}
	public String Html() {
		return this.html;
	}
	public String Guests() {
		return this.guests;
	}
	public String Cards() {
		return this.cards;
	}
	public String User() {
		return this.user;
	}
	public String Signs() {
		return this.signs;
	}
	public String Sign() {
		return this.sign;
	}
	public String Join() {
		return this.join;
	}
	public String Send() {
		return this.send;
	}
}
