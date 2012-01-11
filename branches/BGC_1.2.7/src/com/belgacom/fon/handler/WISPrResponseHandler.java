package com.belgacom.fon.handler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class WISPrResponseHandler extends DefaultHandler {
	enum Tag {
		wispaccessgatewayparam, redirect, responsecode, fonresponsecode, logoffurl, replymessage, authenticationpollreply, messagetype, authenticationreply
	}

	private Tag actualTag;

	private StringBuilder responseCode = new StringBuilder();

	private StringBuilder fonResponseCode = new StringBuilder();

	private StringBuilder logoffURL = new StringBuilder();

	private StringBuilder replyMessage = new StringBuilder();

	private StringBuilder messageType = new StringBuilder();

	@Override
	public void startElement(String uri, String name, String qName, Attributes atts) {
		actualTag = Tag.valueOf(name.trim().toLowerCase());
	}

	@Override
	public void characters(char ch[], int start, int length) {
		String chars = (new String(ch).substring(start, start + length));
		if (actualTag.equals(Tag.responsecode)) {
			responseCode.append(chars);
		} else if (actualTag.equals(Tag.fonresponsecode)) {
			fonResponseCode.append(chars);
		} else if (actualTag.equals(Tag.logoffurl)) {
			logoffURL.append(chars);
		} else if (actualTag.equals(Tag.replymessage)) {
			replyMessage.append(chars);
		} else if (actualTag.equals(Tag.messagetype)) {
			messageType.append(chars);
		}
	}

	public String getResponseCode() {
		return responseCode.toString().trim();
	}

	public String getFonResponseCode() {
		return fonResponseCode.toString().trim();
	}

	public String getLogoffURL() {
		return logoffURL.toString().trim();
	}

	public String getReplyMessage() {
		return replyMessage.toString().trim();
	}

	public String getMessageType() {
		return messageType.toString().trim();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName()).append("{");
		sb.append("responseCode: ").append(responseCode).append(", ");
		sb.append("fonResponseCode: ").append(fonResponseCode).append(", ");
		sb.append("logoffURL: ").append(logoffURL).append(", ");
		sb.append("replyMessage: ").append(replyMessage).append(", ");
		sb.append("messageType: ").append(messageType);
		sb.append("}");

		return sb.toString();
	}
}