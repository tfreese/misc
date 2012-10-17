/**
 * Created: 17.10.2012
 */

package de.freese.sonstiges.server.netty.http;

import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * @author Thomas Freese
 */
public class HttpSnoopServer
{
	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		int port = 8080;

		if (args.length > 0)
		{
			port = Integer.parseInt(args[0]);
		}

		new HttpSnoopServer(port).run();
	}

	/**
	 * 
	 */
	private final int port;

	/**
	 * Erstellt ein neues {@link HttpSnoopServer} Object.
	 * 
	 * @param port int
	 */
	public HttpSnoopServer(final int port)
	{
		super();

		this.port = port;
	}

	/**
	 * 
	 */
	public void run()
	{
		final ChannelGroup allChannels = new DefaultChannelGroup("http-server");
		Executor executor = Executors.newCachedThreadPool();

		// Configure the server.
		final ServerBootstrap bootstrap =
				new ServerBootstrap(new NioServerSocketChannelFactory(executor, executor));
		bootstrap.setOption("reuseAddress", Boolean.TRUE);

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new HttpSnoopServerPipelineFactory());

		// Bind and start to accept incoming connections.
		Channel channel = bootstrap.bind(new InetSocketAddress(this.port));
		allChannels.add(channel);

		System.out.println("Listening on port " + this.port);

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
				bootstrap.releaseExternalResources();
			}
		});
	}
}
