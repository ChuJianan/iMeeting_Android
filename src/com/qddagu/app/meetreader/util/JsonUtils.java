package com.qddagu.app.meetreader.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * json处理工具
 * 
 * @author Administrator
 * 
 */
public class JsonUtils {
	public final static String PATTERN = "yyyy-MM-dd HH:mm:ss";
	public final static Gson gson = new GsonBuilder().setDateFormat(PATTERN).create();
	
	public static String toJson(Object src) throws JsonSyntaxException {
		return gson.toJson(src);
	}
	
	public static <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
		return gson.fromJson(json, classOfT);
	}
}
