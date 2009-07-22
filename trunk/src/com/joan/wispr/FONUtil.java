package com.joan.wispr;

public class FONUtil {
	public static boolean isFonNetWork(String ssid, String macAddress) {
		boolean res = false;
		if (ssid != null) {
			ssid = ssid.toUpperCase();
			res = ssid.startsWith("FON_") || ssid.equals("BTFON") || ssid.equals("Neuf WiFi FON");
		}
		return res;
	}
}
