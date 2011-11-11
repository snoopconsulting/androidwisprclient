package com.belgacom.fon.logger;

import android.util.Log;

public class BTFonLogger extends WISPrLogger {
	protected static String TAG = BTFonLogger.class.getName();

	private static final String NETWORK_PREFIX = "BTFON/";

	@Override
	public LoggerResult login(String user, String password) {
		Log.d(TAG, "Login with " + NETWORK_PREFIX + user);
		return super.login(NETWORK_PREFIX + user, password);
	}
}
