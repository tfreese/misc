package de.freese.sonstiges.server.netty.spdy;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * Server der Webseiten via SPDY und HTTP zur Verfuegung stellt.
 * 
 * @author Norman Maurer <norman@apache.org>
 */
public class SpdyServer
{
	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		int port = 8888;

		// if (args.length > 0)
		// {
		// port = Integer.parseInt(args[0]);
		// }

		new SpdyServer(port).run();
	}

	/**
	 * 
	 */
	private final int port;

	/**
	 * Erstellt ein neues {@link SpdyServer} Object.
	 * 
	 * @param port int
	 */
	public SpdyServer(final int port)
	{
		super();

		this.port = port;
	}

	/**
     * 
     */
	public void run()
	{
		// Configure the server.
		final ServerBootstrap bootstrap =
				new ServerBootstrap(new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		bootstrap.setOption("reuseAddress", Boolean.TRUE);

		// Configure the pipeline factory.
		bootstrap.setPipelineFactory(new SpdyChannelPipelineFactory());

		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(this.port));

		System.out.println("Listening on port " + this.port);

		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			/**
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run()
			{
				System.out.println("shutdown");

				// ChannelGroupFuture future = allChannels.close();
				// future.awaitUninterruptibly();
				bootstrap.releaseExternalResources();
			}
		});
	}
}
