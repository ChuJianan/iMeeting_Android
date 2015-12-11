package com.qddagu.app.meetreader.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.util.BitmapManager;
import com.qddagu.app.meetreader.util.DateUtils;

public class ListViewCloudAdapter extends BaseAdapter {
	private List<Meeting> listItems; // 数据集合
	private LayoutInflater listContainer; // 视图容器
	private BitmapManager bmpManager;
	static class ViewHolder { //自定义控件集合  
		private TextView meeting_title;
		private TextView meeting_time;
		private TextView meeting_place;
		private ImageView meeting_image;
		private TextView publish_time;
		private TextView join_num;
	}
	/**
	 * 实例化Adapter
	 * @param context
	 * @param mHistoryData
	 */
	public ListViewCloudAdapter(Context context, List<Meeting> data) {;
		this.listContainer = LayoutInflater.from(context);
		this.listItems = data;
		this.bmpManager = new BitmapManager();
	}
	
	public void setCloud(List<Meeting> data) {
		this.listItems = data;
	}
	public void addCloud(List<Meeting> data){
		
		listItems.addAll(data);
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.listitem_cloud, null);
			
			holder = new ViewHolder();
			//获取控件对象
			holder.meeting_title = (TextView)convertView.findViewById(R.id.meeting_title);
			holder.meeting_time = (TextView)convertView.findViewById(R.id.meeting_time);
			holder.meeting_place = (TextView)convertView.findViewById(R.id.meeting_place);
			holder.meeting_image = (ImageView)convertView.findViewById(R.id.meeting_image);
			holder.publish_time = (TextView)convertView.findViewById(R.id.publish_time);
			holder.join_num = (TextView)convertView.findViewById(R.id.join_num);
			
			//设置控件集到convertView
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		//设置文字和图片
		final Meeting cloud = listItems.get(position);
		holder.meeting_title.setText(cloud.getTitle());
		holder.meeting_place.setText(cloud.getPlace());
		bmpManager.loadBitmap(cloud.getThemePicture(), holder.meeting_image);
		holder.join_num.setText(String.valueOf(cloud.getJoinNum()));
		String pattern = "yyyy-MM-dd HH:mm";
		String time = DateUtils.format(cloud.getBeginTime(), pattern);
		time += " 至 " + DateUtils.format(cloud.getEndTime(), pattern);
		holder.meeting_time.setText(time);
		holder.publish_time.setText(DateUtils.format(cloud.getCreateTime(), pattern));
		
		return convertView;
	}
	
	public int getCount() { 
		return listItems == null ? 0 : listItems.size();
	}

	public Object getItem(int position) { 
		return listItems == null ? null : listItems.get(position-1);
	}

	public long getItemId(int position) { 
		return 0;
	}
}
