package com.qddagu.app.meetreader.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.util.UIHelper;

/**
 * 会议签到
 * @author SYZ
 */
@SuppressLint("HandlerLeak")
public class JoinActivity extends BaseActivity {
	private ProgressDialog mLoadingDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (appContext.getUserId() == 0) {
			UIHelper.ToastMessage(JoinActivity.this, "请先设置名片信息");
			UIHelper.showUser(JoinActivity.this);
			finish();
			return;
		}
		
		initView();
		saveJoin();
	}
	
	private void initView() {
		mLoadingDialog = new ProgressDialog(this);
		mLoadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mLoadingDialog.setTitle("提示");
		mLoadingDialog.setMessage("正在加载……");
		mLoadingDialog.setCancelable(false);
	}
	
	/**
	 * 我要报名
	 */
	private void saveJoin() {
		mLoadingDialog.show();
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				mLoadingDialog.dismiss();
				if (msg.what == 1) {
					UIHelper.ToastMessage(JoinActivity.this, "报名成功");
				} else if (msg.what == -1) {
					((AppException)msg.obj).makeToast(JoinActivity.this);
				}
				finish();
			};
		};
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
					msg.obj = appContext.saveJoin(appContext.getUserId());
					msg.what = 1;
				} catch (AppException e) {
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			};
		}.start();
	}
}
