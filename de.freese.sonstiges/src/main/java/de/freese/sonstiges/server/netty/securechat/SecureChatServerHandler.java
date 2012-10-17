/*
 * Copyright 2012 The Netty Project The Netty Project licenses this file to you under the Apache
 * License, version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at: http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package de.freese.sonstiges.server.netty.securechat;

import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.handler.ssl.SslHandler;

/**
 * Handles a server-side channel.
 */
public class SecureChatServerHandler extends SimpleChannelUpstreamHandler
{
	/**
	 * @author Thomas Freese
	 */
	private static final class Greeter implements ChannelFutureListener
	{
		/**
		 * 
		 */
		private final SslHandler sslHandler;

		/**
		 * Erstellt ein neues {@link Greeter} Object.
		 * 
		 * @param sslHandler {@link SslHandler}
		 */
		Greeter(final SslHandler sslHandler)
		{
			super();

			this.sslHandler = sslHandler;
		}

		/**
		 * @see org.jboss.netty.channel.ChannelFutureListener#operationComplete(org.jboss.netty.channel.ChannelFuture)
		 */
		@Override
		public void operationComplete(final ChannelFuture future) throws Exception
		{
			if (future.isSuccess())
			{
				// Once session is secured, send a greeting.
				future.getChannel().write(
						"Welcome to " + InetAddress.getLocalHost().getHostName()
								+ " secure chat service!\n");
				future.getChannel().write(
						"Your session is protected by "
								+ this.sslHandler.getEngine().getSession().getCipherSuite()
								+ " cipher suite.\n");

				// Register the channel to the global channel list
				// so the channel received the messages from others.
				channels.add(future.getChannel());
			}
			else
			{
				future.getChannel().close();
			}
		}
	}

	/**
	 * 
	 */
	static final ChannelGroup channels = new DefaultChannelGroup();

	/**
	 * 
	 */
	private static final Logger logger = Logger.getLogger(SecureChatServerHandler.class.getName());

	/**
	 * Erstellt ein neues {@link SecureChatServerHandler} Object.
	 */
	public SecureChatServerHandler()
	{
		super();
	}

	/**
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelConnected(org.jboss.netty.channel.ChannelHandlerContext,
	 *      org.jboss.netty.channel.ChannelStateEvent)
	 */
	@Override
	public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e)
		throws Exception
	{

		// Get the SslHandler in the current pipeline.
		// We added it in SecureChatPipelineFactory.
		final SslHandler sslHandler = ctx.getPipeline().get(SslHandler.class);

		// Get notified when SSL handshake is done.
		ChannelFuture handshakeFuture = sslHandler.handshake();
		handshakeFuture.addListener(new Greeter(sslHandler));
	}

	/**
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#channelDisconnected(org.jboss.netty.channel.ChannelHandlerContext,
	 *      org.jboss.netty.channel.ChannelStateEvent)
	 */
	@Override
	public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent e)
		throws Exception
	{
		// Unregister the channel from the global channel list
		// so the channel does not receive messages anymore.
		channels.remove(e.getChannel());
	}

	/**
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#exceptionCaught(org.jboss.netty.channel.ChannelHandlerContext,
	 *      org.jboss.netty.channel.ExceptionEvent)
	 */
	@Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e)
	{
		logger.log(Level.WARNING, "Unexpected exception from downstream.", e.getCause());
		e.getChannel().close();
	}

	/**
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#handleUpstream(org.jboss.netty.channel.ChannelHandlerContext,
	 *      org.jboss.netty.channel.ChannelEvent)
	 */
	@Override
	public void handleUpstream(final ChannelHandlerContext ctx, final ChannelEvent e)
		throws Exception
	{
		if (e instanceof ChannelStateEvent)
		{
			logger.info(e.toString());
		}

		super.handleUpstream(ctx, e);
	}

	/**
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext,
	 *      org.jboss.netty.channel.MessageEvent)
	 */
	@Override
	public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e)
	{

		// Convert to a String first.
		String request = (String) e.getMessage();

		// Send the received message to all channels but the current one.
		for (Channel c : channels)
		{
			if (c != e.getChannel())
			{
				c.write("[" + e.getChannel().getRemoteAddress() + "] " + request + '\n');
			}
			else
			{
				c.write("[you] " + request + '\n');
			}
		}

		// Close the connection if the client has sent 'bye'.
		if (request.toLowerCase().equals("bye"))
		{
			e.getChannel().close();
		}
	}
}
