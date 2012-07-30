// Created: 28.12.2009
/**
 * 28.12.2009
 */
package de.freese.sonstiges.mina.server.http.mina_beispiel;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 * @author Thomas Freese
 */
public class Server
{
	/**
	 * @param args String[]
	 * @throws IOException Falls was schief geht.
	 */
	public static void main(final String[] args) throws IOException
	{
		NioSocketAcceptor acceptor = new NioSocketAcceptor();

		// Create a service configuration
		acceptor.getFilterChain().addLast("protocolFilter",
				new ProtocolCodecFilter(new HttpServerProtocolCodecFactory()));
		// acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		acceptor.setHandler(new ServerHandler());
		acceptor.bind(new InetSocketAddress(8080));

		System.out.println("Server now listening on port " + 8080);
	}
}
