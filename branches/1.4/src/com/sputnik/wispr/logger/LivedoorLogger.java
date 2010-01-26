package com.sputnik.wispr.logger;

import java.util.HashMap;
import java.util.Map;

public class LivedoorLogger extends SimpleHTTPLogger {
	protected static String TAG = LivedoorLogger.class.getName();

	private static final String NETWORK_SUFIX = "@fon";

	public LivedoorLogger() {
		super("https://vauth.lw.livedoor.com/fauth/index");
	}

	@Override
	public Map<String, String> getPostParameters(String user, String password) {
		Map<String, String> loginParams = new HashMap<String, String>();
		loginParams.put("sn", "009");
		loginParams.put("original_url", BLOCKED_URL);
		loginParams.put("name", user + NETWORK_SUFIX);
		loginParams.put("password", password);

		return loginParams;
	}
}