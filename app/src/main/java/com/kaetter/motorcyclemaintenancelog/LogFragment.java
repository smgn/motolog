package com.kaetter.motorcyclemaintenancelog;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import dbcontrollers.MainLogSource;
import dbcontrollers.MotoLogHelper;
import events.CopyDatabaseEvent;
import events.ReloadMainLogEvent;
import events.ReloadReminderLogEvent;

public class LogFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = "LogFragment";
    private final int LOADER_ID = 1;

	@BindView(R.id.filter) Spinner filter;
	@BindView(R.id.mainList) ListView mainLogListView;
	@BindView(R.id.textNoLogsYet) TextView textNoLogsYet;

	MainLogSource mainLogSource;
	MainLogCursorAdapter mainAdapter;

	public static LogFragment newInstance() {
		return new LogFragment();
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

				Log.d(TAG, "Item clicked in mainLogListView");

				FragmentManager fm = getActivity().getSupportFragmentManager();
				Cursor cursorAt = (Cursor) mainLogListView.getItemAtPosition(position);
				int key = cursorAt.getInt(cursorAt.getColumnIndex(MotoLogHelper.KEY));

				String vehicle = cursorAt.getString(cursorAt.getColumnIndex(MotoLogHelper.FIELD1));
				String maintElem = cursorAt.getString(cursorAt.getColumnIndex(MotoLogHelper.FIELD2));
				String maintType = cursorAt.getString(cursorAt.getColumnIndex(MotoLogHelper.FIELD3));
				double fuelAmount = cursorAt.getFloat(cursorAt.getColumnIndex(MotoLogHelper.FIELD4));
				double consumption = cursorAt.getFloat(cursorAt.getColumnIndex(MotoLogHelper.FIELD5));
				String date = cursorAt.getString(cursorAt.getColumnIndex(MotoLogHelper.FIELD6));
				int odometer = cursorAt.getInt(cursorAt.getColumnIndex(MotoLogHelper.FIELD7));
				String details = cursorAt.getString(cursorAt.getColumnIndex(MotoLogHelper.FIELD8));
				double cash = cursorAt.getFloat(cursorAt.getColumnIndex(MotoLogHelper.FIELD10));

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

		getLoaderManager().initLoader(LOADER_ID, null, this);

		return root;
	}

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        Log.d(TAG, "LogFragment registered on EventBus");
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        Log.d(TAG, "LogFragment unregistered on EventBus");
        super.onPause();
    }

	@Subscribe(sticky = true)
	public void onEvent(ReloadMainLogEvent event) {
        Log.d(TAG, "Event: ReloadMainLogEvent");
        ReloadMainLogEvent stickyEvent =
                EventBus.getDefault().removeStickyEvent(ReloadMainLogEvent.class);
        if (stickyEvent != null) {
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
	}

	@Subscribe
	public void onEvent(CopyDatabaseEvent event) {
		try {
			if (mainLogSource.copyDatabase(event.fromDbPath, event.toDbPath)) {
                EventBus.getDefault().post(new ReloadMainLogEvent());
                EventBus.getDefault().post(new ReloadReminderLogEvent());
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
		AsyncTaskLoader<Cursor> loader = null;
		if (id == LOADER_ID) {
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
		if (loader.getId() == LOADER_ID) {
			mainAdapter.changeCursor(data);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {}
}
