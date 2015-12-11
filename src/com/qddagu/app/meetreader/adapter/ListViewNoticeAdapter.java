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

public class ListViewNoticeAdapter extends BaseAdapter{
	private List<Meeting> listItems; // 数据集合
	private LayoutInflater listContainer; // 视图容器
	private BitmapManager bmpManager;
	static class ViewHolder { //自定义控件集合  
		private TextView notice_title;
		private TextView notice_time;
		private TextView notice_place;
		private ImageView notice_image;
		private TextView publish_time;
		private TextView join_num;
	}

	/**
	 * 实例化Adapter
	 * @param context
	 * @param mFavoriteData
	 */
	public ListViewNoticeAdapter(Context context, List<Meeting> data) {
		this.listContainer = LayoutInflater.from(context);
		this.listItems = data;
		this.bmpManager = new BitmapManager();
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.listitem_notice_new, null);
			
			holder = new ViewHolder();
			//获取控件对象
			holder.notice_title = (TextView)convertView.findViewById(R.id.notice_title);
			holder.notice_time = (TextView)convertView.findViewById(R.id.notice_time);
			holder.notice_place = (TextView)convertView.findViewById(R.id.notice_place);
			holder.notice_image = (ImageView)convertView.findViewById(R.id.notice_image);
			holder.publish_time = (TextView)convertView.findViewById(R.id.publish_time);
			holder.join_num = (TextView)convertView.findViewById(R.id.join_num);
			
			//设置控件集到convertView
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		//设置文字和图片
		final Meeting notice = listItems.get(position);
		holder.notice_title.setText(notice.getTitle());
		String pattern = "MM-dd HH:mm";
		String time = DateUtils.format(notice.getBeginTime(), pattern);
		time += " 至 " + DateUtils.format(notice.getEndTime(), pattern);
		holder.notice_time.setText(time);
		holder.notice_place.setText(notice.getPlace());
		bmpManager.loadBitmap(notice.getThemePicture(), holder.notice_image);
		holder.publish_time.setText(DateUtils.format(notice.getCreateTime(), pattern));
		holder.join_num.setText(String.valueOf(notice.getJoinNum()));
		
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
