package com.kaetter.motorcyclemaintenancelog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public class LogFragment extends Fragment {

	public static LogFragment newInstance() {
		Bundle args = new Bundle();
		LogFragment fragment = new LogFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_log, container, false);

		ButterKnife.bind(this, root);

		return root;
	}
}
