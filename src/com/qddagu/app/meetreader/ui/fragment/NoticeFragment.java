package com.qddagu.app.meetreader.ui.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.adapter.ListViewCloudAdapter;
import com.qddagu.app.meetreader.adapter.ListViewNoticeAdapter;
import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.bean.Notices;
import com.qddagu.app.meetreader.ui.MainActivity;
import com.qddagu.app.meetreader.util.DateUtils;
import com.qddagu.app.meetreader.util.StringUtils;
import com.qddagu.app.meetreader.util.UIHelper;
import com.qddagu.app.meetreader.widget.PullToRefreshListView;

/***
 * 收藏夹
 * @author
 *
 */
public class NoticeFragment extends BaseFragment {
	@ViewInject(R.id.noticelist)  private PullToRefreshListView mNoticesView;
	@ViewInject(R.id.empty_text)  private TextView mEmptyView;
	
	private List<Meeting> mNoticesData = new ArrayList<Meeting>();
	private ListViewNoticeAdapter mNoticeAdapter;
	
	private long mLastTime; //上次加载时间戳
	private View mNoticesFooter;
	private TextView mNoticesMore;
	private ProgressBar mNoticesProgress;
	private int pageNo = 1;//当前页数
	private int pageSize = 6;//每页个数
	//视图是否初始化
	private boolean isViewInited = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_notice, container, false);
		ViewUtils.inject(this, view);
		initView();   //初始化视图
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (getUserVisibleHint()) { //对用户可见时才加载数据
			loadNoticeData(pageNo);
		}
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		//由于ViewPager的预加载，没法正确判断当前Fragment是否可见
		//这个方法解决问题正确判断可见性
		super.setUserVisibleHint(isVisibleToUser);
		if (getUserVisibleHint() && isViewInited) {
			//如果直接点击跳转到本Fragment，setUserVisibleHint方法会先于
			//onCreateView调用，所以加载数据前需要先判断视图是否已初始化
			loadNoticeData(pageNo);
		}
	}
	
	private void initView() {		
		mNoticesView.setEmptyView(mEmptyView);
		mNoticesFooter = getActivity().getLayoutInflater().inflate(R.layout.listview_footer, null);
		mNoticesMore = (TextView)mNoticesFooter.findViewById(R.id.listview_foot_more);
		mNoticesProgress = (ProgressBar)mNoticesFooter.findViewById(R.id.listview_foot_progress);
		mNoticesView.addFooterView(mNoticesFooter);
		mNoticeAdapter = new ListViewNoticeAdapter(getActivity(), mNoticesData);
		mNoticesView.setAdapter(mNoticeAdapter);
		mNoticesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == 0 || view == mNoticesFooter) return;
				Meeting notice = (Meeting) mNoticeAdapter.getItem(position);
				((MainActivity)getActivity()).loadMeeting(notice.getUrl());
			}
		});
		mNoticesView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				mNoticesView.onScrollStateChanged(view, scrollState);
				
				// 数据为空--不用继续下面代码了
				if (mNoticesData.isEmpty())
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(mNoticesFooter) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				int lvDataState = StringUtils.toInt(mNoticesView.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					mNoticesView.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					mNoticesMore.setText(R.string.load_ing);
					mNoticesProgress.setVisibility(View.VISIBLE);
					// 当前pageNo+1
					pageNo++;
					loadNoticeData(pageNo);
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				mNoticesView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
		mNoticesView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			//下拉列表刷新
			@Override
			public void onRefresh() {
				mNoticesProgress.setVisibility(ProgressBar.GONE);
				mNoticesView.onRefreshComplete(DateUtils.format(new Date(), getString(R.string.pull_to_refresh_update_pattern)));
				mNoticesView.setSelection(0);
			}
		});
		isViewInited = true;
	}
	
	@SuppressLint("HandlerLeak")
	private void loadNoticeData(final int pageNo){	
		//10分钟内不重复加载预告信息
		long now = System.currentTimeMillis();
		if (mLastTime > 0 && now - mLastTime < 1000 * 60 * 10) {
			return;
		}
		//end

		mEmptyView.setText("正在获取预告信息");
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what > 0) {
					
					Notices notices = (Notices) msg.obj;
					mNoticesData.clear();
					if(msg.what<pageSize){
					mNoticesView.setTag(UIHelper.LISTVIEW_DATA_FULL);
					mNoticesData.addAll(notices.getNoticeList());
					mNoticeAdapter.notifyDataSetChanged();
					mNoticesProgress.setVisibility(View.GONE);
					mNoticesMore.setText(R.string.load_full);
					}else{
						mNoticesView.setTag(UIHelper.LISTVIEW_DATA_MORE);
						mNoticesData.addAll(notices.getNoticeList());
						mNoticeAdapter.notifyDataSetChanged();
					}
					//记录成功时间
					mLastTime = System.currentTimeMillis();
				} else if (msg.what == 0) {
					mEmptyView.setText("暂无预告信息");
				} else if (msg.what == -1) {
					((AppException)msg.obj).makeToast(getActivity());
				}
			}
		};
		new Thread(){
			public void run() {
				Message msg = new Message();
				
				try {
					Notices list = appContext.getNotices(pageNo);			
					msg.what = list.getNoticeList().size();
					msg.obj = list;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
	            }
				handler.sendMessage(msg);
			}
		}.start();
	}
}
