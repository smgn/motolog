package adapter;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    Context mContext;
    Rowloader rowLoader;

    public MainLogCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mContext = context;
        mCursor = c;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.position = cursor.getPosition();
	    rowLoader = new Rowloader(holder, view, cursor);
	    rowLoader.execute(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rowmain, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    public static class ViewHolder {

        @BindView(R.id.imageView1) ImageView maintTypeImageView;
        @BindView(R.id.key) TextView keyView;
        @BindView(R.id.rowMaintElem) TextView maintElemView;
        @BindView(R.id.rowMaintType) public TextView maintTypeView;
        @BindView(R.id.rowFuelAmount) TextView fuelAmountView;
        @BindView(R.id.rowConsumption) TextView fuelConsumptionView;
        @BindView(R.id.rowConsumptionLabel) TextView rowConsumptionLabelView;
        @BindView(R.id.kmLabel) TextView kmLabelView;
        @BindView(R.id.rowDetails) TextView detailsView;
        @BindView(R.id.rowFuelLabel) TextView fuelLabelView;
        @BindView(R.id.cash) TextView cashView;
        @BindView(R.id.cashLabel) TextView cashLabel;
        @BindView(R.id.odoLabel) TextView odoLabel;
        @BindView(R.id.rowDate) TextView dateView;
        @BindView(R.id.rowOdometer) TextView odometerView;
        int position;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

class Rowloader extends AsyncTask<Object, Void, Bundle> {
    static final String MAINT_ELEM = "maintElem";
    int position;
    MainLogCursorAdapter.ViewHolder holder;
    View view;
	Context context;
    private Cursor cursor;

    public Rowloader(MainLogCursorAdapter.ViewHolder holder, View view, Cursor cursor) {
        this.holder = holder;
        this.view = view;
        this.position = holder.position;
        this.cursor = cursor;
    }

    @Override
    protected Bundle doInBackground(Object... params) {
        context = (Context) params[0];
        Bundle b = new Bundle();

        cursor.moveToPosition(holder.position);
        b.putString("key", cursor.getString(cursor.getColumnIndex(
                MotoLogHelper.KEY)));
        b.putString(MAINT_ELEM, cursor.getString(cursor.getColumnIndex(
                MotoLogHelper.FIELD2)));
        b.putString("maintType", cursor.getString(cursor.getColumnIndex(
                MotoLogHelper.FIELD3)));
        b.putString("fuelAmount", String.valueOf(cursor.getDouble(cursor.getColumnIndex(
                MotoLogHelper.FIELD4))));
        b.putString("consumption", String.valueOf(cursor.getString(cursor.getColumnIndex(
                MotoLogHelper.FIELD5))));
        b.putString("date", this.cursor.getString(cursor.getColumnIndex(
                MotoLogHelper.FIELD6)));
        b.putString("odometer", String.valueOf(cursor.getString(cursor.getColumnIndex(
                MotoLogHelper.FIELD7))));
        b.putString("details", cursor.getString(cursor.getColumnIndex(
                MotoLogHelper.FIELD8)));
        b.putInt("mileageType", cursor.getInt(cursor.getColumnIndex(
                MotoLogHelper.FIELD9)));
        b.putString("cash", String.valueOf(cursor.getDouble(cursor.getColumnIndex(
                MotoLogHelper.FIELD10))));

        switch (cursor.getInt(cursor.getColumnIndex(MotoLogHelper.FIELD9))) {
            case 0:
                b.putString("kmLabel", "l/100km");
                b.putString("fuelLabel", "L");
                break;
            case 1:
                b.putString("kmLabel", "MPG");
                b.putString("fuelLabel", "gl");
                break;
            case 2:
                b.putString("kmLabel", "l/km");
                b.putString("fuelLabel", "L");
                break;
            case 3:
                b.putString("kmLabel", "km/l");
                b.putString("fuelLabel", "L");
                break;
        }

        if (MyListFragment.mileageType == 1) {
            b.putString("odoLabel", "Mi");
        } else {
            b.putString("odoLabel", "km");
        }

        b.putInt("position", position);
        return b;
    }

    @Override
    protected void onPostExecute(Bundle b) {

        if (holder.position == position && holder == view.getTag()) {
            holder.keyView.setText(b.getString("key"));
            holder.maintElemView.setText(b.getString(MAINT_ELEM));
            holder.maintTypeView.setText(context.getString(R.string.wrap_with_parentheses,
                    b.getString("maintType")));
            holder.dateView.setText(Utils.formatDate(b.getString("date")));
            holder.odometerView.setText(b.getString("odometer"));
            holder.cashView.setText(b.getString("cash"));
            if (b.getString("cash").equals("0")) {
                holder.cashLabel.setVisibility(View.GONE);
            }
            holder.odoLabel.setText(b.getString("odoLabel"));
            holder.detailsView.setText(b.getString("details"));

            if (context.getResources().getIdentifier(b.getString(MAINT_ELEM).toLowerCase(),
                    "drawable", context.getPackageName()) != 0) {
                holder.maintTypeImageView.setImageResource(context.getResources()
                        .getIdentifier(b.getString(MAINT_ELEM).toLowerCase(), "drawable",
                                context.getPackageName()));
            } else {
                holder.maintTypeImageView.setImageResource(R.drawable.other);
            }

            if (b.getString(MAINT_ELEM).equals("Fuel")) {
                holder.detailsView.setVisibility(View.INVISIBLE);
                holder.fuelAmountView.setText(b.getString("fuelAmount"));
                holder.fuelConsumptionView.setText(b.getString("consumption"));
                holder.fuelLabelView.setText(b.getString("fuelLabel"));
                holder.kmLabelView.setText(b.getString("kmLabel"));
                holder.rowConsumptionLabelView.setVisibility(View.VISIBLE);
                holder.fuelLabelView.setVisibility(View.VISIBLE);
                holder.fuelConsumptionView.setVisibility(View.VISIBLE);
                holder.kmLabelView.setVisibility(View.VISIBLE);
            } else {
                holder.detailsView.setVisibility(View.VISIBLE);
                holder.rowConsumptionLabelView.setVisibility(View.INVISIBLE);
                holder.fuelLabelView.setVisibility(View.INVISIBLE);
                holder.fuelConsumptionView.setVisibility(View.INVISIBLE);
                holder.kmLabelView.setVisibility(View.INVISIBLE);
                holder.fuelAmountView.setText("");
                holder.fuelLabelView.setText("");
                holder.kmLabelView.setText("");
            }
        }
    }
}

