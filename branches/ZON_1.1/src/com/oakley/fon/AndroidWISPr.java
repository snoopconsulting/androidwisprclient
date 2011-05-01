package com.oakley.fon;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class AndroidWISPr extends PreferenceActivity {
	private static String TAG = AndroidWISPr.class.getSimpleName();

	public static final int CLOSE_ID = Menu.FIRST;

	public static final int LOGOFF_ID = CLOSE_ID + 1;

	public static final int ADVANCED_ID = LOGOFF_ID + 1;

	public static final int HELP_ID = ADVANCED_ID + 1;

	private SharedPreferences mPreferences = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// savePreferences(getIntent());
		addPreferencesFromResource(R.layout.preferences_main);
		setContentView(R.layout.preferences_header);
	}

	@SuppressWarnings("unused")
	private void savePreferences(Intent intent) {
		final Uri uri = intent.getData();
		if (uri != null) {
			String username = uri.getQueryParameter(this.getString(R.string.pref_username));
			String password = uri.getQueryParameter(this.getString(R.string.pref_password));
			String active = uri.getQueryParameter(this.getString(R.string.pref_active));
			String connectionAutoEnable = uri.getQueryParameter(this.getString(R.string.pref_connectionAutoEnable));
			Editor editor = getPreferenceManager().getSharedPreferences().edit();
			if (username != null) {
				editor.putString(this.getString(R.string.pref_username), username);
			}
			if (password != null) {
				editor.putString(this.getString(R.string.pref_password), password);
			}
			if (active != null) {
				editor.putBoolean(this.getString(R.string.pref_active), Boolean.valueOf(active));
			}
			if (connectionAutoEnable != null) {
				editor.putBoolean(this.getString(R.string.pref_connectionAutoEnable), Boolean
						.valueOf(connectionAutoEnable));
			}
			editor.commit();
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		String logOffUrl = getLogOffUrl();
		MenuItem menuItem = menu.findItem(LOGOFF_ID);
		if (menuItem != null) {
			if (logOffUrl != null) {
				menuItem.setEnabled(true);
			} else {
				menuItem.setEnabled(false);
			}
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, CLOSE_ID, 0, R.string.menu_close).setIcon(android.R.drawable.ic_menu_save);
		menu.add(Menu.NONE, HELP_ID, 1, R.string.menu_help).setIcon(android.R.drawable.ic_menu_help);
		menu.add(Menu.NONE, LOGOFF_ID, 1, R.string.menu_logOff).setIcon(android.R.drawable.ic_menu_revert);
		menu.add(Menu.NONE, ADVANCED_ID, 2, R.string.menu_advanced).setIcon(android.R.drawable.ic_menu_preferences);

		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean res = true;
		switch (item.getItemId()) {
			case CLOSE_ID:
				finish();
				break;
			case LOGOFF_ID:
				logOff_clicked(item);
				break;
			case ADVANCED_ID:
				startActivity(new Intent(this, PreferencesAdvanced.class));
				break;
			case HELP_ID:
				showHelpWindow();
				break;
			default:
				res = super.onOptionsItemSelected(item);
		}

		return res;
	}

	private void showHelpWindow() {
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.help_window, (ViewGroup) findViewById(R.id.layout_help));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setTitle(R.string.help_title);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void logOff_clicked(MenuItem item) {
		String logOffUrl = getLogOffUrl();
		Log.d(TAG, "Logoff Button Clicked:" + logOffUrl);
		if (logOffUrl != null) {
			Intent logOffIntent = new Intent(this, LogOffService.class);
			logOffIntent.putExtra(this.getString(R.string.pref_logOffUrl), logOffUrl);
			this.startService(logOffIntent);
		}
		item.setEnabled(false);
	}

	private SharedPreferences getPreferences() {
		if (mPreferences == null) {
			mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		}

		return mPreferences;
	}

	private String getLogOffUrl() {
		String logOffUrl = getPreferences().getString(this.getText(R.string.pref_logOffUrl).toString(), "");
		if (logOffUrl != null) {
			logOffUrl = logOffUrl.trim();
			if (logOffUrl.length() == 0) {
				logOffUrl = null;
			}
		}

		return logOffUrl;
	}
}