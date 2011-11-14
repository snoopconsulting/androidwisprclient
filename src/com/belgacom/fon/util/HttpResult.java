package com.belgacom.fon.util;

import org.apache.http.message.BasicHttpResponse;

public class HttpResult {
	private String content;

	private BasicHttpResponse response;

	private String targetHost;

	public HttpResult(String content, BasicHttpResponse response, String targetHost) {
		this.content = content;
		this.response = response;
		this.targetHost = targetHost;
	}

	public String getContent() {
		return content;
	}

	public BasicHttpResponse getResponse() {
		return response;
	}

	public String getTargetHost() {
		return targetHost;
	}

}
