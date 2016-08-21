//package dialogs;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.kaetter.motorcyclemaintenancelog.MyListFragment;
//import com.kaetter.motorcyclemaintenancelog.NewLogActivity;
//import com.kaetter.motorcyclemaintenancelog.R;
//
//import beans.MaintenanceItem;
//import dbcontrollers.MainLogSource;
//
//public class UpdateDialog extends DialogFragment {
//
//	private int START_NEW_LOG = 0;
//	private MaintenanceItem item;
//
//	public UpdateDialog() {}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//							 Bundle savedInstanceState) {
//
//		item= (MaintenanceItem) getArguments().getSerializable("MaintItem");
//
//		getDialog().getWindow().requestFeature(STYLE_NO_TITLE);
//
//		View view = inflater.inflate(R.layout.dialogmainupdate, container);
//
//		TextView maintElemStringView = (TextView) view
//				.findViewById(R.id.maintElemString);
//
//		//TODO  check for null
//
//		maintElemStringView.setText(item.getMaintElem() + "("
//				+ item.getMaintType() + ")");
//		TextView maintElemDateView = (TextView) view
//				.findViewById(R.id.maintElemDate);
//		maintElemDateView.setText(item.getDate());
//		TextView odometerInDialog = (TextView) view
//				.findViewById(R.id.odometerInDialog);
//		String odometerText = Integer.toString(item.getOdometer());
//		odometerInDialog.setText(odometerText);
//		TextView maintElemDetailsView = (TextView) view
//				.findViewById(R.id.maintElemDetails);
//		maintElemDetailsView.setText(item.getDetails());
//		TextView kmLabel =  (TextView) view.findViewById(R.id.kmLabel);
//
//		if(MyListFragment.mileageType== 1) {
//			kmLabel.setText("miles");
//		}
//
//		Button deleteEntry = (Button) view.findViewById(R.id.deleteButton);
//		Button updateEntry = (Button) view.findViewById(R.id.updateButton);
//		Button cancelEntry = (Button) view.findViewById(R.id.cancelButton);
//
//		deleteEntry.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				new MainLogSource(getActivity()).deleteEntry(item);
//				MyListFragment activity = (MyListFragment) getActivity()
//						.getSupportFragmentManager().findFragmentById(
//								R.id.tab_1);
//
//				activity.onItemChanged();
//
//				MyListFragment activity2 = (MyListFragment) getActivity()
//						.getSupportFragmentManager().findFragmentById(
//								R.id.tab_2);
//
//				if (activity2 != null) {
//					activity2.onItemChanged();
//				}
//
//				Toast.makeText(getActivity(), "Entry was deleted",
//						Toast.LENGTH_SHORT).show();
//				dismiss();
//
//			}
//		});
//
//		updateEntry.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				Intent intent = new Intent(getActivity(), NewLogActivity.class);
////				intent.putExtra("Maintenanceitem", item);
//				intent.putExtra("isModification", true);
//
//				startActivityForResult(intent, START_NEW_LOG);
//
//			}
//		});
//
//		cancelEntry.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				Log.i("UpdateDialog", "dismiss dialogue");
//				dismiss();
//			}
//		});
//
//		return view;
//	}
//
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//		if (requestCode == START_NEW_LOG && resultCode == Activity.RESULT_OK) {
//
//			MyListFragment activity2 = (MyListFragment) getActivity()
//					.getSupportFragmentManager().findFragmentById(R.id.tab_2);
//
//			if (activity2 != null) {
//
//				activity2.onItemChanged();
//			}
//
//			MyListFragment activity1 = (MyListFragment) getActivity()
//					.getSupportFragmentManager().findFragmentById(R.id.tab_1);
//
//			if (activity1 != null) {
//
//				activity1.onItemChanged();
//			}
//
//
//			dismiss();
//		}
//
//	}
//}
