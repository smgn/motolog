package dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kaetter.motorcyclemaintenancelog.NewLogActivity;
import com.kaetter.motorcyclemaintenancelog.NewRem;
import com.kaetter.motorcyclemaintenancelog.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewElementDialog extends DialogFragment {

	@BindView(R.id.title) TextView title;
	@BindView(R.id.newelem) EditText et;
	@BindView(R.id.ok) Button ok;
	@BindView(R.id.nok) Button nok;

	private String type;
	private String callingActivity;

	public interface OnNewElementListener {
		void addNewSharedPreference(String sharedPreference, String value);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = getArguments().getString("newelementdialog");
		callingActivity = getArguments().getString("callingActivity");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		getDialog().getWindow().requestFeature(STYLE_NO_TITLE);

		View root = inflater.inflate(R.layout.dialognewelement, container, false);

		ButterKnife.bind(this, root);

		if (type.equals(NewLogActivity.ELEMVAL)) {
			title.setText("Add new element!");
		}
		if (type.equals(NewLogActivity.ELEMTYPEVAL)) {
			title.setText("Add new element type!");
		}

		return root;
	}

	@OnClick(R.id.ok)
	public void addNewElement() {
		if (String.valueOf(et.getText()).trim().equals("")) {
			Toast.makeText(getActivity(), "A text must be entered! ",  Toast.LENGTH_SHORT).show();
			return;
		}

		if (type.equals(NewLogActivity.ELEMVAL)) {

			String etString = et.getText().toString();

			if (callingActivity.equals(NewLogActivity.TAG)) {

				NewLogActivity activity = (NewLogActivity) getActivity();
				if (activity!=null) {
					activity.addNewSharedPreference(NewLogActivity.ELEMVAL,
							String.valueOf(et.getText()));
					dismiss();

				}
			}

			if (callingActivity.equals(NewRem.tag)) {

				NewRem activity = (NewRem) getActivity();
				if (activity!=null) {

					activity.addNewSharedPreference(NewLogActivity.ELEMVAL,
							String.valueOf(et.getText()));
					dismiss();

				}
			}
		}

		if (type.equals(NewLogActivity.ELEMTYPEVAL)) {

			if (callingActivity.equals(NewRem.tag)) {

				NewRem activity = (NewRem) getActivity();

				if (activity!=null) {
					activity.addNewSharedPreference(NewLogActivity.ELEMTYPEVAL,
							String.valueOf(et.getText()));
					dismiss();
				}
			}

			if (callingActivity.equals(NewLogActivity.TAG)) {

				NewLogActivity activity = (NewLogActivity) getActivity();

				if (activity!=null) {

					activity.addNewSharedPreference(NewLogActivity.ELEMTYPEVAL,
							String.valueOf(et.getText()));

					dismiss();
				}
			}
		}
	}

	@OnClick(R.id.nok)
	public void dismissDialog() {
		getDialog().dismiss();
	}
}
