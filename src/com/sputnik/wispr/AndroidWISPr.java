package com.sputnik.wispr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class AndroidWISPr extends PreferenceActivity {
	private static String TAG = AndroidWISPr.class.getName();

	public static final int CLOSE_ID = Menu.FIRST;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences);
		manageLogOffPreference();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Preference logOffPref = findPreference(this.getText(R.string.pref_logOff));
		SharedPreferences mPreferences = logOffPref.getSharedPreferences();
		final String logOffUrl = mPreferences.getString(this.getText(R.string.pref_logOffUrl).toString(), "");
		if (logOffUrl != null && logOffUrl.trim().length() > 0) {
			logOffPref.setEnabled(true);
		} else {
			logOffPref.setEnabled(false);
		}
	}

	private void manageLogOffPreference() {
		Preference logOffPref = findPreference(this.getText(R.string.pref_logOff));
		SharedPreferences mPreferences = logOffPref.getSharedPreferences();
		final String logOffUrl = mPreferences.getString(this.getText(R.string.pref_logOffUrl).toString(), "");
		if (logOffUrl != null && logOffUrl.trim().length() > 0) {
			logOffPref.setEnabled(true);
		} else {
			logOffPref.setEnabled(false);
		}
		logOffPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				Log.d(TAG, "Logoff Button Clicked:" + logOffUrl);
				Context context = preference.getContext();
				Intent logOffIntent = new Intent(context, LogOffService.class);
				logOffIntent.putExtra(context.getString(R.string.pref_logOffUrl), logOffUrl);
				context.startService(logOffIntent);
				preference.setEnabled(false);
				return true;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(0, CLOSE_ID, 0, R.string.menu_close).setIcon(android.R.drawable.ic_menu_close_clear_cancel);

		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean res = true;
		switch (item.getItemId()) {
			case CLOSE_ID:
				finish();
				break;
			default:
				res = super.onOptionsItemSelected(item);
		}

		return res;
	}
}