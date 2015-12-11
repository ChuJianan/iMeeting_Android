package com.qddagu.app.meetreader.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.qddagu.app.meetreader.util.Downloader.Info;

public class MultiDownloader {
	private static MultiDownloader multiDownloader;
	private Downloader.OnProgressListener downloadListener;
	//private LinkedHashMap<String, Downloader> downloaders;
	private LinkedList<Downloader> downloaders;
	private Set<String> urlSet;
	private Map<String, Info> infoMap;
	private MultiDownloader(){
		infoMap = new HashMap<String, Downloader.Info>();
		urlSet = new HashSet<String>();
		downloaders = new LinkedList<Downloader>();
		downloadListener = new Downloader.OnProgressListener() {
			@Override
			public void onProgress(Info info) {
				switch (info.state) {
				case Downloader.DOWN_UPDATE:
					infoMap.put(info.fileUrl, info);
					break;
				case Downloader.DOWN_FINISH:
					//infoMap.remove(info.fileUrl);
					break;
				case Downloader.DOWN_ERROR:
					break;
				}
				for (OnProgressListener listener : listeners) {
					listener.onProgress(info, getProgress());
				}
			}
		};
	}

	public static synchronized MultiDownloader getInstance() {
		if (multiDownloader == null) {
			multiDownloader = new MultiDownloader();
		}
		return multiDownloader;
	}
	
	public Map<String, Info> getInfos() {
		return infoMap;
	}
	
	public Downloader getTask() {
		synchronized (downloaders) {  
            return (downloaders.size() == 0) ? null : downloaders.removeFirst();
        }  
	}
	
    public void download(String url, String path) {  
        synchronized (downloaders) {  
            if (!isTaskRepeat(url)) {  
                // 增加下载任务  
            	downloaders.addLast(new Downloader(url, path, downloadListener));
            }  
        }  
  
    }  
    public boolean isTaskRepeat(String url) {  
        synchronized (url) {  
            if (urlSet.contains(url)) {  
                return true;  
            } else {  
                System.out.println("下载管理器增加下载任务："+ url);  
                urlSet.add(url);  
                return false;  
            }  
        }  
    }  
    
	//获取总体下载进度
	private int getProgress() {
		if (infoMap.size() < 1) {
			return 100;
		}
		int tempProgress = 0, totalProgress = 0;
		Iterator<Info> it = infoMap.values().iterator();
		while (it.hasNext()) {
			Info info = it.next();
			tempProgress += info.progress;
			totalProgress += 100;
		}
		return tempProgress / totalProgress;
	}
	
	//OnProgressListener mProgressListener;
	List<OnProgressListener> listeners = new ArrayList<OnProgressListener>();
	
	public MultiDownloader setOnProgressListener(OnProgressListener listener) {
		this.listeners.add(listener);
		return multiDownloader;
	}
	
	public interface OnProgressListener {
		void onProgress(Info info, int progress);
	}
}
