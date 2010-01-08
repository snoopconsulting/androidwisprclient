package com.sputnik.wispr.logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.sputnik.wispr.util.HttpUtils;
import com.sputnik.wispr.util.WISPrConstants;

// TODO Not finished yet
public class BTFonLogger extends HTTPLogger {
	private static String TAG = BTFonLogger.class.getName();

	public BTFonLogger() {
		targetURL = "https://www.btfon.com/welcome/bt";
	}

	@Override
	public String login(String user, String password) {
		String res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		try {
			String blockedUrlText = HttpUtils.getUrl(BLOCKED_URL);
			if (!blockedUrlText.equals(CONNECTED)) {
				Map<String, String> postParams = new HashMap<String, String>();
				postParams.clear();
				postParams.put("username", "BTFON/" + user);
				postParams.put("password", password);
				String post = HttpUtils.getUrlByPost("https://www.btopenzone.com:8443/ante", postParams);
				Log.d(TAG, "Got:" + post);

				Log.d(TAG, "Verifying if now we have connection");
				blockedUrlText = HttpUtils.getUrl(BLOCKED_URL);
				Log.d(TAG, "Got:" + blockedUrlText);
				if (blockedUrlText.equals(CONNECTED)) {
					res = WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_SUCCEEDED;
				}
			} else {
				res = WISPrConstants.ALREADY_CONNECTED;
			}
		} catch (IOException e) {
			Log.e(TAG, "Error trying to log", e);
			res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		}

		Log.d(TAG, "Result:" + res);

		return res;
	}
}
