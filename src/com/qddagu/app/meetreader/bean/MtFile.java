package com.qddagu.app.meetreader.bean;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.R;

public class MtFile extends Entity {

	private String url; 			//文件URL
	private String type;			//文件类型
	private String name;			//文件名称
	private String size;			//文件大小
	private String author;			//文件作者
	
	private List<Note> notes = Collections.emptyList();					//文件笔记
	private List<Comment> comments = Collections.emptyList();			//文件评论
	private List<Permission> permissions = Collections.emptyList();		//文件权限
	
	private File file;		//文件
	private byte[] bytes; 	//文件字节流
	
	/**
	 * 获取文件类型图标资源
	 * @return 文件类型图标资源
	 */
	public int getIcon() {
		int icon = R.drawable.ic_file_default;
		if("doc".equals(type) || "docx".equals(type)) {
			icon = R.drawable.ic_file_doc;
		} else if ("xls".equals(type)) {
			icon = R.drawable.ic_file_xls;
		} else if ("pdf".equals(type)) {
			icon = R.drawable.ic_file_pdf;
		} else if ("ppt".equals(type)) {
			icon = R.drawable.ic_file_ppt;
		} else if ("txt".equals(type)) {
			icon = R.drawable.ic_file_txt;
		} else if ("jpg".equals(type) || "jpeg".equals(type) || "png".equals(type) || "gif".equals(type) || "bmp".equals(type)) {
			icon = R.drawable.ic_file_img;
		} else if ("html".equals(type) || "htm".equals(type)) {
			icon = R.drawable.ic_file_htm;
		}
		return icon;
	}
	
	/**
	 * 文件是否拥有value值权限
	 * @param value
	 * @return
	 */
	public boolean ifCan(String value) {
		for (Permission p : permissions) {
			if(value.equals(p.getValue())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 文件阅读器菜单列表
	 * @return
	 */
	public List<String> getMenus() {
		List<String> ps = new ArrayList<String>();
		for (Permission p : permissions) {
			if(p.getValue().startsWith("menu_")) {
				ps.add(p.getValue());
			}
		}
		return ps;
	}
	
	public static MtFile parse(String jsonString) throws AppException {
		MtFile file = null;
		try {
			file = new Gson().fromJson(jsonString, MtFile.class);
		} catch (JsonSyntaxException e) {
			throw AppException.json(e);
		} 
		return file;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public List<Note> getNotes() {
		return notes;
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
	public List<Permission> getPermissions() {
		return this.permissions;
	}

	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
}
