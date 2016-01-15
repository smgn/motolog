package com.kaetter.motorcyclemaintenancelog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public class ReminderFragment extends Fragment {

	public static ReminderFragment newInstance() {
		Bundle args = new Bundle();
		ReminderFragment fragment = new ReminderFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_reminder, container, false);

		ButterKnife.bind(this, root);

		return root;
	}
}
