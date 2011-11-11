package com.belgacom.fon;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.belgacom.fon.util.Utils;

public class NotificationCleaningService extends IntentService {

	private static String TAG = NotificationCleaningService.class.getName();

	public static final String ACTION_CLEAN = "CLEAN";

	public NotificationCleaningService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.v(TAG, "Handling disconection intent: " + intent);
		if (intent.getAction().equals(ACTION_CLEAN)) {
			cleanNotification(this, intent);
			cleanLogOffUrl(this, intent);
		}
	}

	private void cleanLogOffUrl(Context context, Intent intent) {
		Utils.removePreference(context, R.string.pref_logOffUrl);
	}

	private void cleanNotification(Context context, Intent intent) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.cancel(1);
	}
}