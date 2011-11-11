package com.belgacom.fon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BadParcelableException;
import android.util.Log;

import com.belgacom.fon.util.FONUtils;
import com.belgacom.fon.util.Utils;
import com.oakley.fon.R;

public class NetworkConnectivityReceiver extends BroadcastReceiver {
	private static String TAG = NetworkConnectivityReceiver.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d(TAG, "Action Received: " + action + " From intent: " + intent);

		// We look if we are connected
		if (isConnectedIntent(intent)) {
			WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo connectionInfo = wm.getConnectionInfo();
			String ssid = connectionInfo.getSSID();
			String bssid = connectionInfo.getBSSID();
			Log.v(TAG,
					"Conected. SSID:" + ssid + ", bssid:" + bssid + ", supplicantState:"
							+ connectionInfo.getSupplicantState());

			// We look if it's a FON Access Point
			if (FONUtils.isSupportedNetwork(ssid, bssid)) {

				boolean active = Utils.getBooleanPreference(context, R.string.pref_active, true);

				if (active) {
					String username = Utils.getStringPreference(context, R.string.pref_username, "");
					String password = Utils.getStringPreference(context, R.string.pref_password, "");
					if (username.trim().length() > 0 && password.trim().length() > 0) {
						// If the application is active and we have username and password we launch
						// the login intent
						Intent logIntent = new Intent(context, WISPrLoggerService.class);
						logIntent.setAction("LOG");
						logIntent.putExtra(context.getString(R.string.pref_username), username);
						logIntent.putExtra(context.getString(R.string.pref_password), password);
						logIntent.putExtra(context.getString(R.string.pref_ssid), ssid);
						logIntent.putExtra(context.getString(R.string.pref_bssid), bssid);
						context.startService(logIntent);
					} else {
						Log.v(TAG, "Username & Password not available");
						cleanNotification(context);
					}
				} else {
					Log.v(TAG, "Application inactive");
					cleanNotification(context);
				}
			} else {
				Log.v(TAG, "Not a FON Access Point");
				cleanNotification(context);
			}
		} else if (isDisconnectedIntent(intent)) {
			Log.v(TAG, "Disconnected");
			cleanNotification(context);
		}
	}

	private void cleanNotification(Context context) {
		Log.v(TAG, "Cleaning Notificacion Icon");
		Intent cleaningIntent = new Intent(context, NotificationCleaningService.class);
		cleaningIntent.setAction(NotificationCleaningService.ACTION_CLEAN);
		context.startService(cleaningIntent);
	}

	private boolean isConnectedIntent(Intent intent) {
		NetworkInfo networkInfo = null;
		try {
			networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		} catch (BadParcelableException ignored) {}

		return (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
	}

	private boolean isDisconnectedIntent(Intent intent) {
		boolean res = false;
		NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		// Log.d(TAG, "NetworkInfo:" + networkInfo);

		if (networkInfo != null) {
			State state = networkInfo.getState();
			res = (state.equals(NetworkInfo.State.DISCONNECTING) || state.equals(NetworkInfo.State.DISCONNECTED))
					&& (networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
		} else {
			int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
			// Log.d(TAG, "wifiState:" + wifiState);
			if (wifiState == WifiManager.WIFI_STATE_DISABLED || wifiState == WifiManager.WIFI_STATE_DISABLING) {
				res = true;
			}
		}

		return res;
	}
}