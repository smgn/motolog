package com.kaetter.motorcyclemaintenancelog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public class ConfigFragment extends Fragment {

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



		return root;
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
