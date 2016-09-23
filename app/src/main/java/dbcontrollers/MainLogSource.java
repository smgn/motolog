package dbcontrollers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;

import beans.MaintenanceItem;

public class MainLogSource {

	private final String TAG = "MainLogSource";
	private SQLiteDatabase database;
	private MotoLogHelper motoLogHelper;

	String[] allColumns = {
            MotoLogHelper.KEY,
            MotoLogHelper.FIELD1,
			MotoLogHelper.FIELD2,
            MotoLogHelper.FIELD3,
            MotoLogHelper.FIELD4,
			MotoLogHelper.FIELD5,
            MotoLogHelper.FIELD6,
            MotoLogHelper.FIELD7,
			MotoLogHelper.FIELD8,
            MotoLogHelper.FIELD9,
            MotoLogHelper.FIELD10
    };

	public MainLogSource(Context context) {
		motoLogHelper = new MotoLogHelper(context);
		if (database == null || !database.isOpen()) {
			database = motoLogHelper.getWritableDatabase();
		}
	}

	public void addMaintenanceItem(MaintenanceItem item) {

		ContentValues values = new ContentValues();
		values.put(MotoLogHelper.FIELD1, item.getVehicle());
		values.put(MotoLogHelper.FIELD2, item.getMaintElem());
		values.put(MotoLogHelper.FIELD3, item.getMaintType());
		values.put(MotoLogHelper.FIELD4, item.getFuelAmount());
		values.put(MotoLogHelper.FIELD5, item.getConsumption());

        // TODO: make it more readable?
		String formatDate =  item.getDate();
		if(formatDate.length()==9) {
			formatDate=formatDate.substring(0, 5) + "0" + formatDate.substring(5);
		} 
		formatDate = formatDate.replace("/", "-");

		values.put(MotoLogHelper.FIELD6, formatDate);
		values.put(MotoLogHelper.FIELD7, item.getOdometer());
		values.put(MotoLogHelper.FIELD8, item.getDetails());
		values.put(MotoLogHelper.FIELD9, item.getMileageType());
		values.put(MotoLogHelper.FIELD10, item.getCash());

		Log.d(TAG, "MotoLogHelper.FIELD9=" + item.getMileageType());
		database.insert(MotoLogHelper.DATABASE_TABLE, null, values);
	}

	public Cursor getCursor() {
		return database.rawQuery("select * from mainlog order by date desc, _id desc;", null);
//		return database.query(
//                MotoLogHelper.DATABASE_TABLE, allColumns, null, null, null, null, null);
	}
	
	public Cursor getConfCursor(String dateFrom, String dateTo) {
		return database.rawQuery(
                "select * " +
                        "from mainlog " +
                        "where date(date) between date(?) and date(?) order by date desc;",
                new String[] {dateFrom.replace("/", "-"),dateTo.replace("/", "-")});
	}

	public Cursor getCursor(String maintElem) {

		String whereClause = MotoLogHelper.FIELD2 + "= ?";

		String[] whereArgs = { maintElem };
		String orderBy = MotoLogHelper.FIELD7;

		Cursor cursor = database.query(
                MotoLogHelper.DATABASE_TABLE, allColumns,
                whereClause, whereArgs, null, null, orderBy);

		if (cursor.moveToFirst()) {
			return cursor;
		} else {
			return null;
		}
	}

	public int getItemsCount() {
		return (int) DatabaseUtils.queryNumEntries(database, MotoLogHelper.DATABASE_TABLE);
	}

	public void updateEntry(MaintenanceItem item) {

		String whereClause = MotoLogHelper.KEY + "= ?";
		String[] whereArgs = { Integer.toString(item.getKey()) };
		ContentValues valuesToPut = new ContentValues();
		valuesToPut.put(MotoLogHelper.FIELD2, item.getMaintElem());
		valuesToPut.put(MotoLogHelper.FIELD3, item.getMaintType());
		valuesToPut.put(MotoLogHelper.FIELD4, item.getFuelAmount());

        // TODO: make it more readable?
		String formatDate = item.getDate();
		if (formatDate.length() == 9) {
			formatDate = formatDate.substring(0, 5) + "0" + formatDate.substring(5);
		} 
		formatDate = formatDate.replace("/", "-");

		valuesToPut.put(MotoLogHelper.FIELD6, formatDate);
		valuesToPut.put(MotoLogHelper.FIELD7, item.getOdometer());
		valuesToPut.put(MotoLogHelper.FIELD8, item.getDetails());
		valuesToPut.put(MotoLogHelper.FIELD5, item.getConsumption());
		valuesToPut.put(MotoLogHelper.FIELD9, item.getMileageType());
		valuesToPut.put(MotoLogHelper.FIELD10, item.getCash());

		Log.d(TAG, "MotoLogHelper.FIELD9 =" + item.getMileageType());
		database.update(MotoLogHelper.DATABASE_TABLE, valuesToPut, whereClause,
				whereArgs);

	}

	public int deleteEntry(MaintenanceItem item) {
        int result = database.delete(MotoLogHelper.DATABASE_TABLE,
                MotoLogHelper.KEY + "= " + item.getKey(), null);
		if (result == 0) {
            Log.w(TAG, "Unable to delete entry, does not exist?");
        } else {
            Log.d(TAG, "Entry deleted: " + item.toString());
        }

        return result;
	}

	public int getLastItem() {
		Cursor cursor = database.query(MotoLogHelper.DATABASE_TABLE, allColumns,
				null, null, null, null, null);
		cursor.moveToLast();

        int retVal;
		retVal = cursor.getInt(cursor.getColumnIndex(MotoLogHelper.KEY));
        cursor.close();

        return retVal;
	}

	public Cursor getLastItem(String vehicle, String maintItem) {

		String whereClause = MotoLogHelper.FIELD1 + "= ? and " + MotoLogHelper.FIELD2
				+ " =?";

		String[] whereArgs = { vehicle, maintItem };
		String orderBy = MotoLogHelper.FIELD7 + " desc";

		Cursor cursor = database.query(MotoLogHelper.DATABASE_TABLE, allColumns,
				whereClause, whereArgs, null, null, orderBy);

		boolean isNotEmpty = cursor.moveToFirst();

		if (isNotEmpty) {
			return (cursor);
		} else {
			return null;
		}
	}

	public Cursor getItemAtKey(String key, String maintItem) {

		String whereClause = MotoLogHelper.KEY + "<= ? and " + MotoLogHelper.FIELD2 + " =?";
		String[] whereArgs = { key, maintItem };
		String orderBy = MotoLogHelper.FIELD7 + " desc";

		Cursor cursor = database.query(MotoLogHelper.DATABASE_TABLE, allColumns,
				whereClause, whereArgs, null, null, orderBy);

		if (cursor.moveToFirst()) {
			return cursor;
		} else {
			return null;
		}
	}

	public int getLastOdometer(String vehicle) {

		String whereClause = MotoLogHelper.FIELD1 + "= ?";
		String[] whereArgs = { vehicle };
		String orderBy = MotoLogHelper.FIELD7 + " desc";

		Cursor cursor = database.query(MotoLogHelper.DATABASE_TABLE, allColumns,
				whereClause, whereArgs, null, null, orderBy);

		if (cursor.moveToFirst()) {
            int retVal = cursor.getInt(cursor.getColumnIndex(MotoLogHelper.FIELD7));
			cursor.close();
            return retVal;
		} else {
			return 0;
		}
	}

	public boolean copyDatabase(String fromDbPath, String toDbPath) throws IOException {
		return motoLogHelper.copyDatabase(fromDbPath, toDbPath);
	}
}
