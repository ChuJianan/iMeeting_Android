package com.qddagu.app.meetreader.ui.fragment;

import com.qddagu.app.meetreader.AppContext;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

public class BaseFragment extends Fragment {
	protected AppContext appContext;	//全局Context
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		appContext = (AppContext) getActivity().getApplication();
	}
	
	protected View findViewById(int id) {
		return getActivity().findViewById(id);
	}
}
