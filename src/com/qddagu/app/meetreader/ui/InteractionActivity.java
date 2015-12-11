package com.qddagu.app.meetreader.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.api.ApiClient;
import com.qddagu.app.meetreader.bean.Interaction;
import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.bean.URLs;
import com.qddagu.app.meetreader.bean.User;
import com.qddagu.app.meetreader.util.JsonUtils;
import com.qddagu.app.meetreader.util.StringUtils;
import com.qddagu.app.meetreader.util.UIHelper;

public class InteractionActivity extends BaseActivity{
private EditText mText;
private Meeting meeting;
private ProgressDialog progress;
private Handler progressHandler;
private User user;
String t=null;
private int iprogress=0;
private ProgressDialog mLoadingDialog;
protected void onCreate(Bundle savedInstanceState) {
	setContentView(R.layout.activity_ainteraction);
	super.onCreate(savedInstanceState);
	
	meeting = appContext.Meeting();
	user=appContext.getUserInfo();
	setTitle("提问："+meeting.getTitle());
	initView();
	}
private void initView(){
	mLoadingDialog = new ProgressDialog(this);
	mLoadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	mLoadingDialog.setTitle("提示");
	mLoadingDialog.setMessage("正在加载……");
	mLoadingDialog.setCancelable(false);
	mText = (EditText) findViewById(R.id.text);
	View save = findViewById(R.id.save);
	if (save != null) {
		save.setOnClickListener(this);
		save.setVisibility(View.VISIBLE);
	}
	
}
private void inDate(){
	if(!validate()){
		UIHelper.ToastMessage(this, "提问不能为空");
		return;
	}
	new Thread(){
		public void run(){
	Interaction interaction=new Interaction();
	interaction.setAskQuestion(mText.getText().toString());
	interaction.setMeetId(meeting.getId());
	interaction.setUserId(user.getId());
		try {
			try {
				t=appContext.saveGuest(URLs.SAVE_GUEST, interaction);
			} catch (AppException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
		
	}.start();
}
private boolean validate() {
	return !StringUtils.isEmpty(mText.getText().toString().trim());
}
private static final int MAX_PROGRESS=20;
@SuppressLint("HandlerLeak")
private void load(int style){
	
	 progress = new ProgressDialog(this);   
	 progress.setTitle("提示");
   progress.setMessage("正在上传数据，请稍后...");   
   progress.setProgress(style);
   progress.setMax(MAX_PROGRESS);
   progress.show();

    progressHandler=new Handler(){
   	public void handleMessage(Message msg){
   		//super.handleMessage(msg);
   		if(iprogress==MAX_PROGRESS){
   		
   		}
   		if(t!=null){
   			
   		
   			progress.dismiss();	    			
   			   Toast.makeText(getBaseContext(), "提交成功！",Toast.LENGTH_LONG ).show(); 
  				finish();
   		}else{
   			iprogress++;
   			progress.incrementProgressBy(1);
   		
   			progressHandler.sendEmptyMessageDelayed(1, 50+new Random().nextInt(500));
   		}
   	}
   };
   iprogress=(Integer) ((iprogress > 0)?progress:0);
   progress.setProgress(iprogress);
   progressHandler.sendEmptyMessage(1);
	
}
@Override
public void onClick(View v) {
	switch (v.getId()) {
	case R.id.save:
		inDate();
		load(ProgressDialog.STYLE_HORIZONTAL);
		break;
	default:
		super.onClick(v);
		break;
	}
}
}
