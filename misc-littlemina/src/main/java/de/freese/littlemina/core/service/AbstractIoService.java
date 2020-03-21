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
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.littlemina.core.NamePreservingRunnable;
import de.freese.littlemina.core.processor.IoProcessor;
import de.freese.littlemina.core.session.IoSession;
import de.freese.littlemina.core.session.NioSocketSession;

/**
 * @author Thomas Freese
 */
public abstract class AbstractIoService implements IoService
{
    /**
     * Map für alle ThreadIDs jeder {@link IoProcessor} Klasse.
     */
    private static final Map<Class<?>, AtomicInteger> THREAD_IDs = new HashMap<>();

    /**
     *
     */
    private final Object disposalLock = new Object();

    /**
     *
     */
    private Boolean disposed = null;

    /**
    *
    */
    private final Executor executor;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private volatile Selector selector = null;

    /**
     * Name für diesen Thread.
     */
    private final String threadName;

    /**
     * Erstellt ein neues {@link AbstractIoService} Object.
     *
     * @param executor {@link Executor}
     */
    protected AbstractIoService(final Executor executor)
    {
        super();

        Objects.requireNonNull(executor, "executor required");

        this.executor = executor;

        this.threadName = nextThreadName();
    }

    /**
     * Erweitert den Namen dieses Threads für die Ausführung im {@link Executor}.
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
     * @throws IOException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    protected final void closeSelector() throws IOException
    {
        if (getSelector().isOpen())
        {
            getSelector().close();
        }
    }

    /**
     * @see de.freese.littlemina.core.service.IoService#dispose()
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
                setDisposing();

                shutdown();
            }
        }

        setDisposed();
    }

    /**
     * Ausführung innerhalb des {@link Executor}s.
     *
     * @param worker {@link Runnable}
     */
    protected final void executeWorker(final Runnable worker)
    {
        executeWorker(worker, null);
    }

    /**
     * Ausführung innerhalb des {@link Executor}s.
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
     * LockObjekt für die dispose-Methode.
     *
     * @return Object
     */
    protected final Object getDisposalLock()
    {
        return this.disposalLock;
    }

    /**
     * Liefert den {@link Executor} für die Ausführung von I/O Events.
     *
     * @return {@link Executor}
     */
    protected Executor getExecutor()
    {
        return this.executor;
    }

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
     * Liefert <tt>true</tt>, wenn der {@link Selector} einen {@link SelectionKey} mit einer nicht mehr verbundenen Connection hat.
     *
     * @return true boolean
     * @throws IOException Falls was schief geht.
     */
    @SuppressWarnings("resource")
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

                if ((((channel instanceof DatagramChannel) && ((DatagramChannel) channel).isConnected()))
                        || ((channel instanceof SocketChannel) && ((SocketChannel) channel).isConnected()))
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
        return Boolean.TRUE.equals(this.disposed);
    }

    /**
     * @see de.freese.littlemina.core.service.IoService#isDisposing()
     */
    @Override
    public final boolean isDisposing()
    {
        return Boolean.FALSE.equals(this.disposed);
    }

    /**
     * is this session registered for reading.
     *
     * @param session {@link IoSession}
     * @return true is registered for reading
     */
    protected boolean isInterestedInRead(final IoSession session)
    {
        SelectionKey key = session.getSelectionKey();

        return key.isValid() && ((key.interestOps() & SelectionKey.OP_READ) != 0);
    }

    /**
     * is this session registered for writing.
     *
     * @param session {@link IoSession}
     * @return true is registered for writing
     */
    protected boolean isInterestedInWrite(final IoSession session)
    {
        SelectionKey key = session.getSelectionKey();

        return key.isValid() && ((key.interestOps() & SelectionKey.OP_WRITE) != 0);
    }

    /**
     * Is the session ready for reading.
     *
     * @param session {@link IoSession}
     * @return true is ready, false if not ready
     */
    protected boolean isReadable(final IoSession session)
    {
        SelectionKey key = session.getSelectionKey();

        return key.isValid() && key.isReadable();
    }

    /**
     * Is the session ready for writing.
     *
     * @param session {@link IoSession}
     * @return true is ready, false if not ready
     */
    protected boolean isWritable(final IoSession session)
    {
        SelectionKey key = session.getSelectionKey();

        return key.isValid() && key.isWritable();
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
     * Zerstört einen hängenden {@link Selector} und registriert dessen {@link SelectionKey}s an einen neuen {@link Selector}.
     *
     * @throws IOException If we got an exception
     */
    @SuppressWarnings("resource")
    protected final void registerNewSelector() throws IOException
    {
        synchronized (getSelector())
        {
            Set<SelectionKey> keys = getSelector().keys();

            // Open a new selector.
            Selector newSelector = Selector.open();

            for (SelectionKey key : keys)
            {
                SelectableChannel channel = key.channel();

                // Don't forget to attache the session, and back !
                NioSocketSession session = (NioSocketSession) key.attachment();
                SelectionKey newKey = channel.register(newSelector, key.interestOps(), session);
                session.setSelectionKey(newKey);
            }

            getSelector().close();
            setSelector(newSelector);
        }
    }

    /**
     * Selektiert die {@link SelectionKey}s, welche bereit für I/O Operationen sind.
     *
     * @return int; Anzahl der {@link SelectionKey}s
     * @throws Exception Falls was schief geht.
     */
    @SuppressWarnings("resource")
    protected final int select() throws Exception
    {
        return getSelector().select();
    }

    /**
     * Selektiert die {@link SelectionKey}s über einen TimeOut, welche bereit für I/O Operationen sind.
     *
     * @param timeout long
     * @return int; Anzahl der {@link SelectionKey}s
     * @throws Exception Falls was schief geht.
     */
    @SuppressWarnings("resource")
    protected final int select(final long timeout) throws Exception
    {
        return getSelector().select(timeout);
    }

    /**
     *
     */
    protected final void setDisposed()
    {
        this.disposed = Boolean.TRUE;
    }

    /**
     *
     */
    protected final void setDisposing()
    {
        this.disposed = Boolean.FALSE;
    }

    /**
     * Registriert eine {@link IoSession} fuer das lesen.
     *
     * @param session {@link IoSession}
     * @param isInterested true for registering, false for removing
     * @throws Exception Falls was schief geht.
     */
    protected void setInterestedInRead(final IoSession session, final boolean isInterested) throws Exception
    {
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
    }

    /**
     * Registriert eine {@link IoSession} fuer das schreiben.
     *
     * @param session {@link IoSession}
     * @param isInterested true for registering, false for removing
     * @throws Exception Falls was schief geht.
     */
    protected void setInterestedInWrite(final IoSession session, final boolean isInterested) throws Exception
    {
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
    }

    /**
     * @param selector {@link Selector}
     */
    protected final void setSelector(final Selector selector)
    {
        this.selector = selector;
    }

    /**
     * Stoppen des Services durch die {@link #dispose()} Methode.
     */
    protected abstract void shutdown();

    /**
     * Starten des {@link IoService}.
     */
    protected abstract void startup();

    /**
     * Unterbricht die {@link Selector#select()} Methode.
     */
    @SuppressWarnings("resource")
    protected void wakeup()
    {
        getSelector().wakeup();
    }
}
