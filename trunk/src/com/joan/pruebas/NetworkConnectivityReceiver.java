package com.joan.pruebas;

import java.util.Set;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class NetworkConnectivityReceiver extends BroadcastReceiver {
	private static String TAG = NetworkConnectivityReceiver.class.getName();

	private SharedPreferences mPreferences;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d(TAG, "Action Received: " + action + " From intent: " + intent);
		logIntent(intent);

		NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		if (networkInfo != null) {
			State state = networkInfo.getState();
			String typeName = networkInfo.getTypeName();
			if (state.equals(State.CONNECTED) && typeName.equals("WIFI")) {
				WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo connectionInfo = wm.getConnectionInfo();
				String ssid = connectionInfo.getSSID();
				String bssid = connectionInfo.getBSSID();
				Log.d(TAG, "Conected SSID:" + ssid + ", bssid:" + bssid);
				if (isFonNetWork(ssid, bssid)) {

					mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
					boolean active = mPreferences.getBoolean(context.getString(R.string.pref_active), false);
					Log.d(TAG, "Application active:" + active);
					if (active) {
						String userName = mPreferences.getString(context.getString(R.string.pref_username), "");
						String password = mPreferences.getString(context.getString(R.string.pref_password), "");
						Log.d(TAG, "credentials:" + userName + "/" + password);
						Log.d(TAG, "Conectamos!!!! ahora habría que intentar hacer el WISPr");
						WISPrLogger wl = new WISPrLogger();
						String loginResult = wl.login(userName, password);
						Log.d(TAG, "LoginResult:" + loginResult);
						notifyConnectionResult(context, loginResult);
					}
				}
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

	private boolean isFonNetWork(String ssid, String macAddress) {
		ssid = ssid.toUpperCase();

		return ssid.startsWith("FON_") || ssid.equals("BTFON") || ssid.equals("Neuf WiFi FON");
	}

	private void notifyConnectionResult(Context context, String result) {
		int icon = R.drawable.icon;
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
}