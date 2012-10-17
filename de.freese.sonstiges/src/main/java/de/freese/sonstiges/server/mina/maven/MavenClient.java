/**
 * Created: 27.12.2011
 */

package de.freese.sonstiges.server.mina.maven;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import de.freese.sonstiges.server.mina.maven.codec.MavenClientCodecFactory;

/**
 * @author Thomas Freese
 */
public class MavenClient
{
	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		Charset charset = Charset.forName("ISO-8859-1");
		NioSocketConnector connector = new NioSocketConnector();
		IoHandler handler = new MavenClientHandler();
		SocketAddress address = new InetSocketAddress("localhost", 8089);
		// SocketAddress address = new InetSocketAddress("repo1.maven.org", 80);

		connector.setHandler(handler);
		connector.getFilterChain().addLast("logger", new LoggingFilter());
		// connector.getFilterChain().addLast("codec",
		// new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new MavenClientCodecFactory(charset)));

		ConnectFuture future = connector.connect(address);
		// future.awaitUninterruptibly(10, TimeUnit.SECONDS);
		future.awaitUninterruptibly();

		if (!future.isConnected())
		{
			System.out.println("not connected");
			connector.dispose();
			return;
		}

		IoSession session = future.getSession();
		// session.getConfig().setUseReadOperation(true);

		// session.write("/artifactory/remote-repos/javax/servlet/servlet-api/2.5/maven-metadata.xml");
		session.write("/artifactory/remote-repos//com/jgoodies/binding/2.5.0/binding-2.5.0.jar");
		// session.write("/artifactory/remote-repos//com/jgoodies/binding/2.5.0/binding-2.5.0.jar");
		// ReadFuture readFuture = session.read();
		// readFuture.awaitUninterruptibly();

		// if (session != null)
		// {
		// if (session.isConnected())
		// {
		// // session.write("QUIT");
		// // Wait until the connection ends.
		// session.getCloseFuture().awaitUninterruptibly();
		// }
		//
		// session.close(true);
		// }
		//
		// connector.dispose();
	}
}
