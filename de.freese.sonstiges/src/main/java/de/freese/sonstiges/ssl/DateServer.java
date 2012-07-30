/*
 * Created Oct 18, 2005
 */
package de.freese.sonstiges.ssl;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;

import de.freese.base.security.ssl.SSLContextFactory;

/**
 * @author Thomas Freese
 */
public class DateServer extends Thread
{
	/**
	 * @author Thomas Freese
	 */
	class Connect extends Thread
	{
		/**
		 * 
		 */
		private ObjectInputStream ois = null;

		/**
		 * 
		 */
		private ObjectOutputStream oos = null;

		/**
		 * 
		 */
		private final Socket clientSocket;

		/**
		 * Creates a new Connect object.
		 * 
		 * @param clientSocket {@link Socket}
		 */
		public Connect(final Socket clientSocket)
		{
			this.clientSocket = clientSocket;

			try
			{
				this.ois = new ObjectInputStream(this.clientSocket.getInputStream());
				this.oos = new ObjectOutputStream(this.clientSocket.getOutputStream());
			}
			catch (Exception e1)
			{
				try
				{
					this.clientSocket.close();
				}
				catch (Exception e)
				{
					System.out.println(e.getMessage());
				}

				return;
			}

			start();
		}

		/**
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run()
		{
			try
			{
				this.oos.writeObject(new Date());
				this.oos.flush();

				// close streams and connections
				this.ois.close();
				this.oos.close();
				this.clientSocket.close();
			}
			catch (Exception ex)
			{
				// Ignore
			}
		}
	}

	/**
	 * @param argv String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] argv) throws Exception
	{
		new DateServer();
	}

	/**
	 * 
	 */
	private final ServerSocket serverSocket;

	/**
	 * Creates a new DateServer object.
	 * 
	 * @throws Exception Falls was schief geht.
	 */
	public DateServer() throws Exception
	{
		super();

		boolean isSSL = true;
		ServerSocketFactory serverSocketFactory = null;

		if (isSSL)
		{
			// SSLContext sslContext = SSLContextFactory.createDefault();
			SSLContext sslContext =
					SSLContextFactory.createTrusted("src/main/resources/serverKeyStore",
							"server-pw".toCharArray(), "src/main/resources/clientKeyStore",
							"client-pw".toCharArray(), "server1-cert-pw".toCharArray());

			serverSocketFactory = sslContext.getServerSocketFactory();
		}
		else
		{
			serverSocketFactory = ServerSocketFactory.getDefault();
		}

		this.serverSocket = serverSocketFactory.createServerSocket(3000);

		if (this.serverSocket instanceof SSLServerSocket)
		{
			((SSLServerSocket) this.serverSocket).setNeedClientAuth(true);
		}

		System.out.println("Server listening on port 3000.");
		start();
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		while (true)
		{
			try
			{
				System.out.println("Waiting for connections.");

				Socket clientSocket = this.serverSocket.accept();
				System.out.println("Accepted a connection from: " + clientSocket.getInetAddress());

				new Connect(clientSocket);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
}
