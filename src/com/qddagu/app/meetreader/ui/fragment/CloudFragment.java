package com.qddagu.app.meetreader.ui.fragment;

import java.util.Date;
import java.util.List;

import com.google.gson.JsonParseException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.adapter.ListViewCloudAdapter;
import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.bean.Result;
import com.qddagu.app.meetreader.bean.URLs;
import com.qddagu.app.meetreader.ui.MainActivity;
import com.qddagu.app.meetreader.util.DateUtils;
import com.qddagu.app.meetreader.util.JsonUtils;
import com.qddagu.app.meetreader.util.StringUtils;
import com.qddagu.app.meetreader.util.UIHelper;
import com.qddagu.app.meetreader.widget.PullToRefreshListView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CloudFragment extends BaseFragment {
	@ViewInject(R.id.empty_text) private TextView mEmptyView;
	@ViewInject(R.id.cloudlist) private PullToRefreshListView mCloudsView;
	private List<Meeting> mCloudsData;
	private long mLastTime; //上次加载时间
	private View mCloudsFooter;
	private TextView mCloudsMore;
	private ProgressBar mCloudsProgress;
	private ListViewCloudAdapter mCloudAdapter;
	private int pageNo = 1;//当前页数
	private int pageSize = 6;//每页个数
	private boolean isViewInited = false;
	protected int pageNo1=1;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_cloud, container, false);
		ViewUtils.inject(this, view);
		initView();
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		long now = System.currentTimeMillis();
		//10分钟内不重复加载信息
		if (mLastTime > 0 && now - mLastTime < 1000 * 60 * 10) {
			return;
		}else{
		if (getUserVisibleHint()) {
			//如果直接点击跳转到本Fragment，setUserVisibleHint方法会先于
			//onCreateView调用，所以加载数据前需要先判断视图是否已初始化
			loadCloudData(pageNo);
		}
			}
	}
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		//由于ViewPager的预加载，没法正确判断当前Fragment是否可见
		//这个方法解决问题正确判断可见性
		super.setUserVisibleHint(isVisibleToUser);
		long now = System.currentTimeMillis();
		//10分钟内不重复加载信息
		if (mLastTime > 0 && now - mLastTime < 1000 * 60 * 10) {
			return;
		}else{
		if (getUserVisibleHint() && isViewInited) {
			//如果直接点击跳转到本Fragment，setUserVisibleHint方法会先于
			//onCreateView调用，所以加载数据前需要先判断视图是否已初始化
			loadCloudData(pageNo);
			
		}
		}
	}
	private void initView(){
		mCloudsView.setEmptyView(mEmptyView);
		mCloudsFooter = getActivity().getLayoutInflater().inflate(R.layout.listview_footer, null);
		mCloudsMore = (TextView)mCloudsFooter.findViewById(R.id.listview_foot_more);
		mCloudsProgress = (ProgressBar)mCloudsFooter.findViewById(R.id.listview_foot_progress);
		mCloudAdapter = new ListViewCloudAdapter(getActivity(), mCloudsData);
		mCloudsView.addFooterView(mCloudsFooter);
		mCloudsView.setAdapter(mCloudAdapter);
		mCloudsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == 0 || view == mCloudsFooter) return;
				Meeting cloud = (Meeting) mCloudAdapter.getItem(position);
				((MainActivity)getActivity()).loadMeeting(cloud.getUrl());
			}
		});
		mCloudsView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				mCloudsView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);

			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				(mCloudsView).onScrollStateChanged(view, scrollState);
				if (mCloudsData.isEmpty())
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(mCloudsFooter) == view
							.getLastVisiblePosition())
						scrollEnd = true;
					mCloudsMore.setText(R.string.load_full);
					mCloudsProgress.setVisibility(View.GONE);
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				int lvDataState = StringUtils.toInt(mCloudsView.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					mCloudsView.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					mCloudsMore.setText(R.string.load_ing);
					mCloudsProgress.setVisibility(View.VISIBLE);
					// 当前pageNo+1
					pageNo++;
					loadCloudData(pageNo);
				}
				
			}});
		mCloudsView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			//下拉列表刷新
			@Override
			public void onRefresh() {
//			mCloudsData.removeAll(mCloudsData);
//				loadCloudData(pageNo1+1);
				mCloudsProgress.setVisibility(ProgressBar.GONE);
				mCloudsView.onRefreshComplete(DateUtils.format(new Date(), getString(R.string.pull_to_refresh_update_pattern)));
   			    mCloudsView.setSelection(0);
				
			}
		});
		isViewInited=true;
	}
	private void loadCloudData(int pageNo) {
	
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.GET, URLs.CLOUD+pageNo, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					Result result = JsonUtils.fromJson(responseInfo.result, Result.class);
					if (!result.OK()) {
						throw AppException.custom(result.Message());
					} else if (result.clouds.size() > 0) {
						if(mCloudsData==null){
					mCloudsData = result.clouds;
					mCloudsView.setTag(UIHelper.LISTVIEW_DATA_MORE);
				mCloudAdapter.setCloud(mCloudsData);
					mCloudAdapter.notifyDataSetChanged();}
						else{
						if (result.clouds.size()  < pageSize) {
							mCloudsData = result.clouds;
							mCloudsView.setTag(UIHelper.LISTVIEW_DATA_FULL);
							mCloudAdapter.addCloud(mCloudsData);
							mCloudAdapter.notifyDataSetChanged();
							mCloudsProgress.setVisibility(View.GONE);
							mCloudsMore.setText(R.string.load_full);
						} else {
							mCloudsData = result.clouds;
							mCloudsView.setTag(UIHelper.LISTVIEW_DATA_MORE);
							mCloudAdapter.addCloud(mCloudsData);
							mCloudAdapter.notifyDataSetChanged();
							mCloudsMore.setText(R.string.load_more);
						}
						}
						
					} 
					else{
						mCloudsView.setTag(UIHelper.LISTVIEW_DATA_FULL);
						
						mCloudsMore.setText(R.string.load_full);
						mCloudsProgress.setVisibility(View.GONE);
					}
					mLastTime = System.currentTimeMillis();
				} catch (JsonParseException e) {
					e.printStackTrace();
					AppException.json(e).makeToast(getActivity());
				} catch (AppException e) {
					e.printStackTrace();
					e.makeToast(getActivity());
				}
			}
			@Override
			public void onFailure(HttpException error, String msg) {}
		});
	}
}
