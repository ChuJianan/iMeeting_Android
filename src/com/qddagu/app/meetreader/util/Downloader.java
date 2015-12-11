package com.qddagu.app.meetreader.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import android.os.Environment;
import android.os.Handler;

/**
 * 文件下载器
 * @author SYZ
 *
 */
public class Downloader {
	private static final int DOWN_NOSDCARD = 0;
	public final static int DOWN_UPDATE = 0x1;
	public final static int DOWN_FINISH = 0x2;
	public final static int DOWN_ERROR 	= 0x3;
	
	public final static String SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
	
	//private String fileUrl;
	//private String filePath;
	private Handler mHandler;
	private Runnable mRunnable;
	private boolean isCancel;
	private boolean cacheable;		//是否允许使用缓存
	public void cancel() { //取消下载
		this.isCancel = true;
	}
	
	public class Info { //下载信息
		public File file;		//文件
		public String fileName;	//文件名称
		public String fileUrl;	//文件地址
		public String filePath;	//保存地址
		public String fileSize;	//文件总大小
		public String tempSize;	//当前下载大小
		public String message;	//信息
		public int progress;	//下载进度
		public int state;		//下载状态 0-等待 1-下载 2-完成 3-出错
	}
	
	private Info info; // 下载信息
	
	/**
	 * 实例化文件下载器
	 * @param url	文件URL
	 * @param path	保存路径
	 * @param handler 处理器
	 */
	public Downloader(String url, String path, OnProgressListener listener) {
		this(url, path, true, listener);
	}
	
	/**
	 * 实例化文件下载器
	 * @param url
	 * @param cacheable 是否允许使用缓存
	 * @param handler
	 */
	public Downloader(String url, String path, boolean cacheable, final OnProgressListener listener) {
		//this.fileUrl = url;
		//this.filePath = path;
		this.cacheable = cacheable;
		this.info = new Info();
		this.info.fileUrl = url;
		this.info.filePath = path;
		this.info.fileName = new File(path).getName();
		
		this.mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (listener == null) { return; } //为空不监听
				switch (msg.what) {
				case DOWN_FINISH:
					info.progress = 100;
					break;
				case DOWN_ERROR:
					info.message = msg.obj.toString();
					break;
				}
				info.state = msg.what;
				listener.onProgress(info);
			};
		};
		
		this.mRunnable = new Thread(){
			public void run() {
				//判断是否挂载了SD卡
				String storageState = Environment.getExternalStorageState();		
				if(!storageState.equals(Environment.MEDIA_MOUNTED)){
					info.message = "SD卡未挂载";
					mHandler.sendEmptyMessage(DOWN_NOSDCARD);
					return;
				}
				
				String cachePath = SDCARD + "/MeetReader/files/.cache/";
				new File(cachePath).mkdirs();
				File tmpFile = new File(cachePath + Md5.encode(info.fileUrl) + ".tmp");
				File cacheFile = new File(cachePath + Md5.encode(info.fileUrl));
				File file = new File(info.filePath);
				file.getParentFile().mkdirs();
				
				InputStream is = null;
				//ByteArrayOutputStream os = null;
				FileOutputStream fos = null;
				try {					
					//cacheable
					if(Downloader.this.cacheable && cacheFile.exists()) {
						FileUtils.copyFile(cacheFile, file);
						info.file = file;
						mHandler.sendEmptyMessage(DOWN_FINISH);
						return;
					}
					//cacheable end
					
					fos = new FileOutputStream(tmpFile);
					
					//os = new ByteArrayOutputStream(1024);
					
					URL url = new URL(info.fileUrl);
					HttpURLConnection conn = (HttpURLConnection)url.openConnection();
					conn.connect();
					int length = conn.getContentLength();
					is = conn.getInputStream();
					
					//显示文件大小格式：2个小数点显示
			    	DecimalFormat df = new DecimalFormat("0.00");
			    	
			    	//总文件大小
			    	//info.fileSize = df.format((float) length / 1024 / 1024) + "MB";
			    	info.fileSize = FileUtils.formatFileSize(length);
					
					int count = 0;
					byte buf[] = new byte[1024];
					
					do{   		   		
			    		int numread = is.read(buf);//(buf, 0, buf.length);
			    		count += numread;
			    		//当前下载文件大小
			    		info.tempSize = FileUtils.formatFileSize(count);
			    		//当前进度值
			    		info.progress =(int)(((float)count / length) * 100);
			    	    //更新进度
			    	    mHandler.sendEmptyMessage(DOWN_UPDATE);
			    		if(numread <= 0){
			    			if (tmpFile.renameTo(cacheFile)) {
			    				FileUtils.copyFile(cacheFile, file);
			    				info.file = file;
			    				//通知完成
				    			mHandler.sendEmptyMessage(DOWN_FINISH);
							} else {
								throw new IOException("重命名失败");
							}
			    			break;
			    		}
			    		//cacheable
			    		fos.write(buf, 0, numread);
			    		//cacheable end
			    		//os.write(buf, 0, numread); //os.write(buf)会出错，多读出很多字节？
			    	}while(!isCancel);//点击取消就停止下载
					//cacheable
					fos.close();
					//cacheable end
				} catch (MalformedURLException e) {
					mHandler.obtainMessage(DOWN_ERROR, "URL格式有误").sendToTarget();
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					mHandler.obtainMessage(DOWN_ERROR, "服务器文件不存在").sendToTarget();
					e.printStackTrace();
				} catch (ConnectException e) {
					mHandler.obtainMessage(DOWN_ERROR, "服务器连接失败").sendToTarget();
					e.printStackTrace();
				} catch (IOException e) {
					mHandler.obtainMessage(DOWN_ERROR, "文件读写错误").sendToTarget();
					e.printStackTrace();
				} finally {
					try {
						if(is != null) is.close();
						if(fos != null) fos.close();
					} catch (IOException e) { }
				}
			}
		};
	}
	
	public interface OnProgressListener {
		void onProgress(Info info);
	}

	/**
	 * 开始下载文件
	 */
	public void start() {
		new Thread(mRunnable).start();
	}
	
	public Runnable getRunnable() {
		return this.mRunnable;
	}
}
