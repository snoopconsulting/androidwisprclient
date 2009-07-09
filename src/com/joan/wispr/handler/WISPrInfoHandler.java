package com.joan.wispr.handler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class WISPrInfoHandler extends DefaultHandler {
	enum Tag {
		WISPAccessGatewayParam, Redirect, AccessProcedure, LoginURL, AbortLoginURL, MessageType, ResponseCode
	}

	private Tag actualTag;

	private String accessProcedure = "";

	private String loginURL = "";

	private String abortLoginURL = "";

	private String mesageType = "";

	private String responseCode = "";

	@Override
	public void startElement(String uri, String name, String qName, Attributes atts) {
		actualTag = Tag.valueOf(name.trim());
	}

	@Override
	public void characters(char ch[], int start, int length) {
		String chars = (new String(ch).substring(start, start + length));
		if (actualTag.equals(Tag.AccessProcedure)) {
			accessProcedure += chars;
		} else if (actualTag.equals(Tag.LoginURL)) {
			loginURL += chars;
		} else if (actualTag.equals(Tag.AbortLoginURL)) {
			abortLoginURL += chars;
		} else if (actualTag.equals(Tag.MessageType)) {
			mesageType += chars;
		} else if (actualTag.equals(Tag.ResponseCode)) {
			responseCode += chars;
		}
	}

	public String getAccessProcedure() {
		return accessProcedure.trim();
	}

	public String getLoginURL() {
		return loginURL.trim();
	}

	public String getAbortLoginURL() {
		return abortLoginURL.trim();
	}

	public String getMesageType() {
		return mesageType.trim();
	}

	public String getResponseCode() {
		return responseCode.trim();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("WISPrInfoHandler{");
		sb.append("accessProcedure: ").append(accessProcedure).append(", ");
		sb.append("loginURL: ").append(loginURL).append(", ");
		sb.append("abortLoginURL: ").append(abortLoginURL).append(", ");
		sb.append("mesageType: ").append(mesageType).append(", ");
		sb.append("responseCode: ").append(responseCode);
		sb.append("}");

		return sb.toString();
	}
}
