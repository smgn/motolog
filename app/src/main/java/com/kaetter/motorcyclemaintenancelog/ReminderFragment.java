package com.kaetter.motorcyclemaintenancelog;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Date;

import adapter.RemLogCursorAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import dbcontrollers.MainLogSource;
import dbcontrollers.RemLogSource;

public class ReminderFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.filter) Spinner filter;
    @BindView(R.id.reminderList) ListView reminderLogListView;
    @BindView(R.id.textNoRemindersYet) TextView textNoRemindersYet;

    MainLogSource mainLogSource;
	RemLogSource remLogSource;
	RemLogCursorAdapter remAdapter;
    private int odometer;

	public static ReminderFragment newInstance() {
		return new ReminderFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_reminder, container, false);

		ButterKnife.bind(this, root);

        mainLogSource = new MainLogSource(getContext());
        odometer = mainLogSource.getLastOdometer("default");

        remLogSource = new RemLogSource(getContext());
        remAdapter = new RemLogCursorAdapter(
                getActivity(), remLogSource.getCursor(), odometer, new Date());

        reminderLogListView.setEmptyView(textNoRemindersYet);
        reminderLogListView.setAdapter(remAdapter);

        //TODO: Loader

		return root;
	}

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
