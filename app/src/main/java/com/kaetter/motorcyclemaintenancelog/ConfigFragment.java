package com.kaetter.motorcyclemaintenancelog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import events.DatePickedEvent;

public class ConfigFragment extends Fragment {

	@Bind(R.id.bikenametext) EditText bikeName;
	@Bind(R.id.dateofpurchaset) EditText bikeDate;
	@Bind(R.id.initialodometert) EditText bikeOdo;
	@Bind(R.id.otherdetails) EditText bikeOtherDetails;

	public static ConfigFragment newInstance() {
		Bundle args = new Bundle();
		ConfigFragment fragment = new ConfigFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_config, container, false);

		ButterKnife.bind(this, root);

		// get general bike data
		getGeneralBikeData();

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

	public void onEvent(DatePickedEvent event) {
		if (event.type == 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			Calendar cal = Calendar.getInstance();
			cal.set(event.year, event.month, event.day);

			bikeDate.setText(sdf.format(cal.getTime()));
		}
	}

	public void getGeneralBikeData() {

		SharedPreferences generalPref = getActivity().getSharedPreferences(
				getString(R.string.general_preference_file_key),
				Context.MODE_PRIVATE);

		final SharedPreferences.Editor generalEditor = generalPref.edit();

		// bike name
		bikeName.setText(generalPref.getString("bikenametext", ""));
		bikeName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					generalEditor.putString("bikenametext", bikeName
							.getText().toString().trim());
					generalEditor.commit();
				}
			}
		});

		// bike date
		bikeDate.setText(generalPref.getString("dateofpurchaset", ""));
		bikeDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new DatePickerFragment();
				newFragment.show(getChildFragmentManager(), "");
			}
		});

		// bike odometer
		bikeOdo.setText(generalPref.getString("initialodometert", ""));
		bikeOdo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					generalEditor.putString("initialodometert", bikeOdo
							.getText().toString().trim());
					generalEditor.commit();
				}
			}
		});

		bikeOtherDetails.setText(generalPref.getString("otherdetails", ""));
		bikeOtherDetails.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if (!hasFocus) {

					generalEditor.putString("otherdetails", bikeOtherDetails
							.getText().toString().trim());
					generalEditor.commit();
				}
			}
		});
	}

	public static class DatePickerFragment extends DialogFragment
			implements DatePickerDialog.OnDateSetListener {

		@NonNull
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			EventBus.getDefault().post(new DatePickedEvent(0, year, month, day));
		}
	}
}

//if (mTag.equals(TabsFragment.TAB_CONF)) {
//
//		confView = inflater
//		.inflate(R.layout.logconf, container, false);
//		confView.setOnTouchListener(gestureListener);
//		getGeneralBikeData();
//
//		Calendar cal =Calendar.getInstance();
//		from = (Button) confView.findViewById(R.id.from);
//		from.setOnClickListener(new OnClickListener() {
//@Override
//public void onClick(View v) {
//		DialogFragment newFragment = new DatePickerFragment();
//		Bundle b = new Bundle();
//		b.putString("button", "from");
//		newFragment.setArguments(b);
//		newFragment.show(getChildFragmentManager(), "datePicker");
//
//		}
//		});
//
//		String days;
//
//		if (cal.get(Calendar.DAY_OF_MONTH)<10) {
//		days = "0"+cal.get(Calendar.DAY_OF_MONTH);
//
//		} else {
//		days = ""+ cal.get(Calendar.DAY_OF_MONTH);
//		}
//
//
//		if(cal.get(Calendar.MONTH)<9) {
//		from.setText(cal.get(Calendar.YEAR)+"-0"+(cal.get(Calendar.MONTH)+1)+"-"+days);
//		} else {
//		from.setText(cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+days);
//		}
//
//
//
//		to = (Button) confView.findViewById(R.id.to);
//		to.setOnClickListener(new OnClickListener() {
//@Override
//public void onClick(View v) {
//		DialogFragment newFragment = new DatePickerFragment();
//		Bundle b = new Bundle();
//		b.putString("button", "to");
//		newFragment.setArguments(b);
//		newFragment.show(getChildFragmentManager(), "datePicker");
//
//		}
//		});
//
//
//		ArrayList<String> list = new ArrayList<String>();
//		list.add("...");
//
//		Spinner maintelemspinner = (Spinner) confView.findViewById(R.id.maintelemspinner) ;
//
//		ArrayAdapter<String> maintElemAdapter = new ArrayAdapter<String>(
//		getActivity().getApplicationContext(), R.layout.generalspinner,list);
//
//		maintelemspinner.setAdapter(maintElemAdapter);
//
//		maintelemspinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//
//@Override
//public void onItemSelected(AdapterView<?> parent, View view,
//		int pos , long id) {
//		if(pos!=0) {
//		sum = new Summarize();
//
//		sum.execute(2,currentCursor,from.getText().toString(), confView,MyListFragment.this);
//		} else {
//		TextView cashperelementvalue = (TextView)  confView.findViewById(R.id.cashperelementvalue);
//		cashperelementvalue.setText("0");
//		}
//		}
//@Override
//public void onNothingSelected(AdapterView<?> arg0) {
//		}
//		});
//
//
//
//		if (cal.get(Calendar.DAY_OF_MONTH)<10) {
//		days = "0"+cal.get(Calendar.DAY_OF_MONTH);
//
//		} else {
//		days = ""+ cal.get(Calendar.DAY_OF_MONTH);
//		}
//
//		if(cal.get(Calendar.MONTH)<9) {
//		to.setText(cal.get(Calendar.YEAR)+"-0"+(cal.get(Calendar.MONTH)+1)+"-"+days);
//		} else {
//		to.setText(cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+days);
//		}
//
//		Bundle b = new Bundle();
//		b.putString("from", "1800-01-01");
//		b.putString("to", "2200-01-01");
//
//
//		getLoaderManager().initLoader(2, b, this);
//
//		returnView = confView;
//
//		}
