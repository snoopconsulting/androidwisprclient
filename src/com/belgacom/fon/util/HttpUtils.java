package com.belgacom.fon.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtils {
	private static final int DEFAULT_MAX_RETRIES = 3;

	private static String TAG = HttpUtils.class.getName();

	private static final String UTF8 = "UTF-8";

	private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";

	private static final String ENCODING_GZIP = "gzip";

	private static HttpParams defaultHttpParams = new BasicHttpParams();

	static {
		HttpProtocolParams.setUserAgent(defaultHttpParams, "FONAccess; wispr; (Linux; U; Android)");
	}

	public static HttpResult getUrl(String url) throws IOException {
		return getUrl(url, DEFAULT_MAX_RETRIES);
	}

	/**
	 * Request an URL using GET protocol. retrying if necesary.
	 * 
	 * @param url URL to Request
	 * @param maxRetries max number of retries
	 * @return Response Object containing response content, repsonse code, and targethost (final
	 *         target after redirections)
	 * @throws IOException
	 */
	public static HttpResult getUrl(String url, int maxRetries) throws IOException {
		String result = null;
		int retries = 0;
		HttpContext localContext = new BasicHttpContext();
		DefaultHttpClient httpclient = getHttpClient();

		HttpGet httpget = new HttpGet(url);
		while (retries <= maxRetries && result == null) {
			try {
				retries++;
				HttpEntity entity = httpclient.execute(httpget, localContext).getEntity();

				if (entity != null) {
					result = EntityUtils.toString(entity).trim();
				}
			} catch (SocketException se) {
				if (retries > maxRetries) {
					throw se;
				} else {
					Log.v(TAG, "SocketException, retrying " + retries);
				}
			}
		}

		return new HttpResult(result, (BasicHttpResponse) localContext.getAttribute("http.response"),
				((HttpHost) localContext.getAttribute("http.target_host")).toURI());
	}

	/**
	 * Request an URL using POST protocol. retrying if necesary.
	 * 
	 * @param url URL to sent the request to.
	 * @param params Maps of POST params where the key is the name of the param and the value is the
	 *        value of the param
	 * @param maxRetries max number of retries
	 * @return Response Object containing response content, repsonse code, and targethost (final
	 *         target after redirections)
	 * @throws IOException
	 */
	public static HttpResult getUrlByPost(String url, Map<String, String> params, int maxRetries) throws IOException {
		return getUrlByPost(url, params, null, maxRetries);
	}

	/**
	 * Request an URL using POST protocol. retrying if necesary.
	 * 
	 * @param url URL to sent the request to.
	 * @param params Maps of POST params where the key is the name of the param and the value is the
	 *        value of the param
	 * @return Response Object containing response content, repsonse code, and targethost (final
	 *         target after redirections)
	 * @throws IOException
	 */
	public static HttpResult getUrlByPost(String url, Map<String, String> params) throws IOException {
		return getUrlByPost(url, params, DEFAULT_MAX_RETRIES);
	}

	/**
	 * @param url URL to sent the request to.
	 * @param params Map of POST params where the key is the name of the param and the value is the
	 *        value of the param
	 * @param headers Map of HEADER params where the key is the name of the param and the value is
	 *        the value of the param
	 * @param maxRetries max number of retries
	 * @return Response Object containing response content, repsonse code, and targethost (final
	 *         target after redirections)
	 * @throws IOException
	 */
	public static HttpResult getUrlByPost(String url, Map<String, String> params, Map<String, String> headers,
			int maxRetries) throws IOException {
		String result = null;
		int retries = 0;
		HttpContext localContext = new BasicHttpContext();
		DefaultHttpClient httpclient = getHttpClient();

		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		if (params != null) {
			Set<Entry<String, String>> paramsSet = params.entrySet();
			for (Entry<String, String> entry : paramsSet) {
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}

		UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(formParams, UTF8);
		HttpPost httppost = new HttpPost(url);
		httppost.setEntity(postEntity);

		if (headers != null) {
			Set<Entry<String, String>> headersSet = headers.entrySet();
			for (Entry<String, String> entry : headersSet) {
				httppost.setHeader(entry.getKey(), entry.getValue());
			}
		}

		while (retries < maxRetries && result == null) {
			try {
				retries++;
				HttpEntity responseEntity = httpclient.execute(httppost, localContext).getEntity();
				if (responseEntity != null) {
					result = EntityUtils.toString(responseEntity).trim();
				}
			} catch (SocketException se) {
				if (retries > maxRetries) {
					throw se;
				} else {
					Log.v(TAG, "SocketException, retrying " + retries, se);
				}
			}
		}

		HttpHost attribute = (HttpHost) localContext.getAttribute("http.target_host");
		String targetHost = null;
		if (attribute != null) {
			targetHost = attribute.toURI();
		}

		return new HttpResult(result, (BasicHttpResponse) localContext.getAttribute("http.response"), targetHost);
	}

	/**
	 * Parses html souce code to find meta-refresh parameter
	 * 
	 * @param html html to parse
	 * @return <code>url</code> value of the meta-refresh header
	 */
	public static String getMetaRefresh(String html) {
		String meta = null;
		int start = html.toLowerCase().indexOf("<meta http-equiv=\"refresh\" content=\"");
		if (start > -1) {
			start += 36;

			int end = html.indexOf('"', start);
			if (end > -1) {
				meta = html.substring(start, end);
				start = meta.toLowerCase().indexOf("url=");
				if (start > -1) {
					start += 4;
					meta = new String(meta.substring(start));
				}
			}
		}

		return meta;
	}

	/**
	 * Generates and initializes {@link HttpClient} with support for gzip enconding
	 * 
	 * @return httpclient
	 */
	private static DefaultHttpClient getHttpClient() {
		DefaultHttpClient httpclient = new DefaultHttpClient(defaultHttpParams);

		httpclient.addRequestInterceptor(new HttpRequestInterceptor() {
			public void process(HttpRequest request, HttpContext context) {
				if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
					request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
				}
			}
		});

		httpclient.addResponseInterceptor(new HttpResponseInterceptor() {
			public void process(HttpResponse response, HttpContext context) {
				// Inflate any responses compressed with gzip
				final HttpEntity entity = response.getEntity();
				final Header encoding = entity.getContentEncoding();
				if (encoding != null) {
					for (HeaderElement element : encoding.getElements()) {
						if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
							// Log.d(TAG, "Decompresing GZIP Response");
							response.setEntity(new InflatingEntity(response.getEntity()));
							break;
						}
					}
				}
			}
		});

		httpclient.setCookieStore(null);

		return httpclient;
	}

	/**
	 * Simple {@link HttpEntityWrapper} that inflates the wrapped {@link HttpEntity} by passing it
	 * through {@link GZIPInputStream}.
	 */
	private static class InflatingEntity extends HttpEntityWrapper {
		public InflatingEntity(HttpEntity wrapped) {
			super(wrapped);
		}

		@Override
		public InputStream getContent() throws IOException {
			return new GZIPInputStream(wrappedEntity.getContent());
		}

		@Override
		public long getContentLength() {
			return -1;
		}
	}
}