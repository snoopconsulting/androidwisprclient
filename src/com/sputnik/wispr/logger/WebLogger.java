package com.sputnik.wispr.logger;

public interface WebLogger {
	public static final String CONNECTED = "CONNECTED";

	/**
	 * Esto devuelve un HTML ( ilegal ) que lo único que dice es "CONNECTED
	 * 
	 * Se podría usar otro URL para chequear si hay conexión a internet.
	 * 
	 * Por ejemplo el http://www.gstatic.com/generate_204 que no devuelve contenido pero devuelve un 204 ( "204 No Content" )
	 */
	public static final String BLOCKED_URL = "http://ubuntuone.com/p/F6b/";

	public LoggerResult login(String user, String password);
}
