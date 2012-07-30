/*
 * (c) 2004, Nuno Santos, nfsantos@sapo.pt relased under terms of the GNU public license
 * http://www.gnu.org/licenses/licenses.html#TOCGPL
 */
package de.freese.nio.server;

import de.freese.nio.server.handler.ReadWriteSelectorHandler;
import de.freese.nio.server.handler.impl.HTTPReadWriteSelectorHandler;
import de.freese.nio.server.handler.impl.POP3ReadWriteSelectorHandler;

/**
 * Einfache Implementierung fuer die NIO-API als Client-Server Beispiel.
 * 
 * @author Nuno Santos
 * @author Thomas Freese
 */
public class Server
{
	/**
	 * @param args String[]
	 * @throws Exception Falls was schief geht.
	 */
	public static void main(final String[] args) throws Exception
	{
		// int listenPort = Integer.parseInt(args[0]);
		// new Server(110);
		new Server(8080);
	}

	/**
	 * Erstellt ein neues {@link Server} Object.
	 * 
	 * @param port int; Port fuer die Connections.
	 * @throws Exception Falls was schief geht.
	 */
	public Server(final int port) throws Exception
	{
		super();

		Class<? extends ReadWriteSelectorHandler> readWriteHandlerClazz = null;

		switch (port)
		{
			case 110:
				readWriteHandlerClazz = POP3ReadWriteSelectorHandler.class;
				break;

			case 8080:
				readWriteHandlerClazz = HTTPReadWriteSelectorHandler.class;
				break;

			default:
				break;
		}

		final Acceptor acceptor = new Acceptor(port, readWriteHandlerClazz);
		acceptor.openServerSocket();

		Thread hook = new Thread(new Runnable()
		{
			/**
			 * @see java.lang.Runnable#run()
			 */
			@Override
			public void run()
			{
				acceptor.close();
				ThreadQueue.getInstance().shutdownNow();
				System.exit(0);
			}
		});
		Runtime.getRuntime().addShutdownHook(hook);
	}
}
