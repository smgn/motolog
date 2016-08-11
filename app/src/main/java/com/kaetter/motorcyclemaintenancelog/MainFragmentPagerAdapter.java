package com.kaetter.motorcyclemaintenancelog;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
	Context mContext;

	List<Fragment> fragmentList = new ArrayList<>();

	public MainFragmentPagerAdapter(FragmentManager fm, Context context) {
		super(fm);
		mContext = context;

        fragmentList.add(LogFragment.newInstance());
        fragmentList.add(ReminderFragment.newInstance());
        fragmentList.add(ConfigFragment.newInstance());
	}

	@Override
	public Fragment getItem(int position) {
        return fragmentList.get(position);
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
		return fragmentList.size();
	}
}
