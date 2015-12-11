package com.qddagu.app.meetreader.ui;

import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.api.ApiClient;
import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.util.UIHelper;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.ZoomDensity;
/**
 * 会议议程
 * @author SYZ
 */
public class AgendaActivity extends BaseActivity {
	private Meeting meeting;
	private WebView mWebView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_mtinfo);
		super.onCreate(savedInstanceState);
		
		meeting = appContext.Meeting();
		if (meeting == null) {
			UIHelper.ToastMessage(this, "请先选择会议！");
			finish();
			return;
		}
		
		setTitle(meeting.getTitle());
		
		initView();
		initData();
	}
	
	private void initView() {
		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return true; //禁用超链接
			}
		});
		mWebView.getSettings().setDefaultTextEncodingName(ApiClient.UTF_8);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
	}
	
	private void initData() {
        String html = meeting.getAgenda();
		mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
	}
}
