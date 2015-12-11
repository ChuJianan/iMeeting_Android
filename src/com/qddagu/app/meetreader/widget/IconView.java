package com.qddagu.app.meetreader.widget;

import com.qddagu.app.meetreader.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class IconView extends FrameLayout {
	
	private ImageView mIcon;
	private TextView mName;

	public IconView(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater.from(context).inflate(R.layout.icon_view, this, true);
		mIcon = (ImageView) findViewById(R.id.icon);
		mName = (TextView) findViewById(R.id.name);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IconView);
		mIcon.setImageDrawable(typedArray.getDrawable(R.styleable.IconView_icon));
		int resId = typedArray.getResourceId(R.styleable.IconView_name, 0);
		mName.setText(resId > 0 ? getResources().getText(resId) 
				: typedArray.getString(R.styleable.IconView_name));
		typedArray.recycle();
	}

}
