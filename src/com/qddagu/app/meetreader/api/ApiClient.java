package com.qddagu.app.meetreader.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.JsonParseException;
import com.qddagu.app.meetreader.AppContext;
import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.bean.Guests;
import com.qddagu.app.meetreader.bean.InitInfo;
import com.qddagu.app.meetreader.bean.Interaction;
import com.qddagu.app.meetreader.bean.User;
import com.qddagu.app.meetreader.bean.Users;
import com.qddagu.app.meetreader.bean.Comment;
import com.qddagu.app.meetreader.bean.Comments;
import com.qddagu.app.meetreader.bean.Files;
import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.bean.Notices;
import com.qddagu.app.meetreader.bean.URLs;
import com.qddagu.app.meetreader.bean.Update;
import com.qddagu.app.meetreader.util.JsonUtils;
import com.qddagu.app.meetreader.util.StringUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

/**
 * API客户端接口：用于访问网络数据
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ApiClient {

	public static final String UTF_8 = "UTF-8";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";
	
	private final static int TIMEOUT_CONNECTION = 20000;
	private final static int TIMEOUT_SOCKET = 20000;
	private final static int RETRY_TIME = 3;

	private static String appCookie;
	private static String appUserAgent;

	public static void cleanCookie() {
		appCookie = "";
	}
	
	private static String getCookie(AppContext appContext) {
		if(appCookie == null || appCookie == "") {
			appCookie = appContext.getProperty("cookie");
		}
		return appCookie;
	}
	
	private static String getUserAgent(AppContext appContext) {
		if(appUserAgent == null || appUserAgent == "") {
			StringBuilder ua = new StringBuilder("iMeeting");
			ua.append('/'+appContext.getPackageInfo().versionName+'_'+appContext.getPackageInfo().versionCode);//App版本
			ua.append("/Android");//手机系统平台
			ua.append("/"+android.os.Build.VERSION.RELEASE);//手机系统版本
			ua.append("/"+android.os.Build.MODEL); //手机型号
			ua.append("/"+appContext.getAppId());//客户端唯一标识
			appUserAgent = ua.toString();
		}
		return appUserAgent;
	}
	
	private static HttpClient getHttpClient() {        
        HttpClient httpClient = new HttpClient();
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // 设置 默认的超时重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		// 设置 连接超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
		// 设置 读数据超时时间 
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
		// 设置 字符集
		httpClient.getParams().setContentCharset(UTF_8);
		return httpClient;
	}	
	
	private static GetMethod getHttpGet(String url, String cookie, String userAgent) throws MalformedURLException {
		GetMethod httpGet = new GetMethod(url);
		// 设置 请求超时时间
		httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpGet.setRequestHeader("Host", Uri.parse(url).getHost());
		httpGet.setRequestHeader("Connection","Keep-Alive");
		httpGet.setRequestHeader("Cookie", cookie);
		httpGet.setRequestHeader("User-Agent", userAgent);
		return httpGet;
	}
	
	private static PostMethod getHttpPost(String url, String cookie, String userAgent) throws MalformedURLException {
		PostMethod httpPost = new PostMethod(url);
		// 设置 请求超时时间
		httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpPost.setRequestHeader("Host", Uri.parse(url).getHost());
		httpPost.setRequestHeader("Connection","Keep-Alive");
		httpPost.setRequestHeader("Cookie", cookie);
		httpPost.setRequestHeader("User-Agent", userAgent);
		return httpPost;
	}
	
	public static String makeURL(String p_url, Map<String, Object> params) {
		StringBuilder url = new StringBuilder(p_url);
		if(url.indexOf("?")<0)
			url.append('?');

		for(String name : params.keySet()){
			url.append('&');
			url.append(name);
			url.append('=');
			url.append(String.valueOf(params.get(name)));
			//不做URLEncoder处理
			//url.append(URLEncoder.encode(String.valueOf(params.get(name)), UTF_8));
		}

		return url.toString().replace("?&", "?");
	}
	
	/**
	 * get请求URL
	 * @param appContext
	 * @param url
	 * @return JSON
	 * @throws AppException
	 * @throws JSONException 
	 */
	private static String http_get(AppContext appContext, String url) throws AppException, JSONException {
		String json = "";
		JSONObject result = new JSONObject(_get(appContext, url));
		if(result.getInt("status") != 1)
			throw AppException.custom(result.getString("reason"));
		Iterator<?> it = result.keys();
		while (it.hasNext()) {
			String key = it.next().toString();
			if(!"status".equals(key) && !"reason".equals(key))
				json = result.getString(key);
		}
		return json;
	}
	
	/**
	 * 公用get方法
	 * @param url
	 * @throws AppException 
	 */
	private static String _get(AppContext appContext, String url) throws AppException {	
		if (!StringUtils.isUrl(url)) {
			return "";
		}
		//System.out.println("get_url==> "+url);
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);
		
		HttpClient httpClient = null;
		GetMethod httpGet = null;

		String responseBody = "";
		int time = 0;
		do{
			try 
			{
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, cookie, userAgent);			
				int statusCode = httpClient.executeMethod(httpGet);
				if(statusCode != HttpStatus.SC_OK) {
		        	throw AppException.http(statusCode);
		        } else if(statusCode == HttpStatus.SC_OK) {
		            Cookie[] cookies = httpClient.getState().getCookies();
		            String tmpcookies = "";
		            for (Cookie ck : cookies) {
		                tmpcookies += ck.toString()+";";
		            }
		            //保存cookie   
	        		if(appContext != null && tmpcookies != ""){
	        			appContext.setProperty("cookie", tmpcookies);
	        			appCookie = tmpcookies;
	        		}
		        }
				responseBody = httpGet.getResponseBodyAsString();
				//System.out.println("XMLDATA=====>"+responseBody);
				break;				
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);
		
		return responseBody;
	}
	
	/**
	 * 公用post方法
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException
	 */
	private static String _post(AppContext appContext, String url, Map<String, Object> params, Map<String,File> files) throws AppException {
		if (!StringUtils.isUrl(url)) {
			return "";
		}
		//System.out.println("post_url==> "+url);
		String cookie = getCookie(appContext);
		String userAgent = getUserAgent(appContext);
		
		HttpClient httpClient = null;
		PostMethod httpPost = null;
		
		//post表单参数处理
		int length = (params == null ? 0 : params.size()) + (files == null ? 0 : files.size());
		Part[] parts = new Part[length];
		int i = 0;
        if(params != null)
        for(String name : params.keySet()){
        	parts[i++] = new StringPart(name, String.valueOf(params.get(name)), UTF_8);
        	//System.out.println("post_key==> "+name+"    value==>"+String.valueOf(params.get(name)));
        }
        if(files != null)
        for(String file : files.keySet()){
        	try {
				parts[i++] = new FilePart(file, files.get(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        	//System.out.println("post_key_file==> "+file);
        }
		
		String responseBody = "";
		int time = 0;
		do{
			try {
				httpClient = getHttpClient();
				httpPost = getHttpPost(url, cookie, userAgent);	        
		        httpPost.setRequestEntity(new MultipartRequestEntity(parts,httpPost.getParams()));		        
		        int statusCode = httpClient.executeMethod(httpPost);
		        if(statusCode != HttpStatus.SC_OK) {
		        	throw AppException.http(statusCode);
		        } else if(statusCode == HttpStatus.SC_OK) {
		            Cookie[] cookies = httpClient.getState().getCookies();
		            String tmpcookies = "";
		            for (Cookie ck : cookies) {
		                tmpcookies += ck.toString()+";";
		            }
		            //保存cookie   
	        		if(appContext != null && tmpcookies != ""){
	        			appContext.setProperty("cookie", tmpcookies);
	        			appCookie = tmpcookies;
	        		}
		        }
		     	responseBody = httpPost.getResponseBodyAsString();
		        //System.out.println("XMLDATA=====>"+responseBody);
		     	break;	     	
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpPost.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);
        
        return responseBody;
	}
	
	/**
	 * post请求URL
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException 
	 * @throws IOException 
	 * @throws JSONException 
	 * @throws  
	 */
	private static String http_post(AppContext appContext, String url, Map<String, Object> params, Map<String,File> files) throws AppException, JSONException {  
		String json = "";
		JSONObject result = new JSONObject(_post(appContext, url, params, files));
		if(result.getInt("status") != 1)
			throw AppException.custom(result.getString("reason"));
		Iterator<?> it = result.keys();
		while (it.hasNext()) {
			String key = it.next().toString();
			if(!"status".equals(key) && !"reason".equals(key))
				json = result.getString(key);
		}
		return json;
	}
	/**
	 * 向服务端发送数据
	 * @param urls
	 * @param gson
	 * @return
	 * @throws IOException
	 */
	public static String setHttp(String urls,String gson) throws IOException{
		DefaultHttpClient client = new DefaultHttpClient();
		String result = null;
		HttpPost post = new HttpPost(urls);


		try {
			

		StringEntity entity = new StringEntity(gson,HTTP.UTF_8);
		post.setEntity(entity);
		HttpResponse responString = client.execute(post);
		if (responString.getStatusLine().getStatusCode() == 200) {
	        // 获取返回的数据
	        result = EntityUtils.toString(responString.getEntity(), "UTF-8");
	    }
		} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (ClientProtocolException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
	return result;
	}
	/**
	 * 检查版本更新
	 * @param url
	 * @return
	 */
	public static Update checkVersion(final AppContext appContext) throws AppException {
		String url = makeURL(URLs.UPDATE, new HashMap<String, Object>(){{
			put("mac", appContext.getMacAddress());
		}});
		try{
			return Update.parse(http_get(appContext, url));	
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 获取网络图片
	 * @param url
	 * @return
	 */
	public static Bitmap getNetBitmap(String url) throws AppException {
		if (!StringUtils.isUrl(url)) {
			return null;
		}
		//System.out.println("image_url==> "+url);
		HttpClient httpClient = null;
		GetMethod httpGet = null;
		Bitmap bitmap = null;
		int time = 0;
		do{
			try 
			{
				httpClient = getHttpClient();
				httpGet = getHttpGet(url, null, null);
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
		        InputStream inStream = httpGet.getResponseBodyAsStream();
		        bitmap = BitmapFactory.decodeStream(inStream);
		        inStream.close();
		        break;
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				if (httpGet != null) {
					httpGet.releaseConnection();
				}
				httpClient = null;
			}
		}while(time < RETRY_TIME);
		return bitmap;
	}
	
	/**
	 * 获取会议文件清单
	 * @param appContext
	 * @return
	 * @throws AppException 
	 */
	public static Files getFiles(final AppContext appContext, String url, final int pageNo) throws AppException {
		url = makeURL(url, new HashMap<String, Object>(){{
			put("pageNo", pageNo);
			put("mac", appContext.getMacAddress());
		}});
		try{
			return Files.parse(http_get(appContext, url));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 获取会议文件评论列表
	 * @param appContext
	 * @return
	 * @throws AppException 
	 */
	public static Comments getComments(final AppContext appContext, String url, final int fileId) throws AppException {
		url = makeURL(url, new HashMap<String, Object>(){{
			put("id", fileId);
			put("mac", appContext.getMacAddress());
		}});
		try{
			return Comments.parse(http_get(appContext, url));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 保存用户信息
	 * @param appContext
	 * @param user
	 * @param logo
	 * @throws AppException
	 */
	public static User saveUser(AppContext appContext, String url, User user, File logo) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("id", user.getId());
		params.put("name", user.getName());
		params.put("post", user.getPost());
		params.put("site", user.getSite());
		params.put("emial", user.getEmail());
		params.put("phone", user.getPhone());
		params.put("mobile", user.getMobile());
		params.put("company", user.getCompany());
		params.put("address", user.getAddress());
		params.put("fax", user.getFax());
		params.put("mac", appContext.getMacAddress());
		Map<String, File> files = new HashMap<String, File>();
		files.put("logoImg", logo);
		try {
			return User.parse(http_post(appContext, url, params, files));
		} catch (Exception e) {
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 递交名片
	 * @param appContext
	 * @param url
	 * @throws AppException
	 */
	public static void sendCard(final AppContext appContext, String url) throws AppException {
		url = makeURL(url, new HashMap<String, Object>(){{
			put("mac", appContext.getMacAddress());
		}});
		try {
			http_get(appContext, url);
		} catch (Exception e) {
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 获取名片列表
	 * @param appContext
	 * @return
	 * @throws AppException 
	 */
	public static Users getCards(final AppContext appContext, String url, final int pageNo) throws AppException {
		url = makeURL(url, new HashMap<String, Object>(){{
			put("pageNo", pageNo);
			put("mac", appContext.getMacAddress());
		}});
		try{
			return Users.parse(http_get(appContext, url));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 获取会议预告列表
	 * @param appContext
	 * @return
	 * @throws AppException 
	 */
	public static Notices getNotices(final AppContext appContext,final int pageNo) throws AppException {
		String url = makeURL(URLs.NOTICE, new HashMap<String, Object>(){{
			put("pageNo", pageNo);
		}});
		try{
			return Notices.parse(http_get(appContext, url));
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 获取会议嘉宾列表
	 * @param appContext
	 * @return
	 * @throws AppException 
	 */
	public static Guests getGuests(final AppContext appContext, String url) throws AppException {
		url = makeURL(url, new HashMap<String, Object>(){{
			put("mac", appContext.getMacAddress());
		}});
		try{
			return Guests.parse(http_get(appContext, url));
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	
	/**
	 * 获取会议签到列表
	 * @param appContext
	 * @return
	 * @throws AppException 
	 */
	public static Users getSigns(AppContext appContext, String url, 
			final int userId, final int pageNo) throws AppException {
		final String mac = appContext.getMacAddress();
		url = makeURL(url, new HashMap<String, Object>(){{
			put("userId", userId);
			put("pageNo", pageNo);
			put("mac", mac);
		}});
		try{
			return Users.parse(http_get(appContext, url));
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 我要签到
	 * @param appContext
	 * @param card
	 * @throws AppException
	 */
	public static User saveSign(AppContext appContext, String url, int userId) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userId", userId);
		params.put("mac", appContext.getMacAddress());
		try {
			return User.parse(http_post(appContext, url, params, null));
		} catch (Exception e) {
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 我要报名
	 * @param appContext
	 * @param card
	 * @throws AppException
	 */
	public static User saveJoin(AppContext appContext, String url, int userId) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("userId", userId);
		params.put("mac", appContext.getMacAddress());
		try {
			return User.parse(http_post(appContext, url, params, null));
		} catch (Exception e) {
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 发表评论
	 * @param appContext
	 * @param comment
	 * @throws AppException
	 */
	public static Comment saveComment(AppContext appContext, String url, Comment comment) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("fileId", comment.getFileId());
		params.put("title", comment.getTitle());
		params.put("content", comment.getContent());
		params.put("mac", appContext.getMacAddress());
		try {
			return Comment.parse(http_post(appContext, url, params, null));
		} catch (Exception e) {
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 应用初始化
	 * @param appContext
	 * @param url
	 * @return
	 * @throws AppException 
	 */
	public static InitInfo appInit(AppContext appContext) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("mac", appContext.getMacAddress());
		try {
			return InitInfo.parse(http_post(appContext, URLs.APP_INIT, params, null));
		} catch (JsonParseException e) {
			throw AppException.json(e);
		} catch (JSONException e) {
			throw AppException.json(e);
		}catch (Exception e) {
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	/**
	 * 提交问题
	 * @param url
	 * @param gson
	 * @return
	 * @throws IOException
	 * @throws JSONException 
	 * @throws AppException 
	 */
	public static String saveguest(AppContext appContext,String url,Interaction interaction) throws IOException, AppException, JSONException{
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("question", interaction.getAskQuestion());
		map.put("meetId", interaction.getMeetId());
		map.put("userId", interaction.getUserId());
		return (http_post(appContext,url, map,null));
	}
	/**
	 * 获取会议信息
	 * @param appContext
	 * @param url 会议地址
	 * @param macAddr 本机MAC地址
	 * @param user 用户信息
	 * @return	会议信息
	 * @throws AppException
	 */
	public static Meeting getMeeting(AppContext appContext, String url, String macAddr, User user) throws AppException {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("mac", macAddr);
		params.put("user", JsonUtils.toJson(user));
		try {
			return Meeting.parse(http_post(appContext, url, params, null));
		} catch (JsonParseException e) {
			throw AppException.json(e);
		} catch (JSONException e) {
			throw AppException.json(e);
		}catch (Exception e) {
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
}
