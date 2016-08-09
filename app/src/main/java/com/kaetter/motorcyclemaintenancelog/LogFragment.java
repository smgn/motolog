package com.kaetter.motorcyclemaintenancelog;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import adapter.MainLogCursorAdapter;
import butterknife.Bind;
import butterknife.ButterKnife;
import dbcontrollers.MainHelper;
import dbcontrollers.MainLogSource;
import events.CopyDatabaseEvent;

public class LogFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	@Bind(R.id.filter) Spinner filter;
	@Bind(R.id.mainList) ListView mainLogListView;
	@Bind(R.id.textNoLogsYet) TextView textNoLogsYet;

	MainLogSource mainLogSource;
	MainLogCursorAdapter mainAdapter;

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

		mainLogSource = new MainLogSource(getActivity());
		mainAdapter = new MainLogCursorAdapter(getActivity(), null);

		mainLogListView.setEmptyView(textNoLogsYet);
		mainLogListView.setAdapter(mainAdapter);
		mainLogListView.setSelection(mainAdapter.getCount() - 1);

		mainLogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				FragmentManager fm = getActivity().getSupportFragmentManager();
				Cursor cursorAt = (Cursor) mainLogListView.getItemAtPosition(position);
				int key = cursorAt.getInt(cursorAt.getColumnIndex(MainHelper.KEY));

				String vehicle = cursorAt.getString(cursorAt.getColumnIndex(MainHelper.FIELD1));
				String maintElem = cursorAt.getString(cursorAt.getColumnIndex(MainHelper.FIELD2));
				String maintType = cursorAt.getString(cursorAt.getColumnIndex(MainHelper.FIELD3));
				double fuelAmount = cursorAt.getFloat(cursorAt.getColumnIndex(MainHelper.FIELD4));
				double consumption = cursorAt.getFloat(cursorAt.getColumnIndex(MainHelper.FIELD5));
				String date = cursorAt.getString(cursorAt.getColumnIndex(MainHelper.FIELD6));
				int odometer = cursorAt.getInt(cursorAt.getColumnIndex(MainHelper.FIELD7));
				String details = cursorAt.getString(cursorAt.getColumnIndex(MainHelper.FIELD8));
				double cash = cursorAt.getFloat(cursorAt.getColumnIndex(MainHelper.FIELD10));

//				MaintenanceItem item = new MaintenanceItem(key, vehicle,
//						maintElem, maintType, fuelAmount, consumption, date,
//						odometer, details, mileageType, cash);

				// UpdateDialog updateDialog = new UpdateDialog(item);

//				UpdateDialog updateDialog1 = new UpdateDialog();
//
//				Bundle args = new Bundle();
//				args.putSerializable("MaintItem", item);
//				updateDialog1.setArguments(args);
//
//				updateDialog1.show(fm, "fragment_edit_name");

			}

		});

		getLoaderManager().initLoader(1, null, this);

		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
	}

	@Override
	public void onPause() {
		EventBus.getDefault().unregister(this);
		super.onPause();
	}

	@Subscribe
	public void onEvent(CopyDatabaseEvent event) {
		try {
			mainLogSource.copyDatabase(event.fromDbPath, event.toDbPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
		AsyncTaskLoader<Cursor> loader=null;
		if (id == 1) {
			loader = new AsyncTaskLoader<Cursor>(getActivity()) {
				@Override
				public Cursor loadInBackground() {
					if (mainLogSource == null) {
						mainLogSource = new MainLogSource(getContext());
					}
					Cursor cursor;
					if (args == null) {
						cursor = mainLogSource.getCursor();
					}
					else  {
						cursor = mainLogSource.getCursor(args.getString("filter"));
					}
					return cursor;
				}
			};
			loader.forceLoad();
		}
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (loader.getId() == 1) {
			mainAdapter.swapCursor(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {}
}
