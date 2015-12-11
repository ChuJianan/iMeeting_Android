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

public class ListViewFavoriteAdapter extends BaseAdapter{
	private List<Meeting> listItems; // 数据集合
	private LayoutInflater listContainer; // 视图容器
	private BitmapManager bmpManager;
	private CallBack callBack;
	static class ViewHolder { //自定义控件集合  
		private TextView favorite_name;
		private TextView favorite_time;
		private TextView favorite_place;
		private ImageView favorite_image;
		private View delete;
	}
	public interface CallBack {
		public void delete(Meeting favorite);	//删除收藏会议
	}
	/**
	 * 实例化Adapter
	 * @param context
	 * @param mFavoriteData
	 */
	public ListViewFavoriteAdapter(Context context, List<Meeting> mFavoriteData, CallBack callBack) {
		this.listContainer = LayoutInflater.from(context);
		this.listItems = mFavoriteData;
		this.callBack = callBack;
		this.bmpManager = new BitmapManager();
	}
	
	public void setFavorite(List<Meeting> notes) {
		this.listItems = notes;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.listitem_favorite_new, null);
			
			holder = new ViewHolder();
			//获取控件对象
			holder.favorite_name = (TextView)convertView.findViewById(R.id.favorite_name);
			holder.favorite_time = (TextView)convertView.findViewById(R.id.favorite_time);
			holder.favorite_place = (TextView)convertView.findViewById(R.id.favorite_place);
			holder.favorite_image = (ImageView)convertView.findViewById(R.id.favorite_image);
			holder.delete = convertView.findViewById(R.id.delete);
			
			//设置控件集到convertView
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		//设置文字和图片
		final Meeting favorite = listItems.get(position);
		holder.favorite_name.setText(favorite.getTitle());
		holder.favorite_place.setText(favorite.getPlace());
		bmpManager.loadBitmap(favorite.getThemePicture(), holder.favorite_image);
		String pattern = "MM-dd HH:mm";
		String time = DateUtils.format(favorite.getBeginTime(), pattern);
		time += " 至 " + DateUtils.format(favorite.getEndTime(), pattern);
		holder.favorite_time.setText(time);
		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.delete:
					callBack.delete(favorite);
					break;
				}
			}
		};
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
