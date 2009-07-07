package com.joan.wispr;

import java.util.Set;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class WISPrLoggerService extends IntentService {
	private static String TAG = WISPrLoggerService.class.getName();

	public WISPrLoggerService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "starting service, received intent:" + intent);
		logIntent(intent);
		String password = intent.getStringExtra(this.getString(R.string.pref_password));
		String username = intent.getStringExtra(this.getString(R.string.pref_username));
		Log.d(TAG, "credentials" + username + "/" + password);
		WISPrLogger logger = new WISPrLogger();
		String result = logger.login(username, password);
		notifyConnectionResult(this, result);
		// this.stopSelf();
	}

	private void notifyConnectionResult(Context context, String result) {
		int icon = R.drawable.icon;
		SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean notificationsActive = mPreferences.getBoolean(context.getString(R.string.pref_enableNotifications),
				false);
		if (notificationsActive) {
			String notificationTitle = context.getString(R.string.notif_title);
			String notificationText = context.getString(R.string.notif_text);
			if (result.equals(WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_SUCCEEDED)) {
				NotificationManager notificationManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				Log.d(TAG, "Got Notification Service");
				Intent appIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, appIntent, 0);

				Notification notification = new Notification(icon, notificationTitle, System.currentTimeMillis());
				notification.setLatestEventInfo(context, notificationTitle, notificationText, pendingIntent);
				boolean vibrate = mPreferences.getBoolean(context.getString(R.string.pref_vibrate), false);
				if (vibrate) {
					notification.vibrate = new long[] { 100, 250 };
				}

				String ringtone = mPreferences.getString(context.getString(R.string.pref_ringtone), "");
				if (!ringtone.equals("")) {
					notification.sound = Uri.parse(ringtone);
				}

				notificationManager.notify(1, notification);
				Log.d(TAG, "Notification Sent");
			}
		}
	}

	private void logIntent(Intent intent) {
		Log.d(TAG, "intent.getAction:" + intent.getAction());
		Log.d(TAG, "intent.getData():" + intent.getData());
		Log.d(TAG, "intent.getDataString():" + intent.getDataString());
		Log.d(TAG, "intent.getScheme():" + intent.getScheme());
		Log.d(TAG, "intent.getType():" + intent.getType());
		Bundle extras = intent.getExtras();
		if (extras != null && extras.size() > 0) {
			Set<String> keys = extras.keySet();
			for (String key : keys) {
				Object value = extras.get(key);
				Log.d(TAG, "EXTRA: {" + key + "::" + value + "}");
			}
		} else {
			Log.d(TAG, "NO EXTRAS");
		}
	}

}
