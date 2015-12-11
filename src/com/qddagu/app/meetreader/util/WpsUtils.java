package com.qddagu.app.meetreader.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qddagu.app.meetreader.AppContext;
import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.util.Downloader.Info;

public class WpsUtils {
	public static final String OPEN_MODE ="OpenMode";
	public static final String READ_ONLY ="ReadOnly";
	public static final String READ_MODE ="ReadMode";
	public static final String CLEAR_BUFFER ="ClearBuffer";
	public static final String CLEAR_TRACE ="ClearTrace";
	public static final String CLEAR_FILE ="ClearFile";
	public static final String SEND_CLOSE_BROAD ="SendCloseBroad";
	public static final String THIRD_PACKAGE ="ThirdPackage";
	
	private static Downloader mDownloader;
	private final static String WPS_PATH = Downloader.SDCARD + "/MeetReader/wps.apk";
	
	public static void openFile(Activity activity, String path, OnInstallListener listener) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString(OPEN_MODE, READ_ONLY);
		bundle.putBoolean(SEND_CLOSE_BROAD, true);
		bundle.putString(THIRD_PACKAGE, activity.getPackageName());
		bundle.putBoolean(CLEAR_BUFFER, true);
		bundle.putBoolean(CLEAR_TRACE, true);
		//bundle.putBoolean(CLEAR_FILE, true);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String packageName = "cn.wps.moffice_eng";
		String className = "cn.wps.moffice.documentmanager.PreStartActivity";
		intent.setClassName(packageName, className);

		Uri uri = Uri.fromFile(new File(path));
		intent.setData(uri);
		intent.putExtras(bundle);

		try {
			activity.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
			ifInstallWps(activity, listener);
		}
	}
	
	private static void installWps(Activity activity, String path) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String uriString = "file://" + path;
		String type = "application/vnd.android.package-archive";
		intent.setDataAndType(Uri.parse(uriString), type);
		activity.startActivity(intent);
		//activity.finish();
	}
	
	//WPS安装对话框监听
	public interface OnInstallListener {
		void onOk();		//确定安装
		void onCancel();	//取消安装
		void onDown();		//下载完成
	}
	
	public static void ifInstallWps(final Activity activity, final OnInstallListener listener) {
		new AlertDialog.Builder(activity)
		.setTitle("提示")
		.setMessage("检测到您的设备未安装能够打开当前文件的软件，是否下载安装WPS进行阅读？")
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (listener != null) {
					listener.onCancel();
				}
			}
		})
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AppContext context = (AppContext)activity.getApplication();
				downloadWps(activity, context.Meeting().getUrls().Wps(), listener);
			}
		})
		.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if (listener != null) {
					listener.onCancel();
				}
			}
		}).create().show();
	}
	
	public static void downloadWps(final Activity activity, String url, final OnInstallListener listener) {
		
		AlertDialog.Builder builder = new Builder(activity);
		builder.setTitle("正在下载WPS");
		
		final LayoutInflater inflater = LayoutInflater.from(activity);
		View v = inflater.inflate(R.layout.update_progress, null);
		final ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.update_progress);
		final TextView progressText = (TextView) v.findViewById(R.id.update_progress_text);

		builder.setView(v);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				mDownloader.cancel();
				if (listener != null) {
					listener.onCancel();
				}
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				mDownloader.cancel();
			}
		});
		final AlertDialog downloadDialog = builder.create();
		downloadDialog.setCanceledOnTouchOutside(false);
		downloadDialog.show();
		
		mDownloader = new Downloader(url, WPS_PATH, new Downloader.OnProgressListener() {
			@Override
			public void onProgress(Info info) {
				switch (info.state) {
				case Downloader.DOWN_UPDATE:
					progressBar.setProgress(info.progress);
					progressText.setText("正在下载：" + info.tempSize + "/" + info.fileSize);
					break;
				case Downloader.DOWN_FINISH:
					downloadDialog.dismiss();
					installWps(activity, WPS_PATH);
					break;
				case Downloader.DOWN_ERROR:
					downloadDialog.dismiss();
					UIHelper.ToastMessage(activity, info.message);
					break;
				}
			}
		});
		mDownloader.start();
	}
	
	/**
     * 将assets文件夹下的apk fileName，拷贝到路径path 
     * @param context 上下文环境
     * @param fileName apk名称
     * @param path 存储APK路径
     * @return
     */
    public static boolean copyApkFromAssets(Context context, String fileName, String path) {
        boolean bRet = false;
        try {
            InputStream is = context.getAssets().open(fileName);
            File file = new File(path);
            if (file.exists()) {
				return true;
			}
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();
            bRet = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bRet;
    }
}
