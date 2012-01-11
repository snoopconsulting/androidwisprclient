package com.belgacom.fon.util;

public class WISPrUtil {
	public static final String WISPR_TAG_NAME = "WISPAccessGatewayParam";

	public static final String WISPR_TAG_START = "<" + WISPR_TAG_NAME;

	public static final String WISPR_TAG_END = "</" + WISPR_TAG_NAME + ">";

	public static String getWISPrXML(String source) {
		String res = null;
		int start = source.indexOf(WISPR_TAG_START);
		int end = source.indexOf(WISPR_TAG_END, start) + WISPR_TAG_END.length();
		if (start > -1 && end > -1) {
			res = new String(source.substring(start, end));
			if (!res.contains("&amp;")) {
				res = res.replace("&", "&amp;");
			}
		}

		return res;
	}
}