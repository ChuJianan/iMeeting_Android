package com.qddagu.app.meetreader.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import com.qddagu.app.meetreader.AppException;

public class Comments {
	private List<Comment> commentList = new ArrayList<Comment>();
	
	public static Comments parse(String jsonString) throws AppException {
		Comments comments = new Comments();
		try {
			JSONArray jsonComments = new JSONArray(jsonString);
			for (int i = 0; i < jsonComments.length(); i++) {
				comments.getCommentList().add(Comment.parse(jsonComments.getString(i)));
			}
		} catch (JSONException e) {
			throw AppException.json(e);
		} 
		return comments;
	}

	public List<Comment> getCommentList() {
		return commentList;
	}

	public void setCommentList(List<Comment> commentList) {
		this.commentList = commentList;
	}

}
