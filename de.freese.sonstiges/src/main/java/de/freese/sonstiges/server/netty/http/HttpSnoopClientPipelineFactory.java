/**
 * Created: 17.10.2012
 */

package de.freese.sonstiges.server.netty.http;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.ssl.SslHandler;

import de.freese.base.net.ssl.bogus.BogusSSLContextFactory;

/**
 * @author Thomas Freese
 */
public class HttpSnoopClientPipelineFactory implements ChannelPipelineFactory
{
	/**
	 * 
	 */
	private final boolean ssl;

	/**
	 * Erstellt ein neues {@link HttpSnoopClientPipelineFactory} Object.
	 * 
	 * @param ssl boolean
	 */
	public HttpSnoopClientPipelineFactory(final boolean ssl)
	{
		super();

		this.ssl = ssl;
	}

	/**
	 * @see org.jboss.netty.channel.ChannelPipelineFactory#getPipeline()
	 */
	@Override
	public ChannelPipeline getPipeline() throws Exception
	{
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = Channels.pipeline();

		// Enable HTTPS if necessary.
		if (this.ssl)
		{
			SSLEngine engine = BogusSSLContextFactory.getClientContext().createSSLEngine();
			engine.setUseClientMode(true);

			pipeline.addLast("ssl", new SslHandler(engine));
		}

		pipeline.addLast("codec", new HttpClientCodec());

		// Remove the following line if you don't want automatic content decompression.
		pipeline.addLast("inflater", new HttpContentDecompressor());

		// Uncomment the following line if you don't want to handle HttpChunks.
		// pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));

		pipeline.addLast("handler", new HttpSnoopClientHandler());
		return pipeline;
	}
}
