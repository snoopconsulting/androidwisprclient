package com.oakley.fon.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.oakley.fon.R;
import com.oakley.fon.logger.WebLogger;

public class FONUtils {
	private static final String FON_MAC_PREFIX = "00:18:84";

	private static String TAG = FONUtils.class.getName();

	private static Set<String> validSuffix = new HashSet<String>();
	static {
		validSuffix.add(".btopenzone.com");
		validSuffix.add(".fon.com");
		validSuffix.add(".btfon.com");
		validSuffix.add(".neuf.fr");
		validSuffix.add(".sfr.fr");
		validSuffix.add(".livedoor.com");
	}

	public static boolean isSupportedNetwork(String ssid, String bssid) {
		boolean res = false;

		if (ssid != null) {
			res = isFonNetwork(ssid, bssid) || isNeufBox(ssid, bssid) || isBtHub(ssid, bssid)
					|| isLivedoor(ssid, bssid);
		}

		return res;
	}

	public static boolean isFonNetwork(String ssid, String bssid) {
		boolean res = false;

		if (ssid != null) {
			res = isFonera(ssid, bssid) || isBtFonera(ssid, bssid) || isSBPublicFonera(ssid, bssid)
					|| isOIWifi(ssid, bssid);
		}

		return res;
	}

	public static boolean isNeufBox(String ssid, String bssid) {
		ssid = FONUtils.cleanSSID(ssid);
		return ssid != null && (ssid.equalsIgnoreCase("NEUF WIFI FON") || ssid.equalsIgnoreCase("SFR WIFI FON"));
	}

	public static boolean isFonera(String ssid, String bssid) {
		ssid = FONUtils.cleanSSID(ssid);
		return ssid != null && ssid.toUpperCase().startsWith("FON_") && !isLivedoor(ssid, bssid);
	}

	public static boolean isOIWifi(String ssid, String bssid) {
		ssid = FONUtils.cleanSSID(ssid);
		return ssid != null && (ssid.toUpperCase().startsWith("OI_WIFI_FON") || ssid.equalsIgnoreCase("OI WIFI FON"));
	}

	public static boolean isSBPublicFonera(String ssid, String bssid) {
		ssid = FONUtils.cleanSSID(ssid);
		return ssid != null && ssid.equalsIgnoreCase("FON");
	}

	public static boolean isBtFonera(String ssid, String bssid) {
		ssid = FONUtils.cleanSSID(ssid);
		return ssid != null && bssid != null && ssid.equalsIgnoreCase("BTFON") && bssid.startsWith(FON_MAC_PREFIX);
	}

	public static boolean isLivedoor(String ssid, String bssid) {
		ssid = FONUtils.cleanSSID(ssid);
		return ssid != null && bssid != null && ssid.equalsIgnoreCase("FON_livedoor")
				&& !bssid.startsWith(FON_MAC_PREFIX);
	}

	public static boolean isBtHub(String ssid, String bssid) {
		ssid = FONUtils.cleanSSID(ssid);
		boolean result = false;
		if (ssid != null) {
			if (bssid != null) {
				result = ssid.equalsIgnoreCase("BTFON") && !bssid.startsWith(FON_MAC_PREFIX);
			}
			if (!result) {
				result = ssid.equalsIgnoreCase("BTOpenzone-H");
			}
			if (!result) {
				result = ssid.equalsIgnoreCase("“BTWiFi-with-FON");
			}
		}

		return result;
	}

	public static boolean haveConnection() throws IOException {
		String blockedUrlText = HttpUtils.getUrl(WebLogger.BLOCKED_URL).getContent();
		return blockedUrlText.equals(WebLogger.CONNECTED);
	}

	public static String cleanSSID(String SSID) {
		String res = null;
		if (SSID != null) {
			res = SSID.replace("\"", "");
		}

		return res;
	}

	public static boolean isSafeUrl(String url) {
		boolean res = false;
		try {
			if (url == null) {
				res = true;
			} else {
				res = isSafeUrl(new URL(url));
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, e.getMessage());
			res = false;
		}

		return res;
	}

	public static boolean isSafeUrl(URL url) {
		boolean res = false;
		if (url.getProtocol().equalsIgnoreCase("https")) {
			Iterator<String> iterator = validSuffix.iterator();
			while (iterator.hasNext() && res == false) {
				String validSuffix = iterator.next();
				res = url.getHost().toLowerCase().endsWith(validSuffix);
			}
		}

		return res;
	}

	public static boolean areCredentialsConfigured(Context context) {
		String username = Utils.getStringPreference(context, R.string.pref_username, "");
		String password = Utils.getStringPreference(context, R.string.pref_password, "");

		return (username.trim().length() > 0 && password.trim().length() > 0);
	}

	public static void cleanNetworks(Context context) {
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> configuredNetworks = wm.getConfiguredNetworks();

		for (WifiConfiguration wifiConfiguration : configuredNetworks) {
			if (wifiConfiguration.status != WifiConfiguration.Status.CURRENT
					&& FONUtils.isSupportedNetwork(wifiConfiguration.SSID, wifiConfiguration.BSSID)) {
				boolean removeNetwork = wm.removeNetwork(wifiConfiguration.networkId);
				Log.v(TAG, "Removed network " + wifiConfiguration.SSID + ":" + wifiConfiguration.BSSID + "->"
						+ removeNetwork);
			}
		}
	}
}