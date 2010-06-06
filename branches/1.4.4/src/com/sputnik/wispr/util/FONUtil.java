package com.sputnik.wispr.util;

public class FONUtil {
	private static final String FON_MAC_PREFIX = "00:18:84";

	public static boolean isSupportedNetwork(String ssid, String bssid) {
		boolean res = false;

		if (ssid != null) {
			res = isFonera(ssid, bssid) || isNeufBox(ssid, bssid) || isBtFonera(ssid, bssid) || isBtHub(ssid, bssid)
					|| isLivedoor(ssid, bssid) || isSBPublicFonera(ssid, bssid);
		}

		return res;
	}

	public static boolean isNeufBox(String ssid, String bssid) {
		return ssid.equalsIgnoreCase("NEUF WIFI FON") || ssid.equalsIgnoreCase("SFR WIFI FON");
	}

	public static boolean isFonera(String ssid, String bssid) {
		return ssid.toUpperCase().startsWith("FON_") && !isLivedoor(ssid, bssid);
	}

	public static boolean isSBPublicFonera(String ssid, String bssid) {
		return ssid.equalsIgnoreCase("FON");
	}

	public static boolean isBtFonera(String ssid, String bssid) {
		boolean res = false;
		if (ssid.equalsIgnoreCase("BTFON")) {
			res = (bssid != null) && bssid.startsWith(FON_MAC_PREFIX);
		}

		return res;
	}

	public static boolean isLivedoor(String ssid, String bssid) {
		boolean res = false;
		if (ssid.equalsIgnoreCase("FON_livedoor")) {
			res = (bssid != null) && !bssid.startsWith(FON_MAC_PREFIX);
		}

		return res;
	}

	public static boolean isBtHub(String ssid, String bssid) {
		boolean res = false;
		if (ssid.equalsIgnoreCase("BTFON")) {
			res = (bssid != null) && !bssid.startsWith(FON_MAC_PREFIX);
		}

		return res;
	}
}
