package com.qddagu.app.meetreader.ui;

import java.io.IOException;
import java.io.InputStream;

import android.os.Bundle;
import android.webkit.WebView;

import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.bean.Guest;
import com.qddagu.app.meetreader.util.StringUtils;
import com.qddagu.app.meetreader.util.UIHelper;

public class GuestActivity extends BaseActivity {
	private Guest mGuest;
	private WebView mWebView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_guest);
		super.onCreate(savedInstanceState);
		
		mGuest = (Guest) getIntent().getSerializableExtra("guest");
		if (mGuest == null) {
			UIHelper.ToastMessage(this, "非法调用");
			finish();
			return;
		}
		
		setTitle(mGuest.getName());
		
		initView();	//初始化视图
		initData();	//初始化数据
	}
	
	private void initView() {
		mWebView = (WebView) findViewById(R.id.webview);
	}
	
	private void initData() {
		try {
            InputStream in = getResources().getAssets().open("html/guest.html");
            StringBuffer out = new StringBuffer();
            byte[] b = new byte[4096];
            for (int n; (n = in.read(b)) != -1;) {
                out.append(new String(b, 0, n));
            }
            String html = out.toString();
            String picture = mGuest.getGuestPicture();
            if (StringUtils.isEmpty(picture)) {
            	picture = "file:///android_asset/html/avatar_guest.png";
			}
            html = html.replace("${picture}", picture);
            html = html.replace("${name}", mGuest.getName());
            html = html.replace("${post}", mGuest.getPost());
            html = html.replace("${brief}", mGuest.getBrief());
            mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
