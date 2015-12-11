package com.qddagu.app.meetreader.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.bean.Advertising;
import com.qddagu.app.meetreader.util.BitmapManager;
import com.qddagu.app.meetreader.util.UIHelper;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewPagerHomeAdsAdapter extends PagerAdapter {
	private Context mContext;
	private List<Advertising> listItems = Collections.emptyList(); // 数据集合
	private List<View> listViews; // 视图集合
	private LayoutInflater listContainer; // 视图容器
	private BitmapManager 	bmpManager;
	static class ViewHolder { //自定义控件集合  
		ImageView image;
		TextView title;
	}
	
	public ViewPagerHomeAdsAdapter(Context context) {
		this.mContext = context;
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.img_image_loading));
		this.listContainer = LayoutInflater.from(context);
	}
	
	public void setHomeAds(List<Advertising> data) {
		this.listItems = data;
		this.listViews = new ArrayList<View>();
		for (int i = 0; i < listItems.size(); i++) {
			View view = listContainer.inflate(R.layout.pageritem_homeads, null);
			
			ViewHolder holder = new ViewHolder();
			holder.image = (ImageView) view.findViewById(R.id.image);
			holder.title = (TextView) view.findViewById(R.id.title);
			
			Advertising ad = listItems.get(i);
			bmpManager.loadBitmap(ad.getPicture(), holder.image);
			holder.title.setText(ad.getTitle());
			
			listViews.add(view);
		}
	}
	
	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == (arg1);
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		View view = listViews.get(position);
////////加入点击事件不能滑动ViewPager
//		view.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				String url = listItems.get(position).getUrl();
//				UIHelper.openBrowser(mContext, url);
//			}
//		});
		container.addView(view, 0);
		return view;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(listViews.get(position));
	}

}
