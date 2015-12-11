package com.qddagu.app.meetreader.ui;

import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.util.UpdateManager;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * 关于我们
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class AboutActivity extends BaseActivity{
	
	private TextView mVersion;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_about);
		super.onCreate(savedInstanceState);
		
		//获取客户端版本信息
        try { 
        	PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
        	mVersion = (TextView)findViewById(R.id.app_version);
    		mVersion.setText("V"+info.versionName+" For Android");
        } catch (NameNotFoundException e) {    
			e.printStackTrace(System.err);
		} 
        
	}
}
