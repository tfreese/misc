// Created: 28.12.2009
/**
 * 28.12.2009
 */
package de.freese.sonstiges.mina.server.http.mina_beispiel;

import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;

import de.freese.sonstiges.mina.server.http.HttpResponseMessage;

/**
 * Provides a protocol codec for HTTP server.
 * 
 * @author The Apache MINA Project (dev@mina.apache.org)
 * @version $Rev: 590006 $, $Date: 2007-10-30 18:44:02 +0900 (?, 30 10? 2007) $
 */
public class HttpServerProtocolCodecFactory extends DemuxingProtocolCodecFactory
{
	/**
	 * Erstellt ein neues {@link HttpServerProtocolCodecFactory} Object.
	 */
	public HttpServerProtocolCodecFactory()
	{
		super.addMessageDecoder(HttpRequestDecoder.class);
		super.addMessageEncoder(HttpResponseMessage.class, HttpResponseEncoder.class);
	}
}
