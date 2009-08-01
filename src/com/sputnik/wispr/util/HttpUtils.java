package com.sputnik.wispr.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class HttpUtils {
	public static String getUrl(String url) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			entity.writeTo(baos);
		}

		return baos.toString().trim();
	}

	public static String getUrlByPost(String url, Map<String, String> params) throws IOException {
		return getUrlByPost(url, params, null);
	}

	public static String getUrlByPost(String url, Map<String, String> params, Map<String, String> headers)
			throws IOException {
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

		if (headers != null) {
			Set<Entry<String, String>> headersSet = headers.entrySet();
			for (Entry<String, String> entry : headersSet) {
				httppost.setHeader(entry.getKey(), entry.getValue());
			}
		}

		HttpResponse response = httpclient.execute(httppost);
		HttpEntity responseEntity = response.getEntity();
		if (responseEntity != null) {
			responseEntity.writeTo(baos);
		}

		return baos.toString();
	}
}
