package com.qddagu.app.meetreader.adapter;

import java.util.List;

import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.bean.User;
import com.qddagu.app.meetreader.util.DateUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewSignAdapter extends BaseAdapter {
	private List<User> listItems; // 数据集合
	private LayoutInflater listContainer; // 视图容器
	static class ViewHolder { //自定义控件集合  
		public TextView name;
		public TextView time;
		public TextView company;
	}
	
	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 */
	public ListViewSignAdapter(Context context, List<User> data) {
		this.listContainer = LayoutInflater.from(context);
		this.listItems = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.listitem_sign, null);
			
			holder = new ViewHolder();
			//获取控件对象
			holder.name = (TextView)convertView.findViewById(R.id.name);
			holder.time = (TextView)convertView.findViewById(R.id.time);
			holder.company = (TextView)convertView.findViewById(R.id.company);
			
			//设置控件集到convertView
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		//设置文字和图片
		User user = listItems.get(position);
		holder.name.setText(user.getName());
		holder.time.setText(DateUtils.format(user.getSignTime()));
		holder.company.setText(user.getCompany());
		
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
