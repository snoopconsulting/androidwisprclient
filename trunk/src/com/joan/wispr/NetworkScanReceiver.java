package com.joan.wispr;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class NetworkScanReceiver extends BroadcastReceiver {
	private static String TAG = NetworkScanReceiver.class.getName();

	private static Date lastCalled;

	private static final int MIN_PERIOD_BTW_CALLS = 5;// 5 Seconds

	@Override
	public void onReceive(Context context, Intent intent) {
		Date now = new Date();
		if (lastCalled == null || (now.getTime() - lastCalled.getTime() > MIN_PERIOD_BTW_CALLS * 1000)) {
			lastCalled = now;
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			boolean autoConnectEnabled = prefs.getBoolean(context.getString(R.string.pref_connectionAutoEnable), false);

			if (autoConnectEnabled) {
				String action = intent.getAction();
				Log.d(TAG, "Action Received: " + action + " From intent: " + intent);

				WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo connectionInfo = wm.getConnectionInfo();

				if (!connectionInfo.getSupplicantState().equals(SupplicantState.COMPLETED)) {
					ScanResult fonScanResult = getFonNetwork(wm.getScanResults());
					if (fonScanResult != null) {
						Log.d(TAG, "Scan result found:" + fonScanResult);
						WifiConfiguration fonNetwork = lookupConfigurationByScanResult(wm.getConfiguredNetworks(),
								fonScanResult);
						Log.d(TAG, "FON Network found:" + fonNetwork);
						if (fonNetwork == null) {
							fonNetwork = new WifiConfiguration();
							fonNetwork.BSSID = fonScanResult.BSSID;
							// fonNetwork.SSID = "\"" + fonScanResult.SSID + "\"";
							fonNetwork.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
							fonNetwork.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
							fonNetwork.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

							fonNetwork.networkId = wm.addNetwork(fonNetwork);
							wm.saveConfiguration();
							Log.d(TAG, "New FON Network:" + fonNetwork);
						}

						wm.enableNetwork(fonNetwork.networkId, true);
					}
				}
			} else {
				Log.d(TAG, "Events to close, ignoring.");
			}
		}
	}

	private WifiConfiguration lookupConfigurationByScanResult(List<WifiConfiguration> configuredNetworks,
			ScanResult scanResult) {
		boolean found = false;
		WifiConfiguration wifiConfiguration = null;
		Iterator<WifiConfiguration> it = configuredNetworks.iterator();
		while (!found && it.hasNext()) {
			wifiConfiguration = it.next();
			Log.d(TAG, wifiConfiguration.SSID + " equals " + scanResult.SSID);
			if (wifiConfiguration.SSID != null) {
				found = wifiConfiguration.SSID.equals("\"" + scanResult.SSID + "\"");
			}
		}

		if (!found) {
			wifiConfiguration = null;
		}

		return wifiConfiguration;
	}

	private ScanResult getFonNetwork(List<ScanResult> scanResults) {
		ScanResult scanResult = null;
		boolean found = false;

		Iterator<ScanResult> it = scanResults.iterator();
		while (!found && it.hasNext()) {
			scanResult = it.next();
			found = FONUtil.isFonNetWork(scanResult.SSID, scanResult.BSSID);
		}
		if (!found) {
			scanResult = null;
		}

		return scanResult;
	}

	class ScanResultComparator implements Comparator<ScanResult> {
		public int compare(ScanResult scanResult1, ScanResult scanResult2) {
			return scanResult2.level - scanResult1.level;
		}
	}
}