/*
 * (c) 2004, Nuno Santos, nfsantos@sapo.pt relased under terms of the GNU public license
 * http://www.gnu.org/licenses/licenses.html#TOCGPL
 */
package de.freese.nio.beispiele.nuno.handlers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import de.freese.nio.beispiele.nuno.io.CallbackErrorHandler;
import de.freese.nio.beispiele.nuno.io.ConnectorSelectorHandler;
import de.freese.nio.beispiele.nuno.io.SelectorThread;

/**
 * Manages a non-blocking connection attempt to a remote host.
 * 
 * @author Nuno Santos
 */
final public class Connector implements ConnectorSelectorHandler
{
	/**
	 * The socket being connected.
	 */
	private SocketChannel sc;

	/**
	 * The address of the remote endpoint.
	 */
	private final InetSocketAddress remoteAddress;

	/**
	 * The selector used for receiving events.
	 */
	private final SelectorThread selectorThread;

	/**
	 * The listener for the callback events.
	 */
	private final ConnectorListener listener;

	/**
	 * Creates a new instance. The connection is not attempted here. Use connect() to start the
	 * attempt.
	 * 
	 * @param remoteAddress The remote endpoint where to connect.
	 * @param listener The object that will receive the callbacks from this Connector.
	 * @param selector The selector to be used.
	 */
	public Connector(final SelectorThread selector, final InetSocketAddress remoteAddress,
			final ConnectorListener listener)
	{
		this.selectorThread = selector;
		this.remoteAddress = remoteAddress;
		this.listener = listener;
	}

	/**
	 * Starts a non-blocking connection attempt.
	 * 
	 * @throws IOException Falls was schief geht.
	 */
	public void connect() throws IOException
	{
		this.sc = SocketChannel.open();
		// Very important. Set to non-blocking. Otherwise a call
		// to connect will block until the connection attempt fails
		// or succeeds.
		this.sc.configureBlocking(false);
		this.sc.connect(this.remoteAddress);

		// Registers itself to receive the connect event.
		this.selectorThread.registerChannelLater(this.sc, SelectionKey.OP_CONNECT, this,
				new CallbackErrorHandler()
				{
					@Override
					public void handleError(final Exception ex)
					{
						Connector.this.listener.connectionFailed(Connector.this, ex);
					}
				});
	}

	/**
	 * Called by the selector thread when the connection is ready to be completed.
	 */
	@Override
	public void handleConnect()
	{
		try
		{
			if (!this.sc.finishConnect())
			{
				// Connection failed
				this.listener.connectionFailed(this, null);
				return;
			}
			// Connection succeeded
			this.listener.connectionEstablished(this, this.sc);
		}
		catch (IOException ex)
		{
			// Could not connect.
			this.listener.connectionFailed(this, ex);
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "Remote endpoint: " + this.remoteAddress.getAddress().getHostAddress() + ":"
				+ this.remoteAddress.getPort();
	}
}