package com.sputnik.wispr.util;

public class FONUtil {
	public static boolean isSupportedNetwork(String ssid, String bssid) {
		boolean res = isFonera(ssid, bssid) || isNeufBox(ssid, bssid) || isBtFonera(ssid, bssid)
				|| isBtHub(ssid, bssid) || isLivedoor(ssid, bssid);

		return res;
	}

	public static boolean isNeufBox(String ssid, String bssid) {
		return ssid.equalsIgnoreCase("NEUF WIFI FON");
	}

	public static boolean isFonera(String ssid, String bssid) {
		return ssid.toUpperCase().startsWith("FON_") && !isLivedoor(ssid, bssid);
	}

	public static boolean isBtFonera(String ssid, String bssid) {
		boolean res = false;
		if (ssid.equalsIgnoreCase("BTFON")) {
			res = (bssid != null) && bssid.startsWith("00:18:84");
		}

		return res;
	}

	public static boolean isLivedoor(String ssid, String bssid) {
		return ssid.equalsIgnoreCase("FON_livedoor");
	}

	public static boolean isBtHub(String ssid, String bssid) {
		boolean res = false;
		if (ssid.equalsIgnoreCase("BTFON")) {
			res = !bssid.startsWith("00:18:84");
		}

		return res;
	}
}
