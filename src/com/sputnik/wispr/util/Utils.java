package com.sputnik.wispr.util;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Utils {
	public static void logIntent(String TAG, Intent intent) {
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, "intent.getAction():" + intent.getAction());
			Log.d(TAG, "intent.getData():" + intent.getData());
			Log.d(TAG, "intent.getDataString():" + intent.getDataString());
			Log.d(TAG, "intent.getScheme():" + intent.getScheme());
			Log.d(TAG, "intent.getType():" + intent.getType());
			Bundle extras = intent.getExtras();
			if (extras != null && !extras.isEmpty()) {
				for (String key : extras.keySet()) {
					Object value = extras.get(key);
					Log.d(TAG, "EXTRA: {" + key + "::" + value + "}");
				}
			} else {
				Log.d(TAG, "NO EXTRAS");
			}
		}
	}
}
