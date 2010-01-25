package com.sputnik.wispr.logger;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.sputnik.wispr.util.HttpUtils;
import com.sputnik.wispr.util.WISPrConstants;

public class LivedoorLogger extends HTTPLogger {
	private static String TAG = LivedoorLogger.class.getName();

	private static final String NETWORK_SUFIX = "@fon";

	public LivedoorLogger() {
		targetURL = "https://vauth.lw.livedoor.com/fauth/index";
	}

	@Override
	public String login(String user, String password) {
		String res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		try {
			String blockedUrlText = HttpUtils.getUrl(BLOCKED_URL);
			if (!blockedUrlText.equals(CONNECTED)) {
				Map<String, String> loginParams = new HashMap<String, String>();
				loginParams.put("sn", "009");
				loginParams.put("original_url", BLOCKED_URL);
				loginParams.put("name", user + NETWORK_SUFIX);
				loginParams.put("password", password);
				// Log.d(TAG, "loginParams:" + loginParams);

				HttpUtils.getUrlByPost(targetURL, loginParams);
				blockedUrlText = HttpUtils.getUrl(BLOCKED_URL);

				// Log.d(TAG, "Got:" + blockedUrlText);
				if (blockedUrlText.equals(CONNECTED)) {
					res = WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_SUCCEEDED;
				}
			} else {
				res = WISPrConstants.ALREADY_CONNECTED;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error trying to log", e);
			res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		}

		return res;
	}

}
