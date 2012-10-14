// Created: 16.01.2010
/**
 * 16.01.2010
 */
package de.freese.littlemina.core.service;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.base.core.concurrent.NamePreservingRunnable;
import de.freese.littlemina.core.processor.IoProcessor;
import de.freese.littlemina.core.session.NioSocketSession;

/**
 * @author Thomas Freese
 */
public abstract class AbstractIoService implements IoService
{
	/**
	 * Map fuer alle ThreadIDs jeder {@link IoProcessor} Klasse.
	 */
	private static final Map<Class<?>, AtomicInteger> THREAD_IDs =
			new HashMap<Class<?>, AtomicInteger>();

	/**
	 *
	 */
	private final Object disposalLock = new Object();

	/**
	 *
	 */
	private volatile boolean disposed = false;

	/**
	 *
	 */
	private volatile boolean disposing = false;

	/**
	 *
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 *
	 */
	private volatile Selector selector = null;

	/**
	 * Name fuer diesen Thread.
	 */
	private final String threadName;

	/**
	 * Erstellt ein neues {@link AbstractIoService} Object.
	 */
	protected AbstractIoService()
	{
		super();

		this.threadName = nextThreadName();
	}

	/**
	 * Erweitert den Namen dieses Threads fuer die Ausfuehrung im {@link Executor}.
	 * 
	 * @param threadName String
	 * @return String
	 */
	protected String appendThreadName(final String threadName)
	{
		return threadName;
	}

	/**
	 * Schliesst den {@link Selector}.
	 * 
	 * @throws Exception Falls was schief geht.
	 */
	protected final void closeSelector() throws Exception
	{
		getSelector().close();
	}

	/**
	 * Ausfuehrung innerhalb des {@link Executor}s.
	 * 
	 * @param worker {@link Runnable}
	 */
	protected final void executeWorker(final Runnable worker)
	{
		executeWorker(worker, null);
	}

	/**
	 * Ausfuehrung innerhalb des {@link Executor}s.
	 * 
	 * @param worker {@link Runnable}
	 * @param suffix String
	 */
	protected final void executeWorker(final Runnable worker, final String suffix)
	{
		String actualThreadName = appendThreadName(this.threadName);

		if (suffix != null)
		{
			actualThreadName = actualThreadName + '-' + suffix;
		}

		getExecutor().execute(new NamePreservingRunnable(worker, actualThreadName));
	}

	/**
	 * LockObjekt fuer die dispose-Methode.
	 * 
	 * @return Object
	 */
	protected final Object getDisposalLock()
	{
		return this.disposalLock;
	}

	/**
	 * Liefert den {@link Executor}.
	 * 
	 * @return {@link Executor}
	 */
	protected abstract Executor getExecutor();

	/**
	 * @return {@link Logger}
	 */
	protected final Logger getLogger()
	{
		return this.logger;
	}

	/**
	 * @return {@link Selector}
	 */
	protected final Selector getSelector()
	{
		return this.selector;
	}

	/**
	 * Liefert <tt>true</tt>, wenn der {@link Selector} einen {@link SelectionKey} mit einer nicht
	 * mehr verbundenen Connection hat.
	 * 
	 * @return true boolean
	 * @throws IOException Falls was schief geht.
	 */
	protected final boolean isBrokenConnection() throws IOException
	{
		// A flag set to true if we find a broken session
		boolean brokenSession = false;

		synchronized (getSelector())
		{
			// Get the selector keys
			Set<SelectionKey> keys = getSelector().keys();

			// Loop on all the keys to see if one of them
			// has a closed channel
			for (SelectionKey key : keys)
			{
				SelectableChannel channel = key.channel();

				if ((((channel instanceof DatagramChannel) && ((DatagramChannel) channel)
						.isConnected()))
						|| ((channel instanceof SocketChannel) && ((SocketChannel) channel)
								.isConnected()))
				{
					// The channel is not connected anymore. Cancel
					// the associated key then.
					key.cancel();

					// Set the flag to true to avoid a selector switch
					brokenSession = true;
					break;
				}
			}
		}

		return brokenSession;
	}

	/**
	 * @see de.freese.littlemina.core.service.IoService#isDisposed()
	 */
	@Override
	public final boolean isDisposed()
	{
		return this.disposed;
	}

	/**
	 * @see de.freese.littlemina.core.service.IoService#isDisposing()
	 */
	@Override
	public final boolean isDisposing()
	{
		return this.disposing;
	}

	/**
	 * Erzeugt die ThreadID dieser Instanz.<br>
	 * 
	 * @return String
	 */
	private String nextThreadName()
	{
		Class<?> cls = getClass();
		int newThreadID = 0;

		synchronized (THREAD_IDs)
		{
			// Zaehler fuer die Klasse holen
			AtomicInteger threadID = THREAD_IDs.get(cls);

			if (threadID == null)
			{
				threadID = new AtomicInteger(0);
				THREAD_IDs.put(cls, threadID);
			}

			newThreadID = threadID.incrementAndGet();
		}

		return cls.getSimpleName() + '-' + newThreadID;
	}

	/**
	 * Zerstoert einen haengenden {@link Selector} und registriert dessen {@link SelectionKey}s an
	 * einen neuen {@link Selector}.
	 * 
	 * @throws IOException If we got an exception
	 */
	protected final void registerNewSelector() throws IOException
	{
		synchronized (getSelector())
		{
			Set<SelectionKey> keys = getSelector().keys();

			// Open a new selector
			Selector newSelector = Selector.open();

			for (SelectionKey key : keys)
			{
				SelectableChannel ch = key.channel();

				// Don't forget to attache the session, and back !
				NioSocketSession session = (NioSocketSession) key.attachment();
				SelectionKey newKey = ch.register(newSelector, key.interestOps(), session);
				session.setSelectionKey(newKey);
			}

			getSelector().close();
			setSelector(newSelector);
		}
	}

	/**
	 * Selektiert die {@link SelectionKey}s, welche bereit fuer I/O Operationen sind.
	 * 
	 * @return int; Anzahl der {@link SelectionKey}s
	 * @throws Exception Falls was schief geht.
	 */
	protected final int select() throws Exception
	{
		return getSelector().select();
	}

	/**
	 * Selektiert die {@link SelectionKey}s ueber einen TimeOut, welche bereit fuer I/O Operationen
	 * sind.
	 * 
	 * @param timeout long
	 * @return int; Anzahl der {@link SelectionKey}s
	 * @throws Exception Falls was schief geht.
	 */
	protected final int select(final long timeout) throws Exception
	{
		return getSelector().select(timeout);
	}

	/**
	 * @param disposed boolean
	 */
	protected final void setDisposed(final boolean disposed)
	{
		this.disposed = disposed;
	}

	/**
	 * @param disposing boolean
	 */
	protected final void setDisposing(final boolean disposing)
	{
		this.disposing = disposing;
	}

	/**
	 * @param selector {@link Selector}
	 */
	protected final void setSelector(final Selector selector)
	{
		this.selector = selector;
	}

	/**
	 * Unterbricht die {@link Selector#select()} Methode.
	 */
	protected void wakeup()
	{
		getSelector().wakeup();
	}
}
