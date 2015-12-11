package com.qddagu.app.meetreader.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.adapter.ListViewFavoriteAdapter;
import com.qddagu.app.meetreader.bean.Favorites;
import com.qddagu.app.meetreader.bean.Meeting;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.ViewSwitcher;
import android.widget.AdapterView.AdapterContextMenuInfo;

/***
 * 收藏夹
 * @author
 *
 */
public class FavoriteFragment extends BaseFragment {
	private ViewSwitcher mViewSwitcher;
	private ListView favoritelist;
	private List<Meeting> mFavoriteData=new ArrayList<Meeting>() ;
	private ListViewFavoriteAdapter fFavoriteAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_favorite, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		initView();   //初始化视图
		loadFavoriteData();
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		//由于ViewPager的预加载，没法正确判断当前Fragment是否可见
		//这个方法解决问题正确判断可见性
		super.setUserVisibleHint(isVisibleToUser);
		if (getUserVisibleHint()) {
			loadFavoriteData();
		}
	}
	
	private void initView() {
		fFavoriteAdapter = new ListViewFavoriteAdapter(appContext, mFavoriteData,
				new ListViewFavoriteAdapter.CallBack() {
			@Override
			public void delete(Meeting favorite) {
				deleteFavorite(favorite);
			}
		});
		mViewSwitcher=(ViewSwitcher)findViewById(R.id.favorite_switcher);		
		favoritelist=(ListView)findViewById(R.id.favoritelist);
		favoritelist.setAdapter(fFavoriteAdapter);
		favoritelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Meeting favorite = (Meeting) fFavoriteAdapter.getItem(position);
				appContext.setMeeting(favorite);
				getActivity().setResult(Activity.RESULT_OK);
				getActivity().finish();
				//((MainActivity)getActivity()).showFragment(MainActivity.FRAGMENT_MEETING);
			}
		});
		registerForContextMenu(favoritelist);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getActivity().getMenuInflater().inflate(R.menu.listview_favorite, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Meeting meeting = (Meeting)fFavoriteAdapter.getItem(info.position);
		switch (item.getItemId()) {
		case R.id.delete:
			deleteFavorite(meeting);
			break;
		default:
			return super.onContextItemSelected(item);
		}
		return true;
	}
	
	/**
	 * 删除收藏会议
	 * @param meeting
	 */
	private void deleteFavorite(final Meeting meeting) {
		new AlertDialog.Builder(getActivity())
		.setTitle("提示")
		.setMessage("确定删除“" + meeting.getTitle() + "”？")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				appContext.deleteFavorite(meeting);
				mFavoriteData.remove(meeting);
				fFavoriteAdapter.notifyDataSetChanged();
				if (mFavoriteData.size() == 0) {
					mViewSwitcher.setDisplayedChild(1);
				}
			}
		})
		.setNegativeButton("取消", null)
		.create().show();
	}
	
	@SuppressLint("HandlerLeak")
	private void loadFavoriteData(){	
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				//隐藏标题栏进度条
				if (msg.what > 0) {
					Favorites favorite = (Favorites)msg.obj;
					mFavoriteData.clear();
					mFavoriteData.addAll( favorite.getFavoriteList());
					fFavoriteAdapter.notifyDataSetChanged();
					mViewSwitcher.setDisplayedChild(1);
				} else if (msg.what == 0) {
					mViewSwitcher.setDisplayedChild(0);
				} else if (msg.what == -1) {
					((AppException)msg.obj).makeToast(getActivity());
				}
			}
		};
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
					Favorites list=appContext.getFavorites();			
					msg.what = list.getFavoriteList().size();
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
