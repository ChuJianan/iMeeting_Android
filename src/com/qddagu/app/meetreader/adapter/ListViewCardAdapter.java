package com.qddagu.app.meetreader.adapter;

import java.util.List;

import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.bean.User;
import com.qddagu.app.meetreader.util.BitmapManager;
import com.qddagu.app.meetreader.util.StringUtils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewCardAdapter extends BaseAdapter {
	private List<User> listItems; // 数据集合
	private BitmapManager bmpManager;
	private LayoutInflater listContainer; // 视图容器
	static class ViewHolder { //自定义控件集合  
		public TextView name;
		public TextView post;
		public TextView mobile;
		public TextView company;
		public TextView address;
		public TextView site;
		public TextView remark;
		public ImageView logo;
	}
	
	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 */
	public ListViewCardAdapter(Context context, List<User> data) {
		this.listContainer = LayoutInflater.from(context);
		this.listItems = data;
		this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_logo_default));
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder = null;
		
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(R.layout.listitem_card, null);
			
			holder = new ViewHolder();
			//获取控件对象
			holder.name = (TextView)convertView.findViewById(R.id.name);
			holder.post = (TextView)convertView.findViewById(R.id.post);
			holder.mobile = (TextView)convertView.findViewById(R.id.mobile);
			holder.company = (TextView)convertView.findViewById(R.id.company);
			holder.address = (TextView)convertView.findViewById(R.id.address);
			holder.site = (TextView)convertView.findViewById(R.id.site);
			holder.remark = (TextView)convertView.findViewById(R.id.remark);
			holder.logo = (ImageView)convertView.findViewById(R.id.logo);
			
			//设置控件集到convertView
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}
		
		//设置文字和图片
		User card = listItems.get(position);
		holder.name.setText(card.getName());
		holder.post.setText(card.getPost());
		holder.mobile.setText(card.getMobile());
		holder.company.setText(card.getCompany());
		holder.address.setText(card.getAddress());
		holder.site.setText(card.getSite());
		holder.remark.setText(card.getRemark());
		
		//临时加入，后期要修改
		if (StringUtils.isEmpty(card.getLogo()) && "青岛大谷农业信息有限公司".equals(card.getCompany())) {
			holder.logo.setImageResource(R.drawable.ic_logo_dagu);
		} else {
			bmpManager.loadBitmap(card.getLogo(), holder.logo);
		}
		
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
