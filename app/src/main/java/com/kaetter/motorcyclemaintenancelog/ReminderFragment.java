package com.kaetter.motorcyclemaintenancelog;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

    private final String TAG = "ReminderFragment";
    private final int LOADER_ID = 2;

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

        getLoaderManager().initLoader(LOADER_ID, null, this);

		return root;
	}

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        AsyncTaskLoader<Cursor> loader = null;
        if (id == LOADER_ID) {
            loader = new AsyncTaskLoader<Cursor>(getActivity()) {
                @Override
                public Cursor loadInBackground() {
                    if (remLogSource == null) {
                        remLogSource = new RemLogSource(getContext());
                    }
                    return remLogSource.getCursor();
                }
            };
            loader.forceLoad();
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_ID) {
            remAdapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
