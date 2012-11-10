/**
 * Created: 10.11.2012
 */

package de.freese.sonstiges.server.netty.spdy;

import javax.net.ssl.SSLEngine;

import org.eclipse.jetty.npn.NextProtoNego;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.ssl.SslHandler;

import de.freese.sonstiges.ssl.bogus.BogusSSLContextFactory;

/**
 * {@link ChannelPipelineFactory} die die noetigen {@link ChannelHandler} einfuegt.
 * 
 * @author Norman Maurer <norman@apache.org>
 * @author Thomas Freese
 */
public class SpdyChannelPipelineFactory implements ChannelPipelineFactory
{
	/**
	 * Erstellt ein neues {@link SpdyChannelPipelineFactory} Object.
	 */
	public SpdyChannelPipelineFactory()
	{
		super();
	}

	/**
	 * @see org.jboss.netty.channel.ChannelPipelineFactory#getPipeline()
	 */
	@Override
	public ChannelPipeline getPipeline() throws Exception
	{
		// Erzeugen der SSLEngine.
		ChannelPipeline pipeline = Channels.pipeline();
		SSLEngine engine = BogusSSLContextFactory.getServerContext().createSSLEngine();
		engine.setUseClientMode(false);

		// Hinzufuegen von dem erzeugten ProviderImpl der auch SPDY bearbeiten kann.
		NextProtoNego.put(engine, new ProviderImpl());
		NextProtoNego.debug = true;

		pipeline.addLast("sslHandler", new SslHandler(engine));

		// Hinzufuegen des SpdyOrHttpChooserImpl der entweder alle ChannelHandler fuer
		// SPDY oder HTTP in die ChannelPipeline hinzufuegt.
		pipeline.addLast("chooser", new SpdyOrHttpChooserImpl(1024 * 1024, 1024 * 1024));

		return pipeline;
	}
}
