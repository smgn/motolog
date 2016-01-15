package com.kaetter.motorcyclemaintenancelog;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
	final int mPageCount = 3;
	Context mContext;

	public MainFragmentPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		mContext = context;
	}

	@Override
	public Fragment getItem(int position) {
		switch(position) {
			case 0: return LogFragment.newInstance();
			case 1: return ReminderFragment.newInstance();
			case 2: return ConfigFragment.newInstance();
		}
		return null; // should never happen
	}

	@Override
	 public CharSequence getPageTitle(int position) {
		switch(position) {
			case 0: return mContext.getString(R.string.title_log);
			case 1: return mContext.getString(R.string.title_reminder);
			case 2: return mContext.getString(R.string.title_config);
		}
		return "";
	}

	@Override
	public int getCount() {
		return mPageCount;
	}
}
