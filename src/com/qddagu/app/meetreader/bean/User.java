package com.qddagu.app.meetreader.bean;

import java.util.Date;

import com.google.gson.JsonSyntaxException;
import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.util.JsonUtils;
/**
 * 用户
 * @author SYZ
 */
public class User extends Entity{
	private String mac;		//MAC
	private String name;	//姓名
	private String post;	//职位
	private String site;	//网站
	private String email;	//邮箱
	private String phone;	//电话
	private String mobile;	//手机
	private String company;	//公司
	private String address;	//地址
	private String fax;		//传真
	private String remark;	//备注
	private String logo;	//LOGO地址
	
	private Date signTime;		//本次会议的签到时间
	private Date joinTime;		//本次会议的报名时间
	
	public static User parse(String jsonString) throws AppException {
		User user = null;
		try {
			user = JsonUtils.fromJson(jsonString, User.class);
		} catch (JsonSyntaxException e) {
			throw AppException.json(e);
		}
		return user;
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

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getSignTime() {
		return signTime;
	}

	public void setSignTime(Date signTime) {
		this.signTime = signTime;
	}

	public Date getJoinTime() {
		return joinTime;
	}

	public void setJoinTime(Date joinTime) {
		this.joinTime = joinTime;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
}
