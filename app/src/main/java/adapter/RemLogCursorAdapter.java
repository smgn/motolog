package adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff.Mode;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaetter.motorcyclemaintenancelog.MyListFragment;
import com.kaetter.motorcyclemaintenancelog.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import dbcontrollers.MotoLogHelper;

public class RemLogCursorAdapter extends CursorAdapter implements Filterable {

	private int lastOdometer, remColor;
	private Date today, nextIntervalDate;
	private String distanceLabel;

	public RemLogCursorAdapter(Context context, Cursor c, int odometer, Date today) {
		super(context, c, 0);
		lastOdometer = odometer;
		this.today = today;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder holder = (ViewHolder) view.getTag();

		String key = cursor.getString(cursor.getColumnIndex(MotoLogHelper.KEY));
		String maintElem = cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD2R));
		String maintType = cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD3R));
		int reminderType = cursor.getInt(cursor.getColumnIndex(MotoLogHelper.FIELD4R));
		String interval = cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD5R));
		String intervalSize = cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD5Ra));
		String lastInterval = cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD6R));
		String nextInterval = cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD7R));
		String details = cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD8R));
		String date = cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD9R));

//		TextView keyView = (TextView) view.findViewById(R.id.remkey);
//		keyView.setText(key);

		if (context.getResources().getIdentifier(
				"rem" + maintType.toLowerCase(), "drawable",
				context.getPackageName()) != 0) {
			holder.imageLabel.setImageResource(context.getResources().getIdentifier(
					"rem" + maintType.toLowerCase(), "drawable",
					context.getPackageName()));
		} else {
			holder.imageLabel.setImageResource(R.drawable.remother);
		}

		if (context.getResources().getIdentifier(
				maintElem.toLowerCase() + "color", "color",
				context.getPackageName()) != 0) {

			remColor = ContextCompat.getColor(context,
					context.getResources().getIdentifier(
							maintElem.toLowerCase() + "color", "color",
							context.getPackageName()));
		} else {
			remColor = ContextCompat.getColor(context, R.color.othercolor);
		}

		holder.imageLabel.setColorFilter(remColor, Mode.MULTIPLY);
		
		if (MyListFragment.mileageType == 1) {
			distanceLabel = context.getString(R.string.text_miles);
		} else {
			distanceLabel = context.getString(R.string.text_km);
		}

		if (reminderType == 0) {
            holder.textReminder.setText(
                    context.getString(R.string.text_reminder_description,
                            maintType, maintElem, interval, distanceLabel));
			holder.textLastInterval.setText(
                    context.getString(R.string.text_reminder_last_interval,
                            lastInterval, distanceLabel));
			holder.textNextInterval.setText(
                    context.getString(R.string.text_reminder_next_interval,
                            nextInterval, distanceLabel));

//			if (Integer.parseInt(nextInterval) < lastOdometer) {
//
////				nextIntervalView
////						.setBackgroundColor(Color.parseColor("#cc2a36"));
//				toDo.setImageResource(R.drawable.wrench);
//				toDo.setVisibility(View.VISIBLE);
//				toDo.setColorFilter(Color.parseColor("#cc2a36"), Mode.MULTIPLY);
//
//			} else {
//
////				nextIntervalView
////						.setBackgroundColor(Color.parseColor("#333333"));
//				toDo.setVisibility(View.GONE);
//
//			}

		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			try {
				nextIntervalDate = sdf.parse(nextInterval);

			} catch (ParseException e) {

				nextIntervalDate = new Date();
				e.printStackTrace();
			}

//			if (nextIntervalDate.before(today)) {
//				toDo.setImageResource(R.drawable.wrench);
//				toDo.setVisibility(View.VISIBLE);
//				toDo.setColorFilter(Color.parseColor("#cc2a36"), Mode.MULTIPLY);
////				nextIntervalView
////						.setBackgroundColor(Color.parseColor("#cc2a36"));
//
//			} else {
////				nextIntervalView
////						.setBackgroundColor(Color.parseColor("#333333"));
//
//				toDo.setVisibility(View.GONE);
//
//			}
			holder.textReminder.setText(maintType + " " + maintElem + " every "
					+ interval + " " + intervalSize);
			holder.textLastInterval.setText(lastInterval);
			holder.textNextInterval.setText(nextInterval);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.rowrem, parent, false);
		ViewHolder holder = new ViewHolder(view);
		view.setTag(holder);
		return view;
	}

	public static class ViewHolder {

		@BindView(R.id.textReminder) TextView textReminder;
		@BindView(R.id.textLastInterval) TextView textLastInterval;
		@BindView(R.id.textNextInterval) TextView textNextInterval;
		@BindView(R.id.image) ImageView imageLabel;

		public ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}

}
