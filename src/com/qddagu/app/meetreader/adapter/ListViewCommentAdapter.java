package com.qddagu.app.meetreader.adapter;

import java.util.List;

import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.bean.Comment;
import com.qddagu.app.meetreader.util.DateUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewCommentAdapter extends BaseAdapter {
	private List<Comment> listItems; // 数据集合
	private LayoutInflater listContainer; // 视图容器
	static class ViewHolder { //自定义控件集合  
		public TextView title;
		public TextView content;
		public TextView time;
	}
	
	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 */
	public ListViewCommentAdapter(Context context, List<Comment> data) {
		this.listContainer = LayoutInflater.from(context);
		this.listItems = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.listitem_comment, null);
			
			holder = new ViewHolder();
			//获取控件对象
			holder.title = (TextView)convertView.findViewById(R.id.title);
			holder.content = (TextView)convertView.findViewById(R.id.content);
			holder.time = (TextView)convertView.findViewById(R.id.time);
			
			//设置控件集到convertView
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		//设置文字和图片
		Comment comment = listItems.get(position);
		holder.title.setText(comment.getTitle());
		holder.content.setText(comment.getContent());
		holder.time.setText(DateUtils.format(comment.getTime()));
		
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
