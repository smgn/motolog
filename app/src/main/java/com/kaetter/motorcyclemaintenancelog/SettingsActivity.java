package com.kaetter.motorcyclemaintenancelog;

import android.os.Bundle;
import android.preference.PreferenceActivity;


public class SettingsActivity extends PreferenceActivity {
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
//		final Preference pref_UseFuelly = (Preference) findPreference("pref_UseFuelly");
//		
//		pref_UseFuelly.setOnPreferenceClickListener(new OnPreferenceClickListener() {
//			
//			@Override
//			public boolean onPreferenceClick(Preference preference) {
//				
//				if (preference.isEnabled()) {
//					
//					
//					
//				}
//				
//				
//				return false;
//			}
//		});
		
	}
}
