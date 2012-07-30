/*
 * (c) 2004, Nuno Santos, nfsantos@sapo.pt relased under terms of the GNU public license
 * http://www.gnu.org/licenses/licenses.html#TOCGPL
 */
package de.freese.nio.beispiele.nuno.handlers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import de.freese.nio.beispiele.nuno.io.AcceptSelectorHandler;
import de.freese.nio.beispiele.nuno.io.CallbackErrorHandler;
import de.freese.nio.beispiele.nuno.io.SelectorThread;

/**
 * Listens for incoming connections from clients, using a selector to receive connect events.
 * Therefore, instances of this class don't have an associated thread. When a connection is
 * established, it notifies a listener using a callback.
 * 
 * @author Nuno Santos
 */
final public class Acceptor implements AcceptSelectorHandler
{
	/**
	 * Used to receive incoming connections
	 */
	private ServerSocketChannel ssc;

	/**
	 * The selector used by this instance.
	 */
	private final SelectorThread ioThread;

	/**
	 * Port where to listen for connections.
	 */
	private final int listenPort;

	/**
	 * Listener to be notified of new connections and of errors.
	 */
	private final AcceptorListener listener;

	/**
	 * Creates a new instance. No server socket is created. Use openServerSocket() to start
	 * listening.
	 * 
	 * @param listenPort The port to open.
	 * @param listener The object that will receive notifications of incoming connections.
	 * @param ioThread The selector thread.
	 */
	public Acceptor(final int listenPort, final SelectorThread ioThread,
			final AcceptorListener listener)
	{
		super();

		this.ioThread = ioThread;
		this.listenPort = listenPort;
		this.listener = listener;
	}

	/**
	 * Closes the socket. Returns only when the socket has been closed.
	 */
	public void close()
	{
		try
		{
			// Must wait for the socket to be closed.
			this.ioThread.invokeAndWait(new Runnable()
			{
				@Override
				public void run()
				{
					if (Acceptor.this.ssc != null)
					{
						try
						{
							Acceptor.this.ssc.close();
						}
						catch (IOException e)
						{
							// Ignore
						}
					}
				}
			});
		}
		catch (InterruptedException e)
		{
			// Ignore
		}
	}

	/**
	 * Called by SelectorThread when the underlying server socket is ready to accept a connection.
	 * This method should not be called from anywhere else.
	 */
	@Override
	public void handleAccept()
	{
		SocketChannel sc = null;

		try
		{
			sc = this.ssc.accept();
			// Socket s = sc.socket();

			// Reactivate interest to receive the next connection. We
			// can use one of the XXXNow methods since this method is being
			// executed on the selector's thread.
			this.ioThread.addChannelInterestNow(this.ssc, SelectionKey.OP_ACCEPT);
		}
		catch (IOException e)
		{
			this.listener.socketError(this, e);
		}

		if (sc != null)
		{
			// Connection established
			this.listener.socketConnected(this, sc);
		}
	}

	/**
	 * Starts listening for incoming connections. This method does not block waiting for
	 * connections. Instead, it registers itself with the selector to receive connect events.
	 * 
	 * @throws IOException Falls was schief geht.
	 */
	public void openServerSocket() throws IOException
	{
		this.ssc = ServerSocketChannel.open();
		InetSocketAddress isa = new InetSocketAddress(this.listenPort);
		this.ssc.socket().bind(isa, 100);

		// This method might be called from any thread. We must use
		// the xxxLater methods so that the actual register operation
		// is done by the selector's thread. No other thread should access
		// the selector directly.
		this.ioThread.registerChannelLater(this.ssc, SelectionKey.OP_ACCEPT, this,
				new CallbackErrorHandler()
				{
					@Override
					public void handleError(final Exception ex)
					{
						Acceptor.this.listener.socketError(Acceptor.this, ex);
					}
				});
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ListenPort: " + this.listenPort;
	}
}