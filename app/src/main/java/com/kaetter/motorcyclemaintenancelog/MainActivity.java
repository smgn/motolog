package com.kaetter.motorcyclemaintenancelog;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import dbcontrollers.MainLogSource;
import dbcontrollers.MotoLogHelper;
import events.CopyDatabaseEvent;
import events.ReloadMainLogEvent;
import events.ReloadReminderLogEvent;
import events.ScrollViewPagerEvent;

public class MainActivity extends AppCompatActivity {

	@BindView(R.id.toolbar) Toolbar mToolbar;
	@BindView(R.id.tabLayout) TabLayout mTabLayout;
	@BindView(R.id.viewPager) ViewPager mViewPager;

	public static final int REQUEST_LOG = 0;
	public static final int REQUEST_SETTINGS = 1;
	public static final int REQUEST_UPDATE_LOG = 2;
	private final int PERMISSIONS_READ_EXTERNAL_STORAGE = 0;
	private final int PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;
	private final int PERMISSIONS_SETTINGS = 2;

    private final String TAG = "MainActivity";

	SharedPreferences sharedPrefs;
	int mileageType;
	int selectedFilterIndex = -1;

    MainLogSource mainLogSource;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);

		setSupportActionBar(mToolbar);
		if (getSupportActionBar() != null) {
			getSupportActionBar().setTitle("");
			getSupportActionBar().setIcon(R.mipmap.app_icon);
		}

		mViewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager(), this));
		mTabLayout.setupWithViewPager(mViewPager);
		mViewPager.setOffscreenPageLimit(2);

		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mileageType = Integer.parseInt(sharedPrefs.getString("pref_MileageType", "0"));
        mainLogSource = new MainLogSource(this);

		EventBus.getDefault().register(this);
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_addLogEntry:
				Intent intent = new Intent(this, NewLogActivity.class);
				startActivityForResult(intent, REQUEST_LOG);
				return true;
			case R.id.menu_importdb:
				checkReadExternalStoragePermissions();
				return true;
			case R.id.menu_exportdb:
				checkWriteExternalStoragePermissions();
				return true;
			case R.id.menu_filter:
				showFilterDialog();
				return true;
			case R.id.menu_settings:
				Intent intent2 = new Intent(this, SettingsActivity.class);
				startActivityForResult(intent2, REQUEST_SETTINGS);
				return true;
			case R.id.menu_about:
				showAboutDialog();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void requestReadExtStoragePermissions() {
		ActivityCompat.requestPermissions(this,
				new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
				PERMISSIONS_READ_EXTERNAL_STORAGE);
	}

    private void requestWriteExtStoragePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSIONS_WRITE_EXTERNAL_STORAGE);
    }

	private void checkReadExternalStoragePermissions() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {

			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.READ_EXTERNAL_STORAGE)) {

				new MaterialDialog.Builder(this)
						.title(R.string.dialog_title_permission_needed)
						.content(R.string.dialog_message_read_ext_storage)
						.positiveText(R.string.button_ok)
						.onPositive(new MaterialDialog.SingleButtonCallback() {
							@Override
							public void onClick(@NonNull MaterialDialog dialog,
							                    @NonNull DialogAction which) {
								requestReadExtStoragePermissions();
							}
						})
						.show();
			} else {
				requestReadExtStoragePermissions();
			}
		} else {
			showImportDbDialog();
		}
	}

	private void checkWriteExternalStoragePermissions() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

	            new MaterialDialog.Builder(this)
			            .title(R.string.dialog_title_permission_needed)
			            .content(R.string.dialog_message_write_ext_storage)
			            .positiveText(R.string.button_ok)
			            .onPositive(new MaterialDialog.SingleButtonCallback() {
				            @Override
				            public void onClick(@NonNull MaterialDialog dialog,
				                                @NonNull DialogAction which) {
					            requestWriteExtStoragePermissions();
				            }
			            })
			            .show();
            } else {
                requestReadExtStoragePermissions();
            }
		} else {
            showExportDbDialog();
        }
	}

	private void showImportDbDialog() {
		File extSdImport = Environment.getExternalStorageDirectory();

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File[] listOfFiles = extSdImport.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					return filename.toLowerCase().endsWith(".db");
				}
			});

			if (listOfFiles == null) { return; }

			List<String> listFiles = new ArrayList<>();

			for (File file : listOfFiles) {
				listFiles.add(file.getAbsolutePath());
			}

			new MaterialDialog.Builder(this)
					.title(getString(R.string.dialog_title_choose_db_to_import))
					.items(listFiles)
					.negativeText(R.string.button_cancel)
					.positiveText(R.string.button_import)
					.itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
						@Override
						public boolean onSelection(MaterialDialog dialog, View itemView,
						                           int which, CharSequence text) {
							try {
								EventBus.getDefault().post(
										new CopyDatabaseEvent(text.toString(),
												getDatabasePath(MotoLogHelper.DATABASE_NAME)
														.toString()));
								recreate();
							} catch (Exception e) {
								Toast.makeText(getApplicationContext(),
										getString(R.string.db_import_error),
										Toast.LENGTH_LONG)
										.show();
							}
							return false;
						}
					})
					.show();
		}
	}

    private void showExportDbDialog() {
        File extSd = Environment.getExternalStorageDirectory();

        Log.d(TAG, "Media is " + Environment.getExternalStorageState());

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            final String exportPath = extSd.toString() +
                    "/" +
                    getString(R.string.app_name_no_spaces) +
		            new SimpleDateFormat("yyyyMMddHHmm", Locale.US).format(new Date())  +
                    ".db";

	        new MaterialDialog.Builder(this)
			        .title(getString(R.string.text_exporting_database))
			        .content(getString(R.string.text_file_will_be_exported_to, exportPath))
			        .positiveText(R.string.button_ok)
			        .negativeText(R.string.button_cancel)
			        .onPositive(new MaterialDialog.SingleButtonCallback() {
				        @Override
				        public void onClick(@NonNull MaterialDialog dialog,
				                            @NonNull DialogAction which) {
					        File dbFile = getDatabasePath(MotoLogHelper.DATABASE_NAME);
					        try {
						        mainLogSource.copyDatabase(dbFile.toString(), exportPath);
					        } catch (IOException e) {
						        Toast.makeText(getApplicationContext(),
								        getString(R.string.error_export_db_failed),
								        Toast.LENGTH_LONG).show();
					        }
				        }
			        })
			        .show();
        }
    }

	private void showFilterDialog() {

		SharedPreferences elemPref = getSharedPreferences(
				getString(R.string.elem_preference_file_key), MODE_PRIVATE);

		SharedPreferences.Editor elemEditor = elemPref.edit();

		String[] maintElemArray = getResources().getStringArray(R.array.maintElemArray);
		ArrayList<String> maintElemArrayList = new ArrayList<>(Arrays.asList(maintElemArray));

		int elemCount = elemPref.getInt("elemCountString", 0);

		if (elemCount == 0) {
			elemCount = maintElemArray.length - 1;
			elemEditor.putInt("elemTypeCount", elemCount);
			elemEditor.apply();

			for (int i = 0; i <= elemCount; i++) {
				elemEditor.putString("elemVal_" + i, maintElemArray[i]);
				elemEditor.commit();
			}
		}

		if (elemCount > maintElemArray.length - 1) {
			for (int i = maintElemArray.length; i < elemCount; i++) {
				maintElemArrayList.add(elemPref.getString("elemVal_" + i, " "));
			}
		}

		new MaterialDialog.Builder(this)
				.title(R.string.dialog_filter)
				.items(maintElemArrayList)
				.itemsCallbackSingleChoice(selectedFilterIndex,
						new MaterialDialog.ListCallbackSingleChoice() {
					@Override
					public boolean onSelection(MaterialDialog dialog, View view, int which,
					                           CharSequence text) {
						selectedFilterIndex = which;

						Bundle b = new Bundle();
						b.putString("filter", text.toString());
						EventBus.getDefault().post(new ScrollViewPagerEvent());
						EventBus.getDefault().postSticky(new ReloadMainLogEvent(b));
						return true;
					}
				})
				.onNeutral(new MaterialDialog.SingleButtonCallback() {
					@Override
					public void onClick(@NonNull MaterialDialog dialog,
					                    @NonNull DialogAction which) {
						selectedFilterIndex = -1;
						EventBus.getDefault().post(new ScrollViewPagerEvent());
						EventBus.getDefault().postSticky(new ReloadMainLogEvent(null));
					}
				})
				.neutralText(R.string.button_select_all)
				.negativeText(R.string.button_cancel)
				.positiveText(R.string.button_apply)
				.show();
	}

	private void showAboutDialog() {
		new MaterialDialog.Builder(this)
				.title(R.string.dialog_about)
				.customView(R.layout.dialog_about, true)
				.positiveText(R.string.button_ok)
				.show();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
	                                       @NonNull String permissions[],
	                                       @NonNull int[] grantResults) {
		switch (requestCode) {
			case PERMISSIONS_READ_EXTERNAL_STORAGE: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					showImportDbDialog();
				} else if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_DENIED) {
					new MaterialDialog.Builder(this)
							.title(R.string.dialog_title_permission_needed)
							.content(R.string.dialog_message_read_ext_storage_retry)
							.positiveText(R.string.button_ok)
							.neutralText(R.string.button_retry)
							.onNeutral(new MaterialDialog.SingleButtonCallback() {
								@Override
								public void onClick(@NonNull MaterialDialog dialog,
								                    @NonNull DialogAction which) {
									Intent intent = new Intent(
											Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
									Uri uri = Uri.fromParts("package", getPackageName(), null);
									intent.setData(uri);
									startActivityForResult(intent, PERMISSIONS_SETTINGS);
								}
							})
							.show();
				}
			}
			case PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImportDbDialog();
                } else if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
	                new MaterialDialog.Builder(this)
			                .title(R.string.dialog_title_permission_needed)
			                .content(R.string.dialog_message_write_ext_storage_retry)
			                .positiveText(R.string.button_ok)
			                .neutralText(R.string.button_retry)
			                .onNeutral(new MaterialDialog.SingleButtonCallback() {
				                @Override
				                public void onClick(@NonNull MaterialDialog dialog,
				                                    @NonNull DialogAction which) {
					                Intent intent = new Intent(
							                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
					                Uri uri = Uri.fromParts("package", getPackageName(), null);
					                intent.setData(uri);
					                startActivityForResult(intent, PERMISSIONS_SETTINGS);
				                }
			                })
			                .show();
                }
			}
		}
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult");

        switch (requestCode) {
            case REQUEST_LOG:
	            Log.d(TAG, "From NewLogActivity");
                if (resultCode == RESULT_OK) {
                    // post sticky because I don't have time to figure out lifecycles
                    EventBus.getDefault().postSticky(new ReloadMainLogEvent(null));
                    EventBus.getDefault().postSticky(new ReloadReminderLogEvent());
                } else {
                    // TODO: Show error
                }
                break;
            case REQUEST_SETTINGS:
	            Log.d(TAG, "From SettingsActivity");
                if (resultCode == RESULT_OK) {

                } else {
                    // TODO: Show error
                }
                break;
	        case REQUEST_UPDATE_LOG:
		        Log.d(TAG, "From NewLogActivity, updated log");
		        if (resultCode == RESULT_OK) {
			        // post sticky because I don't have time to figure out lifecycles
			        EventBus.getDefault().postSticky(new ReloadMainLogEvent(null));
			        EventBus.getDefault().postSticky(new ReloadReminderLogEvent());
		        } else {
			        // TODO: Show error
		        }
		        break;
        }
    }

	@Subscribe
	public void onEvent(ScrollViewPagerEvent event) {
		mViewPager.setCurrentItem(0);
	}
}