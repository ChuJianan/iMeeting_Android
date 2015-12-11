package com.qddagu.app.meetreader.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.qddagu.app.meetreader.ui.fragment.CloudFragment;
import com.qddagu.app.meetreader.ui.fragment.HomeFragment;
import com.qddagu.app.meetreader.ui.fragment.MoreFragment;
import com.qddagu.app.meetreader.ui.fragment.NoticeFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {
	Context mContext;
	List<Fragment> fragments;
	public MainPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		mContext = context;

		fragments = new ArrayList<Fragment>();
		fragments.add(Fragment.instantiate(mContext, HomeFragment.class.getName()));
		fragments.add(Fragment.instantiate(mContext, CloudFragment.class.getName()));
		fragments.add(Fragment.instantiate(mContext, NoticeFragment.class.getName()));
		fragments.add(Fragment.instantiate(mContext, MoreFragment.class.getName()));
	}

	@Override
	public Fragment getItem(int position) {
		// getItem is called to instantiate the fragment for the given page.
		// Return a DummySectionFragment (defined as a static inner class
		// below) with the page number as its lone argument.
		
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

}
