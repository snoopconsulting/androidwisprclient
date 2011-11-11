package com.belgacom.fon.util;

import android.app.backup.SharedPreferencesBackupHelper;

public class BackupAgent extends BackupAgentHelperWrapper {

	@Override
	public void onCreate() {
		SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(getBackupAgentInstance(),
				getDefaultSharedPreferencesName());
		addHelper("prefs", helper);
	}

	private String getDefaultSharedPreferencesName() {
		return getPackageName() + "_preferences";
	}
}