package com.qddagu.app.meetreader.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
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

public class ListViewHistoryAdapter extends BaseAdapter {
	private List<Meeting> listItems; // 数据集合
	private LayoutInflater listContainer; // 视图容器
	private BitmapManager bmpManager;
	private CallBack callBack;
	static class ViewHolder { //自定义控件集合  
		private TextView history_name;
		private TextView history_time;
		private TextView history_place;
		private ImageView history_image;
		private View favorite;
		private View delete;
	}
	public interface CallBack {
		public void favorite(Meeting history);	//收藏历史会议
		public void delete(Meeting history);	//删除历史会议
	}
	/**
	 * 实例化Adapter
	 * @param context
	 * @param mHistoryData
	 */
	public ListViewHistoryAdapter(Context context, List<Meeting> mHistoryData, CallBack callBack) {;
		this.listContainer = LayoutInflater.from(context);
		this.listItems = mHistoryData;
		this.callBack = callBack;
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.img_image_loading));
	}
	
	public void setHistory(List<Meeting> notes) {
		this.listItems = notes;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.listitem_history, null);
			
			holder = new ViewHolder();
			//获取控件对象
			holder.history_name = (TextView)convertView.findViewById(R.id.history_name);
			holder.history_time = (TextView)convertView.findViewById(R.id.history_time);
			holder.history_place = (TextView)convertView.findViewById(R.id.history_place);
			holder.history_image = (ImageView)convertView.findViewById(R.id.history_image);
			holder.favorite = convertView.findViewById(R.id.favorite);
			holder.delete = convertView.findViewById(R.id.delete);
			
			//设置控件集到convertView
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		//设置文字和图片
		final Meeting history = listItems.get(position);
		holder.history_name.setText(history.getTitle());
		holder.history_place.setText(history.getPlace());
		bmpManager.loadBitmap(history.getThemePicture(), holder.history_image);
		String pattern = "MM-dd HH:mm";
		String time = DateUtils.format(history.getBeginTime(), pattern);
		time += " 至 " + DateUtils.format(history.getEndTime(), pattern);
		holder.history_time.setText(time);
		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.favorite:
					callBack.favorite(history);
					break;
				case R.id.delete:
					callBack.delete(history);
					break;
				}
			}
		};
		holder.favorite.setOnClickListener(listener);
		holder.delete.setOnClickListener(listener);
		return convertView;
	}
	
	public int getCount() { 
		return listItems == null ? 0 : listItems.size();
	}

	public Object getItem(int position) { 
		return listItems == null ? null : listItems.get(position);
	}

	public long getItemId(int position) { 
		return 0;
	}
}
