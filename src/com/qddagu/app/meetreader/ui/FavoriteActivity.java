package com.qddagu.app.meetreader.ui;

import com.qddagu.app.meetreader.AppManager;
import com.qddagu.app.meetreader.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class FavoriteActivity extends FragmentActivity implements OnClickListener {
	public static final int REQUEST_CODE = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);
		setContentView(R.layout.activity_favorite);

		TextView title = (TextView) findViewById(R.id.title);
		if (title != null)
			title.setText("会议收藏夹");
		View back = findViewById(R.id.back);
    	if(back != null) {
    		back.setOnClickListener(this);
    	}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// 结束Activity&从堆栈中移除
		AppManager.getAppManager().finishActivity(this);
	}
}
