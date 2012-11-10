/**
 * Created: 10.11.2012
 */

package de.freese.sonstiges.server.netty.spdy;

import javax.net.ssl.SSLEngine;

import org.eclipse.jetty.npn.NextProtoNego;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.handler.codec.spdy.SpdyOrHttpChooser;

/**
 * {@link SpdyOrHttpChooser} der {@link NextProtoNego} verwendet um festzustellen ob SPDY oder HTTP
 * verwendet werden soll.
 * 
 * @author Norman Maurer <norman@apache.org>
 * @author Thomas Freese
 */
public class SpdyOrHttpChooserImpl extends SpdyOrHttpChooser
{
	/**
	 * Erstellt ein neues {@link SpdyOrHttpChooserImpl} Object.
	 * 
	 * @param maxSpdyContentLength int
	 * @param maxHttpContentLength int
	 */
	public SpdyOrHttpChooserImpl(final int maxSpdyContentLength, final int maxHttpContentLength)
	{
		super(maxSpdyContentLength, maxHttpContentLength);
	}

	/**
	 * @see org.jboss.netty.handler.codec.spdy.SpdyOrHttpChooser#createHttpRequestHandlerForHttp()
	 */
	@Override
	protected ChannelUpstreamHandler createHttpRequestHandlerForHttp()
	{
		return new HttpRequestHandler();
	}

	/**
	 * @see org.jboss.netty.handler.codec.spdy.SpdyOrHttpChooser#createHttpRequestHandlerForSpdy()
	 */
	@Override
	protected ChannelUpstreamHandler createHttpRequestHandlerForSpdy()
	{
		return new SpdyHttpRequestHandler();
	}

	/**
	 * @see org.jboss.netty.handler.codec.spdy.SpdyOrHttpChooser#getProtocol(javax.net.ssl.SSLEngine)
	 */
	@Override
	protected SelectedProtocol getProtocol(final SSLEngine engine)
	{
		// Anhand des ausgewaehlten Protokolls wird das richtige ausgewaehlt.
		ProviderImpl provider = (ProviderImpl) NextProtoNego.get(engine);
		String protocol = provider.getSelectedProtocol();

		if (protocol == null)
		{
			return SelectedProtocol.None;
		}

		switch (protocol)
		{
			case "spdy/2":
				return SelectedProtocol.SpdyVersion2;
			case "spdy/3":
				return SelectedProtocol.SpdyVersion3;
			case "http/1.1":
				return SelectedProtocol.HttpVersion1_1;
			case "http/1.0":
				return SelectedProtocol.HttpVersion1_0;
			default:
				return SelectedProtocol.None;
		}
	}
}