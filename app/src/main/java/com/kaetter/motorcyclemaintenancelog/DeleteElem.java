package com.kaetter.motorcyclemaintenancelog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class DeleteElem extends AppCompatActivity {
	StableArrayAdapter adapter = null;

	ListView listview = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deleteelem);

		listview = (ListView) findViewById(R.id.deleteelemlist);
		final Intent intent = getIntent();


		if (intent.getStringExtra("type").equals(NewLogActivity.ELEMVAL)) {

			buildListElem();


			listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, final View view,
				                        int position, long id) {
					final String item = (String) parent.getItemAtPosition(position);
					deleteSharedPreference(intent.getStringExtra("type"), parent.getItemAtPosition(position).toString());
				}

			});
		}
		if (intent.getStringExtra("type").equals(NewLogActivity.ELEMTYPEVAL)) {

			buildListElemType();


			listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, final View view,
				                        int position, long id) {
					final String item = (String) parent.getItemAtPosition(position);
					deleteSharedPreference(intent.getStringExtra("type"), parent.getItemAtPosition(position).toString());
				}

			});
		}

	}

	private void buildListElem() {
		SharedPreferences elemPref = getSharedPreferences(
				getString(R.string.elem_preference_file_key),
				Context.MODE_PRIVATE);

		Editor ed = elemPref.edit();
		String[] maintElemList = getResources().getStringArray(R.array.maintElemArray);
		int elemCount = elemPref.getInt(NewLogActivity.ELEMCOUNTSTRING, 0);

		String[] values = new String[elemCount - maintElemList.length];

		for (int i = maintElemList.length; i < elemCount; i++) {
			values[i - maintElemList.length] = elemPref.getString(NewLogActivity.ELEMVAL + i, "");
		}

		final ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < values.length; ++i) {
			list.add(values[i]);
		}
		adapter = new StableArrayAdapter(this,
				android.R.layout.simple_list_item_1, list);
		listview.setAdapter(adapter);

	}

	private void buildListElemType() {
		SharedPreferences elemTypePref = getSharedPreferences(
				getString(R.string.elemtype_preference_file_key),
				Context.MODE_PRIVATE);

		Editor ed = elemTypePref.edit();
		String[] maintElemList = getResources().getStringArray(R.array.maintTypeArray);
		int elemCount = elemTypePref.getInt(NewLogActivity.ELEMTYPECOUNTSTRING, 0);

		String[] values = new String[elemCount - maintElemList.length];

		for (int i = maintElemList.length; i < elemCount; i++) {
			values[i - maintElemList.length] = elemTypePref.getString(NewLogActivity.ELEMTYPEVAL + i, "");
		}

		final ArrayList<String> list = new ArrayList<String>();
		for (String value : values) {
			list.add(value);
		}
		adapter = new StableArrayAdapter(this,
				android.R.layout.simple_list_item_1, list);
		listview.setAdapter(adapter);

	}


	private class StableArrayAdapter extends ArrayAdapter<String> {

		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public StableArrayAdapter(Context context, int textViewResourceId,
		                          List<String> objects) {
			super(context, textViewResourceId, objects);
			for (int i = 0; i < objects.size(); ++i) {
				mIdMap.put(objects.get(i), i);
			}
		}

		@Override
		public long getItemId(int position) {
			String item = getItem(position);
			return mIdMap.get(item);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}


	public void deleteSharedPreference(String type, String value) {
		if (type.equals(NewLogActivity.ELEMVAL)) {

			SharedPreferences elemPref = getSharedPreferences(
					getString(R.string.elem_preference_file_key),
					Context.MODE_PRIVATE);
			Editor ed = elemPref.edit();
			int elemCount = elemPref.getInt(NewLogActivity.ELEMCOUNTSTRING, 0);
			if (elemCount == 0) {
				Toast.makeText(getApplicationContext(),
						"something wrong with shared pref", Toast.LENGTH_LONG)
						.show();
			} else {
				String[] values = new String[elemCount - 1];
				int j = 0;
				for (int i = 0; i < elemCount; i++) {
					if (i < 6 || (i >= 6 && !elemPref.getString(NewLogActivity.ELEMVAL + i, "").equals(value))) {
						values[j] = elemPref.getString(NewLogActivity.ELEMVAL + i, "");
						j++;
					}
				}
				ed.clear();
				ed.commit();
				elemCount--;

				for (int i = 0; i < elemCount; i++) {
					ed.putString(NewLogActivity.ELEMVAL + i, values[i]);
				}
				ed.putInt(NewLogActivity.ELEMCOUNTSTRING, elemCount);
				ed.commit();
				buildListElem();
				adapter.notifyDataSetChanged();
				setResult(1);
			}
		}

		if (type.equals(NewLogActivity.ELEMTYPEVAL)) {

			SharedPreferences elemTypePref = getSharedPreferences(
					getString(R.string.elemtype_preference_file_key),
					Context.MODE_PRIVATE);
			Editor ed = elemTypePref.edit();
			int elemCount = elemTypePref.getInt(NewLogActivity.ELEMTYPECOUNTSTRING, 0);
			if (elemCount == 0) {
				Toast.makeText(getApplicationContext(),
						"something wrong with shared pref", Toast.LENGTH_LONG)
						.show();
			} else {
				String[] values = new String[elemCount - 1];
				int j = 0;
				for (int i = 0; i < elemCount; i++) {
					if (i < 5 || (i >= 5 && !elemTypePref.getString(NewLogActivity.ELEMTYPEVAL + i, "").equals(value))) {
						values[j] = elemTypePref.getString(NewLogActivity.ELEMTYPEVAL + i, "");
						j++;
					}
				}
				ed.clear();
				ed.commit();
				elemCount--;

				for (int i = 0; i < elemCount; i++) {
					ed.putString(NewLogActivity.ELEMTYPEVAL + i, values[i]);
				}
				ed.putInt(NewLogActivity.ELEMTYPECOUNTSTRING, elemCount);
				ed.commit();
				buildListElemType();
				adapter.notifyDataSetChanged();
				setResult(1);
			}


		}

	}

}
