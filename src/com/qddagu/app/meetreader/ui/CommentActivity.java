package com.qddagu.app.meetreader.ui;

import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.bean.Comment;
import com.qddagu.app.meetreader.bean.MtFile;
import com.qddagu.app.meetreader.util.StringUtils;
import com.qddagu.app.meetreader.util.UIHelper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

public class CommentActivity extends BaseActivity {
	private EditText mText;
	private Handler mCommentHandler;
	private ProgressDialog mLoadingDialog;
	private MtFile mFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_comment);
		super.onCreate(savedInstanceState);
		
		setTitle("发表评论");
		
		initView();
		initData();
	}
	
	private void initView() {
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
	
	private void initData() {
		mFile = (MtFile) getIntent().getSerializableExtra("file");
	}
	
	private Handler getCommentHandler() {
		if(mCommentHandler == null) {
			mCommentHandler = new Handler(){
				@Override
				public void dispatchMessage(Message msg) {
					mLoadingDialog.dismiss();
					if(msg.what == 1) {
						Intent data = getIntent();
						data.putExtra("comment", (Comment)msg.obj);
						setResult(1, data);
						finish();
					} else if (msg.what == -1) {
						((AppException)msg.obj).makeToast(CommentActivity.this);
					}
				}
			};
		}
		return mCommentHandler;
	}
	
	private boolean validate() {
		return !StringUtils.isEmpty(mText.getText().toString().trim());
	}
	
	private void saveComment(final Handler handler) {
		if (!validate()) {
			UIHelper.ToastMessage(this, "评论不能为空");
			return;
		}
		mLoadingDialog.show();
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					Comment comment = new Comment();
					comment.setFileId(mFile.getId());
					comment.setTitle(mFile.getName() + "的评论");
					comment.setContent(mText.getText().toString());
					msg.obj = appContext.saveComment(comment);
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.save:
			this.saveComment(getCommentHandler());
			break;
		default:
			super.onClick(v);
			break;
		}
	}
}
