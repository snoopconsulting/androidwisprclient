package com.joan.wispr;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.util.Log;

import com.joan.wispr.handler.WISPrInfoHandler;
import com.joan.wispr.handler.WISPrResponseHandler;

public class WISPrLogger {

	private static final String BLOCKED_URL = "http://www.saltando.net/uploads/android.txt";

	private static String TAG = WISPrLogger.class.getName();

	public static final String WISPR_TAG_NAME = "WISPAccessGatewayParam";

	public static final String CONNECTED = "CONNECTED";

	public String login(String user, String password) {
		String res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		try {
			String blockedUrlText = HttpUtils.getUrl(BLOCKED_URL);
			if (!blockedUrlText.equalsIgnoreCase(CONNECTED)) {
				String WISPrXML = this.getWISPrXML(blockedUrlText);
				if (WISPrXML != null) {
					// Log.d(TAG, "XML Found:" + WISPrXML);
					WISPrInfoHandler wisprInfo = new WISPrInfoHandler();
					android.util.Xml.parse(WISPrXML, wisprInfo);

					if (wisprInfo.getMesageType().equals(WISPrConstants.WISPR_MESSAGE_TYPE_INITIAL)
							&& wisprInfo.getResponseCode().equals(WISPrConstants.WISPR_RESPONSE_CODE_NO_ERROR)) {
						res = tryToLogin(user, password, wisprInfo);
					}
				} else {
					// Log.d(TAG, "XML NOT FOUND : " + blockedUrlText);
					res = WISPrConstants.WISPR_NOT_PRESENT;
				}
			} else {
				res = WISPrConstants.ALREADY_CONNECTED;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error trying to log", e);
			res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		}
		Log.d(TAG, "WISPR Login Result: " + res);

		return res;
	}

	private String tryToLogin(String user, String password, WISPrInfoHandler wisprInfo) throws IOException,
			SAXException, ParserConfigurationException, FactoryConfigurationError {
		String res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		String targetURL = wisprInfo.getLoginURL();
		Map<String, String> data = new HashMap<String, String>();
		data.put("UserName", user);
		data.put("Password", password);

		String response = HttpUtils.getUrlByPost(targetURL, data);
		if (response != null) {
			response = getWISPrXML(response);
			WISPrResponseHandler wrh = new WISPrResponseHandler();
			android.util.Xml.parse(response, wrh);
			res = wrh.getResponseCode();
		}

		return res;
	}

	private String getWISPrXML(String source) {
		String res = null;
		int start = source.indexOf("<" + WISPR_TAG_NAME);
		int end = source.indexOf("</" + WISPR_TAG_NAME + ">") + WISPR_TAG_NAME.length() + 3;
		if (start > -1 && end > -1) {
			res = source.substring(start, end);
			res = res.replace("&", "&amp;");
		}

		return res;
	}
}