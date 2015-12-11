package com.qddagu.app.meetreader.adapter;

import java.util.List;

import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.adapter.ListViewFileAdapter.ViewHolder;
import com.qddagu.app.meetreader.api.ApiClient;
import com.qddagu.app.meetreader.bean.Interaction;
import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.bean.MtFile;
import com.qddagu.app.meetreader.bean.URLs;
import com.qddagu.app.meetreader.widget.IconView;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewInteractionAdapter extends BaseAdapter{
	private List<Interaction> listItems; // 数据集合
	Bitmap meetingbmp;
	String title;
	private LayoutInflater listContainer; // 视图容器
	static class ViewHolder { //自定义控件集合  
		public ImageView icon;
		public ImageView micon;
		public TextView name;
		public TextView content;
		public TextView title;
		public TextView ntime;
		public TextView mtime;
		public TextView answer;
		public View answerview;
	}
	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 */
	public ListViewInteractionAdapter(Context context, List<Interaction> data,String title,Bitmap meetingbmp) {
		this.listContainer = LayoutInflater.from(context);
		this.listItems = data;
		this.meetingbmp=meetingbmp;
		this.title=title;
	}
	public void setInteraction(List<Interaction> data) {
		this.listItems = data;
	}
	public void addInteraction(List<Interaction> data){
		
		listItems.addAll(data);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.listitem_interaction, null);
			holder = new ViewHolder();
			holder.name = (TextView)convertView.findViewById(R.id.name);
			holder.content = (TextView)convertView.findViewById(R.id.content);
			holder.icon = (ImageView)convertView.findViewById(R.id.nlogo);
			holder.title = (TextView)convertView.findViewById(R.id.meetname);
			holder.answer = (TextView)convertView.findViewById(R.id.anwser);
			holder.micon = (ImageView)convertView.findViewById(R.id.mlogo);
			holder.mtime= (TextView)convertView.findViewById(R.id.atime);
			holder.ntime=(TextView)convertView.findViewById(R.id.time);
			holder.answerview=(View)convertView.findViewById(R.id.anwserview);
			//设置控件集到convertView
			convertView.setTag(holder);

		}else {
			holder = (ViewHolder)convertView.getTag();
		}
		Interaction interaction = listItems.get(position);
	
		try {
			if(meetingbmp!=null){
			holder.micon.setImageBitmap(meetingbmp);}else{
				holder.micon.setImageLevel(R.drawable.ic_launcher);
			}
			if(ApiClient.getNetBitmap(URLs.HOST+interaction.getLogo())!=null){
			holder.icon.setImageBitmap(ApiClient.getNetBitmap(URLs.HOST+interaction.getLogo()));
			}else{
				holder.icon.setImageLevel(R.drawable.avatar_guest);
			}
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		holder.name.setText(interaction.getName());
		holder.content.setText(interaction.getAskQuestion());
		if(interaction.getResponseQuestion()!=null&&!interaction.getResponseQuestion().equals("")){
		holder.answer.setText(interaction.getResponseQuestion());
		}else{
//			holder.answer.setText("主办方暂时没有回答");
			holder.answerview.setVisibility(View.GONE);
		}
		holder.title.setText(title);
		holder.mtime.setText(interaction.getResponseTime());
		holder.ntime.setText(interaction.getAskTime());
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
