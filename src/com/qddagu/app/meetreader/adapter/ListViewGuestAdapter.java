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
import com.qddagu.app.meetreader.bean.Guest;
import com.qddagu.app.meetreader.util.BitmapManager;

public class ListViewGuestAdapter extends BaseAdapter {
	private List<Guest> listItems; // 数据集合
	private LayoutInflater listContainer; // 视图容器
	private BitmapManager 	bmpManager;
	static class ViewHolder { //自定义控件集合  
		public ImageView picture;
		public TextView name;
		public TextView post;
		public TextView brief;
	}
	
	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 */
	public ListViewGuestAdapter(Context context, List<Guest> data) {
		this.listContainer = LayoutInflater.from(context);
		this.listItems = data;
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.avatar_guest));
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.listitem_guest, null);
			
			holder = new ViewHolder();
			//获取控件对象
			holder.picture = (ImageView)convertView.findViewById(R.id.picture);
			holder.name = (TextView)convertView.findViewById(R.id.name);
			holder.post = (TextView)convertView.findViewById(R.id.post);
			holder.brief = (TextView)convertView.findViewById(R.id.brief);
			
			//设置控件集到convertView
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		//设置文字和图片
		Guest guest = listItems.get(position);
		bmpManager.loadBitmap(guest.getGuestPicture(), holder.picture);
		holder.name.setText(guest.getName());
		holder.post.setText(guest.getPost());
		holder.brief.setText(guest.getBrief());
		
		return convertView;
	}

	@Override
	public int getCount() {
		return listItems == null ? 0 : listItems.size();
	}

	@Override
	public Object getItem(int position) {
		return listItems == null ? null : listItems.get(position - 1);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
}
