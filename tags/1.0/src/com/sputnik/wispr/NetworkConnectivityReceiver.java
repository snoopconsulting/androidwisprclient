package com.sputnik.wispr;

import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sputnik.wispr.util.FONUtil;

public class NetworkConnectivityReceiver extends BroadcastReceiver {
	private static String TAG = NetworkConnectivityReceiver.class.getName();

	private SharedPreferences mPreferences;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d(TAG, "Action Received: " + action + " From intent: " + intent);

		if (isConnectedIntent(intent)) {
			WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo connectionInfo = wm.getConnectionInfo();
			String ssid = connectionInfo.getSSID();
			String bssid = connectionInfo.getBSSID();
			Log.d(TAG, "Conected. SSID:" + ssid + ", bssid:" + bssid + ", supplicantState:"
					+ connectionInfo.getSupplicantState());
			if (FONUtil.isFonNetWork(ssid, bssid)) {
				initPreferences(context);
				boolean active = mPreferences.getBoolean(context.getString(R.string.pref_active), false);
				if (active) {
					String username = mPreferences.getString(context.getString(R.string.pref_username), "");
					String password = mPreferences.getString(context.getString(R.string.pref_password), "");
					if (username.length() > 0 && password.length() > 0) {
						Intent logIntent = new Intent(context, WISPrLoggerService.class);
						logIntent.setAction("LOG");
						logIntent.putExtra(context.getString(R.string.pref_username), username);
						logIntent.putExtra(context.getString(R.string.pref_password), password);
						logIntent.putExtra(context.getString(R.string.pref_ssid), ssid);
						context.startService(logIntent);
					}
				}
			} else {
				cleanNotification(context);
			}
		} else if (isDisconnectedIntent(intent)) {
			Log.d(TAG, "Disconnected");
			cleanNotification(context);
		}
	}

	private void cleanNotification(Context context) {
		initPreferences(context);
		boolean active = mPreferences.getBoolean(context.getString(R.string.pref_active), false);
		if (active) {
			Intent cleaningIntent = new Intent(context, NotificationCleaningService.class);
			cleaningIntent.setAction("CLEAN");
			context.startService(cleaningIntent);
		}
	}

	private void initPreferences(Context context) {
		if (mPreferences == null) {
			mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		}
	}

	private boolean isConnectedIntent(Intent intent) {
		NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		Log.d(TAG, "NetworkInfo:" + networkInfo);
		return (networkInfo != null && networkInfo.isConnected() && networkInfo.getTypeName().equals("WIFI"));
	}

	private boolean isDisconnectedIntent(Intent intent) {
		boolean res = false;
		NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

		Log.d(TAG, "NetworkInfo:" + networkInfo);
		if (networkInfo != null) {
			State state = networkInfo.getState();
			res = (state.equals(NetworkInfo.State.DISCONNECTING) || state.equals(NetworkInfo.State.DISCONNECTED))
					&& networkInfo.getTypeName().equals("WIFI");
		} else {
			int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
			if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
				res = true;
			}
		}
		return res;
	}

	protected void logIntent(Intent intent) {
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