package com.joan.wispr.handler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class WISPrResponseHandler extends DefaultHandler {
	enum Tag {
		WISPAccessGatewayParam, Redirect, ResponseCode, FONResponseCode, LogoffURL, ReplyMessage, AuthenticationPollReply, MessageType
	}

	private Tag actualTag;

	private String responseCode = "";

	private String fonResponseCode = "";

	private String logoffURL = "";

	private String replyMessage = "";

	@Override
	public void startElement(String uri, String name, String qName, Attributes atts) {
		actualTag = Tag.valueOf(name.trim());
	}

	@Override
	public void characters(char ch[], int start, int length) {
		String chars = (new String(ch).substring(start, start + length));
		if (actualTag.equals(Tag.ResponseCode)) {
			responseCode += chars;
		} else if (actualTag.equals(Tag.FONResponseCode)) {
			fonResponseCode += chars;
		} else if (actualTag.equals(Tag.LogoffURL)) {
			logoffURL += chars;
		} else if (actualTag.equals(Tag.ReplyMessage)) {
			replyMessage += chars;
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
}
