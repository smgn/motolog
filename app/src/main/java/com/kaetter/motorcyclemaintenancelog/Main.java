package com.kaetter.motorcyclemaintenancelog;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Main extends AppCompatActivity {

	@Bind(R.id.toolbar) Toolbar mToolbar;
	@Bind(R.id.tabLayout) TabLayout mTabLayout;
	@Bind(R.id.viewPager) ViewPager mViewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		ButterKnife.bind(this);

		setSupportActionBar(mToolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle("");
			getSupportActionBar().setIcon(R.mipmap.app_icon);
		}

		mViewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager(), this));
		mTabLayout.setupWithViewPager(mViewPager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_importdb:
				//TODO: transplant code
				return true;
			case R.id.menu_exportdb:
				//TODO: transplant code
				return true;
			case R.id.menu_filter:
				//TODO: transplant code
				return true;
			case R.id.menu_settings:
				//TODO: transplant code
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}