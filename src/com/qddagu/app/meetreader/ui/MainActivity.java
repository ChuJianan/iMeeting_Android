package com.qddagu.app.meetreader.ui;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.networkbench.agent.impl.NBSAppAgent;
import com.qddagu.app.meetreader.AppContext;
import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.AppManager;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.adapter.MainPagerAdapter;
import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.bean.User;
import com.qddagu.app.meetreader.util.UIHelper;
import com.qddagu.app.meetreader.util.UpdateManager;
import com.zxing.activity.CaptureActivity;

public class MainActivity extends FragmentActivity{	
	private FragmentManager mFragmentManager;
	private MainPagerAdapter mPagerAdapter;
	private ViewPager mViewPager;
	private AppContext appContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//添加Activity到堆栈
		NBSAppAgent.setLicenseKey("d8bd3a33837242539d426e102f443369").withLocationServiceEnabled(true).start(this);
		AppManager.getAppManager().addActivity(this);
		setContentView(R.layout.activity_main);
		appContext = (AppContext) getApplication();
		
		mFragmentManager = getSupportFragmentManager();
		
		initView();
		if (appContext.Meeting() != null) {
			loadMeeting(appContext.Meeting().getUrls().Meeting());
		}
		
		//检查新版本
		UpdateManager.getUpdateManager().checkAppUpdate(this, false);
	}
	
	private void initView() {
		
		final LinearLayout ll = (LinearLayout) findViewById(R.id.bottom_bar);
		
		mPagerAdapter = new MainPagerAdapter(this, mFragmentManager);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {	
				for (int i = 0; i < ll.getChildCount(); i++) {
					ll.getChildAt(i).setSelected(false);
				}
				int i = arg0 < 2 ? arg0 : arg0 + 1;
				ll.getChildAt(i).setSelected(true);
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) { }
			@Override
			public void onPageScrollStateChanged(int arg0) { }
		});
		
		View.OnClickListener cl = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.btn_scan) {
					Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
					startActivityForResult(intent, 0);
					return;
				}
				
				for (int i = 0; i < ll.getChildCount(); i++) {
					ll.getChildAt(i).setSelected(false);
				}
				v.setSelected(true);
				
				switch (v.getId()) {
				case R.id.btn_home:
					mViewPager.setCurrentItem(0, false);
					break;
				case R.id.btn_cloud:
					mViewPager.setCurrentItem(1, false);
					break;
				case R.id.btn_scan:
					Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
					startActivityForResult(intent, 0);
					break;
				case R.id.btn_notice:
					mViewPager.setCurrentItem(2, false);
					break;
				case R.id.btn_more:
					mViewPager.setCurrentItem(3, false);
					break;
				}
			}
		};
		findViewById(R.id.btn_home).setSelected(true);
		findViewById(R.id.btn_home).setOnClickListener(cl);
		findViewById(R.id.btn_more).setOnClickListener(cl);
		findViewById(R.id.btn_scan).setOnClickListener(cl);
		findViewById(R.id.btn_cloud).setOnClickListener(cl);
		findViewById(R.id.btn_notice).setOnClickListener(cl);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0:
			//处理扫描结果
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				String scanResult = bundle.getString("result");
				if (validateUrl(scanResult)) {
					loadMeeting(scanResult);
				}
			}
			break;
		case FavoriteActivity.REQUEST_CODE://显示收藏会议
			if (resultCode == RESULT_OK) {
				//showFragment(MainActivity.FRAGMENT_MEETING);
				UIHelper.showMeeting(MainActivity.this);
			}
			break;
		}
	}
	
	/**
	 * 获取会议
	 * @param handler
	 */
	public void loadMeeting(final String url) {
		final ProgressDialog mLoadingDialog = new ProgressDialog(this);
		mLoadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mLoadingDialog.setTitle("提示");
		mLoadingDialog.setMessage("正在获取会议信息...");
		mLoadingDialog.setCancelable(false);
		mLoadingDialog.show();
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				mLoadingDialog.dismiss();
				if(msg.what == 1) { //会议加载成功
					Meeting meeting = ((Meeting)msg.obj);
					User user = meeting.getUser();
					if ( user != null) {
						//存在用户信息则保存
						appContext.saveUserInfo(user);
					}
					appContext.setMeeting(meeting);//设置当前会议
					appContext.saveHistory(meeting);//保存会议历史
					
					
					UIHelper.showMeeting(MainActivity.this);
				} else if (msg.what == 0) { //暂无会议
					UIHelper.ToastMessage(MainActivity.this, "暂无会议");
				} else if (msg.what == -1 && msg.obj != null) {
					((AppException)msg.obj).makeToast(MainActivity.this);
				}				
			}
		};
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					Meeting meeting = appContext.getMeeting(url);
					msg.what = (meeting != null && meeting.getId() > 0) ? 1 : 0;
					msg.obj = meeting;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
                handler.sendMessage(msg);
			}
		}.start();
	} 
	
	private boolean validateUrl(String url) {
		boolean flag = true;
		try {
			if(!new URL(url).getPath().contains("meeting"))
				throw new MalformedURLException();
		} catch (MalformedURLException e) {
			UIHelper.ToastMessage(this, "该二维码中不包含有效的会议信息");
		}
		return flag;
	}
	
	/**
	 * 监听返回--是否退出程序
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean flag = true;
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			//是否退出应用
			UIHelper.Exit(this);
		}else{
			flag = super.onKeyDown(keyCode, event);
		}
		return flag;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		//结束Activity&从堆栈中移除
		AppManager.getAppManager().finishActivity(this);
	}
}
