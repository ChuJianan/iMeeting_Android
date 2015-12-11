package com.qddagu.app.meetreader.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.api.ApiClient;
import com.qddagu.app.meetreader.bean.MtFile;
import com.qddagu.app.meetreader.util.Downloader;
import com.qddagu.app.meetreader.util.FileUtils;
import com.qddagu.app.meetreader.util.MediaUtils;
import com.qddagu.app.meetreader.util.UIHelper;
import com.qddagu.app.meetreader.util.Downloader.Info;
import com.qddagu.app.meetreader.util.WpsUtils;

@SuppressLint({ "HandlerLeak", "SetJavaScriptEnabled" })
public class ReaderActivity extends BaseActivity {
	private final static String TAG = "Reader";
	private WebView mWebView;
	private ViewSwitcher mWebViewSwitcher;
	private ProgressBar mLoadingBar;
	private ProgressBar mWebProgress;
	private TextView mLoadingText;
	private MtFile mFile;
	
	private Receiver mReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_reader);
		super.onCreate(savedInstanceState);
		
		mFile = (MtFile) getIntent().getSerializableExtra("file");
		setTitle(mFile.getName());
		
		mReceiver = new Receiver(this);
		mReceiver.registerAction("cn.wps.moffice.file.close");
		mReceiver.registerActionAndScheme(Intent.ACTION_PACKAGE_ADDED, "package");
		
		initView(); //初始化视图
		initData();	//初始化数据
	}
	
	private void initView() {
		mWebProgress = (ProgressBar) findViewById(R.id.webview_progress);
		mWebView = (WebView)findViewById(R.id.reader_webview);
		mWebView.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				return mFile.ifCan("copy"); //是否长按复制
			}
		});
		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return true; //禁用超链接
			}
		});
		if(!mFile.ifCan("cache")) {
			mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		}
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setDefaultTextEncodingName(ApiClient.UTF_8);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setSupportZoom(true);
		mWebView.getSettings().setBuiltInZoomControls(true);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int density = metrics.densityDpi;
		switch (density) {
		case 240:
			mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
			break;
		case 160:
			mWebView.getSettings().setDefaultZoom(ZoomDensity.MEDIUM);
			break;
		case 120:
			mWebView.getSettings().setDefaultZoom(ZoomDensity.CLOSE);
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
			break;
		case DisplayMetrics.DENSITY_TV:
			mWebView.getSettings().setDefaultZoom(ZoomDensity.FAR);
			break;
		}
		
		mWebView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if(newProgress == 100) {
					mWebProgress.setVisibility(View.GONE);
				} else {
					//更新加载进度条
					mWebProgress.setVisibility(View.VISIBLE);
					mWebProgress.setProgress(newProgress);;
				}
			}
		});
		
		mWebView.setWebViewClient(new WebViewClient(){
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				String error = "文件加载出错：";
				error += "\n错误代码：" + errorCode;
				error += "\n错误描述：" + description;
				error += "\n文件地址：" + failingUrl;
				mLoadingText.setText(error);
			}
		});
		
		mWebViewSwitcher = (ViewSwitcher)findViewById(R.id.reader_webview_switcher);
		mWebViewSwitcher.setDisplayedChild(0);
		
		mLoadingBar = (ProgressBar)findViewById(R.id.loading_bar);
		mLoadingBar.setProgress(0);
		mLoadingText = (TextView)findViewById(R.id.loading_text);
		
		View.OnClickListener menuListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.menu_save:
					saveFile();
					break;
				case R.id.menu_note:
					UIHelper.showNotes(ReaderActivity.this, mFile);
					break;
				case R.id.menu_comment:
					UIHelper.showComments(ReaderActivity.this, mFile);
					break;
				case R.id.menu_share:
					if (mFile.getFile() == null) {
						UIHelper.ToastMessage(ReaderActivity.this, "请先保存文件");
					} else {
						UIHelper.shareFile(ReaderActivity.this, mFile.getFile());
					}
					break;
				}
			}
		};
		
		//显示可用菜单
		for (String m : mFile.getMenus()) {
			findViewById((getResources().getIdentifier(m, "id", getApplicationInfo().packageName))).setOnClickListener(menuListener);
		}
	}
	
	private void initData() {
		String type = mFile.getType();
		if ("html".equals(type) || 
			"htm".equals(type) || 
			"jpg".equals(type) || 
			"jpeg".equals(type) ||
			"png".equals(type) ||
			"bmp".equals(type) ||
			"gif".equals(type) ||
			"txt".equals(type)) {
			mWebView.loadUrl(mFile.getUrl());
			return; //以上类型不需要下载转换
		}
		
		String mimetype = MediaUtils.getMIMEType(mFile.getUrl());
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromParts("file", "", null), mimetype);
		ResolveInfo ri = getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
		if (ri == null) {	//ri为空说明没有能够打开当前文件的软件
			WpsUtils.ifInstallWps(this, new WpsUtils.OnInstallListener() {
				@Override
				public void onOk() {}
				@Override
				public void onDown() {}
				
				@Override
				public void onCancel() {
					String url = ApiClient.makeURL(appContext.Meeting().getUrls().Html(),new HashMap<String, Object>(){{
						put("fileid", mFile.getId());
					}});
					mWebView.loadUrl(url);
				}
			});
		} else {
			mWebProgress.setVisibility(View.GONE);
			mWebViewSwitcher.setDisplayedChild(1);
			mLoadingText.setText("正在准备下载...");
			String path = Downloader.SDCARD + "/MeetReader/files/";
			path += mFile.getName() + "." + mFile.getType();
			new Downloader(mFile.getUrl(), path, new Downloader.OnProgressListener() {
				@Override
				public void onProgress(Info info) {
					if (isFinishing()) return; //界面已关闭
					switch (info.state) {
					case Downloader.DOWN_UPDATE:
						//更新下载进度条
						mLoadingBar.setProgress(info.progress);
						mLoadingText.setText("正在下载：" + info.tempSize + "/" + info.fileSize);
						break;
					case Downloader.DOWN_FINISH:
						//下载完成
						mFile.setFile(info.file);
						readFileData(mFile);
						break;
					case Downloader.DOWN_ERROR:
						//下载出错
						mLoadingBar.setVisibility(View.GONE);
						mLoadingText.setText(info.message);
						break;
					}
				}			
			}).start();
		}
	}
	
	/**
	 * 阅读文件
	 * @param file
	 */
	private void readFileData(final MtFile file) {
		mLoadingBar.setVisibility(View.GONE);
		mLoadingText.setText("正在准备阅读文件...");
		
		UIHelper.showWpsReader(this, mFile);
		finish();
	}
	
	public interface CallBack {
		void loadHtml();
	}
	
	//判断文件是否已存在，并保存
	private void saveFile() {
		if(!FileUtils.checkSaveLocationExists()) {
			UIHelper.ToastMessage(this, "SD卡未挂载");
			return;
		}
		if (mFile.getFile() == null) {
			UIHelper.ToastMessage(this, "正在下载...");
			String path = Downloader.SDCARD + "/MeetReader/files/";
			path += mFile.getName() + "." + mFile.getType();
			new Downloader(mFile.getUrl(), path, new Downloader.OnProgressListener() {
				@Override
				public void onProgress(Info info) {
					if (isFinishing()) return; //界面已关闭
					switch (info.state) {
					case Downloader.DOWN_UPDATE:
						break;
					case Downloader.DOWN_FINISH:
						//下载完成
						mFile.setFile(info.file);
						saveFile();//下载完成保存文件
						break;
					case Downloader.DOWN_ERROR:
						//下载出错
						UIHelper.ToastMessage(ReaderActivity.this, "下载出错：" + info.message);
						break;
					}
				}
			}).start();
		} else {
			final String folder = Downloader.SDCARD + "/Documents/";
			new File(folder).mkdirs();
			final File file = new File(folder + mFile.getName() + "." + mFile.getType());
			if (!file.exists()) {
				saveFile(file);
			} else {
				new AlertDialog.Builder(ReaderActivity.this)
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}
	
	class Receiver extends BroadcastReceiver {

		Context mContext;
		
		public Receiver(Context context) {
			mContext = context;
		}
		
		//动态注册  
        public void registerAction(String action){    
            IntentFilter filter = new IntentFilter();    
            filter.addAction(action);        
            mContext.registerReceiver(this, filter);    
        }  
        
        public void registerActionAndScheme(String action, String dataScheme){    
            IntentFilter filter = new IntentFilter();    
            filter.addAction(action);        
            filter.addDataScheme(dataScheme);
            mContext.registerReceiver(this, filter);    
        } 
            
        @Override    
        public void onReceive(Context context, Intent intent) {    
        	String action = intent.getAction();
        	Log.d(TAG, "action:" + action);
        	if (action.equals("cn.wps.moffice.file.close")) {
        		Log.d(TAG, "cn.wps.moffice.file.close");
        		//阅读完毕，长按屏幕再次阅读文件
        		View view = findViewById(R.id.loading_layout);
        		if (view != null) {
					view.setOnLongClickListener(new View.OnLongClickListener() {
						@Override
						public boolean onLongClick(View v) {
							readFileData(mFile);
							return true;
						}
					});
				}
        		mLoadingText.setText("长按屏幕再次阅读文件");
			} else if (action.equals(Intent.ACTION_PACKAGE_ADDED) 
        			&& "cn.wps.moffice_eng".equals(intent.getDataString())) {
        		Log.d(TAG, "Intent.ACTION_PACKAGE_ADDED");
        		readFileData(mFile);//安装完成阅读文件
        	}
        }    
	}
}
