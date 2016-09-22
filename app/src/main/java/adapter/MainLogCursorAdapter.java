package adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaetter.motorcyclemaintenancelog.MyListFragment;
import com.kaetter.motorcyclemaintenancelog.R;

import butterknife.ButterKnife;
import dbcontrollers.MotoLogHelper;
import utils.Utils;

public class MainLogCursorAdapter extends CursorAdapter implements Filterable {
    private Context mContext;
	private final String TAG = "MainLogCursorAdapter";

    public MainLogCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mContext = context;
        mCursor = c;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
	    ImageView imageMaintType = ButterKnife.findById(view, R.id.imageView1);
	    TextView textKey = ButterKnife.findById(view, R.id.key);
	    TextView textMaintElem = ButterKnife.findById(view, R.id.rowMaintElem);
	    TextView textMaintType = ButterKnife.findById(view, R.id.rowMaintType);
	    TextView textFuelAmount = ButterKnife.findById(view, R.id.rowFuelAmount);
	    TextView textFuelLabel = ButterKnife.findById(view, R.id.rowFuelLabel);
	    TextView textConsumption = ButterKnife.findById(view, R.id.rowConsumption);
	    TextView textConsumptionLabel = ButterKnife.findById(view, R.id.rowConsumptionLabel);
	    TextView textUnitLabel = ButterKnife.findById(view, R.id.kmLabel);
	    TextView textDetails = ButterKnife.findById(view, R.id.rowDetails);
	    TextView textCash = ButterKnife.findById(view, R.id.cash);
	    TextView textCashLabel = ButterKnife.findById(view, R.id.cashLabel);
	    TextView textOdo = ButterKnife.findById(view, R.id.rowOdometer);
	    TextView textOdoLabel = ButterKnife.findById(view, R.id.odoLabel);
	    TextView textDate = ButterKnife.findById(view, R.id.rowDate);

	    textKey.setText(cursor.getString(cursor.getColumnIndex(MotoLogHelper.KEY)));
	    textMaintElem.setText(cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD2)));
	    textMaintType.setText(context.getString(R.string.wrap_with_parentheses, cursor.getString(cursor.getColumnIndex(
			    MotoLogHelper.FIELD3))));
	    textDate.setText(Utils.formatDate(cursor.getString(cursor.getColumnIndex(
			    MotoLogHelper.FIELD6))));
	    textOdo.setText(String.valueOf(cursor.getString(cursor.getColumnIndex(
			    MotoLogHelper.FIELD7))));
	    textCash.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex(
			    MotoLogHelper.FIELD10))));
	    if (String.valueOf(cursor.getDouble(cursor.getColumnIndex(
			    MotoLogHelper.FIELD10))).equals("0")) {
		    textCashLabel.setVisibility(View.GONE);
	    }
	    if (MyListFragment.mileageType == 1) {
		    textOdoLabel.setText("Mi");
	    } else {
		    textOdoLabel.setText("km");
	    }
	    textDetails.setText(cursor.getString(cursor.getColumnIndex(
			    MotoLogHelper.FIELD8)));

	    if (context.getResources().getIdentifier(cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD2)).toLowerCase(),
			    "drawable", context.getPackageName()) != 0) {
		    imageMaintType.setImageResource(context.getResources()
				    .getIdentifier(cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD2)).toLowerCase(), "drawable",
						    context.getPackageName()));
	    } else {
		    imageMaintType.setImageResource(R.drawable.other);
	    }

	    if (cursor.getString(cursor.getColumnIndex(MotoLogHelper.FIELD2)).equals("Fuel")) {
		    textDetails.setVisibility(View.INVISIBLE);
		    textFuelAmount.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndex(
				    MotoLogHelper.FIELD4))));
		    textConsumption.setText(String.valueOf(cursor.getString(cursor.getColumnIndex(
				    MotoLogHelper.FIELD5))));
		    switch (cursor.getInt(cursor.getColumnIndex(MotoLogHelper.FIELD9))) {
			    case 0:
				    textUnitLabel.setText("l/100km");
				    textFuelLabel.setText("L");
				    break;
			    case 1:
				    textUnitLabel.setText("MPG");
				    textFuelLabel.setText("gl");
				    break;
			    case 2:
				    textUnitLabel.setText("l/km");
				    textFuelLabel.setText("L");
				    break;
			    case 3:
				    textUnitLabel.setText("km/l");
				    textFuelLabel.setText("L");
				    break;
		    }
		    textConsumptionLabel.setVisibility(View.VISIBLE);
		    textFuelLabel.setVisibility(View.VISIBLE);
		    textConsumption.setVisibility(View.VISIBLE);
		    textUnitLabel.setVisibility(View.VISIBLE);
	    } else {
		    textDetails.setVisibility(View.VISIBLE);
		    textConsumptionLabel.setVisibility(View.INVISIBLE);
		    textFuelLabel.setVisibility(View.INVISIBLE);
		    textConsumption.setVisibility(View.INVISIBLE);
		    textUnitLabel.setVisibility(View.INVISIBLE);
		    textFuelAmount.setText("");
		    textFuelLabel.setText("");
		    textUnitLabel.setText("");
	    }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
	    return LayoutInflater.from(mContext).inflate(R.layout.rowmain, parent, false);
    }
}

