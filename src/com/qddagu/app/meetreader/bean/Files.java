package com.qddagu.app.meetreader.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import com.qddagu.app.meetreader.AppException;

public class Files {
	public final static String FILES = "files";
	private List<MtFile> fileList = new ArrayList<MtFile>();

	public Files(){}
	public Files(List<MtFile> files) {
		this.fileList = files;
	}
	
	public List<MtFile> getFileList() {
		return fileList;
	}

	public void setFileList(List<MtFile> fileList) {
		this.fileList = fileList;
	}
	
	public static Files parse(String jsonString) throws AppException {
		Files files = new Files();
		try {
			JSONArray jsonFiles = new JSONArray(jsonString);
			for (int i = 0; i < jsonFiles.length(); i++) {
				files.getFileList().add(MtFile.parse(jsonFiles.getString(i)));
			}
		} catch (JSONException e) {
			throw AppException.json(e);
		}
		return files;
	}
}
