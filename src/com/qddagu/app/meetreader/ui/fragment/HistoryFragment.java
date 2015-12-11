package com.qddagu.app.meetreader.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.ViewSwitcher;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.adapter.ListViewHistoryAdapter;
import com.qddagu.app.meetreader.bean.Historys;
import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.bean.MtFile;
import com.qddagu.app.meetreader.service.DownloadService;
import com.qddagu.app.meetreader.ui.MainActivity;
import com.qddagu.app.meetreader.util.Downloader;
import com.qddagu.app.meetreader.util.MultiDownloader;

/***
 * 历史记录
 * @author Administrator
 *
 */
public class HistoryFragment extends BaseFragment {
	@ViewInject(R.id.history_switcher) ViewSwitcher mViewSwitcher;
	@ViewInject(R.id.historylist) ListView historylist;
	private List<Meeting> mHistoryData=new ArrayList<Meeting>() ;
	private ListViewHistoryAdapter mHistoryAdapter;
	protected Meeting meeting;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_history, container, false);
		ViewUtils.inject(this, view);
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		initView();   //初始化视图
		loadHistoryData();
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		//由于ViewPager的预加载，没法正确判断当前Fragment是否可见
		//这个方法解决问题正确判断可见性
		super.setUserVisibleHint(isVisibleToUser);
		if (getUserVisibleHint()) {
			loadHistoryData();
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (getUserVisibleHint()) {
			loadHistoryData();
		}
	}
	
	private void initView() {
		mViewSwitcher.setDisplayedChild(0);
		mHistoryAdapter = new ListViewHistoryAdapter(appContext, mHistoryData,
				new ListViewHistoryAdapter.CallBack() {
			@Override
			public void favorite(Meeting history) {
				favoriteHistory(history);
			}			
			@Override
			public void delete(Meeting history) {
				deleteHistory(history);
			}
		});
		historylist.setAdapter(mHistoryAdapter);
		historylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Meeting history = (Meeting) mHistoryAdapter.getItem(position);
				((MainActivity)getActivity()).loadMeeting(history.getUrls().Meeting());
			}
		});
		registerForContextMenu(historylist);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.listview_history, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Meeting meeting = (Meeting)mHistoryAdapter.getItem(info.position);
		switch (item.getItemId()) {
		case R.id.favorite:
			favoriteHistory(meeting);
			break;
		case R.id.delete:
			deleteHistory(meeting);
			break;
		default:
			return super.onContextItemSelected(item);
		}
		return true;
	}
	
	/**
	 * 收藏历史会议
	 * @param meeting
	 */
	private void favoriteHistory(Meeting meeting) {
		MultiDownloader multiDownloader = MultiDownloader.getInstance();
		if (meeting.getFiles() != null) {
			for (MtFile file : meeting.getFiles()) {
				String path = Downloader.SDCARD + "/MeetReader/meetings/";
				path += meeting.getTitle() + "/";
				path += file.getName() + "." + file.getType();
				multiDownloader.download(file.getUrl(), path);
			}
		}
		appContext.saveFavorite(meeting);
		getActivity().startService(new Intent(getActivity(), DownloadService.class));
	}
	
	/**
	 * 删除历史会议
	 * @param meeting
	 */
	private void deleteHistory(final Meeting meeting) {
		new AlertDialog.Builder(getActivity())
		.setTitle("提示")
		.setMessage("确定删除“" + meeting.getTitle() + "”？")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				appContext.deleteHistory(meeting);
				mHistoryData.remove(meeting);
				mHistoryAdapter.notifyDataSetChanged();
				if (mHistoryData.size() == 0) {
					mViewSwitcher.setDisplayedChild(1);
				}
			}
		})
		.setNegativeButton("取消", null)
		.create().show();
	}
	
	@SuppressLint("HandlerLeak")
	private void loadHistoryData(){
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				//隐藏标题栏进度条
				if (msg.what > 0) {
					Historys history = (Historys) msg.obj;
					mHistoryData.clear();
					mHistoryData.addAll(history.getHistoryList());
					mHistoryAdapter.notifyDataSetChanged();
					mViewSwitcher.setDisplayedChild(0);
				} else if (msg.what == 0) {
					mViewSwitcher.setDisplayedChild(1);
				} else if (msg.what == -1) {
					mViewSwitcher.setDisplayedChild(1);
					((AppException)msg.obj).makeToast(getActivity());
				}
			}
		};
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
					Historys list=appContext.getHistorys();
					msg.what = list.getHistoryList().size();
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
