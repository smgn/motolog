package com.kaetter.motorcyclemaintenancelog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class TabsFragment extends Fragment implements OnTabChangeListener {

	public static final String TAG = "FragmentTabs";
	public static final String TAB_LOG = "log";
	public static final String TAB_REM = "reminder";
	public static final String TAB_CONF = "config.";

	public static int mCurrentTab;
	private Menu cmenu;


	@Override
	public void onAttach(Context activity) {
		super.onAttach(activity);
	}

	public static void changeTab(boolean b) {
//		if (b&&mCurrentTab<2)
//			mTabHost.setCurrentTab(mCurrentTab+1);
//		else
//			if(!b&& mCurrentTab>0 )
//			mTabHost.setCurrentTab(mCurrentTab-1);
		Log.d(TAG,"[skipped] changeTab");
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.tabs_fragment, null);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);

		// manually start loading stuff in the first tab
		updateTab(TAB_LOG, R.id.tab_1);
	}



	public static TabSpec newTab(String tag, int labelId, int tabContentId, Activity activity, TabHost mTabHost) {
		Log.d(TAG, "buildTab(): tag=" + tag);

		View indicator = activity.getLayoutInflater().inflate(
				R.layout.tab,
				(ViewGroup) activity.findViewById(android.R.id.tabs), false);
		((TextView) indicator.findViewById(R.id.text)).setText(labelId);

		TabSpec tabSpec = mTabHost.newTabSpec(tag);
		tabSpec.setIndicator(indicator);
		Log.d(tag, tabContentId + " tabContentId " + indicator.getId()
				+ " indicator get Id ");
		tabSpec.setContent(tabContentId);
		return tabSpec;
	}

	
	
	
	@Override
	public void onTabChanged(String tabId) {
		Log.d(TAG, "onTabChanged(): tabId=" + tabId);
		if (TAB_LOG.equals(tabId)) {
			Log.d(TAG, tabId);
			if (cmenu != null) {

				if (!cmenu.findItem(R.id.menu_filter).isVisible()) {
					cmenu.findItem(R.id.menu_filter).setVisible(true);
					cmenu.findItem(R.id.menu_exportdb).setVisible(true);
					cmenu.findItem(R.id.menu_importdb).setVisible(true);
				}
			}

			updateTab(tabId, R.id.tab_1);
			mCurrentTab = 0;
			return;
		}
		if (TAB_REM.equals(tabId)) {
			Log.d(TAG, tabId);
			if (cmenu != null) {

				if (cmenu.findItem(R.id.menu_filter).isVisible()) {
					cmenu.findItem(R.id.menu_filter).setVisible(false);
					cmenu.findItem(R.id.menu_exportdb).setVisible(false);
					cmenu.findItem(R.id.menu_importdb).setVisible(false);
				}
			}
			updateTab(tabId, R.id.tab_2);
			mCurrentTab = 1;
			return;
		}
		if (TAB_CONF.equals(tabId)) {
			Log.d(TAG, tabId);
			if (cmenu != null) {

				if (cmenu.findItem(R.id.menu_filter).isVisible()) {
					cmenu.findItem(R.id.menu_filter).setVisible(false);
					cmenu.findItem(R.id.menu_exportdb).setVisible(false);
					cmenu.findItem(R.id.menu_importdb).setVisible(false);
				}
			}
			updateTab(tabId, R.id.tab_3);
			mCurrentTab = 2;
			return;
		}
	}

	private void updateTab(String tabId, int placeholder) {
//		if(getActivity()==null){
//			Log.d(TAG, "[skipped] updatingTab");
//			return;
//		}
		Log.d(TAG, "updatingTab");
		FragmentManager fm = getActivity().getSupportFragmentManager();
		if (fm.findFragmentByTag(tabId) == null) {

			fm.beginTransaction()
					.replace(placeholder, new MyListFragment(tabId), tabId)
					.commit();
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		Log.d(TAG, "onCreateOptionsMenu mcurrent tab is" + mCurrentTab);
		inflater.inflate(R.menu.activity_main, menu);
		this.cmenu = menu;
		if (mCurrentTab == 0) {

			menu.findItem(R.id.menu_filter).setVisible(true);
			menu.findItem(R.id.menu_exportdb).setVisible(true);
			menu.findItem(R.id.menu_importdb).setVisible(true);
		} else {

			menu.findItem(R.id.menu_filter).setVisible(false);
			menu.findItem(R.id.menu_exportdb).setVisible(false);
			menu.findItem(R.id.menu_importdb).setVisible(false);
		}

		// super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onResume() {

		super.onResume();
	}



}
