package com.joan.wispr;

public class FONUtil {
	public static boolean isFonNetWork(String ssid, String macAddress) {
		ssid = ssid.toUpperCase();

		return ssid.startsWith("FON_") || ssid.equals("BTFON") || ssid.equals("Neuf WiFi FON");
	}
}
