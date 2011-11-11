package com.belgacom.fon.logger;

import com.belgacom.fon.util.WISPrConstants;

public class LoggerResult {
	protected String result;

	protected String logOffUrl;

	public LoggerResult(String result, String logOffUrl) {
		this.result = result;
		this.logOffUrl = logOffUrl;
	}

	public String getResult() {
		return result;
	}

	public String getLogOffUrl() {
		return logOffUrl;
	}

	public boolean hasSucceded() {
		return result.equals(WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_SUCCEEDED) || result.equals(WISPrConstants.ALREADY_CONNECTED);
	}

	public boolean hasFailed() {
		return result.equals(WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR)
				|| result.equals(WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_FAILED) || result.equals(WISPrConstants.WISPR_NOT_PRESENT);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{result: " + result + ", logOffUrl:" + logOffUrl + "}";
	}
}
