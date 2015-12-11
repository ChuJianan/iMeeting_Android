package com.qddagu.app.meetreader.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.bean.Note;
import com.qddagu.app.meetreader.util.DateUtils;

public class ListViewNoteAdapter extends BaseAdapter {
	private List<Note> listItems; // 数据集合
	private LayoutInflater listContainer; // 视图容器
	static class ViewHolder { //自定义控件集合  
		public TextView title;
		public TextView time;
		public TextView content;
	}
	
	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 */
	public ListViewNoteAdapter(Context context, List<Note> data) {
		this.listContainer = LayoutInflater.from(context);
		this.listItems = data;
	}
	
	public void setNotes(List<Note> notes) {
		this.listItems = notes;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.listitem_note, null);
			
			holder = new ViewHolder();
			//获取控件对象
			holder.title = (TextView)convertView.findViewById(R.id.title);
			holder.time = (TextView)convertView.findViewById(R.id.time);
			holder.content = (TextView)convertView.findViewById(R.id.content);
			
			//设置控件集到convertView
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		//设置文字和图片
		Note note = listItems.get(position);
		holder.title.setText(note.getTitle());
		holder.time.setText(DateUtils.format(note.getTime()));
		holder.content.setText(note.getContent());
		
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
		return listItems == null ? null : listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
}
