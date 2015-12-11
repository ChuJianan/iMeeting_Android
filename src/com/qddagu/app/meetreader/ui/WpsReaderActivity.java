package com.qddagu.app.meetreader.ui;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.adapter.GridViewReaderAdapter;
import com.qddagu.app.meetreader.adapter.GridViewReaderAdapter.ReaderMenu;
import com.qddagu.app.meetreader.bean.MtFile;
import com.qddagu.app.meetreader.util.Downloader;
import com.qddagu.app.meetreader.util.FileUtils;
import com.qddagu.app.meetreader.util.MediaUtils;
import com.qddagu.app.meetreader.util.UIHelper;
import com.qddagu.app.meetreader.util.WpsUtils;

@SuppressLint({ "HandlerLeak", "SetJavaScriptEnabled" })
public class WpsReaderActivity extends BaseActivity {
	private final static String TAG = "WpsReader";
	private GridView mMenusView;
	private GridViewReaderAdapter mMenusAdapter;
	private MtFile mFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_reader_wps);
		super.onCreate(savedInstanceState);
		
		mFile = (MtFile) getIntent().getSerializableExtra("file");
		setTitle(mFile.getName());
		
		initView(); //初始化视图
		readFile();	//阅读文件
	}
	
	private void readFile() {
		if (UIHelper.isAvilible(this, "cn.wps.moffice_eng")) {
			WpsUtils.openFile(this, mFile.getFile().getAbsolutePath(), null);
		} else {
			String mimetype = MediaUtils.getMIMEType(mFile.getUrl());
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(mFile.getFile()), mimetype);
			startActivity(intent);
		}
	}
	
	private void initView() {
		mMenusView = (GridView) findViewById(R.id.menu);
		mMenusAdapter = new GridViewReaderAdapter(this);
		mMenusView.setAdapter(mMenusAdapter);
		mMenusView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ReaderMenu menu = (ReaderMenu) mMenusAdapter.getItem(position);
				if (!"menu_read".equals(menu.value) && !mFile.ifCan(menu.value)) {
					UIHelper.ToastMessage(WpsReaderActivity.this, "你没有这个权限");
					return;
				}
				switch (menu.id) {
				case R.id.menu_read:
					readFile();//阅读文件
					break;
				case R.id.menu_save:
					saveFile();
					break;
				case R.id.menu_note:
					UIHelper.showNotes(WpsReaderActivity.this, mFile);
					break;
				case R.id.menu_comment:
					UIHelper.showComments(WpsReaderActivity.this, mFile);
					break;
				case R.id.menu_share:
					UIHelper.shareFile(WpsReaderActivity.this, mFile.getFile());
					break;
				}
			}
		});
	}
	
	//判断文件是否已存在，并保存
	private void saveFile() {
		if(!FileUtils.checkSaveLocationExists()) {
			UIHelper.ToastMessage(this, "SD卡未挂载");
			return;
		}
		final String folder = Downloader.SDCARD + "/Documents/";
		new File(folder).mkdirs();
		final File file = new File(folder + mFile.getName() + "." + mFile.getType());
		if (!file.exists()) {
			saveFile(file);
		} else {
			new AlertDialog.Builder(WpsReaderActivity.this)
			.setTitle("提示")
			.setMessage("文件已存在，是否覆盖？")
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					saveFile(file);
				}
			})
			.setNegativeButton("取消", null)
			.create().show();
		}
	}
	
	//保存文件
	private void saveFile(File file) {
		try {
			FileUtils.copyFile(mFile.getFile(), file);
			UIHelper.ToastMessage(this, "保存成功，文件路径：" + file.getAbsolutePath(), Toast.LENGTH_LONG);
		} catch (IOException e) {
			e.printStackTrace();
			UIHelper.ToastMessage(this, "保存失败：" + e.getMessage());
		}
	}
}
