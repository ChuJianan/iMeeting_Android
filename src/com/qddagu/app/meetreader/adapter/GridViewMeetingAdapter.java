package com.qddagu.app.meetreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.ui.AgendaActivity;
import com.qddagu.app.meetreader.ui.BrowserActivity;
import com.qddagu.app.meetreader.ui.InteractionsActivity;
import com.qddagu.app.meetreader.ui.JoinActivity;
import com.qddagu.app.meetreader.ui.PeripheryActivity;
import com.qddagu.app.meetreader.ui.SignActivity;
import com.qddagu.app.meetreader.ui.UserActivity;
import com.qddagu.app.meetreader.ui.CardWallActivity;
import com.qddagu.app.meetreader.ui.FilesActivity;
import com.qddagu.app.meetreader.ui.GuestsActivity;
import com.qddagu.app.meetreader.ui.MtInfoActivity;

/**
 * User: SYZ
 * Date: 13-4-1
 * Time: 下午4:31
 */
public class GridViewMeetingAdapter extends BaseAdapter {
    private Context mContext;
    private List<Icon> mIcons = new ArrayList<Icon>();    //数据集合
    private LayoutInflater mContainer;  //视图容器
    public class Icon {
    	public int icon;
    	public String name;
    	public Class<?> activity;
    	public Icon(String name, int icon, Class<?> activity) {
    		this.icon = icon;
    		this.name = name;
    		this.activity = activity;
    	}
    }
    static class GridItemView { //自定义视图
        public ImageView icon;
        public TextView name;
    }

    public GridViewMeetingAdapter(Context context){
        mContext = context;
        mIcons.add(new Icon("会议简介", R.drawable.ic_meeting_brief, MtInfoActivity.class));
        mIcons.add(new Icon("会议嘉宾", R.drawable.ic_meeting_guests, GuestsActivity.class));
        mIcons.add(new Icon("会议议程", R.drawable.ic_meeting_agenda, AgendaActivity.class));
        mIcons.add(new Icon("文件列表", R.drawable.ic_meeting_files, FilesActivity.class));
        mIcons.add(new Icon("我要报名", R.drawable.ic_meeting_join, JoinActivity.class));
        mIcons.add(new Icon("现场互动", R.drawable.ic_meeting_interaction, InteractionsActivity.class));
        mIcons.add(new Icon("签到情况", R.drawable.ic_meeting_sign, SignActivity.class));
        mIcons.add(new Icon("名片墙", R.drawable.ic_meeting_cardwall, CardWallActivity.class));
        mIcons.add(new Icon("我的名片", R.drawable.ic_meeting_card, UserActivity.class));
        mIcons.add(new Icon("会议微博", R.drawable.ic_meeting_weibo, BrowserActivity.class));
        mIcons.add(new Icon("会议周边", R.drawable.ic_meeting_around, PeripheryActivity.class));
        //mIcons.add(new Icon("赞助商", R.drawable.ic_meeting_sponsors, null));
        mContainer = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GridItemView gridItemView;

        if(convertView == null) {
            convertView = mContainer.inflate(R.layout.griditem_meeting, null);
            gridItemView = new GridItemView();
            gridItemView.icon = (ImageView) convertView.findViewById(R.id.icon);
            gridItemView.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(gridItemView);
        } else {
            gridItemView = (GridItemView) convertView.getTag();
        }
        
        Icon icon = mIcons.get(position);
        gridItemView.name.setText(icon.name);
        gridItemView.icon.setImageResource(icon.icon);

        return convertView;
    }

    @Override
    public int getCount() {
        return mIcons.size();
    }

    @Override
    public Object getItem(int position) {
        return mIcons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
