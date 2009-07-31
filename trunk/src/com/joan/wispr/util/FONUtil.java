package com.joan.wispr.util;

public class FONUtil {
	public static boolean isFonNetWork(String ssid, String bssid) {
		boolean res = false;
		if (ssid != null) {
			ssid = ssid.toUpperCase();
			res = ssid.startsWith("FON_") || ssid.equals("NEUF WIFI FON");
			if (!res && ssid.equals("BTFON")) {
				res = bssid.startsWith("00:18:84");
			}
		}
		return res;
	}
}
