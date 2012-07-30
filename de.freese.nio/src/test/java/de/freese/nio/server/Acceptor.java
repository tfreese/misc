/*
 * (c) 2004, Nuno Santos, nfsantos@sapo.pt relased under terms of the GNU public license
 * http://www.gnu.org/licenses/licenses.html#TOCGPL
 */
package de.freese.nio.server;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.nio.server.handler.AcceptSelectorHandler;
import de.freese.nio.server.handler.ReadWriteSelectorHandler;

/**
 * Listener fuer eingehende Connections auf einen bestimmten Port.<br>
 * Die Connection Events werden ueber einen Selector verarbeitet.
 * 
 * @author Nuno Santos
 * @author Thomas Freese
 */
public final class Acceptor implements AcceptSelectorHandler
{
	/**
	 *
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Acceptor.class);

	/**
	 * Fuer die eingehenden Connections.
	 */
	private ServerSocketChannel ssc = null;

	/**
	 * Der SelectorThread dieser Instanz.
	 */
	private final SelectorThread selectorThread;

	/**
	 * Port fuer die Connections.
	 */
	private final int port;

	/**
	 *
	 */
	private final Constructor<? extends ReadWriteSelectorHandler> handlerConstructor;

	/**
	 * Erstellt ein neues {@link Acceptor} Object.<br>
	 * Fuer die Aktivierung openServerSocket() aufrufen.
	 * 
	 * @param port int
	 * @param readWriteHandlerClazz Class
	 * @throws IOException Falls was schief geht.
	 * @throws NoSuchMethodException Bei Falscher HandlerKlasse
	 */
	Acceptor(final int port, final Class<? extends ReadWriteSelectorHandler> readWriteHandlerClazz)
		throws IOException, NoSuchMethodException
	{
		super();

		this.port = port;
		this.selectorThread = new SelectorThread("Acceptor (" + port + ")");

		this.handlerConstructor =
				readWriteHandlerClazz.getConstructor(SocketChannel.class, SelectorThread.class);
	}

	/**
	 * Schliessen des Socktes.
	 */
	public void close()
	{
		try
		{
			// Warten bis Socket geschlossen ist.
			this.selectorThread.invokeAndWait(new Runnable()
			{
				/**
				 * @see java.lang.Runnable#run()
				 */
				@Override
				public void run()
				{
					if (Acceptor.this.ssc != null)
					{
						try
						{
							Acceptor.this.ssc.close();
						}
						catch (IOException ex)
						{
							LOGGER.error(null, ex);
						}
					}
				}
			});
		}
		catch (InterruptedException ex)
		{
			LOGGER.error(null, ex);
		}
	}

	/**
	 * @see de.freese.nio.server.handler.AcceptSelectorHandler#handleAccept()
	 */
	@Override
	public void handleAccept()
	{
		SocketChannel sc = null;

		try
		{
			sc = this.ssc.accept();
			// Socket s = sc.socket();

			// Da der OP_ACCEPT im SelectorThread deaktiviert wurde um mehrfache Events des
			// gleichen Typs zu vermeiden, muss er hier wieder reaktiviert werden.
			// Da diese Methode innerhalb des SelectorThread aufgerufen wird kann die xxxNow Methode
			// verwendet werden.
			this.selectorThread.addChannelInterestNow(this.ssc, SelectionKey.OP_ACCEPT);
		}
		catch (IOException ex)
		{
			LOGGER.error(null, ex);
		}

		if (sc != null)
		{
			// Connection established
			LOGGER.info("Socket connected: {}", sc.socket().getInetAddress());

			try
			{
				// Groesse des TCP-Buffers begrenzen sonst gibt bei vielen Verbindung schnell
				// OutOfMemory Exception.
				sc.socket().setReceiveBufferSize(2 * 1024);
				sc.socket().setSendBufferSize(2 * 1024);

				ReadWriteSelectorHandler rwSH =
						this.handlerConstructor.newInstance(sc, this.selectorThread);

				rwSH.configHandle();
			}
			catch (Exception ex)
			{
				LOGGER.error(null, ex);
			}
		}
	}

	/**
	 * Oeffnet den ServerSocketChannel und registriert sich selbst als Listener fuer ACCEPT-Events.
	 * 
	 * @throws IOException Falls was schief geht.
	 */
	public void openServerSocket() throws IOException
	{
		this.ssc = ServerSocketChannel.open();
		InetSocketAddress isa = new InetSocketAddress(this.port);

		try
		{
			this.ssc.socket().bind(isa, 100);
		}
		catch (BindException ex)
		{
			this.ssc.close();

			IOException ioe = new IOException(ex.getMessage());
			ioe.initCause(ex);
			throw ioe;
		}

		// Diese Methode kann von jedem Thread aufgerufen werden. Da die Channel Interest aber nur
		// im
		// SelectorThread geaendert werden duerfen muss hier die xxxLater Methode verwendet werden.
		this.selectorThread.registerChannelInterestLater(this.ssc, SelectionKey.OP_ACCEPT, this);

		LOGGER.info("Listening on port: {}", Integer.valueOf(this.port));
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "(" + this.port + ")";
	}
}