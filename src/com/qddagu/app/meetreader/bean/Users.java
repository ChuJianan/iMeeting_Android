package com.qddagu.app.meetreader.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import com.qddagu.app.meetreader.AppException;

public class Users {
	private List<User> userList = new ArrayList<User>();
	
	public static Users parse(String jsonString) throws AppException {
		Users users = new Users();
		try {
			JSONArray jsonUsers = new JSONArray(jsonString);
			for (int i = 0; i < jsonUsers.length(); i++) {
				users.getUserList().add(User.parse(jsonUsers.getString(i)));
			}
		} catch (JSONException e) {
			throw AppException.json(e);
		} 
		return users;
	}

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> userList) {
		this.userList = userList;
	}

}
