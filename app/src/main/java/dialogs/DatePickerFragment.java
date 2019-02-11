package dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import events.DatePickedEvent;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

	Calendar initialDate;

	public interface EditDateDialogListener {
	    void onFinishEditDialog(String b,String inputText);
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		if (getArguments() == null) {
			initialDate = Calendar.getInstance();
		} else if (getArguments().getInt("year", 0) == 0 &&
				getArguments().getInt("month", 0) == 0 &&
				getArguments().getInt("day", 0) == 0) {
			initialDate = Calendar.getInstance();
		} else {
			initialDate = Calendar.getInstance();
			initialDate.set(Calendar.YEAR, getArguments().getInt("year"));
			initialDate.set(Calendar.MONTH, getArguments().getInt("month"));
			initialDate.set(Calendar.DAY_OF_MONTH, getArguments().getInt("day"));
		}

		return new DatePickerDialog(
				getActivity(),
				this,
				initialDate.get(Calendar.YEAR),
				initialDate.get(Calendar.MONTH),
				initialDate.get(Calendar.DAY_OF_MONTH));
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {

		EventBus.getDefault().post(new DatePickedEvent(year, month, day));

//		String b = getArguments().getString("button");
//		EditDateDialogListener activity = (EditDateDialogListener) getParentFragment();
//		String days;
//		if (day<10) {
//			days = "0"+day;
//
//		} else {
//			days = ""+ day;
//		}
//
//
//		if (month<9) {
//		activity.onFinishEditDialog(b,new String(year+ "-0"+ (month +1) + "-"+ days));
//		} else {
//			activity.onFinishEditDialog(b,new String(year+ "-"+ (month +1) + "-"+ days));
//		}
	}
}