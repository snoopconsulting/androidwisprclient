package com.oakley.fon.logger;

public interface WebLogger {
	public static final String CONNECTED = "CONNECTED";

	public static final String BLOCKED_URL = "http://cm.fon.mobi/android.txt";

	public LoggerResult login(String user, String password);
}
