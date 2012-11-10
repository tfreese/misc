/**
 * Created: 10.11.2012
 */

package de.freese.sonstiges.server.netty.spdy;

import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;

/**
 * {@link HttpRequestHandler} der eine {@link HttpResponse} fuer {@link HttpRequest} and den Client
 * zurueck schreibt wenn SPDY verwendet wurde.
 * 
 * @author Norman Maurer <norman@apache.org>
 * @author Thomas Freese
 */
public class SpdyHttpRequestHandler extends HttpRequestHandler
{
	/**
	 * Erstellt ein neues {@link SpdyHttpRequestHandler} Object.
	 */
	public SpdyHttpRequestHandler()
	{
		super();
	}

	/**
	 * @see de.freese.sonstiges.server.netty.spdy.HttpRequestHandler#getContent()
	 */
	@Override
	protected String getContent()
	{
		return "Serve via SPDY\r\n";
	}
}
