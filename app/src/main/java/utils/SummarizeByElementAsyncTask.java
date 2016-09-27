package utils;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dbcontrollers.MotoLogHelper;
import events.SummarizeByElementEvent;

// TODO: convert this to some other better background task mechanism?
public class SummarizeByElementAsyncTask extends AsyncTask<Object, String, Bundle> {

	@Override
	protected Bundle doInBackground(Object... params) {

		Bundle b = new Bundle();
		Cursor cursor = (Cursor) params[0]; // cursor has data between from and to date
		String selectedElement = (String) params[1];
		double cashperelem = 0;
		int elementCount = 0;

		ArrayList<String> mType = new ArrayList<>();
		mType.add("...");

		cursor.moveToFirst();

		// count all rows matching selectedElement and
		// total up cash spent for selectedElement in cursor data
		while (!cursor.isAfterLast()) {
			if (!mType.contains(cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD2)))) {
				mType.add(cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD2)));
			}

			if (cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD2))
					.equals(selectedElement)) {
				cashperelem = cashperelem + cursor.getDouble(10);
				elementCount++;
			}
			cursor.moveToNext();
		}

		b.putStringArrayList("elements", mType);
		b.putDouble("cashperelement", Math.round((cashperelem * 100.0) / 100.0));
		b.putInt("countperelement", elementCount);

		return b;
	}

	@Override
	protected void onPostExecute(Bundle bundle) {

		List<String> sortedElementList = bundle.getStringArrayList("elements");
		if (sortedElementList != null) {
			Collections.sort(sortedElementList);
		}

		EventBus.getDefault().post(new SummarizeByElementEvent(
				bundle.getDouble("cashperelement"),
				bundle.getInt("countperelement")));
	}
}
