package com.sputnik.wispr.handler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class WISPrResponseHandler extends DefaultHandler {
	enum Tag {
		wispaccessgatewayparam, redirect, responsecode, fonresponsecode, logoffurl, replymessage, authenticationpollreply, messagetype, authenticationreply,
		loginresultsurl, delay
	}

	private Tag actualTag;

	private String responseCode = "";

	private String fonResponseCode = "";

	private String logoffURL = "";

	private String replyMessage = "";

	private String messageType = "";
	
	private String loginResultUrl= "";
	
	private String delay= "";

	@Override
	public void startElement(String uri, String name, String qName, Attributes atts) {
		actualTag = Tag.valueOf(name.trim().toLowerCase());
	}

	@Override
	public void characters(char ch[], int start, int length) {
		String chars = (new String(ch).substring(start, start + length));
		if (actualTag.equals(Tag.responsecode)) {
			responseCode += chars;
		} else if (actualTag.equals(Tag.fonresponsecode)) {
			fonResponseCode += chars;
		} else if (actualTag.equals(Tag.logoffurl)) {
			logoffURL += chars;
		} else if (actualTag.equals(Tag.replymessage)) {
			replyMessage += chars;
		} else if (actualTag.equals(Tag.messagetype)) {
			messageType += chars;
		} else if (actualTag.equals(Tag.loginresultsurl)) {
			loginResultUrl += chars;
		} else if (actualTag.equals(Tag.delay)) {
			delay += chars;
		} else {
			//FIXME loggear
			System.out.println("TAG no parseado: " + actualTag + " valor: " + chars);
		}
	}

	public String getResponseCode() {
		return responseCode.trim();
	}

	public String getFonResponseCode() {
		return fonResponseCode.trim();
	}

	public String getLogoffURL() {
		return logoffURL.trim();
	}

	public String getReplyMessage() {
		return replyMessage.trim();
	}

	public String getMessageType() {
		return messageType.trim();
	}
	
	public String getLoginResultUrl() {
		return loginResultUrl.trim();
	}
	
	public String getDelay() {
		return delay.trim();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("WISPrResponseHandler{");
		sb.append("responseCode: ").append(responseCode).append(", ");
		sb.append("fonResponseCode: ").append(fonResponseCode).append(", ");
		sb.append("logoffURL: ").append(logoffURL).append(", ");
		sb.append("replyMessage: ").append(replyMessage).append(", ");
		sb.append("messageType: ").append(messageType);
		sb.append("loginResultUrl: ").append(loginResultUrl);
		sb.append("delay: ").append(delay);
		sb.append("}");

		return sb.toString();
	}
}
