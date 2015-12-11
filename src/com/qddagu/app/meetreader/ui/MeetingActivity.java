package com.qddagu.app.meetreader.ui;

import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.adapter.GridViewMeetingAdapter;
import com.qddagu.app.meetreader.adapter.GridViewMeetingAdapter.Icon;
import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.util.DateUtils;
import com.qddagu.app.meetreader.util.StringUtils;
import com.qddagu.app.meetreader.util.UIHelper;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

public class MeetingActivity extends BaseActivity {

	private TextView mMeetingTitle;
	private TextView mMeetingTime;
	private TextView mMeetingPlace;
	private GridView mIconsView;
	private GridViewMeetingAdapter mIconsAdapter;
	private Meeting meeting;
	private ProgressDialog mLoadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_meeting);
		super.onCreate(savedInstanceState);
		
		meeting = appContext.Meeting();
		if (meeting == null) {
			UIHelper.ToastMessage(this, "请先选择会议！");
			finish();
			return;
		}
		
		setTitle("会议详情");

		initView(); //初始化视图
		initData(); //初始化数据
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		default:
			super.onClick(v);
			break;
		}
	}
	
	private void initView() {
		mLoadingDialog = new ProgressDialog(this);
		mLoadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mLoadingDialog.setTitle("提示");
		mLoadingDialog.setMessage("正在加载……");
		mLoadingDialog.setCancelable(false);
		
		mMeetingTitle = (TextView) findViewById(R.id.meeting_title);
		mMeetingTime = (TextView) findViewById(R.id.meeting_time);
		mMeetingPlace = (TextView) findViewById(R.id.meeting_place);
		mIconsView = (GridView) findViewById(R.id.icons);
		mIconsAdapter = new GridViewMeetingAdapter(this);
		mIconsView.setAdapter(mIconsAdapter);
		mIconsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Icon icon = (Icon) mIconsAdapter.getItem(position);
				if (icon.activity == null) {
					UIHelper.ToastMessage(MeetingActivity.this, "功能升级中....");
				} else if ((icon.activity == JoinActivity.class 
							|| icon.activity == SignActivity.class
							|| icon.activity == CardWallActivity.class||icon.activity==InteractionsActivity.class)
						&& appContext.getUserId() == 0) {
					UIHelper.showUserDialog(MeetingActivity.this);
				} else if (icon.activity == BrowserActivity.class
						&& "会议微博".equals(icon.name)) {
					String url = meeting.getWeibo();
					if (!StringUtils.isEmpty(url)) {
						UIHelper.showBrowser(MeetingActivity.this, url);
					} else {
						UIHelper.ToastMessage(MeetingActivity.this, "会议微博暂未开通");
					}
				} else {
					UIHelper.showActivity(MeetingActivity.this, icon.activity);
				}
			}
		});
	}
	
	private void initData() {
		String[] titles = meeting.getTitle().split(" ");
		if (titles.length > 1) {
			mMeetingTitle.setText(titles[0] + "\n" + titles[titles.length - 1]);
		} else {
			mMeetingTitle.setText(meeting.getTitle());
		}
		String pattern = "MM-dd HH:mm";
		String time = DateUtils.format(meeting.getBeginTime(), pattern);
		time += " 至 " + DateUtils.format(meeting.getEndTime(), pattern);
		mMeetingTime.setText(time);
		mMeetingPlace.setText(meeting.getPlace());
	}
}
