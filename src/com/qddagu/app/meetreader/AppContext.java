package com.qddagu.app.meetreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.qddagu.app.meetreader.api.ApiClient;
import com.qddagu.app.meetreader.bean.Comment;
import com.qddagu.app.meetreader.bean.Comments;
import com.qddagu.app.meetreader.bean.Favorites;
import com.qddagu.app.meetreader.bean.Files;
import com.qddagu.app.meetreader.bean.Guests;
import com.qddagu.app.meetreader.bean.Historys;
import com.qddagu.app.meetreader.bean.InitInfo;
import com.qddagu.app.meetreader.bean.Interaction;
import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.bean.Note;
import com.qddagu.app.meetreader.bean.Notes;
import com.qddagu.app.meetreader.bean.Notices;
import com.qddagu.app.meetreader.bean.User;
import com.qddagu.app.meetreader.bean.Users;
import com.qddagu.app.meetreader.db.FavoriteDB;
import com.qddagu.app.meetreader.db.HistoryDB;
import com.qddagu.app.meetreader.db.NoteDB;
import com.qddagu.app.meetreader.util.FileUtils;
import com.qddagu.app.meetreader.util.JsonUtils;
import com.qddagu.app.meetreader.util.Md5;
import com.qddagu.app.meetreader.util.MethodsCompat;
import com.qddagu.app.meetreader.util.StringUtils;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class AppContext extends Application {
	
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;
	
	public static final int PAGE_SIZE = 20;//默认分页大小
	private static final int CACHE_TIME = 60*60000;//缓存失效时间
	
	private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();
	
	private Meeting meeting;
	private int userId = 0;		//用户ID

	@Override
	public void onCreate() {
		super.onCreate();
        //注册App异常崩溃处理器
		mInstance = this;
		initEngineManager(this);
        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
	}
	
	/**
	 * 初始化用户信息
	 */
	public void initUserInfo() {
		User user = getUserInfo();
		if (user != null && user.getId() > 0) {
			 this.userId = user.getId();
		}
	}
	
	public int getUserId() {
		return this.userId;
	}
	
	/**
	 * 获取用户信息
	 * @return
	 */
	public User getUserInfo() {
		return JsonUtils.fromJson(getProperty("user"), User.class);
	}
	
	/**
	 * 保存用户信息
	 * @param user
	 */
	public void saveUserInfo(final User user) {
		this.userId = user.getId();
		setProperty("user", JsonUtils.toJson(user));
	}
	
	//获取JSON数据
    protected String getData(String fileName, String name) {
    	String data = "";
        try {
            InputStream in = getResources().getAssets().open(fileName);
            StringBuffer out = new StringBuffer();
            byte[] b = new byte[4096];
            for (int n; (n = in.read(b)) != -1;) {
                out.append(new String(b, 0, n));
            }
            JSONObject json = new JSONObject(out.toString());
            data = json.getString(name);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
			e.printStackTrace();
		}
        return data;
    }

	/**
	 * 检测当前系统声音是否为正常模式
	 * @return
	 */
	public boolean isAudioNormal() {
		AudioManager mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE); 
		return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
	}
	
	/**
	 * 检测网络是否可用
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}		
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if(!StringUtils.isEmpty(extraInfo)){
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}
	
	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}
	
	/**
	 * 获取App安装包信息
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try { 
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
		if(info == null) info = new PackageInfo();
		return info;
	}
	
	/**
	 * 获取App唯一标识
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if(StringUtils.isEmpty(uniqueID)){
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}
	
	/**
	 * 获取无线MAC地址
	 * @return
	 */
	public String getMacAddress(){ 
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE); 
        return wifiManager.getConnectionInfo().getMacAddress(); 
	}
	
	/**
	 * 获取会议文件清单
	 * @return
	 * @throws AppException 
	 */
	public Files getFiles(int pageNo) throws AppException {
		Files files = null;
		if(isNetworkConnected()) {
			files = ApiClient.getFiles(this, meeting.getUrls().Files(), pageNo);
		}
		if(files == null) 
			files = new Files();
		return files;
	}
	
	/**
	 * 获取会议文件评论
	 * @return
	 * @throws AppException 
	 */
	public Comments getComments(int fileId) throws AppException {
		Comments comments = new Comments();
		if(isNetworkConnected()) {
			comments = ApiClient.getComments(this, meeting.getUrls().Comments(), fileId);
		}
		return comments;
	}
	
	/**
	 * 获取名片列表
	 * @return
	 * @throws AppException 
	 */
	public Users getCards(int pageNo) throws AppException {
		Users cards = new Users();
		if(isNetworkConnected()) {
			cards = ApiClient.getCards(this, meeting.getUrls().Cards(), pageNo);
		}
		return cards;
	}
	
	/**
	 * 保存用户信息
	 * @param note
	 * @throws AppException
	 */
	public User saveUser(User user, File logo) throws AppException {
		User result = new User();
		if(isNetworkConnected()) {
			result = ApiClient.saveUser(this, meeting.getUrls().User(), user, logo);
		}
		return result;
	}
	
	/**
	 * 递交名片信息
	 * @param note
	 * @throws AppException
	 */
	public void sendCard() throws AppException {
		if(isNetworkConnected()) {
			ApiClient.sendCard(this, meeting.getUrls().Send());
		}
	}
	
	/**
	 * 我要签到
	 * @param note
	 * @throws AppException
	 */
	public User saveSign(int userId) throws AppException {
		User result = new User();
		if(isNetworkConnected()) {
			result = ApiClient.saveSign(this, meeting.getUrls().Sign(), userId);
		}
		return result;
	}
	
	/**
	 * 我要报名
	 * @param note
	 * @throws AppException
	 */
	public User saveJoin(int userId) throws AppException {
		User result = new User();
		if(isNetworkConnected()) {
			result = ApiClient.saveSign(this, meeting.getUrls().Join(), userId);
		}
		return result;
	}
	
	/**
	 * 获取会议预告列表
	 * @return
	 * @throws AppException 
	 */
	public Notices getNotices(int pageNo) throws AppException {
		Notices notices = new Notices();
		if(isNetworkConnected()) {
			notices = ApiClient.getNotices(this,pageNo);
		}
		return notices;
	}
	
	/**
	 * 获取会议嘉宾列表
	 * @return
	 * @throws AppException 
	 */
	public Guests getGuests() throws AppException {
		Guests guests = new Guests();
		if(isNetworkConnected()) {
			guests = ApiClient.getGuests(this, meeting.getUrls().Guests());
		}
		return guests;
	}
	
	/**
	 * 获取会议签到列表
	 * @return
	 * @throws AppException 
	 */
	public Users getSigns(int userId, int pageNo) throws AppException {
		Users signs = new Users();
		if(isNetworkConnected()) {
			signs = ApiClient.getSigns(this, meeting.getUrls().Signs(), userId, pageNo);
		}
		return signs;
	}
	
	/**
	 * 发表评论
	 * @param comment
	 * @return 
	 * @throws AppException
	 */
	public Comment saveComment(Comment comment) throws AppException {
		Comment result = new Comment();
		if(isNetworkConnected()) {
			result = ApiClient.saveComment(this, meeting.getUrls().Comment(), comment);
		}
		return result;
	}
	/**
	 * 发表提问
	 * @param url
	 * @param map
	 * @return
	 * @throws IOException
	 * @throws AppException
	 * @throws JSONException
	 */
	public String saveGuest(String url,Interaction map) throws IOException, AppException, JSONException{
		String t=null;
		t=ApiClient.saveguest(this, url, map);
		return t;
		
	}
	/**
	 * 获取会议文件笔记
	 * @return
	 * @throws AppException 
	 */
	public Notes getNotes(String fileUrl) throws AppException {
		return Notes.parse(new NoteDB(this).select(Md5.encode(fileUrl)));
	}
	
	/**
	 * 获取会议历史记录
	 * @param title
	 * @return
	 * @throws AppException
	 */
	public Historys getHistorys() throws AppException {
		return new Historys(new HistoryDB(this).select());
	}
	/**
	 * 获取会议收藏夹
	 * @param title
	 * @return
	 * @throws AppException
	 */
	public Favorites getFavorites() throws AppException {
		return new Favorites(new FavoriteDB(this).select());
	}
	
	/**
	 * 删除收藏会议
	 * @param meeting
	 */
	public void deleteFavorite(Meeting meeting) {
		new FavoriteDB(this).delete(meeting);
	}
	
	/**
	 * 删除历史会议
	 * @param meeting
	 */
	public void deleteHistory(Meeting meeting) {
		new HistoryDB(this).delete(meeting);
	}
	
	/**
	 * 删除笔记
	 * @param meeting
	 */
	public void deleteNote(Note note) {
		new NoteDB(this).delete(note.getId());
	}
	
	/**
	 * 历史会议
	 * @param meeting
	 */
	public void saveHistory(Meeting meeting) {
		new HistoryDB(this).save(meeting);
	}
	
	/**
	 * 收藏会议
	 * @param meeting
	 */
	public void saveFavorite(Meeting meeting) {
		new FavoriteDB(this).save(meeting);
	}
	
	/**
	 * 保存笔记
	 * @param note
	 * @throws AppException
	 */
	public void saveNote(Note note) throws AppException {
		new NoteDB(this).save(note);
	}
	
	/**
	 * 应用初始化
	 * @return
	 * @throws AppException
	 */
	public InitInfo appInit() throws AppException {
		InitInfo info = null;
		if (isNetworkConnected()) {
			info = ApiClient.appInit(this);
		}
		if (info == null) {
			info = new InitInfo();
		}
		return info;
	}
	
	/**
	 * 获取会议信息
	 * @return
	 * @throws AppException
	 */
	public Meeting getMeeting(String url) throws AppException {
		//获取新的会议时重新开始新的会话，解决可能获取不到文件列表的问题
		//ApiClient.cleanCookie();	//清理cookies
		//this.cleanCookie();			//清理cookies
		
		Meeting meeting = null;
		if(isNetworkConnected()) {
			meeting = ApiClient.getMeeting(this, url, getMacAddress(), getUserInfo());
		}
		if(meeting == null) 
			meeting = new Meeting();
		return meeting;
	}
	
	/**
	 * 获取文档图片保存路径
	 * @return
	 */
	public String getImagePath() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		String cachePath = sdCardExist ? getExternalCacheDir().getAbsolutePath() : getCacheDir().getAbsolutePath();
		return cachePath + FileUtils.FILE_SPLITTER;
	}

	/**
	 * 设置服务器接口文件路径
	 * @param url
	 */
	public void setHostApiUrl(String url) {
		setProperty(AppConfig.CONF_URL_API_HOST, url);
	}
	
	/**
	 * 是否加载显示文章图片
	 * @return
	 */
	public boolean isLoadImage()
	{
		String perf_loadimage = getProperty(AppConfig.CONF_LOAD_IMAGE);
		//默认是加载的
		if(StringUtils.isEmpty(perf_loadimage))
			return true;
		else
			return StringUtils.toBool(perf_loadimage);
	}
	
	/**
	 * 设置是否加载文章图片
	 * @param b
	 */
	public void setConfigLoadimage(boolean b)
	{
		setProperty(AppConfig.CONF_LOAD_IMAGE, String.valueOf(b));
	}
	
	/**
	 * 清除保存的缓存
	 */
	public void cleanCookie()
	{
		removeProperty(AppConfig.CONF_COOKIE);
	}
	
	/**
	 * 判断缓存数据是否可读
	 * @param cachefile
	 * @return
	 */
	private boolean isReadDataCache(String cachefile) {
		return readObject(cachefile) != null;
	}
	
	/**
	 * 判断缓存是否存在
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile)
	{
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if(data.exists())
			exist = true;
		return exist;
	}
	
	/**
	 * 判断缓存是否失效
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile)
	{
		boolean failure = false;
		File data = getFileStreamPath(cachefile);
		if(data.exists() && (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
			failure = true;
		else if(!data.exists())
			failure = true;
		return failure;
	}
	
	/**
	 * 清除app缓存
	 */
	public void clearAppCache()
	{
		//清除webview缓存	  
		deleteDatabase("webview.db");  
		deleteDatabase("webview.db-shm");  
		deleteDatabase("webview.db-wal");  
		deleteDatabase("webviewCache.db");  
		deleteDatabase("webviewCache.db-shm");  
		deleteDatabase("webviewCache.db-wal");  
		//清除数据缓存
		clearCacheFolder(getExternalCacheDir(),System.currentTimeMillis());
		clearCacheFolder(getFilesDir(),System.currentTimeMillis());
		clearCacheFolder(getCacheDir(),System.currentTimeMillis());
		//2.2版本才有将应用缓存转移到sd卡的功能
		if(isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)){
			clearCacheFolder(MethodsCompat.getExternalCacheDir(this),System.currentTimeMillis());
		}
		//清除编辑器保存的临时内容
		Properties props = getProperties();
		for(Object key : props.keySet()) {
			String _key = key.toString();
			if(_key.startsWith("temp"))
				removeProperty(_key);
		}
	}	
	
	/**
	 * 清除缓存目录
	 * @param dir 目录
	 * @param numDays 当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {          
	    int deletedFiles = 0;         
	    if (dir!= null && dir.isDirectory()) {             
	        try {                
	            for (File child:dir.listFiles()) {    
	                if (child.isDirectory()) {              
	                    deletedFiles += clearCacheFolder(child, curTime);          
	                }  
	                if (child.lastModified() < curTime) {     
	                    if (child.delete()) {                   
	                        deletedFiles++;           
	                    }    
	                }    
	            }             
	        } catch(Exception e) {       
	            e.printStackTrace();    
	        }     
	    }       
	    return deletedFiles;     
	}
	
	/**
	 * 将对象保存到内存缓存中
	 * @param key
	 * @param value
	 */
	public void setMemCache(String key, Object value) {
		memCacheRegion.put(key, value);
	}
	
	/**
	 * 从内存缓存中获取对象
	 * @param key
	 * @return
	 */
	public Object getMemCache(String key){
		return memCacheRegion.get(key);
	}
	
	/**
	 * 保存磁盘缓存
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void setDiskCache(String key, String value) throws IOException {
		FileOutputStream fos = null;
		try{
			fos = openFileOutput("cache_"+key+".data", Context.MODE_PRIVATE);
			fos.write(value.getBytes());
			fos.flush();
		}finally{
			try {
				fos.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 获取磁盘缓存数据
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public String getDiskCache(String key) throws IOException {
		FileInputStream fis = null;
		try{
			fis = openFileInput("cache_"+key+".data");
			byte[] datas = new byte[fis.available()];
			fis.read(datas);
			return new String(datas);
		}finally{
			try {
				fis.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 保存对象
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try{
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			try {
				oos.close();
			} catch (Exception e) {}
			try {
				fos.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 读取对象
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file){
		if(!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try{
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable)ois.readObject();
		}catch(FileNotFoundException e){
		}catch(Exception e){
			e.printStackTrace();
			//反序列化失败 - 删除缓存文件
			if(e instanceof InvalidClassException){
				File data = getFileStreamPath(file);
				data.delete();
			}
		}finally{
			try {
				ois.close();
			} catch (Exception e) {}
			try {
				fis.close();
			} catch (Exception e) {}
		}
		return null;
	}

	public boolean containsProperty(String key){
		Properties props = getProperties();
		 return props.containsKey(key);
	}
	
	public void setProperties(Properties ps){
		AppConfig.getAppConfig(this).set(ps);
	}

	public Properties getProperties(){
		return AppConfig.getAppConfig(this).get();
	}
	
	public void setProperty(String key,String value){
		AppConfig.getAppConfig(this).set(key, value);
	}
	
	public String getProperty(String key){
		return AppConfig.getAppConfig(this).get(key);
	}
	public void removeProperty(String...key){
		AppConfig.getAppConfig(this).remove(key);
	}

	public Meeting Meeting() {
		return meeting;
	}

	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}
    private static AppContext mInstance = null;
    public boolean m_bKeyRight = true;
    public BMapManager mBMapManager = null;

    public static final String strKey = "EVUY5fSGV7I2UKG38X1MK4cy";
	
	
	public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if (!mBMapManager.init(strKey,new MyGeneralListener())) {
            Toast.makeText(AppContext.getInstance().getApplicationContext(), 
                    "BMapManager  初始化错误", Toast.LENGTH_LONG).show();
        }
	}
	
	public static AppContext getInstance() {
		return mInstance;
	}
	
	
	// 常用事件监听，用来处理�?常的网络错误，授权验证错误等
    static class MyGeneralListener implements MKGeneralListener {
        
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(AppContext.getInstance().getApplicationContext(), "您的网络出错啦！",
                    Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(AppContext.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
            // ...
        }
        @Override
        public void onGetPermissionState(int iError) {
        	//非零值表示key验证未�?�?
            if (iError != 0) {
                //授权Key错误�?
//                Toast.makeText(DemoApplication.getInstance().getApplicationContext(), 
//                        "请在 DemoApplication.java文件输入正确的授权Key,并检查您的网络连接是否正常！error: "+iError, Toast.LENGTH_LONG).show();
            	AppContext.getInstance().m_bKeyRight = false;
            }
            else{
            	AppContext.getInstance().m_bKeyRight = true;
//            	Toast.makeText(DemoApplication.getInstance().getApplicationContext(), 
//                        "key认证成功", Toast.LENGTH_LONG).show();
            }
        }
    }
}
