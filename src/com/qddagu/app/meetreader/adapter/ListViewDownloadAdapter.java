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
import com.qddagu.app.meetreader.util.Downloader.Info;

public class ListViewDownloadAdapter extends BaseAdapter {
	private List<Info> listItems; // 数据集合
	private LayoutInflater listContainer; // 视图容器
	static class ViewHolder { //自定义控件集合  
		public ImageView icon;
		public TextView name;
		public TextView progress;
	}
	
	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 */
	public ListViewDownloadAdapter(Context context, List<Info> data) {
		this.listContainer = LayoutInflater.from(context);
		this.listItems = data;
	}
	
	public void setInfos(List<Info> data) {
		this.listItems = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.listitem_download, null);
			
			holder = new ViewHolder();
			//获取控件对象
			holder.icon = (ImageView)convertView.findViewById(R.id.file_icon);
			holder.name = (TextView)convertView.findViewById(R.id.file_name);
			holder.progress = (TextView)convertView.findViewById(R.id.progress);
			
			//设置控件集到convertView
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		//设置文字和图片
		Info info = listItems.get(position);
		holder.icon.setImageResource(R.drawable.ic_file_default);
		holder.name.setText(info.fileName);
		String text = "";
		switch (info.state) {
		case 0:
			text = "等待";
			break;
		case 1:
			text = info.tempSize + "/" + info.fileSize;
			break;
		case 2:
			text = "完成";
			break;
		case 3:
			text = info.message;
			break;
		}
		holder.progress.setText(text);
		
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
