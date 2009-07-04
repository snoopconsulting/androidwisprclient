package com.joan.pruebas;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WISPrLogger extends Service {
	private static final String BLOCKED_URL = "http://www.hp.com";

	private static String TAG = WISPrLogger.class.getName();

	public static final String WISPR_TAG_NAME = "WISPAccessGatewayParam";

	public WISPrLogger() {

	}

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

	private String tryToLogin(String user, String password, WISPrInfoHandler wisprInfo) throws IOException, SAXException,
			ParserConfigurationException, FactoryConfigurationError {
		String res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		String targetURL = wisprInfo.getLoginURL();
		Map<String, String> data = new HashMap<String, String>();
		data.put("UserName", user);
		data.put("Password", password);

		String response = getUrlByPost(targetURL, data, null);
		if (response != null) {
			response = getWISPrXML(response);
			WISPrResponseHandler wrh = new WISPrResponseHandler();
			parseXML(response, wrh);
			res = wrh.getResponseCode();
		}

		return res;
	}

	private String getUrl(String url) throws IOException {
		InputStream is = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			URL u = new URL(url);
			URLConnection connection = u.openConnection();
			connection.setUseCaches(false);
			is = connection.getInputStream();
			byte[] buf = new byte[4 * 1024]; // 4K buffer
			int bytesRead;
			while ((bytesRead = is.read(buf)) != -1) {
				baos.write(buf, 0, bytesRead);
			}
		} finally {
			if (is != null) {
				is.close();
			}
		}

		return new String(baos.toByteArray());
	}

	private String getUrlByPost(String url, Map<String, String> params, Map<String, String> props) throws IOException {
		// Prepare the data
		String data = "";
		if (params != null) {
			for (String paramName : params.keySet()) {
				data += URLEncoder.encode(paramName, "UTF-8") + "=" + URLEncoder.encode(params.get(paramName), "UTF-8") + "&";
			}
		}

		// Send data
		URL u = new URL(url);
		URLConnection conn = u.openConnection();
		if (props != null) {
			for (String propName : props.keySet()) {
				conn.setRequestProperty(propName, props.get(propName));
			}
		}

		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();

		// Get the response
		InputStream is = conn.getInputStream();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[4 * 1024]; // 4K buffer
		int bytesRead;
		while ((bytesRead = is.read(buf)) != -1) {
			baos.write(buf, 0, bytesRead);
		}

		return new String(baos.toByteArray());
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

	public void parseXML(String xml, ContentHandler handler) throws SAXException, IOException, ParserConfigurationException,
			FactoryConfigurationError {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser sp = spf.newSAXParser();
		XMLReader xr = sp.getXMLReader();
		xr.setContentHandler(handler);
		xr.parse(new InputSource(new ByteArrayInputStream(xml.getBytes())));
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}