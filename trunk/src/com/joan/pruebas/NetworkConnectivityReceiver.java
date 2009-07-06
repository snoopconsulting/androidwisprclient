package com.joan.pruebas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetworkConnectivityReceiver extends BroadcastReceiver {
	private static String TAG = NetworkConnectivityReceiver.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d(TAG, "Action Received: " + action + " From intent: " + intent);

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
					SharedPreferences preferences = context.getSharedPreferences("PruebaJoan", Context.MODE_PRIVATE);
					String userName = preferences.getString("username", "");
					String password = preferences.getString("password", "");
					Log.d(TAG, "Conectamos!!!! ahora habría que intentar hacer el WISPr");
					WISPrLogger wl = new WISPrLogger();
					String loginResult = wl.login(userName, password);
					Log.d(TAG, "LoginResult:" + loginResult);
					notifyConnectionResult(loginResult);
				}
			}
		}
	}

	private boolean isFonNetWork(String ssid, String macAddress) {
		ssid = ssid.toUpperCase();

		return ssid.startsWith("FON_") || ssid.equals("BTFON") || ssid.equals("Neuf WiFi FON");
	}

	private void notifyConnectionResult(String result) {
		int icon = R.drawable.icon;
		CharSequence text = "";
		if (result.equals(WISPrConstants.WISPR_RESPONSE_CODE_NO_ERROR)) {

		}
	}
}