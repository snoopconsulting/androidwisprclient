package com.oakley.fon.util;

import android.annotation.TargetApi;
import android.app.backup.SharedPreferencesBackupHelper;

@TargetApi(8)
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