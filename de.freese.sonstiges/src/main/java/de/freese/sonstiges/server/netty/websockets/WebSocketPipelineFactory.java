package de.freese.sonstiges.server.netty.websockets;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

/**
 * {@link ChannelPipelineFactory} die alle noetigen {@link ChannelHandler} in die erzeugte
 * {@link ChannelPipeline} einfuegt und diese dann zur Verfuegung stellt.
 * 
 * @author Norman Maurer <norman@apache.org>
 * @author Thomas Freese
 */
public class WebSocketPipelineFactory implements ChannelPipelineFactory
{
	/**
	 * 
	 */
	private final ChannelGroup group;

	/**
	 * Erstellt ein neues {@link WebSocketPipelineFactory} Object.
	 * 
	 * @param group {@link ChannelGroup}
	 */
	public WebSocketPipelineFactory(final ChannelGroup group)
	{
		super();

		this.group = group;
	}

	/**
	 * @see org.jboss.netty.channel.ChannelPipelineFactory#getPipeline()
	 */
	@Override
	public ChannelPipeline getPipeline() throws Exception
	{
		// Pipeline Object erstellen
		ChannelPipeline pipeline = Channels.pipeline();

		// Decoder der ChannelBuffer zu HttpRequest's umwandelt
		pipeline.addLast("reqDecoder", new HttpRequestDecoder());

		// Aggregator der HttpChunks' in HttpRequest's aggregiert
		pipeline.addLast("chunkAggregator", new HttpChunkAggregator(65536));

		// Encoder der HttpResponse's zu ChannelBuffer umwandelt
		pipeline.addLast("reqEncoder", new HttpResponseEncoder());

		// Handler der den richtigen WebSocket Handshaker einfuegt
		// und die Index-Seite zur Verfügung stellt
		pipeline.addLast("handler", new WebSocketServerHandler(this.group));

		return pipeline;
	}
}
