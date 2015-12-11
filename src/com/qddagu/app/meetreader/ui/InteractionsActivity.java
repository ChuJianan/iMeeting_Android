package com.qddagu.app.meetreader.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.adapter.ListViewFileAdapter;
import com.qddagu.app.meetreader.adapter.ListViewInteractionAdapter;
import com.qddagu.app.meetreader.api.ApiClient;
import com.qddagu.app.meetreader.bean.Interaction;
import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.bean.MtFile;
import com.qddagu.app.meetreader.bean.Result;
import com.qddagu.app.meetreader.bean.URLs;
import com.qddagu.app.meetreader.bean.User;
import com.qddagu.app.meetreader.ui.fragment.BaseFragment;
import com.qddagu.app.meetreader.util.DateUtils;
import com.qddagu.app.meetreader.util.JsonUtils;
import com.qddagu.app.meetreader.util.StringUtils;
import com.qddagu.app.meetreader.util.UIHelper;
import com.qddagu.app.meetreader.widget.PullToRefreshListView;





@SuppressLint("HandlerLeak")
public class InteractionsActivity extends BaseActivity{
	
	private PullToRefreshListView mInteractionView;
	private View mInteractionFooter;
	private TextView mInteractionMore;
	private ProgressBar mInteractionProgress;
	private ListViewInteractionAdapter mInteractionAdapter;
	private List<Interaction> mInteractionData = new ArrayList<Interaction>();
	private Handler mInteractionHandler;
	private long mLastTime; //上次加载时间
	
	private SignActivity sign;
	private int pageNo = 1;//当前页数
	private int pageSize = 20;//每页个数
	private Meeting meeting;
	private User user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_files);
		super.onCreate(savedInstanceState);
		
		meeting = appContext.Meeting();
		if (meeting.isInteraction() == false) {
			UIHelper.ToastMessage(this, "本次会议没有开通互动！");
			finish();
			return;
		}
		if (!meeting.isSend()) {
			UIHelper.ToastMessage(InteractionsActivity.this, "请先递交个人名片");
			UIHelper.showUser(InteractionsActivity.this);
			finish();
			return;
		}
//		user=appContext.getUserInfo();
		if(appContext.getUserId() == 0){
			UIHelper.ToastMessage(InteractionsActivity.this, "请先签到");
			UIHelper.showSign(InteractionsActivity.this);
			finish();
			return;
		}
		setTitle(meeting.getTitle());
		
		try {
			initView();
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//初始化视图
		loadInteractionData(pageNo);//初始化数据
	}
	private void initView() throws AppException {
		mInteractionAdapter = new ListViewInteractionAdapter(this, mInteractionData,meeting.getTitle(),ApiClient.getNetBitmap(meeting.getThemePicture()));
		mInteractionFooter = getLayoutInflater().inflate(R.layout.listview_footer, null);
		mInteractionMore = (TextView)mInteractionFooter.findViewById(R.id.listview_foot_more);
		mInteractionProgress = (ProgressBar)mInteractionFooter.findViewById(R.id.listview_foot_progress);
		mInteractionView = (PullToRefreshListView)findViewById(R.id.file_list_view);
		mInteractionView.addFooterView(mInteractionFooter);
		mInteractionView.setAdapter(mInteractionAdapter);
    	View interaction=findViewById(R.id.interaction);
    	if(interaction!=null){
    		interaction.setOnClickListener(this);
    		interaction.setVisibility(View.VISIBLE);
    	}
		mInteractionView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//点击头部、底部栏无效
        		if(position == 0 || view == mInteractionFooter) return;
        		
//        		MtFile file = (MtFile)mInteractionAdapter.getItem(position);
//        		UIHelper.showReader(InteractionActivity.this, file);
			}
		});
		mInteractionView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				mInteractionView.onScrollStateChanged(view, scrollState);
				
				// 数据为空--不用继续下面代码了
				if (mInteractionData.isEmpty())
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(mInteractionFooter) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				int lvDataState = StringUtils.toInt(mInteractionView.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					mInteractionView.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					mInteractionMore.setText(R.string.load_ing);
					mInteractionProgress.setVisibility(View.VISIBLE);
					// 当前pageNo+1
					pageNo++;
					loadInteractionData(pageNo);
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				mInteractionView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
		mInteractionView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			//下拉列表刷新
			@Override
			public void onRefresh() {
				
				mInteractionData = new ArrayList<Interaction>();
				try {
					mInteractionAdapter = new ListViewInteractionAdapter(InteractionsActivity.this, mInteractionData,meeting.getTitle(),ApiClient.getNetBitmap(meeting.getThemePicture()));
				} catch (AppException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				loadInteractionData(pageNo);
				mInteractionProgress.setVisibility(ProgressBar.GONE);
				mInteractionView.onRefreshComplete(DateUtils.format(new Date(), getString(R.string.pull_to_refresh_update_pattern)));
   			    mInteractionView.setSelection(0);
			}
		});
	}
	private void loadInteractionData(final int pageNo){
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.GET, URLs.INTERACTION+"meetId="+meeting.getId()+"&pageNo="+pageNo, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					Result result = JsonUtils.fromJson(responseInfo.result, Result.class);
					if (!result.OK()) {
						throw AppException.custom(result.Message());
					} else if (result.guests.size() > 0) {
						if(mInteractionData==null){
					mInteractionData = result.guests;
					mInteractionView.setTag(UIHelper.LISTVIEW_DATA_MORE);
				mInteractionAdapter.setInteraction(mInteractionData);
					mInteractionAdapter.notifyDataSetChanged();}
						else{
						if (result.clouds.size()  < pageSize) {
							mInteractionData = result.guests;
							mInteractionView.setTag(UIHelper.LISTVIEW_DATA_FULL);
							mInteractionAdapter.addInteraction(mInteractionData);
							mInteractionAdapter.notifyDataSetChanged();
							mInteractionProgress.setVisibility(View.GONE);
							mInteractionMore.setText(R.string.load_full);
						} else {
							mInteractionData = result.guests;
							mInteractionView.setTag(UIHelper.LISTVIEW_DATA_MORE);
							mInteractionAdapter.addInteraction(mInteractionData);
							mInteractionAdapter.notifyDataSetChanged();
							mInteractionMore.setText(R.string.load_more);
						}
						}
						
					} 
					else{
						mInteractionView.setTag(UIHelper.LISTVIEW_DATA_FULL);
						
						mInteractionMore.setText(R.string.load_full);
						mInteractionProgress.setVisibility(View.GONE);
					}
					mLastTime = System.currentTimeMillis();
				} catch (JsonParseException e) {
					e.printStackTrace();
					AppException.json(e).makeToast(InteractionsActivity.this);
				} catch (AppException e) {
					e.printStackTrace();
					e.makeToast(InteractionsActivity.this);
				}
			}
			@Override
			public void onFailure(HttpException error, String msg) {}
		});

	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.interaction:
			UIHelper.showInteraction(InteractionsActivity.this);
			break;
		default:
			super.onClick(v);
			break;
		}
	}
}