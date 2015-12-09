package com.sputnik.wispr.util;

import java.io.IOException;

import org.xml.sax.SAXException;

import com.sputnik.wispr.handler.WISPrResponseHandler;

public class PollNotificationUtils {

	
	public static WISPrResponseHandler analyze (WISPrResponseHandler wr) throws IOException, SAXException{
		
		try {
			Thread.sleep(Integer.valueOf(wr.getDelay()));
		} catch (Exception e) {}
		WISPrResponseHandler rta = null;
		String htmlResponse = HttpUtils.getUrl(wr.getLoginResultUrl(), 2);
		if (htmlResponse != null){
			String response = WISPrUtil.getWISPrXML(htmlResponse);
			rta = new WISPrResponseHandler();
			android.util.Xml.parse(response, rta);
			if (WISPrConstants.WISPR_RESPONSE_CODE_AUTH_PENDING.equals(rta.getResponseCode())){
				analyze(rta);
			}
		}
		return rta;
	}

}
