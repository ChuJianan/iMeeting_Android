package com.qddagu.app.meetreader.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.qddagu.app.meetreader.AppException;

public class Interactions {
	public final static String FILES = "files";
	private List<Interaction> fileList = new ArrayList<Interaction>();

	public Interactions(){}
	public Interactions(List<Interaction> files) {
		this.fileList = files;
	}
	
	public List<Interaction> getInteractionList() {
		return fileList;
	}

	public void setInteractionList(List<Interaction> fileList) {
		this.fileList = fileList;
	}
	
	public static Interactions parse(String jsonString) throws AppException {
		Interactions interactions = new Interactions();
		try {
			JSONArray jsonFiles = new JSONArray(jsonString);
			for (int i = 0; i < jsonFiles.length(); i++) {
				interactions.getInteractionList().add(Interaction.parse(jsonFiles.getString(i)));
			}
		} catch (JSONException e) {
			throw AppException.json(e);
		}
		return interactions;
	}
}
