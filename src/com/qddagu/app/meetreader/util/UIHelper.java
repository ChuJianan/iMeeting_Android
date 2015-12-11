package com.qddagu.app.meetreader.util;


import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.qddagu.app.meetreader.AppContext;
import com.qddagu.app.meetreader.AppException;
import com.qddagu.app.meetreader.AppManager;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.api.ApiClient;
import com.qddagu.app.meetreader.bean.Advertising;
import com.qddagu.app.meetreader.bean.User;
import com.qddagu.app.meetreader.bean.Guest;
import com.qddagu.app.meetreader.bean.Meeting;
import com.qddagu.app.meetreader.bean.MtFile;
import com.qddagu.app.meetreader.bean.Note;
import com.qddagu.app.meetreader.ui.AboutActivity;
import com.qddagu.app.meetreader.ui.BrowserActivity;
import com.qddagu.app.meetreader.ui.CardActivity;
import com.qddagu.app.meetreader.ui.InteractionActivity;
import com.qddagu.app.meetreader.ui.SignActivity;
import com.qddagu.app.meetreader.ui.UserActivity;
import com.qddagu.app.meetreader.ui.CommentActivity;
import com.qddagu.app.meetreader.ui.CommentsActivity;
import com.qddagu.app.meetreader.ui.FavoriteActivity;
import com.qddagu.app.meetreader.ui.FilesActivity;
import com.qddagu.app.meetreader.ui.GuestActivity;
import com.qddagu.app.meetreader.ui.MainActivity;
import com.qddagu.app.meetreader.ui.MeetingActivity;
import com.qddagu.app.meetreader.ui.NoteActivity;
import com.qddagu.app.meetreader.ui.NotesActivity;
import com.qddagu.app.meetreader.ui.ReaderActivity;
import com.qddagu.app.meetreader.ui.SettingActivity;
import com.qddagu.app.meetreader.ui.WpsReaderActivity;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class UIHelper {

	public final static int LISTVIEW_ACTION_INIT = 0x01;
	public final static int LISTVIEW_ACTION_REFRESH = 0x02;
	public final static int LISTVIEW_ACTION_SCROLL = 0x03;
	public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;
	
	public final static int LISTVIEW_DATA_MORE = 0x01;
	public final static int LISTVIEW_DATA_LOADING = 0x02;
	public final static int LISTVIEW_DATA_FULL = 0x03;
	public final static int LISTVIEW_DATA_EMPTY = 0x04;
	
	public final static int LISTVIEW_DATATYPE_NEWS = 0x01;
	public final static int LISTVIEW_DATATYPE_BLOG = 0x02;
	public final static int LISTVIEW_DATATYPE_POST = 0x03;
	public final static int LISTVIEW_DATATYPE_TWEET = 0x04;
	public final static int LISTVIEW_DATATYPE_ACTIVE = 0x05;
	public final static int LISTVIEW_DATATYPE_MESSAGE = 0x06;
	public final static int LISTVIEW_DATATYPE_COMMENT = 0x07;
	
	public final static int REQUEST_CODE_FOR_RESULT = 0x01;
	public final static int REQUEST_CODE_FOR_REPLY = 0x02;
	
	/** 全局web样式 */
	public final static String WEB_STYLE = "<style>* {font-size:16px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} " +
			"img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} " +
			"pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;} " +
			"a.tag {font-size:15px;text-decoration:none;background-color:#bbd6f3;border-bottom:2px solid #3E6D8E;border-right:2px solid #7F9FB6;color:#284a7b;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;}</style>";
	
	
	/**
	 * 显示进入“我的名片”对话框
	 * @param activity
	 */
	public static void showUserDialog(final Activity activity) {
		new AlertDialog.Builder(activity)
		.setTitle("提示")
		.setMessage("您还没有设置您的名片信息，点击“确定”按钮进入“我的名片”进行设置。")
		.setPositiveButton("确定", new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				UIHelper.showUser(activity);
			}
		})
		.show();
	}
	
	/**
	 * 判断是否已安装packageName应用程序
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isAvilible(Context context, String packageName) {
		PackageInfo packageInfo;
		try {
		    packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
		    packageInfo = null;
		    e.printStackTrace();
		}
		return packageInfo != null;
	}
	
	/**
	 * 单文件分享
	 * @param activity
	 * @param file
	 */
	public static void shareFile(Activity activity, File file) {
		Intent share = new Intent(Intent.ACTION_SEND);
		share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		share.setType("*/*");
		activity.startActivity(Intent.createChooser(share, "分享"));
	}
	
	/**
	 * 显示嘉宾
	 * @param activity
	 */
	public static void showGuest(Activity activity, Guest guest) {
		Intent intent = new Intent(activity, GuestActivity.class);
		intent.putExtra("guest", guest);
		activity.startActivity(intent);
	}
	
	/**
	 * 显示首页
	 * @param activity
	 */
	public static void showHome(Activity activity)
	{
		Intent intent = new Intent(activity,MainActivity.class);
		activity.startActivity(intent);
		activity.finish();
	}
	
	/**
	 * 显示会议
	 * @param activity
	 */
	public static void showMeeting(Activity activity) {
		Intent intent = new Intent(activity, MeetingActivity.class);
		activity.startActivity(intent);
	}
	
	/**
	 * 显示界面Activity
	 * @param activity
	 */
	public static void showActivity(Activity context, Class<?> cls) {
		Intent intent = new Intent(context, cls);
		context.startActivity(intent);
	}
	
	/**
	 * 显示会议文件列表
	 * @param activity
	 */
	public static void showFiles(Activity activity) {
		Intent intent = new Intent(activity, FilesActivity.class);
		activity.startActivity(intent);
	}
	
	/**
	 * 显示阅读器
	 * @param context
	 * @param file
	 */
	public static void showReader(Context context, MtFile file) {
		Intent intent = new Intent(context, ReaderActivity.class);
		intent.putExtra("file", file);
		context.startActivity(intent);
	}
	
	/**
	 * 显示WPS阅读器
	 * @param context
	 * @param file
	 */
	public static void showWpsReader(Context context, MtFile file) {
		Intent intent = new Intent(context, WpsReaderActivity.class);
		intent.putExtra("file", file);
		context.startActivity(intent);
	}
	
	/**
	 * 显示名片
	 * @param context
	 * @param file
	 */
	public static void showCard(Activity activity, User card) {
		Intent intent = new Intent(activity, CardActivity.class);
		intent.putExtra("card", card);
		activity.startActivity(intent);
	}
	
	/**
	 * 用户信息界面
	 * @param context
	 * @param file
	 */
	public static void showUser(Activity activity) {
		Intent intent = new Intent(activity, UserActivity.class);
		activity.startActivity(intent);
	}
/**
 * 签到界面
 * @param activity
 */
	public static void showSign(Activity activity){
		Intent intent = new Intent(activity, SignActivity.class);
		activity.startActivity(intent);
	}
	/**
	 * 显示评论列表
	 * @param context
	 * @param file
	 */
	public static void showComments(Activity activity, MtFile file) {
		Intent intent = new Intent(activity, CommentsActivity.class);
		intent.putExtra("file", file);
		activity.startActivity(intent);
	}
	
	/**
	 * 评论文件
	 * @param context
	 * @param file
	 */
	public static void showComment(Activity activity) {
		Intent intent = new Intent(activity, CommentActivity.class);
		activity.startActivity(intent);
	}
	/**
	 * 提问
	 * @param activity
	 */
	public static void showInteraction(Activity activity){
		Intent intent=new Intent(activity ,InteractionActivity.class);
		activity.startActivity(intent);
	}
	/**
	 * 显示笔记列表
	 * @param context
	 * @param file
	 */
	public static void showNotes(Activity activity, MtFile file) {
		Intent intent = new Intent(activity, NotesActivity.class);
		intent.putExtra("file", file);
		activity.startActivity(intent);
	}
	
	/**
	 * 新建笔记
	 * @param context
	 * @param file
	 */
	public static void showNote(Activity activity, MtFile file) {
		Intent intent = new Intent(activity, NoteActivity.class);
		intent.putExtra("file", file);
		activity.startActivityForResult(intent, 1);
	}
	
	/**
	 * 编辑笔记
	 * @param context
	 * @param file
	 */
	public static void showNote(Activity activity, Note note) {
		Intent intent = new Intent(activity, NoteActivity.class);
		intent.putExtra("note", note);
		activity.startActivityForResult(intent, 1);
	}
	
	/**
	 * 显示会议收藏夹
	 * @param context
	 * @param file
	 */
	public static void showFavorite(Activity activity) {
		Intent intent = new Intent(activity, FavoriteActivity.class);
		activity.startActivityForResult(intent, 1);
	}
	
	/**
	 * 显示系统设置界面
	 * @param context
	 */
	public static void showSetting(Context context)
	{
		Intent intent = new Intent(context, SettingActivity.class);
		context.startActivity(intent);
	}	
	
	/**
	 * 加载广告
	 */
	public static void loadAdvertising(final Activity activity, List<Advertising> ads) {
		if(ads != null && ads.size() > 0) {
			for (final Advertising ad : ads) {
				int id = activity.getResources().getIdentifier(ad.getPosition(), 
						"id", activity.getPackageName());
				ImageView iv = (ImageView) activity.findViewById(id);
				if (iv != null) {
					UIHelper.showLoadImage(iv, ad.getPicture(), null);
					iv.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent();
							intent.setData(Uri.parse(ad.getUrl()));
							intent.setAction(Intent.ACTION_VIEW);
							activity.startActivity(intent); 
						}
					});
				}
			}
		}
	}
	
	/**
	 * 加载显示图片
	 * @param imgFace
	 * @param faceURL
	 * @param errMsg
	 */
	public static void showLoadImage(final ImageView imgView,final String imgURL,final String errMsg) {
		
		if (imgView == null || StringUtils.isEmpty(imgURL)) return;
		
		//是否有缓存图片
    	final String filename = FileUtils.getFileName(imgURL);
    	//Environment.getExternalStorageDirectory();返回/sdcard
    	String filepath = imgView.getContext().getFilesDir() + java.io.File.separator + filename;
    	java.io.File file = new java.io.File(filepath);
		if(file.exists()){
			Bitmap bmp = ImageUtils.getBitmap(imgView.getContext(), filename);
			imgView.setImageBitmap(bmp);
			imgView.setVisibility(View.VISIBLE);
			return;
    	}
		
		//从网络获取&写入图片缓存
		final Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				if (msg.what==1 && msg.obj != null) {
					imgView.setImageBitmap((Bitmap)msg.obj);
					imgView.setVisibility(View.VISIBLE);
					try {
                    	//写图片缓存
						ImageUtils.saveImage(imgView.getContext(), filename, (Bitmap)msg.obj);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (!StringUtils.isEmpty(errMsg)) {			
					ToastMessage(imgView.getContext(), errMsg);
				}
			}
		};
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {
					Bitmap bmp = ApiClient.getNetBitmap(imgURL);
					msg.what = 1;
					msg.obj = bmp;
				} catch (AppException e) {
					e.printStackTrace();
	            	msg.what = -1;
	            	msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	/**
	 * 内部浏览器打开
	 * @param activity
	 * @param url
	 */
	public static void showBrowser(Activity activity, String url){
		try {
			Uri uri = Uri.parse(url);  
			Intent it = new Intent(Intent.ACTION_VIEW, uri, activity, BrowserActivity.class);  
			activity.startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
			ToastMessage(activity, "无法浏览此网页", 500);
		} 
	}
	
	/**
	 * 打开浏览器
	 * @param context
	 * @param url
	 */
	public static void openBrowser(Context context, String url){
		try {
			Uri uri = Uri.parse(url);  
			Intent it = new Intent(Intent.ACTION_VIEW, uri);  
			context.startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
			ToastMessage(context, "无法浏览此网页", 500);
		} 
	}
	
	/**
	 * 获取TextWatcher对象
	 * @param context
	 * @param tmlKey
	 * @return
	 */
	public static TextWatcher getTextWatcher(final Activity context, final String temlKey) {
		return new TextWatcher() {		
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//保存当前EditText正在编辑的内容
				((AppContext)context.getApplication()).setProperty(temlKey, s.toString());
			}		
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}		
			public void afterTextChanged(Editable s) {}
		};
	}
	
	/**
	 * 弹出Toast消息
	 * @param msg
	 */
	public static void ToastMessage(Context cont,String msg)
	{
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}
	public static void ToastMessage(Context cont,int msg)
	{
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}
	public static void ToastMessage(Context cont,String msg,int time)
	{
		Toast.makeText(cont, msg, time).show();
	}
	
	/**
	 * 点击返回监听事件
	 * @param activity
	 * @return
	 */
	public static View.OnClickListener finish(final Activity activity)
	{
		return new View.OnClickListener() {
			public void onClick(View v) {
				activity.finish();
			}
		};
	}	
	
	/**
	 * 显示关于我们
	 * @param context
	 */
	public static void showAbout(Context context)
	{
		Intent intent = new Intent(context, AboutActivity.class);
		context.startActivity(intent);
	}
	
	/**
	 * 文章是否加载图片显示
	 * @param activity
	 */
	public static void changeSettingIsLoadImage(Activity activity)
	{
		AppContext ac = (AppContext)activity.getApplication();
		if(ac.isLoadImage()){
			ac.setConfigLoadimage(false);
			ToastMessage(activity, "已设置文章不加载图片");
		}else{
			ac.setConfigLoadimage(true);
			ToastMessage(activity, "已设置文章加载图片");
		}
	}
	public static void changeSettingIsLoadImage(Activity activity,boolean b)
	{
		AppContext ac = (AppContext)activity.getApplication();
		ac.setConfigLoadimage(b);
	}
	
	/**
	 * 清除app缓存
	 * @param activity
	 */
	public static void clearAppCache(Activity activity)
	{
		final AppContext ac = (AppContext)activity.getApplication();
		final Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				if(msg.what==1){
					ToastMessage(ac, "缓存清除成功");
				}else{
					ToastMessage(ac, "缓存清除失败");
				}
			}
		};
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {				
					ac.clearAppCache();
					msg.what = 1;
				} catch (Exception e) {
					e.printStackTrace();
	            	msg.what = -1;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	/**
	 * 发送App异常崩溃报告
	 * @param cont
	 * @param crashReport
	 */
	public static void sendAppCrashReport(final Context cont, final String crashReport)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_error);
		builder.setMessage(R.string.app_error_message);
		builder.setPositiveButton(R.string.submit_report, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//发送异常报告
				Intent i = new Intent(Intent.ACTION_SEND);
				//i.setType("text/plain"); //模拟器
				i.setType("message/rfc822") ; //真机
				i.putExtra(Intent.EXTRA_EMAIL, new String[]{"qddagu@126.com"});
				i.putExtra(Intent.EXTRA_SUBJECT,"爱会议Android客户端 - 错误报告");
				i.putExtra(Intent.EXTRA_TEXT,crashReport);
				cont.startActivity(Intent.createChooser(i, "发送错误报告"));
				//退出
				AppManager.getAppManager().AppExit(cont);
			}
		});
		builder.setNegativeButton(R.string.sure, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//退出
				AppManager.getAppManager().AppExit(cont);
			}
		});
		builder.show();
	}
	
	/**
	 * 退出程序
	 * @param cont
	 */
	public static void Exit(final Context cont)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		//builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.prompt);
		builder.setMessage(R.string.app_menu_surelogout);
		builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//退出
				AppManager.getAppManager().AppExit(cont);
			}
		});
		builder.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}
}
