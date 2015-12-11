package com.qddagu.app.meetreader;

import java.io.IOException;
import java.io.InputStream;

import com.networkbench.agent.impl.NBSAppAgent;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.bean.InitInfo;
import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.bean.User;
import com.qddagu.app.meetreader.ui.MainActivity;
import com.qddagu.app.meetreader.util.StringUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

/**
 * 应用程序启动类：显示欢迎界面并跳转到主界面
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
@SuppressLint("HandlerLeak")
public class AppStart extends Activity implements OnClickListener {

	private static final String VERSION_KEY = "version";
	private ImageView mBtnEnter;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View view = View.inflate(this, R.layout.app_start, null);
		setContentView(view);
		NBSAppAgent.setLicenseKey("d8bd3a33837242539d426e102f443369").withLocationServiceEnabled(true).start(this);
		mBtnEnter = (ImageView) findViewById(R.id.flip);
		mBtnEnter.setOnClickListener(this);

		//渐变展示启动屏
		AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
		aa.setDuration(1000);//2000
		view.startAnimation(aa);
		//处理初始化业务
		initThread = new Thread(doInit);
		initThread.start();
		
		//兼容低版本cookie（1.5版本以下，包括1.5.0,1.5.1）
		AppContext appContext = (AppContext)getApplication();
		String cookie = appContext.getProperty("cookie");
		if(StringUtils.isEmpty(cookie)) {
			String cookie_name = appContext.getProperty("cookie_name");
			String cookie_value = appContext.getProperty("cookie_value");
			if(!StringUtils.isEmpty(cookie_name) && !StringUtils.isEmpty(cookie_value)) {
				cookie = cookie_name + "=" + cookie_value;
				appContext.setProperty("cookie", cookie);
				appContext.removeProperty("cookie_domain","cookie_name","cookie_value","cookie_version","cookie_path");
			}
		}
    }
    
	private Thread initThread;
    private Runnable doInit = new Runnable() {
		@Override
		public void run() {
			try {
				init();
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			mHandler.obtainMessage(1).sendToTarget();
		}
	};

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1) {
				redirectTo(MainActivity.class);
			}
		}
	};
    
    /**
     * 跳转到...
     */
    private void redirectTo(Class<?> activity){        
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        finish();
    }
    
    private void init() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			int currentVersion = info.versionCode;
	    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    	int lastVersion = prefs.getInt(VERSION_KEY, 0);
	    	AppContext appContext = (AppContext) getApplication();
	    	if (currentVersion > lastVersion) {
	    	     //如果当前版本大于上次版本，该版本属于第一次启动
	    		 Meeting meeting = getMeeting();
	    		 if (meeting != null) {
		    		 appContext.setMeeting(meeting);
		    		 appContext.saveHistory(meeting);
	    		 }
	    	     //将当前版本写入preference中，则下次启动的时候，据此判断，不再为首次启动
	    	     prefs.edit().putInt(VERSION_KEY,currentVersion).commit();
	    	} else {
	    		//注释掉，未测试
				//非第一次启动，读取最后一次会议信息
				//List<Meeting> historys = appContext.getHistorys().getHistoryList();
				//if (!historys.isEmpty()) {
				//	Meeting meeting = historys.get(historys.size() - 1);
				//	appContext.setMeeting(meeting);
				//}
			}
	    	
	    	appContext.initUserInfo();	//初始化用户信息
	    	
//	    	InitInfo initInfo = appContext.appInit();
//			appContext.setHomeAds(initInfo.getAdvertisings());
//			User user = initInfo.getUser();
//			if (appContext.getUserId() == 0 && user != null) {
//				appContext.saveUserInfo(user);
//			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    //获取Init数据
    protected Meeting getMeeting() {
        Meeting meeting = null;
        try {
            InputStream in = getResources().getAssets().open("init.json");
            StringBuffer out = new StringBuffer();
            byte[] b = new byte[4096];
            for (int n; (n = in.read(b)) != -1;) {
                out.append(new String(b, 0, n));
            }
            meeting = Meeting.parse(out.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AppException e) {
            e.printStackTrace();
        }
        return meeting;
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.flip:
			initThread = null;
			redirectTo(MainActivity.class);
			break;
		}
	}
}