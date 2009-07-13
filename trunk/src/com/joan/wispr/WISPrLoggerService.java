package com.joan.wispr;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
		String password = intent.getStringExtra(this.getString(R.string.pref_password));
		String username = intent.getStringExtra(this.getString(R.string.pref_username));
		WISPrLogger logger = new WISPrLogger();
		String result = logger.login(username, password);
		notifyConnectionResult(this, result);
	}

	private void notifyConnectionResult(Context context, String result) {
		int icon = R.drawable.icon;
		SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean notificationsActive = mPreferences.getBoolean(context.getString(R.string.pref_enableNotifications),
				false);
		if (notificationsActive) {
			String notificationTitle = null;
			String notificationText = null;
			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);

			Intent appIntent = null;
			if (result.equals(WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_SUCCEEDED)) {
				notificationTitle = context.getString(R.string.notif_title_ok);
				notificationText = context.getString(R.string.notif_text_ok);
				appIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
			} else if (!result.equals(WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR)
					&& !result.equals(WISPrConstants.ALREADY_CONNECTED)) {
				notificationTitle = context.getString(R.string.notif_title_ko);
				notificationText = context.getString(R.string.notif_text_ko) + " {" + result + "}";
				appIntent = new Intent(context, AndroidWISPr.class);
			}

			if (appIntent != null) {
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, appIntent, 0);
				Notification notification = new Notification(icon, notificationTitle, System.currentTimeMillis());
				notification.setLatestEventInfo(context, notificationTitle, notificationText, pendingIntent);
				boolean vibrate = mPreferences.getBoolean(context.getString(R.string.pref_vibrate), false);
				if (vibrate) {
					notification.vibrate = new long[] { 100, 250, 100, 500 };
				}

				String ringtone = mPreferences.getString(context.getString(R.string.pref_ringtone), "");
				if (!ringtone.equals("")) {
					notification.sound = Uri.parse(ringtone);
				}

				notificationManager.notify(1, notification);
			}
		}
	}
}