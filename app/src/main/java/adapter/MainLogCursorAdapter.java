package adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaetter.motorcyclemaintenancelog.MyListFragment;
import com.kaetter.motorcyclemaintenancelog.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import dbcontrollers.MotoLogHelper;
import utils.Utils;

public class MainLogCursorAdapter extends CursorAdapter implements Filterable {
	private final String TAG = "MainLogCursorAdapter";

    public MainLogCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
	    ViewHolder viewHolder = (ViewHolder) view.getTag();

	    viewHolder.textKey.setText(cursor.getString(cursor.getColumnIndex(MotoLogHelper.KEY)));
	    viewHolder.textMaintElem.setText(cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD2)));
	    viewHolder.textMaintType.setText(context.getString(R.string.wrap_with_parentheses,
			    cursor.getString(cursor.getColumnIndex(
			    MotoLogHelper.FIELD3))));
	    viewHolder.textDate.setText(Utils.formatDate(cursor.getString(cursor.getColumnIndex(
			    MotoLogHelper.FIELD6))));
	    viewHolder.textOdo.setText(String.valueOf(cursor.getString(cursor.getColumnIndex(
			    MotoLogHelper.FIELD7))));

	    Log.d(TAG, " " + cursor.getDouble(cursor.getColumnIndex(
			    MotoLogHelper.FIELD10)));

	    viewHolder.textCash.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex(
			    MotoLogHelper.FIELD10))));
	    if (String.valueOf(cursor.getDouble(cursor.getColumnIndex(
			    MotoLogHelper.FIELD10))).equals("0")) {
		    viewHolder.textCashLabel.setVisibility(View.GONE);
	    }
	    if (MyListFragment.mileageType == 1) {
		    viewHolder.textOdoLabel.setText("Mi");
	    } else {
		    viewHolder.textOdoLabel.setText("km");
	    }
	    viewHolder.textDetails.setText(cursor.getString(cursor.getColumnIndex(
			    MotoLogHelper.FIELD8)));

	    if (context.getResources().getIdentifier(
			    cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD2)).toLowerCase(),
			    "drawable", context.getPackageName()) != 0) {
		    viewHolder.imageMaintType.setImageResource(context.getResources()
				    .getIdentifier(cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD2))
						    .toLowerCase(), "drawable", context.getPackageName()));
	    } else {
		    viewHolder.imageMaintType.setImageResource(R.drawable.other);
	    }

	    if (cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD2)).equals("Fuel")) {
		    viewHolder.textDetails.setVisibility(View.INVISIBLE);
		    viewHolder.textFuelAmount.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex(
				    MotoLogHelper.FIELD4))));
		    viewHolder.textConsumption.setText(String.valueOf(cursor.getString(cursor.getColumnIndex(
				    MotoLogHelper.FIELD5))));
		    switch (cursor.getInt(cursor.getColumnIndex(MotoLogHelper.FIELD9))) {
			    case 0:
				    viewHolder.textUnitLabel.setText("l/100km");
				    viewHolder.textFuelLabel.setText("L");
				    break;
			    case 1:
				    viewHolder.textUnitLabel.setText("MPG");
				    viewHolder.textFuelLabel.setText("gl");
				    break;
			    case 2:
				    viewHolder.textUnitLabel.setText("l/km");
				    viewHolder.textFuelLabel.setText("L");
				    break;
			    case 3:
				    viewHolder.textUnitLabel.setText("km/l");
				    viewHolder.textFuelLabel.setText("L");
				    break;
		    }
		    viewHolder.textConsumptionLabel.setVisibility(View.VISIBLE);
		    viewHolder.textFuelLabel.setVisibility(View.VISIBLE);
		    viewHolder.textConsumption.setVisibility(View.VISIBLE);
		    viewHolder.textUnitLabel.setVisibility(View.VISIBLE);
		    viewHolder.separator.setVisibility(View.VISIBLE);
	    } else {
		    viewHolder.textDetails.setVisibility(View.VISIBLE);
		    viewHolder.textConsumptionLabel.setVisibility(View.INVISIBLE);
		    viewHolder.textFuelLabel.setVisibility(View.INVISIBLE);
		    viewHolder.textConsumption.setVisibility(View.INVISIBLE);
		    viewHolder.textUnitLabel.setVisibility(View.INVISIBLE);
		    viewHolder.textFuelAmount.setText("");
		    viewHolder.textFuelLabel.setText("");
		    viewHolder.textUnitLabel.setText("");
		    if (TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD8)))) {
			    viewHolder.separator.setVisibility(View.INVISIBLE);
		    } else {
			    viewHolder.separator.setVisibility(View.VISIBLE);
		    }
	    }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
	    View view = LayoutInflater.from(context).inflate(R.layout.rowmain, parent, false);
	    ViewHolder viewHolder = new ViewHolder(view);
	    view.setTag(viewHolder);
	    return view;
    }

	public static class ViewHolder {
		@BindView(R.id.imageView1) ImageView imageMaintType;
		@BindView(R.id.key) TextView textKey;
		@BindView(R.id.rowMaintElem) TextView textMaintElem;
		@BindView(R.id.rowMaintType) TextView textMaintType;
		@BindView(R.id.rowFuelAmount) TextView textFuelAmount;
		@BindView(R.id.rowFuelLabel) TextView textFuelLabel;
		@BindView(R.id.rowConsumption) TextView textConsumption;
		@BindView(R.id.rowConsumptionLabel) TextView textConsumptionLabel;
		@BindView(R.id.kmLabel) TextView textUnitLabel;
		@BindView(R.id.rowDetails) TextView textDetails;
		@BindView(R.id.cash) TextView textCash;
		@BindView(R.id.cashLabel) TextView textCashLabel;
		@BindView(R.id.rowOdometer) TextView textOdo;
		@BindView(R.id.odoLabel) TextView textOdoLabel;
		@BindView(R.id.rowDate) TextView textDate;
		@BindView(R.id.separator) View separator;

		ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}
}

