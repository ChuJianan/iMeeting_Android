package com.qddagu.app.meetreader.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.adapter.ListViewCardAdapter;
import com.qddagu.app.meetreader.bean.User;
import com.qddagu.app.meetreader.bean.Users;
import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.util.DateUtils;
import com.qddagu.app.meetreader.util.StringUtils;
import com.qddagu.app.meetreader.util.UIHelper;
import com.qddagu.app.meetreader.widget.PullToRefreshListView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class CardWallActivity extends BaseActivity {
	private PullToRefreshListView mCardsView;
	private View mCardsFooter;
	private TextView mCardsMore;
	private ProgressBar mCardsProgress;
	private List<User> mCardsData = new ArrayList<User>();
	private ListViewCardAdapter mCardAdapter;
	private Handler mCardsHandler;
	private int pageNo = 1;//当前页数
	private int pageSize = 20;//每页个数
	private Meeting meeting;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_cardwall);
		super.onCreate(savedInstanceState);
		
		if (appContext.getUserId() == 0) {
			UIHelper.ToastMessage(CardWallActivity.this, "请先设置名片信息");
			UIHelper.showUser(CardWallActivity.this);
			finish();
			return;
		}
		
		meeting = appContext.Meeting();
		if (!meeting.isSend()) {
			UIHelper.ToastMessage(CardWallActivity.this, "请先递交个人名片");
			UIHelper.showUser(CardWallActivity.this);
			finish();
			return;
		}
		setTitle("名片墙：" + meeting.getTitle());
		
		initView();
		initData();
	}
	
	private void initView() {
		mCardAdapter = new ListViewCardAdapter(this, mCardsData);
		mCardsFooter = getLayoutInflater().inflate(R.layout.listview_footer, null);
		mCardsMore = (TextView)mCardsFooter.findViewById(R.id.listview_foot_more);
		mCardsProgress = (ProgressBar)mCardsFooter.findViewById(R.id.listview_foot_progress);
		mCardsView = (PullToRefreshListView) findViewById(R.id.list);
		mCardsView.addFooterView(mCardsFooter);
		mCardsView.setAdapter(mCardAdapter);
		mCardsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//点击头部、底部栏无效
        		if(position == 0 || view == mCardsFooter) return;
        		
        		User card = (User) mCardAdapter.getItem(position);
        		UIHelper.showCard(CardWallActivity.this, card);
			}
		});
		mCardsView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				mCardsView.onScrollStateChanged(view, scrollState);
				
				// 数据为空--不用继续下面代码了
				if (mCardsData.isEmpty())
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(mCardsFooter) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				int lvDataState = StringUtils.toInt(mCardsView.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					mCardsView.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					mCardsMore.setText(R.string.load_ing);
					mCardsProgress.setVisibility(View.VISIBLE);
					// 当前pageNo+1
					pageNo++;
					loadCardsData(mCardsHandler, pageNo, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				mCardsView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
		mCardsView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadCardsData(mCardsHandler, 1, UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});
	}
	
	private void initData() {
		initCardsData();
	}
	
	private void initCardsData() {
		mCardsHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				
				if (msg.what >= 0) {
					Users list = (Users)msg.obj;
					
					if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
						mCardsData.clear();
						mCardsData.addAll(list.getUserList());
					} else {
						List<User> cards = new ArrayList<User>(list.getUserList());
						cards.removeAll(mCardsData);
						mCardsData.addAll(cards);
					}
					
					if (msg.what < pageSize) {
						mCardsView.setTag(UIHelper.LISTVIEW_DATA_FULL);
						mCardAdapter.notifyDataSetChanged();
						mCardsMore.setText(R.string.load_full);
					} else {
						mCardsView.setTag(UIHelper.LISTVIEW_DATA_MORE);
						mCardAdapter.notifyDataSetChanged();
						mCardsMore.setText(R.string.load_more);
					}
				} else if (msg.what == -1) {
					//有异常--显示加载出错 & 弹出错误消息
					mCardsView.setTag(UIHelper.LISTVIEW_DATA_MORE);
					mCardsMore.setText(R.string.load_error);
					((AppException)msg.obj).makeToast(CardWallActivity.this);
				}
				if (mCardAdapter.getCount() == 0) {
					mCardsView.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					mCardsMore.setText(R.string.load_empty);
				}
				mCardsProgress.setVisibility(ProgressBar.GONE);
				if(msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH){
					mCardsView.onRefreshComplete(DateUtils.format(new Date(), getString(R.string.pull_to_refresh_update_pattern)));
					mCardsView.setSelection(0);
				}
			}
		};
		loadCardsData(mCardsHandler, 1, UIHelper.LISTVIEW_ACTION_INIT);
	}
	
	private void loadCardsData(final Handler handler, final int pageNo, final int action) {
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
					Users list = appContext.getCards(pageNo);	
					//名片墙中增加大谷信息名片，并置顶
					User dagu = new User();
					dagu.setName("云瑞信息");
					dagu.setCompany("青岛云瑞信息科技有限公司");
					dagu.setSite("www.yunruiinfo.com");
					list.getUserList().add(0, dagu);
					//end
					msg.what = list.getUserList().size();
					msg.obj = list;
	            } catch (AppException e) {
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
			mCardsView.clickRefresh();
			break;
		default:
			super.onClick(v);
			break;
		}
	}
}
