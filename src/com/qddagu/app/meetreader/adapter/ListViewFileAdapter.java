package com.qddagu.app.meetreader.adapter;

import java.util.List;

import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.bean.MtFile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewFileAdapter extends BaseAdapter {
	private List<MtFile> listItems; // 数据集合
	private LayoutInflater listContainer; // 视图容器
	static class ViewHolder { //自定义控件集合  
		public ImageView icon;
		public TextView name;
		public TextView size;
		public TextView author;
	}
	
	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 */
	public ListViewFileAdapter(Context context, List<MtFile> data) {
		this.listContainer = LayoutInflater.from(context);
		this.listItems = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.listitem_file, null);
			
			holder = new ViewHolder();
			//获取控件对象
			holder.icon = (ImageView)convertView.findViewById(R.id.file_icon);
			holder.name = (TextView)convertView.findViewById(R.id.file_name);
			holder.size = (TextView)convertView.findViewById(R.id.file_size);
			holder.author = (TextView)convertView.findViewById(R.id.file_author);
			
			//设置控件集到convertView
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		//设置文字和图片
		MtFile file = listItems.get(position);
		holder.icon.setImageResource(file.getIcon());
		holder.name.setText(file.getName());
		holder.size.setText(file.getSize());
		holder.author.setText(file.getAuthor());
		
		return convertView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems == null ? 0 : listItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listItems == null ? null : listItems.get(position - 1);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
}
