/**
 * Created: 17.10.2012
 */

package de.freese.sonstiges.server.netty.http;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

/**
 * @author Thomas Freese
 */
public class HttpSnoopClient
{
	/**
	 * @param args String[]
	 * @throws URISyntaxException Falls was schief geht.
	 */
	public static void main(final String[] args) throws URISyntaxException
	{
		URI uri = new URI("http://localhost:8080");

		if (args.length == 1)
		{
			uri = new URI(args[0]);
		}

		new HttpSnoopClient(uri).run();
	}

	/**
	 * 
	 */
	private final URI uri;

	/**
	 * Erstellt ein neues {@link HttpSnoopClient} Object.
	 * 
	 * @param uri {@link URI}
	 */
	public HttpSnoopClient(final URI uri)
	{
		super();

		this.uri = uri;
	}

	/**
	 * 
	 */
	public void run()
	{
		String scheme = this.uri.getScheme() == null ? "http" : this.uri.getScheme();
		String host = this.uri.getHost() == null ? "localhost" : this.uri.getHost();
		int port = this.uri.getPort();

		if (port == -1)
		{
			if (scheme.equalsIgnoreCase("http"))
			{
				port = 80;
			}
			else if (scheme.equalsIgnoreCase("https"))
			{
				port = 443;
			}
		}

		if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))
		{
			System.err.println("Only HTTP(S) is supported.");
			return;
		}

		boolean ssl = scheme.equalsIgnoreCase("https");

		// Configure the client.
		ClientBootstrap bootstrap =
				new ClientBootstrap(new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new HttpSnoopClientPipelineFactory(ssl));

		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));

		// Wait until the connection attempt succeeds or fails.
		Channel channel = future.awaitUninterruptibly().getChannel();

		if (!future.isSuccess())
		{
			future.getCause().printStackTrace();
			bootstrap.releaseExternalResources();
			return;
		}

		// Prepare the HTTP request.
		HttpRequest request =
				new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, this.uri.getRawPath());
		request.setHeader(HttpHeaders.Names.HOST, host);
		request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
		request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);

		// Set some example cookies.
		CookieEncoder httpCookieEncoder = new CookieEncoder(false);
		httpCookieEncoder.addCookie("my-cookie", "foo");
		httpCookieEncoder.addCookie("another-cookie", "bar");
		request.setHeader(HttpHeaders.Names.COOKIE, httpCookieEncoder.encode());

		// Send the HTTP request.
		channel.write(request);

		// Wait for the server to close the connection.
		channel.getCloseFuture().awaitUninterruptibly();

		// Shut down executor threads to exit.
		bootstrap.releaseExternalResources();
	}
}
