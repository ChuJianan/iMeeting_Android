package com.qddagu.app.meetreader.ui;

import com.qddagu.app.meetreader.AppContext;
import com.qddagu.app.meetreader.AppManager;
import com.qddagu.app.meetreader.R;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingActivity extends PreferenceActivity {
	
	private SharedPreferences mPreferences;
	private Preference mMeetingHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//添加Activity到堆栈
		AppManager.getAppManager().addActivity(this);
		
		//设置显示Preferences
		addPreferencesFromResource(R.xml.preferences);
		
		//获得SharedPreferences
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		final AppContext ac = (AppContext)getApplication();
		
		//设置服务器接口文件路径
		mMeetingHost = (EditTextPreference)findPreference("url_api_host");
		mMeetingHost.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				ac.setHostApiUrl(newValue.toString());
				return true;
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//结束Activity&从堆栈中移除
		AppManager.getAppManager().finishActivity(this);
	}
}
