package dbcontrollers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import beans.ReminderItem;

public class RemLogSource {

	private final String TAG = "RemLogSource";
	private SQLiteDatabase database;

    String[] allColumns = {
            MotoLogHelper.KEY,
            MotoLogHelper.FIELD1R,
			MotoLogHelper.FIELD2R,
            MotoLogHelper.FIELD3R,
            MotoLogHelper.FIELD4R,
			MotoLogHelper.FIELD5R,
            MotoLogHelper.FIELD5Ra,
            MotoLogHelper.FIELD6R,
            MotoLogHelper.FIELD7R,
			MotoLogHelper.FIELD8R,
            MotoLogHelper.FIELD9R
    };

	public RemLogSource(Context context) {
        MotoLogHelper motoLogHelper = new MotoLogHelper(context);
        if (database == null || !database.isOpen()) {
            database = motoLogHelper.getWritableDatabase();
        }
	}

	public void addReminderItem(ReminderItem item) {

		ContentValues values = new ContentValues();
		values.put(MotoLogHelper.FIELD1R, item.getVehicle());
		values.put(MotoLogHelper.FIELD2R, item.getMaintElem());
		values.put(MotoLogHelper.FIELD3R, item.getMaintType());
		values.put(MotoLogHelper.FIELD4R, item.getReminderType());
		values.put(MotoLogHelper.FIELD5R, item.getInterval());
		values.put(MotoLogHelper.FIELD5Ra, item.getIntervalSize());
		values.put(MotoLogHelper.FIELD6R, item.getLastInterval());
		values.put(MotoLogHelper.FIELD7R, item.getNextInterval());
		values.put(MotoLogHelper.FIELD8R, item.getDetails());
		values.put(MotoLogHelper.FIELD9R, item.getDateInserted());

		database.insert(MotoLogHelper.DATABASE_TABLER, null, values);
	}

	public Cursor getCursor() {
		return database.query(
                MotoLogHelper.DATABASE_TABLER, allColumns, null, null, null, null, null);
	}

	public int getItemsCount() {
		return (int) DatabaseUtils.queryNumEntries(database, MotoLogHelper.DATABASE_TABLER);
	}

	public void updateEntry(ReminderItem item) {
		System.out.println(item.getKey());
		String whereClause = MotoLogHelper.KEY + "= ?";
		String[] whereArgs = { Integer.toString(item.getKey()) };
		ContentValues valuesToPut = new ContentValues();
		valuesToPut.put(MotoLogHelper.FIELD2R, item.getMaintElem());
		valuesToPut.put(MotoLogHelper.FIELD3R, item.getMaintType());
		valuesToPut.put(MotoLogHelper.FIELD4R, item.getReminderType());
		valuesToPut.put(MotoLogHelper.FIELD5R, item.getInterval());
		valuesToPut.put(MotoLogHelper.FIELD5Ra, item.getIntervalSize());
		valuesToPut.put(MotoLogHelper.FIELD6R, item.getLastInterval());
		valuesToPut.put(MotoLogHelper.FIELD7R, item.getNextInterval());
		valuesToPut.put(MotoLogHelper.FIELD8R, item.getDetails());
		valuesToPut.put(MotoLogHelper.FIELD9R, item.getDateInserted());

		database.update(MotoLogHelper.DATABASE_TABLER, valuesToPut, whereClause,
				whereArgs);
	}

	public void deleteEntry(ReminderItem item) {
		database.delete(
                MotoLogHelper.DATABASE_TABLER, MotoLogHelper.KEY + "= " + item.getKey(), null);
	}

	public int getLastItem() {
		Cursor cursor = database.query(MotoLogHelper.DATABASE_TABLER,
                allColumns, null, null, null, null, null);
		cursor.moveToLast();

        int retVal = cursor.getInt(cursor.getColumnIndex(MotoLogHelper.KEY));
		cursor.close();

        return retVal;
	}
}
