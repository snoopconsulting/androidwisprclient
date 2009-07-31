package com.joan.wispr.logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.joan.wispr.util.HttpUtils;
import com.joan.wispr.util.WISPrConstants;

public class BTFonLogger extends HTTPLogger {

	@Override
	public String login(String user, String password) {
		String res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		try {
			String blockedUrlText = HttpUtils.getUrl(BLOCKED_URL);
			if (!blockedUrlText.equals(CONNECTED)) {
				Map<String, String> postParams = new HashMap<String, String>();
				postParams.put("partnerNetwork", "fon");
				postParams.put("xhtmlLogon", "https://www.btopenzone.com:8443/ante");
				postParams.put(userParam, user);
				postParams.put(passwordParam, password);

				HttpUtils.getUrlByPost("https://www.btfon.com/welcome/bt", postParams);
			} else {
				res = WISPrConstants.ALREADY_CONNECTED;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}
}
