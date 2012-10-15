package me.normanmaurer.javamagazin.netty.examples.ws;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;

/**
 * {@link ChannelPipelineFactory} die den {@link WebSocketBroadcastHandler} in die
 * {@link ChannelPipeline} einfuegt. Dieser uebernimmt dann das senden aller empfangenen UDP
 * Nachrichten and die verbundenen WebSocket Clients.
 * 
 * @author Norman Maurer <norman@apache.org>
 * @author Thomas Freese
 */
public class WebSocketBroadcastPipelineFactory implements ChannelPipelineFactory
{
	/**
	 * 
	 */
	private final ChannelGroup group;

	/**
	 * Erstellt ein neues {@link WebSocketBroadcastPipelineFactory} Object.
	 * 
	 * @param group {@link ChannelGroup}
	 */
	public WebSocketBroadcastPipelineFactory(final ChannelGroup group)
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
		return Channels.pipeline(new WebSocketBroadcastHandler(this.group));
	}
}
