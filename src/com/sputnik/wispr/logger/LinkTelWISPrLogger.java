package com.sputnik.wispr.logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.util.Log;

import com.sputnik.wispr.handler.WISPrInfoHandler;
import com.sputnik.wispr.handler.WISPrResponseHandler;
import com.sputnik.wispr.util.FONUtil;
import com.sputnik.wispr.util.HttpUtils;
import com.sputnik.wispr.util.WISPrConstants;
import com.sputnik.wispr.util.WISPrUtil;

public class LinkTelWISPrLogger implements WebLogger {

	private static final String TAG = LinkTelWISPrLogger.class.getName();

	private static final String DEFAULT_LOGOFF_URL = "http://192.168.182.1:3990/logoff";

	//Para probar directamente. Este también debería devolver un 302 y manda cookies
	//NOTA podemos agregar también un User-Agent: CaptiveNetworkSupport/1.0 wispr
	//NOTA La spec dice que mejor usar WISPR!client_software_identifier, por ej WISPR!fibertel_zone
	//Ver sección 7.7, no deberíamos mandar un https
	private static final String URL_ALTERNATIVO = "https://portal.linktelwifi.com.br/user/login/signup";

	protected static final String USER_PARAM = "UserName";

	protected static final String PASSWORD_PARAM = "Password";
	
	protected static final String VERSION_PARAM = "WISPrVersion";
	
	protected static final String WISPR_VERSION = "2.0";



	public LoggerResult login(String user, String password) {
		LoggerResult res = new LoggerResult(WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR, null);
		try {
			String blockedUrlText = HttpUtils.getUrlFollowRedirects(BLOCKED_URL,2);
			//FIXME El BLOCKED_URL devuelve este texto ( CONNECTED ). Es un poco frágil, apuntarlo a algo más permanente
			if (!blockedUrlText.equalsIgnoreCase(CONNECTED)) {  
				//Quiere decir que no pudo acceder al recurso entonces se fija si se tiene que loguear
				
				//Busco si está el tag de Wispr ( "WISPAccessGatewayParam" )
				// https://msdn.microsoft.com/en-us/library/windows/hardware/dn408679.aspx
				
				String WISPrXML = WISPrUtil.getWISPrXML(blockedUrlText);
				if (WISPrXML != null) {
					// Log.d(TAG, "XML Found:" + WISPrXML);
					
					WISPrInfoHandler wisprInfo = new WISPrInfoHandler();
					android.util.Xml.parse(WISPrXML, wisprInfo);

					if (wisprInfo.getMessageType().equals(WISPrConstants.WISPR_MESSAGE_TYPE_INITIAL)
							&& wisprInfo.getResponseCode().equals(WISPrConstants.WISPR_RESPONSE_CODE_NO_ERROR)) {
						
						// 7.9 WISPr Redirect
						
						// 7.10 WISPr Login
						res = tryToLogin(user, password, wisprInfo);
						
						// Este devuelve el código del error, por ejemplo si falló el login lo maneja el cliente
						// en este ejemplo el WISPrLovverService.notifyConnectionResult
						// ver p. 30 de la spec
						
					} else if (wisprInfo.getMessageType().equals(WISPrConstants.WISPR_MESSAGE_TYPE_PROXY_NOTIFICATION)
							&& wisprInfo.getResponseCode().equals(WISPrConstants.WISPR_RESPONSE_CODE_PROXY_DETECTION)) {
						//Este es el caso proxy, 7.8, así que tenemos que levantar 
						// <NextURL><![CDATA[https://portal.linktelwifi.com.br/captive.php?UI=02a3b8&NI=0050e802a3b8&UIP=201.54.224.241&MA=E0F84715DEB8&RN=LinktelWiFi_Aptilo_Grat&PORT=503&RAD=yes&CC=no&PMS=no&SIP=192.0.3.206&OS=http://ubuntuone.com/p/F6b/]]></NextURL>
						// y seguir el url ese que va a traer el wispr redirect posta 
						
						
					} else {
						res = new LoggerResult(WISPrConstants.WISPR_NOT_PRESENT, "Message type desconocido"); 						
					}
				} else {
					// Log.d(TAG, "XML NOT FOUND : " + blockedUrlText);
					res = new LoggerResult(WISPrConstants.WISPR_NOT_PRESENT, null); //Pasa por acá, no encuentra ese gateway en el HTML
				}
			} else {
				res = new LoggerResult(WISPrConstants.ALREADY_CONNECTED, DEFAULT_LOGOFF_URL);
			}
		} catch (Exception e) {
			Log.e(TAG, "Error trying to log", e);
			res = new LoggerResult(WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR, null);
		}
		Log.d(TAG, "WISPR Login Result: " + res);

		return res;
	}

	private LoggerResult tryToLogin(String user, String password, WISPrInfoHandler wisprInfo) throws IOException,
			ParserConfigurationException, FactoryConfigurationError {
		String res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		String logOffUrl = null;
		String targetURL = wisprInfo.getLoginURL();
		Log.d(TAG, "Trying to Log " + targetURL);
		Map<String, String> data = new HashMap<String, String>();
		data.put(USER_PARAM, user);
		data.put(PASSWORD_PARAM, password);
		data.put(VERSION_PARAM, WISPR_VERSION);

		String htmlResponse = HttpUtils.getUrlByPost(targetURL, data);
		
		//FIXME No chequea si vuelve un poll:
		/*
		 * If the access gateway requires the client software to poll for the result of the authentication, 
		 * it SHALL return a WISPr response with a <PollNotification> message in response to the WISPr request. 
		 * Please refer to section 7.11 for the <PollNotification> message format and procedures.
		 */

		
		//FIXME Tampoco maneja estos casos:
		
		/*
		 * If the access gateway receives a WISPr login request from a User Device, while a session is already active, 
		 * it SHALL return a WISPr response with an < AuthenticationReply > or < EAPAuthenticationReply > message 
		 * that indicates a Invalid State for Request (252) response code. Upon receipt of a WISPr response with 
		 * a < AuthenticationReply > or < EAPAuthenticationReply > message that indicates a 
		 * Invalid State for Request (252) response code, the client software SHALL restart the WISPr discovery 
		 * procedure using an “Arbitrary URL” as described in section 7.7.
		 */
		
		
		Log.d(TAG, "WISPR Reponse:" + htmlResponse);
		if (htmlResponse != null) {
			String response = WISPrUtil.getWISPrXML(htmlResponse);
			if (response != null) {
				 Log.d(TAG, "WISPr redirect response:" + response);
				WISPrResponseHandler wrh = new WISPrResponseHandler();
				try {
					android.util.Xml.parse(response, wrh);
					res = wrh.getResponseCode();
					logOffUrl = wrh.getLogoffURL();
				} catch (SAXException saxe) {
					res = WISPrConstants.WISPR_NOT_PRESENT;
				}
			} else {
				res = WISPrConstants.WISPR_NOT_PRESENT;
			}
		} else {
			throw new IOException("Respuesta null al enviar el redirect a WISPr");
		}

		// If we dont find the WISPR Response or we cannot parse it, we check if we have connection
		if (res.equals(WISPrConstants.WISPR_NOT_PRESENT)) {
			//FIXME Este chequeo vuelve a intentar acceder al blocked URL, pero el chequeo es frágil, no sé si ese URL es permanente
			if (FONUtil.haveConnection()) {
				res = WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_SUCCEEDED;
			}
		}

		return new LoggerResult(res, logOffUrl);
	}
}