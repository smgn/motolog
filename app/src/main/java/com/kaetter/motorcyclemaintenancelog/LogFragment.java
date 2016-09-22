package com.kaetter.motorcyclemaintenancelog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import adapter.MainLogCursorAdapter;
import beans.MaintenanceItem;
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
    SharedPreferences sharedPref;

    private int mileageType;

	public static LogFragment newInstance() {
		return new LogFragment();
	}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        mileageType = Integer.parseInt(sharedPref.getString("pref_MileageType", "0"));
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

                Log.d(TAG, "Item clicked in mainLogListView at position " + position);

                Cursor cursorAt = (Cursor) mainLogListView.getItemAtPosition(position);
                int key = cursorAt.getInt(cursorAt.getColumnIndex(MotoLogHelper.KEY));

                String vehicle = cursorAt.getString(cursorAt.getColumnIndex(MotoLogHelper.FIELD1));
                String maintElem =
                        cursorAt.getString(cursorAt.getColumnIndex(MotoLogHelper.FIELD2));
                String maintType =
                        cursorAt.getString(cursorAt.getColumnIndex(MotoLogHelper.FIELD3));
                double fuelAmount =
                        cursorAt.getFloat(cursorAt.getColumnIndex(MotoLogHelper.FIELD4));
                double consumption =
                        cursorAt.getFloat(cursorAt.getColumnIndex(MotoLogHelper.FIELD5));
                String date = cursorAt.getString(cursorAt.getColumnIndex(MotoLogHelper.FIELD6));
                int odometer = cursorAt.getInt(cursorAt.getColumnIndex(MotoLogHelper.FIELD7));
                String details = cursorAt.getString(cursorAt.getColumnIndex(MotoLogHelper.FIELD8));
                double cash = cursorAt.getFloat(cursorAt.getColumnIndex(MotoLogHelper.FIELD10));

                final MaintenanceItem item = new MaintenanceItem(key, vehicle, maintElem, maintType,
                        fuelAmount, consumption, date, odometer, details, mileageType, cash);

                MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                        .title(getString(R.string.dialog_main_log_title,
                                item.getMaintElem(), item.getMaintType()))
                        .customView(R.layout.dialogmainupdate, true)
                        .negativeText(R.string.dialog_button_cancel)
                        .positiveText(R.string.dialog_button_update)
                        .neutralText(R.string.dialog_button_delete)
                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog,
                                                @NonNull DialogAction which) {
                                // delete this log entry
                                new MaterialDialog.Builder(getContext())
                                        .title(R.string.dialog_title_warning)
                                        .content(R.string.dialog_text_delete_entry)
                                        .positiveText(R.string.dialog_button_yes)
                                        .negativeText(R.string.dialog_button_no)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog,
                                                                @NonNull DialogAction which) {
                                                if (mainLogSource.deleteEntry(item) != 0) {
                                                    EventBus.getDefault().postSticky(
                                                            new ReloadMainLogEvent());
                                                }
                                            }
                                        })
                                        .show();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog,
                                                @NonNull DialogAction which) {
                                // update this log entry
                                Intent intent = new Intent(getActivity(), NewLogActivity.class);
                                intent.putExtra("isModification", true);
	                            intent.putExtra("Maintenanceitem", item);
                                startActivityForResult(intent, MainActivity.REQUEST_UPDATE_LOG);
                            }
                        })
                        .build();

                View dialogView = dialog.getCustomView();

                if (dialogView != null) {
                    TextView textDate = ButterKnife.findById(dialogView, R.id.maintElemDate);
                    TextView textOdometer = ButterKnife.findById(dialogView, R.id.odometerInDialog);
                    TextView textDetails = ButterKnife.findById(dialogView, R.id.maintElemDetails);

                    textDate.setText(item.getDate());
                    textOdometer.setText(
                            getString(R.string.dialog_text_odometer,
                                    item.getOdometer(),
                                    mileageType == 0 ? getString(R.string.text_miles) :
                                            getString(R.string.text_km)));
                    textDetails.setText(item.getDetails());

                    dialog.show();
                }
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
