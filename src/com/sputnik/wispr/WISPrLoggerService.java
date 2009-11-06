package com.sputnik.wispr;

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

import com.sputnik.wispr.logger.NeufLogger;
import com.sputnik.wispr.logger.WISPrLogger;
import com.sputnik.wispr.logger.WebLogger;
import com.sputnik.wispr.util.WISPrConstants;

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
		String ssid = intent.getStringExtra(this.getString(R.string.pref_ssid));

		WebLogger logger = null;
		if (ssid.equalsIgnoreCase("NEUF WIFI FON")) {
			logger = new NeufLogger();
		} else {
			logger = new WISPrLogger();
		}
		String result = logger.login(username, password);
		notifyConnectionResult(this, result, ssid);
	}

	private void notifyConnectionResult(Context context, String result, String ssid) {
		int icon = R.drawable.f;
		long[] vibratePattern = null;

		SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean notificationsActive = mPreferences.getBoolean(context
				.getString(R.string.pref_connectionNotificationsEnable), false);
		if (notificationsActive) {
			String notificationTitle = null;
			String notificationText = null;
			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);

			if (ssid == null) {
				ssid = context.getString(R.string.notif_default_ssid);
			}

			Intent appIntent = null;
			Notification notification = null;
			PendingIntent pendingIntent = null;
			boolean useRingtone = true;
			boolean useVibration = true;

			if (result.equals(WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_SUCCEEDED)) {
				notificationTitle = context.getString(R.string.notif_title_ok);
				notificationText = String.format(context.getString(R.string.notif_text_ok), ssid);
				appIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
				vibratePattern = new long[] { 100, 250 };

				pendingIntent = PendingIntent.getActivity(context, 1, appIntent, 0);
				notification = new Notification(icon, notificationTitle, System.currentTimeMillis());
				notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
			} else if (result.equals(WISPrConstants.ALREADY_CONNECTED)) {
				notificationTitle = context.getString(R.string.notif_title_ok);
				notificationText = String.format(context.getString(R.string.notif_text_ok), ssid);
				appIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
				pendingIntent = PendingIntent.getActivity(context, 1, appIntent, 0);
				notification = new Notification(icon, notificationTitle, System.currentTimeMillis());
				notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
				useRingtone = false;
				useVibration = false;
			} else if (!result.equals(WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR)) {
				notificationTitle = context.getString(R.string.notif_title_ko);
				notificationText = context.getString(R.string.notif_text_ko) + " {" + result + "}";
				appIntent = new Intent(context, AndroidWISPr.class);
				vibratePattern = new long[] { 100, 250, 100, 500 };

				pendingIntent = PendingIntent.getActivity(context, 1, appIntent, 0);
				notification = new Notification(icon, notificationTitle, System.currentTimeMillis());
				notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
			}

			if (appIntent != null) {
				notification.setLatestEventInfo(context, notificationTitle, notificationText, pendingIntent);

				if (useVibration) {
					boolean vibrate = mPreferences
							.getBoolean(context.getString(R.string.pref_connectionVibrate), false);
					if (vibrate) {
						notification.vibrate = vibratePattern;
					}
				}

				if (useRingtone) {
					String ringtone = mPreferences.getString(context.getString(R.string.pref_connectionRingtone), "");
					if (!ringtone.equals("")) {
						notification.sound = Uri.parse(ringtone);
					}
				}

				notificationManager.notify(1, notification);
			}
		}
	}
}