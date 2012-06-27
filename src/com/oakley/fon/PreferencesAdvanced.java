package com.oakley.fon;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

public class PreferencesAdvanced extends PreferenceActivity {

	public static final int BACK_ID = Menu.FIRST;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences_advanced);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, BACK_ID, 0, R.string.menu_back).setIcon(android.R.drawable.ic_menu_revert);

		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean res = true;
		switch (item.getItemId()) {
			case BACK_ID:
				setResult(RESULT_OK);
				finish();
				break;
			default:
				res = super.onOptionsItemSelected(item);
		}

		return res;
	}
}