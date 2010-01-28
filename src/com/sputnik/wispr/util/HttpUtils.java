package com.sputnik.wispr.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

public class HttpUtils {
	static HttpParams defaultHttpParams = new BasicHttpParams();

	static {
		defaultHttpParams.setParameter(CoreProtocolPNames.USER_AGENT, "wispr");
	}

	public static String getUrl(String url) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DefaultHttpClient httpclient = new DefaultHttpClient(defaultHttpParams);
		httpclient.setCookieStore(null);
		HttpGet httpget = new HttpGet(url);
		HttpEntity entity = httpclient.execute(httpget).getEntity();
		if (entity != null) {
			entity.writeTo(baos);
		}

		String result = baos.toString().trim();
		baos.close();

		return result;
	}

	public static String getUrlByPost(String url, Map<String, String> params) throws IOException {
		return getUrlByPost(url, params, null);
	}

	public static String getUrlByPost(String url, Map<String, String> params, Map<String, String> headers)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DefaultHttpClient httpclient = new DefaultHttpClient(defaultHttpParams);
		httpclient.setCookieStore(null);

		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		if (params != null) {
			Set<Entry<String, String>> paramsSet = params.entrySet();
			for (Entry<String, String> entry : paramsSet) {
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}

		UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
		HttpPost httppost = new HttpPost(url);
		httppost.setEntity(postEntity);

		if (headers != null) {
			Set<Entry<String, String>> headersSet = headers.entrySet();
			for (Entry<String, String> entry : headersSet) {
				httppost.setHeader(entry.getKey(), entry.getValue());
			}
		}

		HttpEntity responseEntity = httpclient.execute(httppost).getEntity();
		if (responseEntity != null) {
			responseEntity.writeTo(baos);
		}

		String result = baos.toString().trim();
		baos.close();

		return result;
	}
}