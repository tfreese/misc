// Created: 10.01.2010
/**
 * 10.01.2010
 */
package de.freese.littlemina.core.acceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.freese.littlemina.core.IoHandler;
import de.freese.littlemina.core.processor.IoProcessor;
import de.freese.littlemina.core.processor.NioSocketProcessor;
import de.freese.littlemina.core.processor.SimpleIoProcessorPool;
import de.freese.littlemina.core.service.AbstractIoService;
import de.freese.littlemina.core.session.IoSession;
import de.freese.littlemina.core.session.NioSocketSession;

/**
 * Basisimplementierung eines {@link IoProcessor}s.<br>
 * Der {@link IoProcessor} teilt sich den {@link Executor} mit dem {@link IoAcceptor}.
 * 
 * @author Thomas Freese
 */
public class NioSocketAcceptor extends AbstractIoService implements IoAcceptor
{
	/**
	 * @author Thomas Freese
	 */
	private class Acceptor implements Runnable
	{
		/**
		 * Erstellt ein neues {@link Acceptor} Object.
		 */
		public Acceptor()
		{
			super();
		}

		/**
		 * This method will process new sessions for the Worker class. All keys that have had their
		 * status updates as per the Selector.selectedKeys() method will be processed here. Only
		 * keys that are ready to accept connections are handled here.
		 * <p/>
		 * Session objects are created by making new instances of SocketSessionImpl and passing the
		 * session object to the SocketIoProcessor class.
		 * 
		 * @param handles {@link Iterator}
		 * @throws Exception Falls was schief geht.
		 */
		private void processHandles(final Iterator<ServerSocketChannel> handles) throws Exception
		{
			while (handles.hasNext())
			{
				ServerSocketChannel handle = handles.next();
				handles.remove();

				NioSocketSession session = accept(handle);

				// add the session to the SocketIoProcessor
				session.getProcessor().scheduleAdd(session);
			}
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			registerHandles();

			while (NioSocketAcceptor.this.selectable)
			{
				try
				{
					// Auf neue Abfragen warten
					int selected = select();

					if (selected > 0)
					{
						processHandles(getSelectedHandles());
					}
				}
				catch (Throwable ex)
				{
					getLogger().warn(null, ex);
				}
			}

			// Aufraeumen
			unregisterHandles();

			// Cleanup all the processors, and shutdown the acceptor.
			if (!NioSocketAcceptor.this.selectable && isDisposing())
			{
				try
				{
					NioSocketAcceptor.this.processor.dispose();
				}
				catch (Exception ex)
				{
					getLogger().error(null, ex);
				}

				try
				{
					synchronized (getDisposalLock())
					{
						closeSelector();
					}
				}
				catch (Exception ex)
				{
					getLogger().error(null, ex);
				}
			}
		}
	}

	/**
	 * Thead fuer die Annahme eingehender Requests.
	 */
	private Acceptor acceptor = null;

	/**
	 *
	 */
	private final Object acceptorLock = new Object();

	/**
	 * Liefert <tt>true</tt>, wenn der {@link Executor} innerhalb dieser Instanz erzeugt und nicht
	 * vom Caller uebergeben wurde.
	 */
	private final boolean createdExecutor;

	/**
	 * Executor fuer die Ausfuehrung von I/O events.
	 */
	private final Executor executor;

	/**
	 *
	 */
	private ServerSocketChannel handle = null;

	/**
	 *
	 */
	private IoHandler handler = null;

	/**
	 *
	 */
	private InetSocketAddress localAddress = null;

	/**
	 *
	 */
	private final IoProcessor<NioSocketSession> processor;

	/** A flag set when the acceptor has been created and initialized */
	private volatile boolean selectable;

	/**
	 * Erstellt ein neues {@link NioSocketAcceptor} Object.
	 */
	public NioSocketAcceptor()
	{
		this(null);
	}

	/**
	 * Erstellt ein neues {@link NioSocketAcceptor} Object.
	 * 
	 * @param executor {@link Executor}
	 */
	@SuppressWarnings("unchecked")
	public NioSocketAcceptor(final Executor executor)
	{
		super();

		if (executor == null)
		{
			this.executor = Executors.newCachedThreadPool();
			this.createdExecutor = true;
		}
		else
		{
			this.executor = executor;
			this.createdExecutor = false;
		}

		SimpleIoProcessorPool pool =
				new SimpleIoProcessorPool(NioSocketProcessor.class, this.executor);
		pool.fillPool();

		this.processor = pool;
		// this.processor = new NioSocketProcessor(this.executor);

		try
		{
			// Initialize the selector
			setSelector(Selector.open());

			// The selector is now ready, we can switch the
			// flag to true so that incoming connection can be accepted
			this.selectable = true;
		}
		catch (RuntimeException ex)
		{
			throw ex;
		}
		catch (Exception ex)
		{
			throw new RuntimeException("Failed to initialize.", ex);
		}
		finally
		{
			if (!this.selectable)
			{
				try
				{
					closeSelector();
				}
				catch (Exception ex)
				{
					getLogger().error(null, ex);
				}
			}
		}
	}

	/**
	 * Accept a client connection for a server socket and return a new {@link IoSession} associated
	 * with the given {@link IoProcessor}
	 * 
	 * @param handle the server handle
	 * @return {@link NioSocketSession}
	 * @throws Exception any exception thrown by the underlying systems calls
	 */
	protected NioSocketSession accept(final ServerSocketChannel handle) throws Exception
	{
		SelectionKey key = handle.keyFor(getSelector());

		if ((key == null) || (!key.isValid()) || (!key.isAcceptable()))
		{
			return null;
		}

		// accept the connection from the client
		SocketChannel ch = handle.accept();

		if (ch == null)
		{
			return null;
		}

		return new NioSocketSession(this.processor, getHandler(), ch);
	}

	/**
	 * @see de.freese.littlemina.core.service.AbstractIoService#appendThreadName(java.lang.String)
	 */
	@Override
	protected String appendThreadName(final String threadName)
	{
		return threadName + ": " + getLocalAddress().getPort();
	}

	/**
	 * @see de.freese.littlemina.core.acceptor.IoAcceptor#bind(java.net.InetSocketAddress)
	 */
	@Override
	public void bind(final InetSocketAddress localAddress) throws IOException
	{
		if (isDisposing())
		{
			throw new IllegalStateException("Already disposed.");
		}

		if (localAddress == null)
		{
			throw new NullPointerException("localAddresses");
		}

		if (getHandler() == null)
		{
			throw new IllegalStateException("handler is not set.");
		}

		this.localAddress = localAddress;

		// creates the Acceptor instance and has the local
		// executor kick it off.
		startupAcceptor();

		// As we just started the acceptor, we have to unblock the select()
		// in order to process the bind request we just have added to the
		// registerQueue.
		wakeup();
	}

	/**
	 * @see de.freese.littlemina.core.acceptor.IoAcceptor#bind(int)
	 */
	@Override
	public void bind(final int port) throws IOException
	{
		bind(new InetSocketAddress(port));
	}

	/**
	 * Close a server socket.
	 * 
	 * @param handle the server socket
	 * @throws Exception any exception thrown by the underlying systems calls
	 */
	protected void close(final ServerSocketChannel handle) throws Exception
	{
		SelectionKey key = handle.keyFor(getSelector());

		if (key != null)
		{
			key.cancel();
		}

		handle.close();
	}

	/**
	 * @see de.freese.littlemina.core.acceptor.IoAcceptor#dispose()
	 */
	@Override
	public final void dispose()
	{
		if (isDisposing())
		{
			return;
		}

		synchronized (getDisposalLock())
		{
			if (!isDisposing())
			{
				setDisposing(true);

				this.selectable = false;
				disposeInternal();
			}
		}

		if (this.createdExecutor && (getExecutor() instanceof ExecutorService))
		{
			ExecutorService executorService = (ExecutorService) getExecutor();
			executorService.shutdown();

			while (!executorService.isTerminated())
			{
				try
				{
					executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
				}
				catch (InterruptedException ex)
				{
					// Ignore
				}
			}
		}

		setDisposed(true);
	}

	/**
	 * Freigeben aller Resourcen.
	 */
	protected void disposeInternal()
	{
		startupAcceptor();
		wakeup();
	}

	/**
	 * @see de.freese.littlemina.core.service.AbstractIoService#getExecutor()
	 */
	@Override
	protected Executor getExecutor()
	{
		return this.executor;
	}

	/**
	 * @see de.freese.littlemina.core.acceptor.IoAcceptor#getHandler()
	 */
	@Override
	public IoHandler getHandler()
	{
		return this.handler;
	}

	/**
	 * @see de.freese.littlemina.core.acceptor.IoAcceptor#getLocalAddress()
	 */
	@Override
	public final InetSocketAddress getLocalAddress()
	{
		return this.localAddress;
	}

	/**
	 * {@link Iterator} for the set of server sockets found with acceptable incoming connections
	 * during the last {@link Selector#select()} call.
	 * 
	 * @return the list of server handles ready
	 */
	protected Iterator<ServerSocketChannel> getSelectedHandles()
	{
		return new ServerSocketChannelIterator(getSelector().selectedKeys());
	}

	/**
	 * Open a server socket for a given local address.
	 * 
	 * @param localAddress the associated local address
	 * @return the opened server socket
	 * @throws Exception any exception thrown by the underlying systems calls
	 */
	protected ServerSocketChannel open(final SocketAddress localAddress) throws Exception
	{
		// Creates the listening ServerSocket
		ServerSocketChannel channel = ServerSocketChannel.open();

		boolean success = false;

		try
		{
			// This is a non blocking socket channel
			channel.configureBlocking(false);

			// Configure the server socket,
			ServerSocket socket = channel.socket();

			// Set the reuseAddress flag accordingly with the setting
			socket.setReuseAddress(false);

			// XXX: Do we need to provide this property? (I think we need to remove it.)
			socket.setReceiveBufferSize(2048);

			// and bind.
			socket.bind(localAddress, 50);

			// Register the channel within the selector for ACCEPT event
			channel.register(getSelector(), SelectionKey.OP_ACCEPT);
			success = true;

		}
		finally
		{
			if (!success)
			{
				close(channel);
			}
		}

		return channel;
	}

	/**
	 * Sets up the socket communications. Sets items such as:
	 * <p/>
	 * Blocking Reuse address Receive buffer size Bind to listen port Registers OP_ACCEPT for
	 * selector.
	 */
	private void registerHandles()
	{
		try
		{
			this.handle = open(getLocalAddress());
		}
		catch (Exception ex)
		{
			getLogger().error(null, ex);
		}
	}

	/**
	 * {@link IoHandler} fuer das konkrete Protokoll.
	 * 
	 * @param handler {@link IoHandler}
	 */
	public void setHandler(final IoHandler handler)
	{
		this.handler = handler;
	}

	/**
	 * This method is called by the doBind() and doUnbind() methods. If the acceptor is null, the
	 * acceptor object will be created and kicked off by the executor. If the acceptor object is
	 * null, probably already created and this class is now working, then nothing will happen and
	 * the method will just return.
	 */
	private void startupAcceptor()
	{
		synchronized (this.acceptorLock)
		{
			if (this.acceptor == null)
			{
				this.acceptor = new Acceptor();
				executeWorker(this.acceptor);
			}
		}
	}

	/**
	 * This method just checks to see if anything has been placed into the cancellation queue. The
	 * only thing that should be in the cancelQueue is CancellationRequest objects and the only
	 * place this happens is in the doUnbind() method.
	 */
	private void unregisterHandles()
	{
		try
		{
			close(this.handle);
		}
		catch (Throwable ex)
		{
			getLogger().error(null, ex);
		}
	}
}
