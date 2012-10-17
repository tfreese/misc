package de.freese.sonstiges.server.netty.websockets;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * {@link SimpleChannelUpstreamHandler} der alle empfangenen UDP Nachrichten als
 * {@link TextWebSocketFrame} an alle verbundenen {@link Channel} via {@link ChannelGroup} sendet.
 * 
 * @author Norman Maurer <norman@apache.org>
 * @author Thomas Freese
 */
public class WebSocketBroadcastHandler extends SimpleChannelUpstreamHandler
{
	/**
	 * 
	 */
	private final ChannelGroup wsGroup;

	/**
	 * Erstellt ein neues {@link WebSocketBroadcastHandler} Object.
	 * 
	 * @param wsGroup {@link ChannelGroup}
	 */
	public WebSocketBroadcastHandler(final ChannelGroup wsGroup)
	{
		super();

		this.wsGroup = wsGroup;
	}

	/**
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext,
	 *      org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e)
		throws Exception
	{
		// Senden der empfangenen UDP Nachricht an alle verbundenen WebSocket Clients
		this.wsGroup.write(new TextWebSocketFrame((ChannelBuffer) e.getMessage()));
	}
}
