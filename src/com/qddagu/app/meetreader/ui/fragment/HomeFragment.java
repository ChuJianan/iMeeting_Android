package com.qddagu.app.meetreader.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonParseException;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.adapter.ViewPagerHomeAdsAdapter;
import com.qddagu.app.meetreader.bean.Advertising;
import com.qddagu.app.meetreader.bean.Result;
import com.qddagu.app.meetreader.bean.URLs;
import com.qddagu.app.meetreader.util.JsonUtils;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends BaseFragment {
	
	//首页轮播广告
	@ViewInject(R.id.home_ads_viewpager)
	private ViewPager mHomeAdsPager;
	private List<Advertising> mHomeAdsData = new ArrayList<Advertising>();
	private ViewPagerHomeAdsAdapter mHomeAdsAdapter;
	//广告轮播
	private Handler mHomeAdsHandler; 
	private Runnable mHomeAdsRunnable;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		ViewUtils.inject(this, view);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		showHistorys(); //历史会议
		loadHomeAds();	//轮播广告
	}
	
	private void loadHomeAds() {
		if (mHomeAdsHandler == null) {
			mHomeAdsHandler = new Handler();
			mHomeAdsRunnable = new Runnable() {
				@Override
				public void run() {
					//轮播广告
					int count = mHomeAdsAdapter.getCount();
					if(count > 0) {
						int current = mHomeAdsPager.getCurrentItem() + 1;
						int item = current % count;
						mHomeAdsPager.setCurrentItem(item);
						mHomeAdsHandler.postDelayed(this, 5000);
					}
				}
			};
			//启动轮播
			mHomeAdsHandler.postDelayed(mHomeAdsRunnable, 5000);
		}
		
		mHomeAdsAdapter = new ViewPagerHomeAdsAdapter(getActivity());
		mHomeAdsPager.setAdapter(mHomeAdsAdapter);
		
		final DbUtils db = DbUtils.create(getActivity());
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.GET, URLs.HOME_ADS, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					Result result = JsonUtils.fromJson(responseInfo.result, Result.class);
					if (!result.OK()) {
						throw AppException.custom(result.Message());
					} else if (result.ads.size() > 0) {
						db.dropTable(Advertising.class);
						mHomeAdsData = result.ads;
						mHomeAdsAdapter.setHomeAds(mHomeAdsData);
						mHomeAdsAdapter.notifyDataSetChanged();
						db.saveAll(result.ads);
					}
				} catch (JsonParseException e) {
					e.printStackTrace();
					AppException.json(e).makeToast(getActivity());
				} catch (DbException e) {
					e.printStackTrace();
					AppException.sqlite(e).makeToast(getActivity());
				} catch (AppException e) {
					e.printStackTrace();
					e.makeToast(getActivity());
				}
			}
			
			@Override
			public void onStart() {
				try {
					mHomeAdsData = db.findAll(Advertising.class);
					if (mHomeAdsData != null) {
						mHomeAdsAdapter.setHomeAds(mHomeAdsData);
						mHomeAdsAdapter.notifyDataSetChanged();
					}
				} catch (DbException e) {
					e.printStackTrace();
					AppException.sqlite(e).makeToast(getActivity());
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {}
		});
	}

	private void showHistorys() {
		getFragmentManager().beginTransaction()
		.replace(R.id.home_meeting_historys, new HistoryFragment())
		.commit(); 
	}
}
