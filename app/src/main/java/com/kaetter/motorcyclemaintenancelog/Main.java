package com.kaetter.motorcyclemaintenancelog;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import dbcontrollers.MainHelper;
import dbcontrollers.MainLogSource;
import events.CopyDatabaseEvent;

public class Main extends AppCompatActivity {

	@BindView(R.id.toolbar) Toolbar mToolbar;
	@BindView(R.id.tabLayout) TabLayout mTabLayout;
	@BindView(R.id.viewPager) ViewPager mViewPager;

	final int START_NEW_LOG = 0;
	final int START_SETTINGS = 1;
	final int PERMISSIONS_READ_EXTERNAL_STORAGE = 0;
	final int PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;
	final int PERMISSIONS_SETTINGS = 2;

    private final String TAG = "Main";

	SharedPreferences sharedPrefs;
	int mileageType;

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
				startActivityForResult(intent, START_NEW_LOG);
				return true;
			case R.id.menu_importdb:
				checkReadExternalStoragePermissions();
				return true;
			case R.id.menu_exportdb:
				checkWriteExternalStoragePermissions();
				return true;
			case R.id.menu_filter:
				//TODO: transplant code
				return true;
			case R.id.menu_settings:
				Intent intent2 = new Intent(this, SettingsActivity.class);
				startActivityForResult(intent2, START_SETTINGS);
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

			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.READ_EXTERNAL_STORAGE)) {

				// Show an expanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.

				final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
				alertDialog.setTitle(getString(R.string.dialog_title_permission_needed));
				alertDialog.setMessage(getString(R.string.dialog_message_read_ext_storage));
				alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						requestReadExtStoragePermissions();
					}
				});
				alertDialog.show();

			} else {
				// No explanation needed, we can request the permission.
				requestReadExtStoragePermissions();
			}
		} else {
			showImportDbDialog();
		}
	}

	private void checkWriteExternalStoragePermissions() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle(getString(R.string.dialog_title_permission_needed));
                alertDialog.setMessage(getString(R.string.dialog_message_write_ext_storage));
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestWriteExtStoragePermissions();
                    }
                });
                alertDialog.show();

            } else {
                // No explanation needed, we can request the permission.
                requestReadExtStoragePermissions();
            }
		} else {
            showExportDbDialog();
        }
	}

	private void showImportDbDialog() {
		File extSdImport = Environment.getExternalStorageDirectory();
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File[] list = extSdImport.listFiles();
			File[] listOfFiles = extSdImport.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					return filename.toLowerCase().startsWith("motolog");
				}
			});

			if (listOfFiles == null) {
				return;
			}

			final ArrayAdapter<File> arrayAdapter = new ArrayAdapter<>(this,
					android.R.layout.select_dialog_singlechoice,
					listOfFiles);

			final AlertDialog.Builder builderImport = new AlertDialog.Builder(this);
			builderImport.setTitle(getString(R.string.dialog_title_choose_db_to_import));
			builderImport.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int position) {

					AlertDialog.Builder builderInner =
							new AlertDialog.Builder(builderImport.getContext());

					String message = getString(R.string.importing) + " " +
							arrayAdapter.getItem(position) + System.getProperty("line.separator") +
							System.getProperty("line.separator") +
							getString(R.string.your_current_data_will_be_overwritten);

					builderInner.setMessage(message);
					final String fromDbPath = arrayAdapter.getItem(position).toString();

					builderInner.setPositiveButton("Import", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							File toDbPath = getDatabasePath(MainHelper.DATABASE_NAME);
							try {
								EventBus.getDefault().post(
										new CopyDatabaseEvent(fromDbPath, toDbPath.toString()));
								recreate();
							} catch (Exception e) {
								Toast.makeText(getApplicationContext(),
										getString(R.string.db_import_error),
										Toast.LENGTH_LONG)
										.show();
							}
						}
					});
					builderInner.setNegativeButton("Cancel", null);
					builderInner.show();
				}
			});
			builderImport.show();
		}
	}

    private void showExportDbDialog() {
        File extSd = Environment.getExternalStorageDirectory();

        Log.d(TAG, "Media is " + Environment.getExternalStorageState());

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.US);
            Date date = new Date();

            final String exportPath = extSd.toString() +
                    "/" +
                    getString(R.string.app_name_no_spaces) +
                    sdf.format(date)  +
                    ".db";
            builder.setMessage(getString(R.string.text_file_will_be_exported_to) + exportPath)
                    .setTitle(getString(R.string.text_exporting_database));

            builder.setPositiveButton(getString(R.string.button_ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File dbFile = getDatabasePath(MainHelper.DATABASE_NAME);
                            try {
                                mainLogSource.copyDatabase(dbFile.toString(), exportPath);
                            } catch (IOException e) {
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.error_export_db_failed),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });

            builder.setNegativeButton(getString(R.string.button_cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            // do nothing
                        }
                    });

            builder.show();
        }
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
					final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
					alertDialog.setTitle(getString(R.string.dialog_title_permission_needed));
					alertDialog.setMessage(
							getString(R.string.dialog_message_read_ext_storage_retry));
					alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					alertDialog.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent =
									new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
							Uri uri = Uri.fromParts("package", getPackageName(), null);
							intent.setData(uri);
							startActivityForResult(intent, PERMISSIONS_SETTINGS);
						}
					});
					alertDialog.show();
				}
			}
			case PERMISSIONS_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImportDbDialog();
                } else if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setTitle(getString(R.string.dialog_title_permission_needed));
                    alertDialog.setMessage(
                            getString(R.string.dialog_message_write_ext_storage_retry));
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent =
                                    new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, PERMISSIONS_SETTINGS);
                        }
                    });
                    alertDialog.show();
                }
			}
		}
	}
}