package com.sputnik.wispr;

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

import com.sputnik.wispr.util.FONUtil;

public class NetworkScanReceiver extends BroadcastReceiver {
	private static String TAG = NetworkScanReceiver.class.getName();

	private static Date lastCalled;

	private static final int MIN_PERIOD_BTW_CALLS = 10;// 10 Seconds

	private static SharedPreferences prefs;

	@Override
	public void onReceive(Context context, Intent intent) {
		Date now = new Date();

		Log.d(TAG, "Action Received: " + intent.getAction() + " From intent: " + intent);

		if (lastCalled == null || (now.getTime() - lastCalled.getTime() > MIN_PERIOD_BTW_CALLS * 1000)) {
			lastCalled = now;
			initPrefs(context);
			boolean autoConnectEnabled = prefs.getBoolean(context.getString(R.string.pref_connectionAutoEnable), false);

			if (autoConnectEnabled) {
				WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo connectionInfo = wm.getConnectionInfo();

				Log.d(TAG, "connectionInfo.getSupplicantState():" + connectionInfo.getSupplicantState());

				if (connectionInfo.getSupplicantState().equals(SupplicantState.SCANNING)) {
					ScanResult fonScanResult = getFonNetwork(wm.getScanResults());
					if (fonScanResult != null) {
						Log.d(TAG, "Scan result found:" + fonScanResult);
						WifiConfiguration fonNetwork = lookupConfigurationByScanResult(wm.getConfiguredNetworks(),
								fonScanResult);
						Log.d(TAG, "FON Network found:" + fonNetwork);
						if (fonNetwork == null) {
							fonNetwork = new WifiConfiguration();
							fonNetwork.BSSID = fonScanResult.BSSID;
							fonNetwork.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
							fonNetwork.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
							fonNetwork.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

							fonNetwork.networkId = wm.addNetwork(fonNetwork);
							wm.saveConfiguration();
							fonNetwork.SSID = "\"" + fonScanResult.SSID + "\"";
							int updateNetworkResult = wm.updateNetwork(fonNetwork);
							Log.d(TAG, "New FON Network:" + updateNetworkResult + "::" + fonNetwork);
						}

						wm.enableNetwork(fonNetwork.networkId, true);
						lastCalled = new Date();
						Log.d(TAG, "Trying to connect");
					}
				}
			}
		} else {
			Log.d(TAG, "Events to close, ignoring.");
		}
	}

	private void initPrefs(Context context) {
		if (prefs == null) {
			prefs = PreferenceManager.getDefaultSharedPreferences(context);
		}
	}

	private WifiConfiguration lookupConfigurationByScanResult(List<WifiConfiguration> configuredNetworks,
			ScanResult scanResult) {
		boolean found = false;
		WifiConfiguration wifiConfiguration = null;
		Iterator<WifiConfiguration> it = configuredNetworks.iterator();
		while (!found && it.hasNext()) {
			wifiConfiguration = it.next();
			Log.d(TAG, wifiConfiguration.SSID + " equals " + "\"" + scanResult.SSID + "\"");
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

	// Comparator to order Scanresults from high signal level to low
	class ScanResultComparator implements Comparator<ScanResult> {
		public int compare(ScanResult scanResult1, ScanResult scanResult2) {
			return scanResult2.level - scanResult1.level;
		}
	}
}