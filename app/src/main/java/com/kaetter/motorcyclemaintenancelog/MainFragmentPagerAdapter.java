package com.kaetter.motorcyclemaintenancelog;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
	final int mPageCount = 3;
	String[] mTabTitles;
	Context mContext;

	public MainFragmentPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		mContext = context;
		mTabTitles = new String[] {mContext.getString(R.string.title_log),
				mContext.getString(R.string.title_reminder),
				mContext.getString(R.string.title_config)};
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
	public int getCount() {
		return mPageCount;
	}
}
