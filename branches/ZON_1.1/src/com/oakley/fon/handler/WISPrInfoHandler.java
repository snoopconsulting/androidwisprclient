package com.oakley.fon.handler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class WISPrInfoHandler extends DefaultHandler {
	enum Tag {
		wispaccessgatewayparam, redirect, accessprocedure, loginurl, abortloginurl, messagetype, responsecode, accesslocation, locationname
	}

	private Tag actualTag;

	private StringBuilder accessProcedure = new StringBuilder();

	private StringBuilder accessLocation = new StringBuilder();

	private StringBuilder loginURL = new StringBuilder();

	private StringBuilder abortLoginURL = new StringBuilder();

	private StringBuilder messageType = new StringBuilder();

	private StringBuilder responseCode = new StringBuilder();

	private StringBuilder locationName = new StringBuilder();

	@Override
	public void startElement(String uri, String name, String qName, Attributes atts) {
		actualTag = Tag.valueOf(name.trim().toLowerCase());
	}

	@Override
	public void characters(char ch[], int start, int length) {
		String chars = (new String(ch).substring(start, start + length));
		if (actualTag.equals(Tag.accessprocedure)) {
			accessProcedure.append(chars);
		} else if (actualTag.equals(Tag.loginurl)) {
			loginURL.append(chars);
		} else if (actualTag.equals(Tag.abortloginurl)) {
			abortLoginURL.append(chars);
		} else if (actualTag.equals(Tag.messagetype)) {
			messageType.append(chars);
		} else if (actualTag.equals(Tag.responsecode)) {
			responseCode.append(chars);
		} else if (actualTag.equals(Tag.accesslocation)) {
			accessLocation.append(chars);
		} else if (actualTag.equals(Tag.locationname)) {
			locationName.append(chars);
		}
	}

	public String getAccessProcedure() {
		return accessProcedure.toString().trim();
	}

	public String getLoginURL() {
		return loginURL.toString().trim();
	}

	public String getAbortLoginURL() {
		return abortLoginURL.toString().trim();
	}

	public String getMessageType() {
		return messageType.toString().trim();
	}

	public String getResponseCode() {
		return responseCode.toString().trim();
	}

	public String getAccessLocation() {
		return accessLocation.toString().trim();
	}

	public String getLocationName() {
		return locationName.toString().trim();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName()).append("{");
		sb.append("accessProcedure: ").append(accessProcedure).append(", ");
		sb.append("accessLocation: ").append(accessLocation).append(", ");
		sb.append("locationName: ").append(locationName).append(", ");
		sb.append("loginURL: ").append(loginURL).append(", ");
		sb.append("abortLoginURL: ").append(abortLoginURL).append(", ");
		sb.append("messageType: ").append(messageType).append(", ");
		sb.append("responseCode: ").append(responseCode);
		sb.append("}");

		return sb.toString();
	}
}
