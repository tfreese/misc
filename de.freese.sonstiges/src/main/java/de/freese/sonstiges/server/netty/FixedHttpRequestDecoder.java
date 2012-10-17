/**
 * Created: 17.10.2012
 */

package de.freese.sonstiges.server.netty;

import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpMessage;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpVersion;

/**
 * {@link #createMessage(String[])} verwendete die falschen Strings.
 * 
 * @author Thomas Freese
 */
public class FixedHttpRequestDecoder extends HttpRequestDecoder
{
	/**
	 * Erstellt ein neues {@link FixedHttpRequestDecoder} Object.
	 */
	public FixedHttpRequestDecoder()
	{
		super();
	}

	/**
	 * Erstellt ein neues {@link FixedHttpRequestDecoder} Object.
	 * 
	 * @param maxInitialLineLength int
	 * @param maxHeaderSize int
	 * @param maxChunkSize int
	 */
	public FixedHttpRequestDecoder(final int maxInitialLineLength, final int maxHeaderSize,
			final int maxChunkSize)
	{
		super(maxInitialLineLength, maxHeaderSize, maxChunkSize);
	}

	/**
	 * @see org.jboss.netty.handler.codec.http.HttpRequestDecoder#createMessage(java.lang.String[])
	 */
	@Override
	protected HttpMessage createMessage(final String[] initialLine) throws Exception
	{
		return new DefaultHttpRequest(HttpVersion.valueOf(initialLine[1]),
				HttpMethod.valueOf(initialLine[0]), initialLine[2]);
	}
}
