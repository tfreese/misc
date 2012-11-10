package de.freese.sonstiges.server.netty.websockets;

import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * @author Norman Maurer <norman@apache.org>
 * @author Thomas Freese
 */
public class WebSocketServer
{
	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		int wsPort = 8888;
		int udpPort = 9999;

		if (args.length == 2)
		{
			wsPort = Integer.parseInt(args[0]);
			udpPort = Integer.parseInt(args[1]);
		}

		new WebSocketServer(wsPort, udpPort).startUp();
	}

	/**
	 * Dieser Gruppe werden alle WebSocket client Verbindungen zu gefuegt um somit eine einfache
	 * Kommunikation an alle zu ermoeglichen
	 */
	private final ChannelGroup group = new DefaultChannelGroup();

	/**
	 * 
	 */
	private final int port;

	/**
	 * 
	 */
	private final int udpPort;

	/**
	 * Erstellt ein neues {@link WebSocketServer} Object.
	 * 
	 * @param port int
	 * @param udpPort int
	 */
	public WebSocketServer(final int port, final int udpPort)
	{
		super();

		this.port = port;
		this.udpPort = udpPort;
	}

	/**
	 * Starten des Servers.
	 */
	public void startUp()
	{
		final ChannelGroup allChannels = new DefaultChannelGroup("websocket-server");

		// Bereite den UDP/Datagram Channel vor.
		final ConnectionlessBootstrap udpBootstrap =
				new ConnectionlessBootstrap(new NioDatagramChannelFactory());
		udpBootstrap.setOption("reuseAddress", Boolean.TRUE);

		// Setzen der WebSocketBroadcastPipelineFactory die das Senden von UDP
		// Nachrichten an die WebSocket Clients uebernimmt.
		udpBootstrap.setPipelineFactory(new WebSocketBroadcastPipelineFactory(this.group));

		// Binden des Sockets der die UDP Nachrichten entgegen nimmt.
		Channel channel = udpBootstrap.bind(new InetSocketAddress(this.udpPort));
		allChannels.add(channel);

		// Bereite den Channel vor.
		final ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory());
		bootstrap.setOption("reuseAddress", Boolean.TRUE);

		// Setzen der WebSocketPipelineFactory, die das Bearbeiten von HTTP
		// und WebSocket Requests uebernimmt.
		bootstrap.setPipelineFactory(new WebSocketPipelineFactory(this.group));

		// Binden des Sockets der nun bereit ist Requests entgegen zu nehmen.
		channel = bootstrap.bind(new InetSocketAddress(this.port));
		allChannels.add(channel);

		// Stoppen
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			/**
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run()
			{
				System.out.println("shutdown");

				ChannelGroupFuture future = allChannels.close();
				future.awaitUninterruptibly();
				udpBootstrap.releaseExternalResources();
				bootstrap.releaseExternalResources();

				// Client stoppen
				// ChannelFactory factory = ...;
				// ClientBootstrap bootstrap = ...;
				// ChannelFuture future = bootstrap.connect(...);
				// future.awaitUninterruptibly();
				// if (!future.isSuccess()) {
				// future.getCause().printStackTrace();
				// }
				// future.getChannel().getCloseFuture().awaitUninterruptibly();
				// factory.releaseExternalResources();
			}
		});
	}
}
