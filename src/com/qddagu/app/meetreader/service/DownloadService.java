package com.qddagu.app.meetreader.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.ui.DownloadActivity;
import com.qddagu.app.meetreader.util.Downloader;
import com.qddagu.app.meetreader.util.Downloader.Info;
import com.qddagu.app.meetreader.util.MultiDownloader;

public class DownloadService extends Service {
	private static final int NOTIFY_ID = 0;
	Notification mNotification;
	NotificationManager mNotificationManager;
	PendingIntent mPendingIntent;
	int progress;
	
	MultiDownloader multiDownloader;
	ExecutorService pool;
	@Override
	public void onCreate() {
		super.onCreate();
		
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, DownloadActivity.class), 0);
		
		mNotification = new NotificationCompat.Builder(this)
			.setTicker("正在准备下载会议文件")
			.setContentTitle("正在下载会议文件")
			.setContentText("点击查看正在下载的会议文件")
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentIntent(mPendingIntent)
			.setWhen(System.currentTimeMillis())
			.setOngoing(true)
			.build();
		// 设置setLatestEventInfo方法,如果不设置会App报错异常
 	    mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
 	    
 	    // 注册此通知
 	    // 如果该NOTIFICATION_ID的通知已存在，会显示最新通知的相关信息 ，比如tickerText 等
 	    mNotificationManager.notify(NOTIFY_ID, mNotification);
 	    
 	    multiDownloader = MultiDownloader.getInstance();
 	    multiDownloader.setOnProgressListener(new MultiDownloader.OnProgressListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onProgress(Info info, int progress) {
				if (progress < 100) {  
                    // 更新进度  
					mNotification.setLatestEventInfo(DownloadService.this,
                    		"正在下载会议文件", progress + "%", mPendingIntent);
                } else {  
                    // 下载完毕后变换通知形式  
                    mNotification.flags = Notification.FLAG_AUTO_CANCEL;  
                    mNotification.setLatestEventInfo(DownloadService.this,
                    		"下载完成", "文件已下载完毕", mPendingIntent);
                    stopSelf();
                }  
  
                // 最后别忘了通知一下,否则不会更新  
                mNotificationManager.notify(NOTIFY_ID, mNotification);  
			}
		});
 	    
 	    pool = Executors.newFixedThreadPool(5);
 	    
 	    new Thread() {
 	    	public void run() {
 	    		while (true) {
					try {
						Downloader task = multiDownloader.getTask();
						if (task != null) {
							pool.execute(task.getRunnable());
						} else {
							Thread.sleep(1000);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
 	    	};
 	    }.start();
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
