// Created: 10.01.2010
/**
 * 10.01.2010
 */
package de.freese.littlemina.core.processor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import de.freese.littlemina.core.buffer.IoBuffer;
import de.freese.littlemina.core.service.AbstractIoService;
import de.freese.littlemina.core.session.IoSession;
import de.freese.littlemina.core.session.NioSocketSession;

/**
 * @author Thomas Freese
 */
public class NioSocketProcessor extends AbstractIoService implements IoProcessor<NioSocketSession>
{
	/**
	 * @author Thomas Freese
	 */
	private class Processor implements Runnable
	{
		/**
		 * Erstellt ein neues {@link Processor} Object.
		 */
		public Processor()
		{
			super();
		}

		/**
		 * Zum testen
		 */
		private void debugAllSessions()
		{
			Iterator<NioSocketSession> iterator = getAllSessions();

			if (iterator.hasNext())
			{
				getLogger().info("\n");
			}

			while (iterator.hasNext())
			{
				NioSocketSession session = iterator.next();

				getLogger().info(
						session.getChannel() + " " + session.getSelectionKey() + " "
								+ session.getSelectionKey().interestOps());
			}
		}

		/**
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			while (true)
			{
				try
				{
					int selected = select();
					// Select mit Timeout fuer Idle IoSessions
					// long t0 = System.currentTimeMillis();
					// int selected = select(1000L);
					// long t1 = System.currentTimeMillis();
					// long delta = (t1 - t0);
					//
					// synchronized (NioSocketProcessor.this.wakeupCalled)
					// {
					// if ((selected == 0) && !NioSocketProcessor.this.wakeupCalled.get()
					// && (delta < 100))
					// {
					// // Last chance : the select() may have been
					// // interrupted because we have had an closed channel.
					// if (isBrokenConnection())
					// {
					// // we can reselect immediately
					// continue;
					// }
					// else
					// {
					// getLogger().warn(
					// "Create a new selector. Selected is 0, delta = "
					// + (t1 - t0));
					// // Ok, we are hit by the nasty epoll
					// // spinning.
					// // Basically, there is a race condition
					// // which causes a closing file descriptor not to be
					// // considered as available as a selected channel, but
					// // it stopped the select. The next time we will
					// // call select(), it will exit immediately for the same
					// // reason, and do so forever, consuming 100%
					// // CPU.
					// // We have to destroy the selector, and
					// // register all the socket on a new one.
					// registerNewSelector();
					// }
					//
					// // and continue the loop
					// continue;
					// }
					//
					// NioSocketProcessor.this.wakeupCalled.getAndSet(false);
					// }

					if (getLogger().isDebugEnabled())
					{
						getLogger().debug("Selected = {}", selected);

						debugAllSessions();
					}

					if (selected > 0)
					{
						process();
					}

					processNewSessions();
					processRemoveSessions();

					if (isDisposing())
					{
						synchronized (getDisposalLock())
						{
							if (isSelectorEmpty())
							{
								break;
							}

							for (Iterator<NioSocketSession> i = getAllSessions(); i.hasNext();)
							{
								scheduleRemove(i.next());
							}

							wakeup();
						}
					}
				}
				catch (Throwable ex)
				{
					getLogger().error(null, ex);
				}
			}

			try
			{
				synchronized (getDisposalLock())
				{
					if (isDisposing())
					{
						closeSelector();
					}
				}
			}
			catch (Throwable ex)
			{
				getLogger().error(null, ex);
			}

			NioSocketProcessor.this.processor = null;
		}
	}

	/**
	 *
	 */
	private final Executor executor;

	/**
	 * Queue fuer die neuen {@link IoSession}s.
	 */
	private final Queue<NioSocketSession> newSessions =
			new ConcurrentLinkedQueue<NioSocketSession>();

	/**
	 * Der Processor thread, verarbeitet die eingehenden Daten.
	 */
	private Processor processor = null;

	/**
	 *
	 */
	private final Object processorLock = new Object();

	/**
	 * Queue fuer die zum entfernen vorgesehenden {@link IoSession}s.
	 */
	private final Queue<NioSocketSession> removingSessions =
			new ConcurrentLinkedQueue<NioSocketSession>();

	/**
	 *
	 */
	private AtomicBoolean wakeupCalled = new AtomicBoolean(false);

	/**
	 * Erstellt ein neues {@link NioSocketProcessor} Object.
	 * 
	 * @param executor {@link Executor}
	 */
	public NioSocketProcessor(final Executor executor)
	{
		super();

		if (executor == null)
		{
			throw new NullPointerException("executor");
		}

		this.executor = executor;

		try
		{
			// Open a new selector
			setSelector(Selector.open());
		}
		catch (IOException e)
		{
			throw new RuntimeException("Failed to open a selector.", e);
		}
	}

	/**
	 * @see de.freese.littlemina.core.processor.IoProcessor#dispose()
	 */
	@Override
	public void dispose()
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
				startupProcessor();
				wakeup();
			}
		}

		setDisposed(true);
	}

	/**
	 * Get an {@link Iterator} for the list of {@link IoSession} polled by this {@link IoProcessor}
	 * 
	 * @return {@link Iterator} of {@link IoSession}
	 */
	protected Iterator<NioSocketSession> getAllSessions()
	{
		return new IoSessionIterator(getSelector().keys());
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
	 * Get an {@link Iterator} for the list of {@link IoSession} found selected by the last call of
	 * select(int).
	 * 
	 * @return {@link Iterator} of {@link IoSession} read for I/Os operation
	 */
	protected Iterator<NioSocketSession> getSelectedSessions()
	{
		return new IoSessionIterator(getSelector().selectedKeys());
	}

	/**
	 * Initialize the polling of a session. Add it to the polling process.
	 * 
	 * @param session the {@link IoSession} to add to the polling
	 * @throws Exception any exception thrown by the underlying system calls
	 */
	protected void init(final NioSocketSession session) throws Exception
	{
		SelectableChannel channel = session.getChannel();
		channel.configureBlocking(false);
		session.setSelectionKey(channel.register(getSelector(), SelectionKey.OP_READ, session));

		getLogger().debug(session.toString());
	}

	/**
	 * is this session registered for reading
	 * 
	 * @param session the session queried
	 * @return true is registered for reading
	 */
	protected boolean isInterestedInRead(final NioSocketSession session)
	{
		SelectionKey key = session.getSelectionKey();

		return key.isValid() && ((key.interestOps() & SelectionKey.OP_READ) != 0);
	}

	/**
	 * is this session registered for writing
	 * 
	 * @param session the session queried
	 * @return true is registered for writing
	 */
	protected boolean isInterestedInWrite(final NioSocketSession session)
	{
		SelectionKey key = session.getSelectionKey();

		return key.isValid() && ((key.interestOps() & SelectionKey.OP_WRITE) != 0);
	}

	/**
	 * Is the session ready for reading
	 * 
	 * @param session the session queried
	 * @return true is ready, false if not ready
	 */
	protected boolean isReadable(final NioSocketSession session)
	{
		SelectionKey key = session.getSelectionKey();

		return key.isValid() && key.isReadable();
	}

	/**
	 * Say if the list of {@link IoSession} polled by this {@link IoProcessor} is empty
	 * 
	 * @return true if at least a session is managed by this {@link IoProcessor}
	 */
	protected boolean isSelectorEmpty()
	{
		return getSelector().keys().isEmpty();
	}

	/**
	 * Is the session ready for writing
	 * 
	 * @param session the session queried
	 * @return true is ready, false if not ready
	 */
	protected boolean isWritable(final NioSocketSession session)
	{
		SelectionKey key = session.getSelectionKey();

		return key.isValid() && key.isWritable();
	}

	/**
	 * @throws Exception Falls was schief geht.
	 */
	private void process() throws Exception
	{
		for (Iterator<NioSocketSession> i = getSelectedSessions(); i.hasNext();)
		{
			NioSocketSession session = i.next();
			process(session);
			i.remove();
		}
	}

	/**
	 * Deal with session ready for the read or write operations, or both.
	 * 
	 * @param session {@link NioSocketSession}
	 */
	private void process(final NioSocketSession session)
	{
		getLogger().debug("{}: InterestOps={}", session.toString(),
				session.getSelectionKey().interestOps());

		if (isReadable(session))
		{
			read(session);
		}

		if (isWritable(session))
		{
			write(session);
		}
	}

	/**
	 * 
	 */
	private void processNewSessions()
	{
		for (NioSocketSession session = this.newSessions.poll(); session != null; session =
				this.newSessions.poll())
		{
			try
			{
				init(session);

				session.getHandler().sessionOpened(session);
			}
			catch (Exception ex)
			{
				getLogger().error(session.toString(), ex);

				scheduleRemove(session);
			}
		}
	}

	/**
	 * Schliesst und Zerstoert alle {@link IoSession}s in der removing-Queue.
	 */
	private void processRemoveSessions()
	{
		for (NioSocketSession session = this.removingSessions.poll(); session != null; session =
				this.removingSessions.poll())
		{
			try
			{
				getLogger().debug(session.toString());

				// Destroy
				SocketChannel ch = session.getChannel();
				SelectionKey key = session.getSelectionKey();

				if (key != null)
				{
					key.cancel();
				}

				ch.close();

				session.getHandler().sessionClosed(session);
			}
			catch (Exception ex)
			{
				getLogger().error(session.toString(), ex);
			}
		}
	}

	/**
	 * @param session {@link NioSocketSession}
	 */
	private void read(final NioSocketSession session)
	{
		getLogger().debug(session.toString());

		boolean hasFragmentation = true;
		// int readBytes = 0;
		@SuppressWarnings("unused")
		int read = 0;

		try
		{
			IoBuffer buffer = IoBuffer.allocate(1024);
			buffer.setAutoExpand(true);

			try
			{
				if (hasFragmentation)
				{
					while ((read = read(session, buffer)) > 0)
					{
						// readBytes += read;

						if (!buffer.hasRemaining())
						{
							break;
						}
					}
				}
				else
				{
					read = read(session, buffer);

					// if (read > 0)
					// {
					// readBytes = read;
					// }
				}
			}
			finally
			{
				buffer.flip();
			}

			session.setBuffer(buffer);
			setInterestedInRead(session, false);
			session.getHandler().messageReceived(session);
		}
		catch (Exception ex)
		{
			getLogger().error(session.toString(), ex);
			scheduleRemove(session);
		}
	}

	/**
	 * Reads a sequence of bytes from a {@link IoSession} into the given {@link ByteBuffer}.
	 * 
	 * @param session {@link NioSocketSession}
	 * @param buffer {@link IoBuffer}
	 * @return the number of bytes read
	 * @throws Exception any exception thrown by the underlying system calls
	 */
	private int read(final NioSocketSession session, final IoBuffer buffer) throws Exception
	{
		return session.getChannel().read(buffer.getByteBuffer());
	}

	/**
	 * @see de.freese.littlemina.core.processor.IoProcessor#scheduleAdd(de.freese.littlemina.core.session.IoSession)
	 */
	@Override
	public void scheduleAdd(final NioSocketSession session)
	{
		if (isDisposing())
		{
			throw new IllegalStateException("Already disposed.");
		}

		getLogger().debug(session.toString());

		this.newSessions.add(session);
		startupProcessor();
		wakeup();
	}

	/**
	 * @see de.freese.littlemina.core.processor.IoProcessor#scheduleRemove(de.freese.littlemina.core.session.IoSession)
	 */
	@Override
	public void scheduleRemove(final NioSocketSession session)
	{
		getLogger().debug(session.toString());

		this.removingSessions.add(session);
		startupProcessor();
		wakeup();
	}

	/**
	 * @see de.freese.littlemina.core.processor.IoProcessor#scheduleWrite(de.freese.littlemina.core.session.IoSession)
	 */
	@Override
	public void scheduleWrite(final NioSocketSession session) throws Exception
	{
		getLogger().debug(session.toString());

		setInterestedInWrite(session, true);
		startupProcessor();
		wakeup();
	}

	/**
	 * Registriert eine {@link IoSession} fuer das lesen.
	 * 
	 * @param session {@link IoSession}
	 * @param isInterested true for registering, false for removing
	 * @throws Exception Falls was schief geht.
	 */
	private void setInterestedInRead(final NioSocketSession session, final boolean isInterested)
		throws Exception
	{
		// synchronized (session.getSelectionKey())
		// {
		SelectionKey key = session.getSelectionKey();
		int oldInterestOps = key.interestOps();
		int newInterestOps = oldInterestOps;

		if (isInterested)
		{
			newInterestOps |= SelectionKey.OP_READ;
		}
		else
		{
			newInterestOps &= ~SelectionKey.OP_READ;
		}

		if (oldInterestOps != newInterestOps)
		{
			key.interestOps(newInterestOps);
		}
		// }
	}

	/**
	 * Registriert eine {@link IoSession} fuer das schreiben.
	 * 
	 * @param session {@link IoSession}
	 * @param isInterested true for registering, false for removing
	 * @throws Exception Falls was schief geht.
	 */
	private void setInterestedInWrite(final NioSocketSession session, final boolean isInterested)
		throws Exception
	{
		// synchronized (session.getSelectionKey())
		// {
		SelectionKey key = session.getSelectionKey();
		int oldInterestOps = key.interestOps();
		int newInterestOps = oldInterestOps;

		if (isInterested)
		{
			newInterestOps |= SelectionKey.OP_WRITE;
		}
		else
		{
			newInterestOps &= ~SelectionKey.OP_WRITE;
		}

		if (oldInterestOps != newInterestOps)
		{
			key.interestOps(newInterestOps);
		}
		// }
	}

	/**
	 * Startet den internen Processor ueber den {@link Executor}.
	 */
	private void startupProcessor()
	{
		synchronized (this.processorLock)
		{
			if (this.processor == null)
			{
				this.processor = new Processor();
				executeWorker(this.processor);
			}
		}
	}

	/**
	 * @see de.freese.littlemina.core.service.AbstractIoService#wakeup()
	 */
	@Override
	protected void wakeup()
	{
		synchronized (this.wakeupCalled)
		{
			this.wakeupCalled.getAndSet(true);

			super.wakeup();
		}
	}

	/**
	 * @param session {@link NioSocketSession}
	 */
	private void write(final NioSocketSession session)
	{
		getLogger().debug(session.toString());

		try
		{
			if (session.getBuffer() != null)
			{
				session.getChannel().write(session.getBuffer().getByteBuffer());
			}

			setInterestedInWrite(session, false);

			// Nach erfolgreichen Schreiben pruefen ob geschlossen werden soll.
			if (session.isMarkedForClosing())
			{
				scheduleRemove(session);
			}
			else
			{
				setInterestedInRead(session, true);
			}
		}
		catch (Exception ex)
		{
			getLogger().error(session.toString(), ex);
			scheduleRemove(session);
		}
	}
}
