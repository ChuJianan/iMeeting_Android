package com.qddagu.app.meetreader.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.adapter.ListViewDownloadAdapter;
import com.qddagu.app.meetreader.util.Downloader.Info;
import com.qddagu.app.meetreader.util.MultiDownloader;

import android.os.Bundle;
import android.widget.ListView;

public class DownloadActivity extends BaseActivity {
	ListView mDownloadView;
	ListViewDownloadAdapter mDownloadAdapter;
	MultiDownloader multiDownloader;
	Map<String, Info> mInfos;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_download);
		super.onCreate(savedInstanceState);
		
		setTitle("下载管理");
		
		multiDownloader = MultiDownloader.getInstance();
		mInfos = multiDownloader.getInfos();
		initView();
	}
	
	private List<Info> toList(Object[] infos) {
		List<Info> infoList = new ArrayList<Info>();
		for (Object info : infos) {
			infoList.add((Info)info);
		}
		return infoList;
	}
	
	private void initView() {
		mDownloadAdapter = new ListViewDownloadAdapter(this, toList(mInfos.values().toArray()));
		mDownloadView = (ListView) findViewById(R.id.download_list_view);
		mDownloadView.setAdapter(mDownloadAdapter);
		
		multiDownloader.setOnProgressListener(new MultiDownloader.OnProgressListener() {
			@Override
			public void onProgress(Info info, int progress) {
				mInfos.put(info.fileUrl, info);
				mDownloadAdapter.setInfos(toList(mInfos.values().toArray()));
				mDownloadAdapter.notifyDataSetChanged();
				setTitle("下载管理（" + progress + "%）");
			}
		});
	}
}
