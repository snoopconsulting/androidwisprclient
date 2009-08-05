package com.sputnik.wispr;

import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
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

		NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		if (networkInfo != null) {
			Log.d(TAG, "NetworkInfo:" + networkInfo);
			if (networkInfo.isConnected() && networkInfo.getTypeName().equals("WIFI")) {
				WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo connectionInfo = wm.getConnectionInfo();
				SupplicantState supplicantState = connectionInfo.getSupplicantState();
				String ssid = connectionInfo.getSSID();
				String bssid = connectionInfo.getBSSID();
				Log.d(TAG, "Conected. SSID:" + ssid + ", bssid:" + bssid + ", supplicantState:" + supplicantState);
				if (FONUtil.isFonNetWork(ssid, bssid)) {

					mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
					boolean active = mPreferences.getBoolean(context.getString(R.string.pref_active), false);
					if (active) {
						String userName = mPreferences.getString(context.getString(R.string.pref_username), "");
						String password = mPreferences.getString(context.getString(R.string.pref_password), "");
						Intent logIntent = new Intent(context, WISPrLoggerService.class);
						logIntent.setAction("LOG");
						logIntent.putExtra(context.getString(R.string.pref_username), userName);
						logIntent.putExtra(context.getString(R.string.pref_password), password);
						logIntent.putExtra(context.getString(R.string.pref_ssid), ssid);
						context.startService(logIntent);
					}
				}
			}
		}
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