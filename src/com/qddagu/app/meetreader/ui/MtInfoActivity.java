package com.qddagu.app.meetreader.ui;

import java.io.IOException;
import java.io.InputStream;

import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.bean.Person;
import com.qddagu.app.meetreader.util.UIHelper;

import android.os.Bundle;
import android.webkit.WebView;
/**
 * 会议简介
 * @author SYZ
 */
public class MtInfoActivity extends BaseActivity {
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
	}
	
	private void initData() {
		try {
            InputStream in = getResources().getAssets().open("html/mtinfo.html");
            StringBuffer out = new StringBuffer();
            byte[] b = new byte[4096];
            for (int n; (n = in.read(b)) != -1;) {
                out.append(new String(b, 0, n));
            }
            String html = out.toString();
            html = html.replace("${title}", meeting.getTitle());
            html = html.replace("${place}", meeting.getPlace());
            html = html.replace("${brief}", meeting.getBrief());
            //html = html.replace("${picture}", meeting.getThemePicture());
            html = html.replace("${ispublic}", meeting.isPublic() ? "公开会议" : "非公开会议");
            Person person = meeting.getPerson();
			if(person == null || meeting.isPublic()) {
				html = html.replace("${person}", "none");
			} else {
				html = html.replace("${name}", person.getName());
				html = html.replace("${post}", person.getPost());
			}
            mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
