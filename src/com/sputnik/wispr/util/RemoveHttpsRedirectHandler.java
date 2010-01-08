package com.sputnik.wispr.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

public class RemoveHttpsRedirectHandler extends DefaultRedirectHandler {
	private static String TAG = RemoveHttpsRedirectHandler.class.getName();

	@Override
	public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
		String uri = super.getLocationURI(response, context).toString();
		if (uri.startsWith("https")) {
			uri = uri.replaceFirst("https", "http");
			Log.d(TAG, "Removing https from redirec:" + uri);
		}
		URI notSafeUri = null;
		try {
			notSafeUri = new URI(uri);
		} catch (URISyntaxException e) {
			Log.e(TAG, "error change URI", e);
		}
		return notSafeUri;
	}
}