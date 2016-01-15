package com.kaetter.motorcyclemaintenancelog;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

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

		mViewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager(), this));
		mTabLayout.setupWithViewPager(mViewPager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}
}