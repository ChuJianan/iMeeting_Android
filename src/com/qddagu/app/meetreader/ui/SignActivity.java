package com.qddagu.app.meetreader.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.adapter.ListViewSignAdapter;
import com.qddagu.app.meetreader.bean.User;
import com.qddagu.app.meetreader.bean.Users;
import com.qddagu.app.meetreader.util.DateUtils;
import com.qddagu.app.meetreader.util.StringUtils;
import com.qddagu.app.meetreader.util.UIHelper;
import com.qddagu.app.meetreader.widget.PullToRefreshListView;

/**
 * 会议签到
 * @author SYZ
 */
@SuppressLint("HandlerLeak")
public class SignActivity extends BaseActivity {
	private PullToRefreshListView mSignsView;
	private View mSignsFooter;
	private TextView mSignsMore;
	private ProgressBar mSignsProgress;
	private List<User> mSignsData = new ArrayList<User>();
	private ListViewSignAdapter mSignAdapter;
	private Handler mSignsHandler;
	private int pageNo = 1;//当前页数
	private int pageSize = 20;//每页个数
	private Button mBtnSign;
	private boolean isSigned = false;	//是否已签到
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_sign);
		super.onCreate(savedInstanceState);
		
		if (appContext.getUserId() == 0) {
			UIHelper.ToastMessage(SignActivity.this, "请先设置名片信息");
			UIHelper.showUser(SignActivity.this);
			finish();
			return;
		}
		
		setTitle("签到情况");
		
		initView();
		initData();
	}
	
	public boolean getisSigned() {
		return isSigned;
	}

	public void setSigned(boolean isSigned) {
		this.isSigned = isSigned;
	}

	private void initView() {
		mSignAdapter = new ListViewSignAdapter(this, mSignsData);
		mSignsFooter = getLayoutInflater().inflate(R.layout.listview_footer, null);
		mSignsMore = (TextView)mSignsFooter.findViewById(R.id.listview_foot_more);
		mSignsProgress = (ProgressBar)mSignsFooter.findViewById(R.id.listview_foot_progress);
		mSignsView = (PullToRefreshListView) findViewById(R.id.list);
		mSignsView.addFooterView(mSignsFooter);
		mSignsView.setAdapter(mSignAdapter);
		mSignsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//点击头部、底部栏无效
        		if(position == 0 || view == mSignsFooter) return;
			}
		});
		mSignsView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				mSignsView.onScrollStateChanged(view, scrollState);
				
				// 数据为空--不用继续下面代码了
				if (mSignsData.isEmpty())
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(mSignsFooter) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				int lvDataState = StringUtils.toInt(mSignsView.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					mSignsView.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					mSignsMore.setText(R.string.load_ing);
					mSignsProgress.setVisibility(View.VISIBLE);
					// 当前pageNo+1
					pageNo++;
					loadSignsData(mSignsHandler, pageNo, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				mSignsView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
		mSignsView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				loadSignsData(mSignsHandler, 1, UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});
		mBtnSign = (Button) findViewById(R.id.sign);
		mBtnSign.setOnClickListener(this);
	}
	
	private void initData() {
		initSignsData();
	}
	
	private void initSignsData() {
		mSignsHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what >= 0) {
					Users list = (Users) msg.obj;
					
					if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
						mSignsData.clear();
						mSignsData.addAll(list.getUserList());
					} else {
						List<User> users = new ArrayList<User>(list.getUserList());
						users.removeAll(mSignsData);
						mSignsData.addAll(users);
					}
					
					if (msg.what < pageSize) {
						mSignsView.setTag(UIHelper.LISTVIEW_DATA_FULL);
						mSignAdapter.notifyDataSetChanged();
						mSignsMore.setText(R.string.load_full);
					} else {
						mSignsView.setTag(UIHelper.LISTVIEW_DATA_MORE);
						mSignAdapter.notifyDataSetChanged();
						mSignsMore.setText(R.string.load_more);
					}
					
					//与后台约定：如果该用户已签到则将该
					//用户信息放到列表第一项以便于进行检查
					if (!isSigned) {//如果未签到则检查签到信息
						List<User> users = list.getUserList();
						String mac = appContext.getMacAddress();
						for (User u : users) {
							if (mac.equals(u.getMac())) {
								mBtnSign.setText("已签到");
								mBtnSign.setEnabled(false);
								setSigned( true);
								break;
							}
						}
						if (!isSigned) {
							mBtnSign.setEnabled(true);
							mBtnSign.setText("我要签到");
						}
					}
				} else if (msg.what == -1) {
					//有异常--显示加载出错 & 弹出错误消息
					mSignsView.setTag(UIHelper.LISTVIEW_DATA_MORE);
					mSignsMore.setText(R.string.load_error);
					((AppException)msg.obj).makeToast(SignActivity.this);
				}
				if (mSignAdapter.getCount() == 0) {
					mSignsView.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					mSignsMore.setText(R.string.load_empty);
				}
				mSignsProgress.setVisibility(ProgressBar.GONE);
				if(msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH){
					mSignsView.onRefreshComplete(DateUtils.format(new Date(), getString(R.string.pull_to_refresh_update_pattern)));
					mSignsView.setSelection(0);
				}
			}
		};
		loadSignsData(mSignsHandler, 1, UIHelper.LISTVIEW_ACTION_INIT);
	}
	
	private void loadSignsData(final Handler handler, final int pageNo, final int action) {
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
					Users list = appContext.getSigns(appContext.getUserId(), pageNo);			
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
	
	private void saveSign() {
		mBtnSign.setText("正在签到，请稍等...");
		mBtnSign.setEnabled(false);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					UIHelper.ToastMessage(SignActivity.this, "签到成功");
					mBtnSign.setText("已签到");
					mBtnSign.setEnabled(false);
					setSigned(true);
				} else if (msg.what == -1) {
					((AppException)msg.obj).makeToast(SignActivity.this);
					mBtnSign.setEnabled(true);
					mBtnSign.setText("我要签到");
				}
			};
		};
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
//签到控制，防伪签
//					WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//					if (wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
//						UIHelper.ToastMessage(appContext, "请先连接会场WiFi");
//						return;
//					}
//					WifiInfo info = wifi.getConnectionInfo();
					msg.obj = appContext.saveSign(appContext.getUserId());
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
		case R.id.refresh:
			mSignsView.clickRefresh();
			break;
		case R.id.sign:
			saveSign();
			break;
		default:
			super.onClick(v);
			break;
		}
	}
}
