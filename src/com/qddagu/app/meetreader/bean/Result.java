package com.qddagu.app.meetreader.bean;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import com.qddagu.app.meetreader.AppException;

/**
 * 数据操作结果实体类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class Result {
	
	public List<Meeting> clouds = Collections.emptyList();	//会议云列表
	public List<Advertising> ads = Collections.emptyList();	//首页广告列表
	public List<Interaction> guests=Collections.emptyList();	//现场互动问答列表
	private int status;
	private String message;
	
	public boolean OK() {
		return status == 1;
	}
	public String Message() {
		return message;
	}

	/**
	 * 解析调用结果
	 * 
	 * @param jsonString
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	public static Result parse(String jsonString) throws IOException, AppException {
		Result result = null;
		
		return result;
	}

	@Override
	public String toString(){
		return String.format("RESULT: CODE:%d,MSG:%s", status, message);
	}

}
