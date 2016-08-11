package com.kaetter.motorcyclemaintenancelog;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Logger;

import beans.MaintenanceItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dbcontrollers.MainHelper;
import dbcontrollers.MainLogSource;

public class RefuelActivity extends AppCompatActivity {

    @BindView(R.id.tilFuel) TextInputLayout tilFuel;
    @BindView(R.id.tilOdometer) TextInputLayout tilOdometer;
    @BindView(R.id.tilPrice) TextInputLayout tilPrice;
    @BindView(R.id.editTextFuel) EditText editTextFuel;
    @BindView(R.id.editTextOdometer) EditText editTextOdometer;
    @BindView(R.id.editTextPrice) EditText editTextPrice;

	private MainLogSource mainLogSource;
	private int mileageType;
    private int cashType;

    @Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setContentView(R.layout.refuelactivity);

        ButterKnife.bind(this);

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		mileageType = Integer.parseInt(sharedPrefs.getString("pref_MileageType", "0"));
        cashType = Integer.parseInt(sharedPrefs.getString("pref_CashType", "0"));

		if (mileageType == 1) {
            tilFuel.setHint(getString(R.string.activity_refuel_hint_fuel_amount_gallons));
            tilOdometer.setHint(
                    getString(R.string.activity_refuel_hint_odometer_reading_gallons));
            tilPrice.setHint(getString(R.string.activity_refuel_hint_price_gallon));
		} else {
            tilFuel.setHint(getString(R.string.activity_refuel_hint_fuel_amount_litres));
            tilOdometer.setHint(getString(R.string.activity_refuel_hint_odometer_reading_km));
            tilPrice.setHint(getString(R.string.activity_refuel_hint_price_litre));
		}

		mainLogSource = new MainLogSource(getApplication().getApplicationContext());

        editTextFuel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    tilFuel.setError(getString(R.string.error_required));
                } else {
                    tilFuel.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        editTextOdometer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    tilOdometer.setError(getString(R.string.error_required));
                } else {
                    tilOdometer.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
	}

	public double getConsumption(String vehicle, String maintElem, double fuel, int odometer) {
		Cursor cursor = mainLogSource.getLastItem(vehicle, maintElem);

        //TODO: cursor is always null right now, why?
		if (cursor != null) {
			double consumption;
            switch (mileageType) {
                case 0:
                    consumption = 100
                            * fuel
                            / (odometer - cursor.getDouble(cursor
                            .getColumnIndex(MainHelper.FIELD7)));
                    break;
                case 2:
                    consumption = fuel
                            / (odometer - cursor.getDouble(cursor
                            .getColumnIndex(MainHelper.FIELD7)));
                    break;
                default:
                    consumption = (odometer - cursor.getDouble(cursor
                            .getColumnIndex(MainHelper.FIELD7))) / fuel;
                    break;
            }

			consumption = Math.round(consumption * 100.0) / 100.0;

			if (consumption < 0) {
				return 0;
			} else {
				return consumption;
			}
		} else
			return 0;
	}

    @OnClick(R.id.buttonCreate)
    public void buttonCreateOnClick() {

        double fuelAmount;
        int odometer;
        double cash;

        String today = new SimpleDateFormat("yyyy/MM/dd", Locale.US).format(new Date());

        String vehicle = "default";
        String[] maintElemArray = getResources().getStringArray(R.array.maintElemArray);
        String maintElem = maintElemArray[0];
        String[] maintTypeArray = getResources().getStringArray(R.array.maintTypeArray);
        String maintType = maintTypeArray[0];

        // validation
        if (!TextUtils.isEmpty(editTextFuel.getText().toString())) {
            fuelAmount = Double.parseDouble(editTextFuel.getText().toString());
        } else {
            tilFuel.setError(getString(R.string.error_required));
            return;
        }

        if (!TextUtils.isEmpty(editTextOdometer.getText().toString())) {
            odometer = Integer.parseInt(editTextOdometer.getText().toString());
        } else {
            tilOdometer.setError(getString(R.string.error_required));
            return;
        }
        if (TextUtils.isEmpty(editTextPrice.getText().toString())) {
            cash = 0;
        } else {
            try {
                cash = Double.parseDouble(editTextPrice.getText()
                        .toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, getString(R.string.error_price), Toast.LENGTH_LONG).show();
                return;
            }
        }
        // end validation

        if (cashType == 0) {
            cash = cash * fuelAmount;
        }
        cash = Math.round(cash * 100.0) / 100.0;

        double consumption = getConsumption(vehicle, maintElem, fuelAmount, odometer);

        String details = "quick entry";

        MaintenanceItem mItem = new MaintenanceItem(vehicle,
                maintElem, maintType, fuelAmount, consumption,
                today, odometer, details, mileageType, cash);

        mainLogSource.addMaintenanceItem(mItem);

        switch (mileageType) {
            case 0:
                Toast.makeText(this,
                        getString(R.string.text_new_fuel_entry_added) +
                                "\r\n\r\n" + getString(R.string.text_fuel_consumption_is) +
                                consumption +
                                "l/100km" +
                                "\r\n\r\n" +
                                getString(R.string.text_have_a_safe_trip),
                        Toast.LENGTH_LONG).show();
                finish();
            case 2:
                Toast.makeText(this,
                        getString(R.string.text_new_fuel_entry_added) +
                                "\r\n\r\n" + getString(R.string.text_fuel_consumption_is) +
                                consumption +
                                "l/km" +
                                "\r\n\r\n" +
                                getString(R.string.text_have_a_safe_trip),
                        Toast.LENGTH_LONG).show();
                finish();
            default:
                Toast.makeText(this,
                        getString(R.string.text_new_fuel_entry_added) +
                                "\r\n\r\n" + getString(R.string.text_fuel_consumption_is) +
                                consumption +
                                "MPG" +
                                "\r\n\r\n" +
                                getString(R.string.text_have_a_safe_trip),
                        Toast.LENGTH_LONG).show();
                finish();
        }
    }

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
}
