package dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kaetter.motorcyclemaintenancelog.MyListFragment;
import com.kaetter.motorcyclemaintenancelog.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import beans.ReminderItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dbcontrollers.RemLogSource;

public class NewRemDialog extends DialogFragment {

	@BindView(R.id.nextInterval) TextView nextIntervalView;
	@BindView(R.id.reminderdescription) TextView reminderDescription;
	@BindView(R.id.description) TextView descriptionView;
	@BindView(R.id.updateButton) Button updateEntry;
	@BindView(R.id.cancelButton) Button cancelEntry;

	private ReminderItem remItem;
	private String remDescription;
	private boolean isModification;
	private String[] intervalSizeArray;
	private String[] intervalSizeArrayP;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isModification = getArguments().getBoolean("isModification");
		remItem = (ReminderItem) getArguments().getSerializable("reminderItem");
		remDescription = getArguments().getString("remDescription");
		intervalSizeArray = getArguments().getStringArray("intervalSizeArray");
		intervalSizeArrayP = getArguments().getStringArray("intervalSizeArrayP");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getDialog().getWindow().requestFeature(STYLE_NO_TITLE);
		View view = inflater.inflate(R.layout.dialognewrem, container, false);
		ButterKnife.bind(this, view);

		setNextInterval(remItem);

		nextIntervalView.setText(remItem.getNextInterval());

		if (remItem.getReminderType() == 0) {
			if (MyListFragment.mileageType == 0 || MyListFragment.mileageType == 2)
				nextIntervalView.append(getString(R.string.text_km));
			else
				nextIntervalView.append(getString(R.string.text_miles));
		}

		reminderDescription.setText(remDescription);

		descriptionView.setText(remItem.getDetails());

		if (isModification) {
			updateEntry.setText(getString(R.string.text_update));
		}

		return view;
	}

	@OnClick(R.id.updateButton)
	public void updateEntry() {
		RemLogSource rml = new RemLogSource(getActivity());
		if (isModification) {

			rml.updateEntry(remItem);

		} else {
			rml.addReminderItem(remItem);
		}

		dismiss();
		getActivity().finish();
	}

	@OnClick(R.id.cancelButton)
	public void cancelEntry() {
		Log.i("UpdateDialog", "dismiss dialogue");
		dismiss();
	}

	public ReminderItem setNextInterval(ReminderItem remItem) {

		if (remItem.getReminderType() == 0) { // mileage
			int nextInterval = Integer.parseInt(remItem.getLastInterval())
					+ Integer.parseInt(remItem.getInterval());

			remItem.setNextInterval(String.valueOf(nextInterval));
		} else { // date

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

			String lastIntervalDateString = String.valueOf(remItem.getLastInterval());

			Calendar c = Calendar.getInstance();

			try {
				c.setTime(sdf.parse(lastIntervalDateString));
			} catch (ParseException e) {
				e.printStackTrace();
			}

			if (remItem.getIntervalSize().equals(intervalSizeArray[0]) ||
					remItem.getIntervalSize().equals(intervalSizeArrayP[0])) {
				// weeks
				c.add(Calendar.WEEK_OF_YEAR, Integer.parseInt(remItem.getInterval()));
			}

			if (remItem.getIntervalSize().equals(intervalSizeArray[1]) ||
					remItem.getIntervalSize().equals(intervalSizeArrayP[1])) {
				// months
				c.add(Calendar.MONTH, Integer.parseInt(remItem.getInterval()));
			}

			if (remItem.getIntervalSize().equals(intervalSizeArray[2]) ||
					remItem.getIntervalSize().equals(intervalSizeArrayP[2])) {
				// years
				c.add(Calendar.YEAR, Integer.parseInt(remItem.getInterval()));
			}

			Date d = c.getTime();
			remItem.setNextInterval(sdf.format(d));
		}
		return remItem;
	}
}
