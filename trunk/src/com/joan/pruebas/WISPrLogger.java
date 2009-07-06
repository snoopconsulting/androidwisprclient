package com.joan.pruebas;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.util.Log;

public class WISPrLogger {
	private static final String BLOCKED_URL = "http://wifi.fon.com";

	private static String TAG = WISPrLogger.class.getName();

	public static final String WISPR_TAG_NAME = "WISPAccessGatewayParam";

	public String login(String user, String password) {
		String res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		try {
			String blockedUrlText = this.getUrl(BLOCKED_URL);
			// Log.d(TAG, "Received blocked URL:" + blockedUrlText);
			String WISPrXML = this.getWISPrXML(blockedUrlText);
			if (WISPrXML != null) {
				Log.d(TAG, "XML Found:" + WISPrXML);
				WISPrInfoHandler wisprInfo = new WISPrInfoHandler();
				parseXML(WISPrXML, wisprInfo);

				if (wisprInfo.getMesageType().equals(WISPrConstants.WISPR_MESSAGE_TYPE_INITIAL)
						&& wisprInfo.getResponseCode().equals(WISPrConstants.WISPR_RESPONSE_CODE_NO_ERROR)) {
					res = tryToLogin(user, password, wisprInfo);
				}
			} else {
				Log.d(TAG, "XML NOT FOUND : " + blockedUrlText);
			}
		} catch (Exception e) {
			Log.e(TAG, "Error trying to log", e);
			res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		}

		return res;
	}

	private String tryToLogin(String user, String password, WISPrInfoHandler wisprInfo) throws IOException,
			SAXException, ParserConfigurationException, FactoryConfigurationError {
		String res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		String targetURL = wisprInfo.getLoginURL();
		Map<String, String> data = new HashMap<String, String>();
		data.put("UserName", user);
		data.put("Password", password);

		String response = getUrlByPost(targetURL, data);
		if (response != null) {
			Log.d(TAG, "got response:" + response);
			response = getWISPrXML(response);
			Log.d(TAG, "found xml:" + response);
			WISPrResponseHandler wrh = new WISPrResponseHandler();
			parseXML(response, wrh);
			res = wrh.getResponseCode();
		}

		return res;
	}

	private String getUrl(String url) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			entity.writeTo(baos);
		}

		return baos.toString();
	}

	private String getUrlByPost(String url, Map<String, String> params) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		HttpClient httpclient = new DefaultHttpClient();

		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		if (params != null) {
			for (String paramName : params.keySet()) {
				formparams.add(new BasicNameValuePair(paramName, params.get(paramName)));
			}
		}

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		HttpPost httppost = new HttpPost(url);
		httppost.setEntity(entity);
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity responseEntity = response.getEntity();
		if (responseEntity != null) {
			responseEntity.writeTo(baos);
		}

		return baos.toString();
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

	public void parseXML(String xml, ContentHandler handler) throws SAXException, IOException,
			ParserConfigurationException, FactoryConfigurationError {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		XMLReader xr = sp.getXMLReader();
		xr.setContentHandler(handler);
		xr.parse(new InputSource(new ByteArrayInputStream(xml.getBytes())));
	}
}