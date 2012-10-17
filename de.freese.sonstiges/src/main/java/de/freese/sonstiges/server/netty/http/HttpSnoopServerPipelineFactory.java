/**
 * Created: 17.10.2012
 */

package de.freese.sonstiges.server.netty.http;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import de.freese.sonstiges.server.netty.FixedHttpRequestDecoder;

/**
 * @author Thomas Freese
 */
public class HttpSnoopServerPipelineFactory implements ChannelPipelineFactory
{
	/**
	 * Erstellt ein neues {@link HttpSnoopServerPipelineFactory} Object.
	 */
	public HttpSnoopServerPipelineFactory()
	{
		super();
	}

	/**
	 * @see org.jboss.netty.channel.ChannelPipelineFactory#getPipeline()
	 */
	@Override
	public ChannelPipeline getPipeline() throws Exception
	{
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = Channels.pipeline();

		// Uncomment the following line if you want HTTPS
		// SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
		// engine.setUseClientMode(false);
		// pipeline.addLast("ssl", new SslHandler(engine));

		pipeline.addLast("decoder", new FixedHttpRequestDecoder());
		// Uncomment the following line if you don't want to handle HttpChunks.
		// pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
		pipeline.addLast("encoder", new HttpResponseEncoder());
		// Remove the following line if you don't want automatic content compression.
		pipeline.addLast("deflater", new HttpContentCompressor());
		pipeline.addLast("handler", new HttpSnoopServerHandler());

		return pipeline;
	}
}
