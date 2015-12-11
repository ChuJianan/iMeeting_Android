package com.qddagu.app.meetreader.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.adapter.ListViewFileAdapter;
import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.bean.MtFile;
import com.qddagu.app.meetreader.bean.Files;
import com.qddagu.app.meetreader.service.DownloadService;
import com.qddagu.app.meetreader.util.DateUtils;
import com.qddagu.app.meetreader.util.Downloader;
import com.qddagu.app.meetreader.util.MultiDownloader;
import com.qddagu.app.meetreader.util.StringUtils;
import com.qddagu.app.meetreader.util.UIHelper;
import com.qddagu.app.meetreader.widget.PullToRefreshListView;


public class FilesActivity extends BaseActivity {
	private PullToRefreshListView mFilesView;
	private View mFilesFooter;
	private TextView mFilesMore;
	private ProgressBar mFilesProgress;
	private ListViewFileAdapter mFilesAdapter;
	private List<MtFile> mFilesData = new ArrayList<MtFile>();
	private Handler mFilesHandler;
	private int pageNo = 1;//当前页数
	private int pageSize = 20;//每页个数
	private Meeting meeting;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_files);
		super.onCreate(savedInstanceState);
		
		meeting = appContext.Meeting();
		if (meeting == null) {
			UIHelper.ToastMessage(this, "请先选择会议！");
			finish();
			return;
		}
		
		setTitle(meeting.getTitle());
		
		initView();//初始化视图
		initFilesData();//初始化数据
	}
	
	private void initView() {
		mFilesAdapter = new ListViewFileAdapter(this, mFilesData);
		mFilesFooter = getLayoutInflater().inflate(R.layout.listview_footer, null);
		mFilesMore = (TextView)mFilesFooter.findViewById(R.id.listview_foot_more);
		mFilesProgress = (ProgressBar)mFilesFooter.findViewById(R.id.listview_foot_progress);
		mFilesView = (PullToRefreshListView)findViewById(R.id.file_list_view);
		mFilesView.addFooterView(mFilesFooter);
		mFilesView.setAdapter(mFilesAdapter);
		mFilesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//点击头部、底部栏无效
        		if(position == 0 || view == mFilesFooter) return;
        		
        		MtFile file = (MtFile)mFilesAdapter.getItem(position);
        		UIHelper.showReader(FilesActivity.this, file);
			}
		});
		mFilesView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				mFilesView.onScrollStateChanged(view, scrollState);
				
				// 数据为空--不用继续下面代码了
				if (mFilesData.isEmpty())
					return;

				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(mFilesFooter) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				
				int lvDataState = StringUtils.toInt(mFilesView.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					mFilesView.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					mFilesMore.setText(R.string.load_ing);
					mFilesProgress.setVisibility(View.VISIBLE);
					// 当前pageNo+1
					pageNo++;
					loadFilesData(mFilesHandler, pageNo, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				mFilesView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
		mFilesView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			//下拉列表刷新
			@Override
			public void onRefresh() {
				loadFilesData(mFilesHandler, 1, UIHelper.LISTVIEW_ACTION_REFRESH);
			}
		});
		View refresh = findViewById(R.id.refresh);
		if (refresh != null) {
			refresh.setVisibility(View.VISIBLE);
		}
	}
	
	private void initFilesData() {
		mFilesHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what >= 0) {
					Files list = (Files)msg.obj;
					
					if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
						mFilesData.clear();
						mFilesData.addAll(list.getFileList());
					} else {
						List<MtFile> files = new ArrayList<MtFile>(list.getFileList());
						files.removeAll(mFilesData);
						mFilesData.addAll(files);
					}
					//刷新文件列表数据后写入历史会议
					meeting.setFiles(mFilesData);
					appContext.setMeeting(meeting);
					appContext.saveHistory(meeting);
					
					if (msg.what < pageSize) {
						mFilesView.setTag(UIHelper.LISTVIEW_DATA_FULL);
						mFilesAdapter.notifyDataSetChanged();
						mFilesMore.setText(R.string.load_full);
					} else {
						mFilesView.setTag(UIHelper.LISTVIEW_DATA_MORE);
						mFilesAdapter.notifyDataSetChanged();
						mFilesMore.setText(R.string.load_more);
					}
					
				} else if (msg.what == -1 && msg.obj != null) {
					//有异常--显示加载出错 & 弹出错误消息
					mFilesView.setTag(UIHelper.LISTVIEW_DATA_MORE);
					mFilesMore.setText(R.string.load_error);
					((AppException)msg.obj).makeToast(FilesActivity.this);
				}
				if(mFilesAdapter.getCount()==0){
					mFilesView.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					mFilesMore.setText(R.string.load_empty);
				}
				mFilesProgress.setVisibility(ProgressBar.GONE);
				if(msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH){
					mFilesView.onRefreshComplete(DateUtils.format(new Date(), getString(R.string.pull_to_refresh_update_pattern)));
					mFilesView.setSelection(0);
				}
			}
		};
		this.loadFilesData(mFilesHandler, 1, UIHelper.LISTVIEW_ACTION_INIT);
	}
	
	/**
	 * 加载文件列表数据
	 * @param handler
	 * @param action
	 */
	private void loadFilesData(final Handler handler, final int pageNo, final int action) {
		new Thread(){
			public void run() {
				Message msg = new Message();
				if (action == UIHelper.LISTVIEW_ACTION_INIT 
						&& meeting.getFiles() != null
						&& meeting.getFiles().size() > 0) {
					msg.what = meeting.getFiles().size();
					msg.obj = new Files(meeting.getFiles());
				} else {
					try {
						Files list = appContext.getFiles(pageNo);
						msg.what = list.getFileList().size();
						msg.obj = list;
		            } catch (AppException e) {
		            	e.printStackTrace();
		            	msg.what = -1;
		            	msg.obj = e;
		            }
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
			mFilesView.clickRefresh();
			break;
		default:
			super.onClick(v);
			break;
		}
	}
	
}
