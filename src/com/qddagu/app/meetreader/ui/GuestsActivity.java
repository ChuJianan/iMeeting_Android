package com.qddagu.app.meetreader.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.adapter.ListViewGuestAdapter;
import com.qddagu.app.meetreader.bean.Guest;
import com.qddagu.app.meetreader.bean.Guests;
import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.util.DateUtils;
import com.qddagu.app.meetreader.util.UIHelper;
import com.qddagu.app.meetreader.widget.PullToRefreshListView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

@SuppressLint("HandlerLeak")
public class GuestsActivity extends BaseActivity {
	private PullToRefreshListView mGuestsView;
	private View mGuestsFooter;
	private TextView mGuestsMore;
	private ProgressBar mGuestsProgress;
	private List<Guest> mGuestsData = new ArrayList<Guest>();
	private ListViewGuestAdapter mGuestsAdapter;
	private Handler mGuestsHandler;
	private Meeting meeting;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_guests);
		super.onCreate(savedInstanceState);
		
		meeting = appContext.Meeting();
		if (meeting == null) {
			UIHelper.ToastMessage(this, "请先选择会议！");
			finish();
			return;
		}
		
		setTitle(meeting.getTitle());
		
		initView();
		initData();
	}
	
	private void initView() {
		mGuestsAdapter = new ListViewGuestAdapter(this, mGuestsData);
		mGuestsFooter = getLayoutInflater().inflate(R.layout.listview_footer, null);
		mGuestsMore = (TextView)mGuestsFooter.findViewById(R.id.listview_foot_more);
		mGuestsProgress = (ProgressBar)mGuestsFooter.findViewById(R.id.listview_foot_progress);
		mGuestsView = (PullToRefreshListView) findViewById(R.id.list);
		mGuestsView.addFooterView(mGuestsFooter);
		mGuestsView.setAdapter(mGuestsAdapter);
		mGuestsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//点击头部、底部栏无效
        		if(position == 0 || view == mGuestsFooter) return;
        		
        		Guest guest = (Guest) mGuestsAdapter.getItem(position);
        		UIHelper.showGuest(GuestsActivity.this, guest);
			}
		});
		mGuestsView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadGuestsData(mGuestsHandler, UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});
		View refresh = findViewById(R.id.refresh);
		if (refresh != null) {
			refresh.setVisibility(View.VISIBLE);
		}
	}
	
	private void initData() {
		initGuestsData();
	}
	
	private void initGuestsData() {
		mGuestsHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what >= 0) {
					Guests list = (Guests)msg.obj;
					mGuestsData.clear();
					mGuestsData.addAll(list.getGuestList());
					//刷新列表
					mGuestsView.setTag(UIHelper.LISTVIEW_DATA_FULL);
					mGuestsAdapter.notifyDataSetChanged();
					mGuestsMore.setText(R.string.load_full);
				} else if (msg.what == -1) {
					mGuestsMore.setText(R.string.load_error);
					((AppException)msg.obj).makeToast(GuestsActivity.this);
				}
				if (mGuestsAdapter.getCount() == 0) {
					mGuestsMore.setText(R.string.load_empty);
				}
				mGuestsProgress.setVisibility(ProgressBar.GONE);
				if(msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH){
					mGuestsView.onRefreshComplete(DateUtils.format(new Date(), getString(R.string.pull_to_refresh_update_pattern)));
					mGuestsView.setSelection(0);
				}
			}
		};
		loadGuestsData(mGuestsHandler, UIHelper.LISTVIEW_ACTION_INIT);
	}
	
	private void loadGuestsData(final Handler handler, final int action) {
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
					Guests list = new Guests();
					list = appContext.getGuests();
					msg.what = list.getGuestList().size();
					msg.obj = list;
	            } catch (Exception e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				msg.arg1 = action;//告知handler当前action
                handler.sendMessage(msg);
			}
		}.start();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.refresh:
			mGuestsView.clickRefresh();
			break;
		default:
			super.onClick(v);
			break;
		}
	}
}
