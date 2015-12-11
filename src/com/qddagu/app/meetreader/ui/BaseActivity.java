package com.qddagu.app.meetreader.ui;

import java.util.List;

import com.qddagu.app.meetreader.AppContext;
import com.qddagu.app.meetreader.AppManager;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.bean.Advertising;
import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.util.UIHelper;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * 应用程序Activity的基类
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-9-18
 */
public class BaseActivity extends Activity implements OnClickListener{
	protected AppContext appContext;	//全局Context
	private TextView mTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);
		
		appContext = (AppContext) getApplication();

		//初始化通用组件
		initBaseView();
	}
	
	private void initBaseView() {
		mTitle = (TextView) findViewById(R.id.title);
		View back = findViewById(R.id.back);
    	if(back != null) {
    		back.setOnClickListener(this);
    	}
    	View refresh = findViewById(R.id.refresh);
    	if(refresh != null) {
    		refresh.setOnClickListener(this);
    	}

    	//加载广告
    	loadAdvertising();
	}
	
	protected void setTitle(String title) {
		if(mTitle != null)
			mTitle.setText(title);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		//结束Activity&从堆栈中移除
		AppManager.getAppManager().finishActivity(this);
	}
	
	/**
	 * 加载广告
	 */
	private void loadAdvertising() {
		Meeting meeting = appContext.Meeting();
		if (meeting == null) {
			return;
		}
		List<Advertising> ads = meeting.getAdvertisings();
		UIHelper.loadAdvertising(this, ads);
	}
}
