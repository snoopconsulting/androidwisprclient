package com.sputnik.wispr;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationCleaningService extends IntentService {

	private static String TAG = NotificationCleaningService.class.getName();

	public static final String ACTION_CLEAN = "CLEAN";

	public NotificationCleaningService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "Handling disconection intent: " + intent);
		if (intent.getAction().equals(ACTION_CLEAN)) {
			cleanNotification(this, intent);
		}
	}

	private void cleanNotification(Context context, Intent intent) {
		NotificationManager notificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.cancel(1);
	}
}
