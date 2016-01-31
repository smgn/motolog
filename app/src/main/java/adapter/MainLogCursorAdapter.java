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

import dbcontrollers.MainHelper;

public class MainLogCursorAdapter extends CursorAdapter implements Filterable {
    Context mContext;
    Rowloader rowLoader;

    public MainLogCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mContext = context;
        mCursor = c;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursora) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.position = cursora.getPosition();
	    rowLoader = new Rowloader(holder, view, cursora);
	    rowLoader.execute(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rowmain, parent, false);
        ViewHolder holder = new ViewHolder();
        holder.keyView = (TextView) view.findViewById(R.id.key);
        holder.maintElemView = (TextView) view.findViewById(R.id.rowMaintElem);
        holder.maintTypeView = (TextView) view.findViewById(R.id.rowMaintType);
        holder.fuelAmountView = (TextView) view.findViewById(R.id.rowFuelAmount);
        holder.fuelConsumptionView = (TextView) view.findViewById(R.id.rowConsumption);
        holder.rowConsumptionLabelView = (TextView) view.findViewById(R.id.rowConsumptionLabel);
        holder.kmLabelView = (TextView) view.findViewById(R.id.kmLabel);
        holder.detailsView = (TextView) view.findViewById(R.id.rowDetails);
        holder.fuelLabelView = (TextView) view.findViewById(R.id.rowFuelLabel);
        holder.cashView = (TextView) view.findViewById(R.id.cash);
        holder.odoLabel = (TextView) view.findViewById(R.id.odoLabel);
        holder.dateView = (TextView) view.findViewById(R.id.rowDate);
        holder.odometerView = (TextView) view.findViewById(R.id.rowOdometer);
        holder.cashLabel = (TextView) view.findViewById(R.id.cashLabel);

        holder.maintTypeImageView = (ImageView) view
                .findViewById(R.id.imageView1);
        view.setTag(holder);
        return view;
    }

    public class ViewHolder {
        public ImageView maintTypeImageView;
        public TextView keyView;
        public TextView maintElemView;
        public TextView maintTypeView;
        public TextView fuelAmountView;
        public TextView fuelConsumptionView;
        public TextView rowConsumptionLabelView;
        public TextView kmLabelView;
        public TextView detailsView;
        public TextView fuelLabelView;
        public TextView cashView;
        public TextView cashLabel;
        public TextView odoLabel;
        public TextView dateView;
        public TextView odometerView;
        public int position;
    }
}

class Rowloader extends AsyncTask<Object, Void, Bundle> {
    static final String MAINT_ELEM = "maintElem";
    Integer position;
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

        this.cursor.moveToPosition(holder.position);
        b.putString("key", cursor.getString(this.cursor.getColumnIndex(MainHelper.KEY)));
        b.putString(MAINT_ELEM, this.cursor.getString(this.cursor
                .getColumnIndex(MainHelper.FIELD2)));
        b.putString("maintType", this.cursor.getString(this.cursor
                .getColumnIndex(MainHelper.FIELD3)));
        b.putString("fuelAmount", String.valueOf(this.cursor.getDouble(this.cursor
                .getColumnIndex(MainHelper.FIELD4))));
        b.putString("consumption", String.valueOf(this.cursor.getString(this.cursor
                .getColumnIndex(MainHelper.FIELD5))));
        b.putString("date", this.cursor
                .getString(this.cursor.getColumnIndex(MainHelper.FIELD6)));
        b.putString("odometer", String.valueOf(this.cursor.getString(this.cursor
                .getColumnIndex(MainHelper.FIELD7))));
        b.putString("details", this.cursor.getString(this.cursor
                .getColumnIndex(MainHelper.FIELD8)));
        b.putInt("mileageType", this.cursor.getInt(this.cursor
                .getColumnIndex(MainHelper.FIELD9)));
        b.putString("cash", String.valueOf(this.cursor.getDouble(this.cursor
                .getColumnIndex(MainHelper.FIELD10))));

        switch (this.cursor.getInt(this.cursor
                .getColumnIndex(MainHelper.FIELD9))) {

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
            holder.maintTypeView.setText("(" + b.getString("maintType") + ")");
            holder.dateView.setText(b.getString("date"));
            holder.odometerView.setText(b.getString("odometer"));
            holder.cashView.setText(b.getString("cash"));
            if (b.getString("cash").equals("0"))
                holder.cashLabel.setVisibility(View.GONE);
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
                holder.detailsView.setVisibility(View.GONE);
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

