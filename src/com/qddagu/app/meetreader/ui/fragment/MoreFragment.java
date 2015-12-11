package com.qddagu.app.meetreader.ui.fragment;

import com.qddagu.app.meetreader.R;
import com.qddagu.app.meetreader.util.UIHelper;
import com.qddagu.app.meetreader.util.UpdateManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class MoreFragment extends BaseFragment implements OnClickListener {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_more, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		initView();
	}
	
	private void initView() {
		findViewById(R.id.more_user).setOnClickListener(this);
		findViewById(R.id.more_favorite).setOnClickListener(this);
		findViewById(R.id.more_update).setOnClickListener(this);
		findViewById(R.id.more_about).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.more_user:
			UIHelper.showUser(getActivity());
			break;
		case R.id.more_favorite:
			UIHelper.showFavorite(getActivity());
			break;
		case R.id.more_update:
			UpdateManager.getUpdateManager().checkAppUpdate(getActivity(), true);
			break;
		case R.id.more_about:
			UIHelper.showAbout(getActivity());
			break;
		}
	}
}
