/*
 * Created Oct 18, 2005
 */
package de.freese.sonstiges.ssl;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Date;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import de.freese.base.security.ssl.SSLContextFactory;

/**
 * @author Thomas Freese
 */
public class DateClient
{
	/**
	 * @param argv String[]
	 */
	public static void main(final String[] argv)
	{
		try
		{
			boolean isSSL = true;
			SocketFactory socketFactory = null;

			if (isSSL)
			{
				// SSLContext sslContext = SSLContextFactory.createDefault();
				SSLContext sslContext =
						SSLContextFactory.createTrusted("src/main/resources/serverKeyStore",
								"server-pw".toCharArray(), "src/main/resources/clientKeyStore",
								"client-pw".toCharArray(), "server1-cert-pw".toCharArray());

				socketFactory = sslContext.getSocketFactory();
			}
			else
			{
				socketFactory = SocketFactory.getDefault();
			}

			try (Socket socket = socketFactory.createSocket("localhost", 3000))
			{
				if (socket instanceof SSLSocket)
				{
					SSLSocket sslSocket = (SSLSocket) socket;

					sslSocket.startHandshake();

					SSLSession session = sslSocket.getSession();
					System.out.println("Cipher suite in use is " + session.getCipherSuite());
					System.out.println("Protocol is " + session.getProtocol());
				}

				// get the input and output streams from the SSL connection
				try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream()))
				{
					Date date = (Date) ois.readObject();
					System.out.print("The date is: " + date);
				}

				// try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream()))
				// {
				// // TODO
				// }
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}
