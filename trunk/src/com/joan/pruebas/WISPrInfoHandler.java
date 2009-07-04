package com.joan.pruebas;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class WISPrInfoHandler extends DefaultHandler {
	enum Tag {
		WISPAccessGatewayParam, Redirect, AccessProcedure, LoginURL, AbortLoginURL, MessageType, ResponseCode
	}

	private Tag actualTag;

	private String accessProcedure;

	private String loginURL;

	private String abortLoginURL;

	private String mesageType;

	private String responseCode;

	@Override
	public void startElement(String uri, String name, String qName, Attributes atts) {
		actualTag = Tag.valueOf(name.trim());
	}

	@Override
	public void characters(char ch[], int start, int length) {
		String chars = (new String(ch).substring(start, start + length));
		if (actualTag.equals(Tag.AccessProcedure)) {
			accessProcedure = chars;
		} else if (actualTag.equals(Tag.LoginURL)) {
			loginURL = (loginURL == null) ? "" : loginURL;
			loginURL += chars;
		} else if (actualTag.equals(Tag.AbortLoginURL)) {
			abortLoginURL = chars;
		} else if (actualTag.equals(Tag.MessageType)) {
			mesageType = chars;
		} else if (actualTag.equals(Tag.ResponseCode)) {
			responseCode = chars;
		}
	}

	public String getAccessProcedure() {
		return accessProcedure;
	}

	public String getLoginURL() {
		return loginURL;
	}

	public String getAbortLoginURL() {
		return abortLoginURL;
	}

	public String getMesageType() {
		return mesageType;
	}

	public String getResponseCode() {
		return responseCode;
	}
}
