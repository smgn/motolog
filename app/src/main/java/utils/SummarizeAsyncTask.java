package utils;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import dbcontrollers.MotoLogHelper;
import events.SummarizeEvent;

// TODO: convert this to some other better background task mechanism?
public class SummarizeAsyncTask extends AsyncTask<Object, String, Bundle> {

	private int daysBetween;
	private SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd", Locale.US);
	private final String TAG = "SummarizeAsyncTask";

	@Override
	protected Bundle doInBackground(Object... params) {

		Bundle b = new Bundle();

		Cursor cursor = (Cursor) params[0]; // cursor has data between from and to date
		cursor.moveToFirst();

		String dateMin = (String) params[1];
		String dateMax = (String) params[2];
		double cash = 0;
		int firstOdo = 0;
		int lastOdo = cursor.getInt(cursor.getColumnIndex(MotoLogHelper.FIELD7));

		int numEntriesInDb = cursor.getCount();
		b.putInt("numEntries", numEntriesInDb);
		Log.d(TAG, "numEntriesInDb: " + numEntriesInDb);

		ArrayList<String> mType = new ArrayList<>();
		mType.add("...");

		// go through each row in database, get earliest and latest odometer readings
		// and total up the cash spent
		while (!cursor.isAfterLast()) {
			if (!mType.contains(cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD2)))) {
				mType.add(cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD2)));
			}

			cash = cash + cursor.getDouble(cursor.getColumnIndex(MotoLogHelper.FIELD10));

			if (cursor.getInt(cursor.getColumnIndex(MotoLogHelper.FIELD7)) > lastOdo) {
				lastOdo = cursor.getInt(cursor.getColumnIndex(MotoLogHelper.FIELD7));
			} else {
				if (cursor.getInt(cursor.getColumnIndex(MotoLogHelper.FIELD7)) > firstOdo) {
					if (firstOdo == 0) {
						firstOdo =
								cursor.getInt(cursor.getColumnIndex(MotoLogHelper.FIELD7));
					}
				} else {
					firstOdo = cursor.getInt(cursor.getColumnIndex(MotoLogHelper.FIELD7));
				}
			}
			cursor.moveToNext();
		}

		// get num of days between dateMin and dateMax
		try {
			daysBetween = daysBetween(sdf.parse(dateMin), sdf.parse(dateMax));
		} catch (ParseException e) {
			e.printStackTrace();
			Log.e(TAG, "Unable to calculate number of days between " +
					dateMin + " and " + dateMax);
		}

		b.putDouble("totalCost", Math.round((cash * 100.0) / 100.0));
		b.putInt("totalDistance", lastOdo - firstOdo);
		if (daysBetween == 0) {
			b.putDouble("costPerDay", Math.round((cash * 100.0) / 100.0));
		} else {
			b.putDouble("costPerDay", Math.round((cash / daysBetween) * 100.0) / 100.0);
		}
		b.putDouble("costPerDistance", Math.round((cash / (lastOdo - firstOdo)) * 100.0) / 100.0);

		b.putStringArrayList("elements", mType);

		return b;
	}

	@Override
	protected void onPostExecute(Bundle b) {

		List<String> sortedElementList = b.getStringArrayList("elements");
		if (sortedElementList != null) {
			Collections.sort(sortedElementList);
		}

		EventBus.getDefault().post(new SummarizeEvent(
				b.getDouble("totalCost"),
				b.getDouble("costPerDay"),
				b.getInt("totalDistance"),
				b.getDouble("costPerDistance"),
				b.getInt("numEntries"),
				sortedElementList));
	}

	private int daysBetween(Date startDate, Date endDate) {
		Calendar sDate = getDatePart(startDate);
		Calendar eDate = getDatePart(endDate);

		int daysBetween = 0;
		while (sDate.before(eDate)) {
			sDate.add(Calendar.DAY_OF_MONTH, 1);
			daysBetween++;
		}
		return daysBetween;
	}

	private Calendar getDatePart(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);            // set hour to midnight
		cal.set(Calendar.MINUTE, 0);                 // set minute in hour
		cal.set(Calendar.SECOND, 0);                 // set second in minute
		cal.set(Calendar.MILLISECOND, 0);            // set millisecond in second
		return cal;
	}
}
