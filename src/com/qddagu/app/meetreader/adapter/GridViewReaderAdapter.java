package com.qddagu.app.meetreader.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qddagu.app.meetreader.R;

/**
 * User: SYZ
 * Date: 13-4-1
 * Time: 下午4:31
 */
public class GridViewReaderAdapter extends BaseAdapter {
    private Context mContext;
    private List<ReaderMenu> mMenus = new ArrayList<ReaderMenu>();    //数据集合
    private LayoutInflater mContainer;  //视图容器
    public class ReaderMenu {
    	public int icon;
    	public String name;
    	public String value;
    	public int id;
    	public ReaderMenu(String name, String value, int icon, int id) {
    		this.icon = icon;
    		this.value = value;
    		this.name = name;
    		this.id = id;
    	}
    }
    static class GridItemView { //自定义视图
        public ImageView icon;
        public TextView name;
    }

    public GridViewReaderAdapter(Context context){
        mContext = context;
        mMenus.add(new ReaderMenu("再次阅读", "menu_read", R.drawable.ic_reader_read, R.id.menu_read));
        mMenus.add(new ReaderMenu("保存文件", "menu_save", R.drawable.ic_reader_save, R.id.menu_save));
        mMenus.add(new ReaderMenu("我要分享", "menu_share", R.drawable.ic_reader_share, R.id.menu_share));
        mMenus.add(new ReaderMenu("我要评论", "menu_comment", R.drawable.ic_reader_comment, R.id.menu_comment));
        mMenus.add(new ReaderMenu("记录笔记", "menu_note", R.drawable.ic_reader_note, R.id.menu_note));
        mContainer = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GridItemView gridItemView;

        if(convertView == null) {
            convertView = mContainer.inflate(R.layout.griditem_reader, null);
            gridItemView = new GridItemView();
            gridItemView.icon = (ImageView) convertView.findViewById(R.id.icon);
            gridItemView.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(gridItemView);
        } else {
            gridItemView = (GridItemView) convertView.getTag();
        }
        
        ReaderMenu menu = mMenus.get(position);
        gridItemView.name.setText(menu.name);
        gridItemView.icon.setImageResource(menu.icon);

        return convertView;
    }

    @Override
    public int getCount() {
        return mMenus.size();
    }

    @Override
    public Object getItem(int position) {
        return mMenus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
