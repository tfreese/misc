// Created: 10.01.2010
/**
 * 10.01.2010
 */
package de.freese.littlemina.core.processor;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import de.freese.littlemina.core.buffer.AbstractIoBuffer;
import de.freese.littlemina.core.service.AbstractIoService;
import de.freese.littlemina.core.session.IoSession;
import de.freese.littlemina.core.session.NioSocketSession;

/**
 * Der Processor verwaltet die Lese- und Schreibvorgänge einer Session.
 *
 * @author Thomas Freese
 */
public class NioSocketProcessor extends AbstractIoService implements IoProcessor<NioSocketSession>
{
    /**
     * Queue für die neuen {@link IoSession}s.
     */
    private final Queue<NioSocketSession> newSessions = new ConcurrentLinkedQueue<>();

    /**
     * Queue für die zum entfernen vorgesehenden {@link IoSession}s.
     */
    private final Queue<NioSocketSession> removingSessions = new ConcurrentLinkedQueue<>();

    /**
     * Erstellt ein neues {@link NioSocketProcessor} Object.
     *
     * @param executor {@link Executor}
     */
    public NioSocketProcessor(final Executor executor)
    {
        super(executor);

        try
        {
            // Open a new selector
            setSelector(Selector.open());

            startup();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to open a selector.", e);
        }
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
     * Get an {@link Iterator} for the list of {@link IoSession} found selected by the last call of select(int).
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
        @SuppressWarnings("resource")
        SelectableChannel channel = session.getChannel();
        channel.configureBlocking(false);
        session.setSelectionKey(channel.register(getSelector(), SelectionKey.OP_READ, session));

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug(session.toString());
        }
    }

    /**
     * Methode zum Verarbeiten der Verbindungen.
     */
    private void process()
    {
        while (true)
        {
            try
            {
                int readyChannels = select();
                // Select mit Timeout für Idle IoSessions
                // long t0 = System.currentTimeMillis();
                // int readyChannels = select(1000L);
                // long t1 = System.currentTimeMillis();
                // long delta = (t1 - t0);
                //
                // synchronized (NioSocketProcessor.this.wakeupCalled)
                // {
                // if ((readyChannels == 0) && !NioSocketProcessor.this.wakeupCalled.get()
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
                    getLogger().debug("Selected = {}", readyChannels);

                    for (Iterator<NioSocketSession> i = getAllSessions(); i.hasNext();)
                    {
                        NioSocketSession session = i.next();
                        // Kein remove da nur debug !

                        getLogger().debug("{} {} {}", session.getChannel(), session.getSelectionKey(), session.getSelectionKey().interestOps());
                    }
                }

                if (readyChannels > 0)
                {
                    for (Iterator<NioSocketSession> i = getSelectedSessions(); i.hasNext();)
                    {
                        NioSocketSession session = i.next();
                        i.remove(); // Wichtig damit der Key aus dem Selector entfernt wird.

                        process(session);
                    }
                }

                processNewSessions();
                processRemoveSessions();

                if (isDisposing())
                {
                    break;
                }
            }
            catch (Throwable ex)
            {
                getLogger().error(null, ex);
            }
        }
    }

    /**
     * Deal with session ready for the read or write operations, or both.
     *
     * @param session {@link NioSocketSession}
     */
    private void process(final NioSocketSession session)
    {
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("{}: InterestOps={}", session.toString(), session.getSelectionKey().interestOps());
        }

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
     * Verarbeitet die neuen Sessions / Verbindungen.
     */
    private void processNewSessions()
    {
        for (NioSocketSession session = this.newSessions.poll(); session != null; session = this.newSessions.poll())
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
     * Schliesst und Zerstört alle {@link IoSession}s in der removing-Queue.
     */
    private void processRemoveSessions()
    {
        for (NioSocketSession session = this.removingSessions.poll(); session != null; session = this.removingSessions.poll())
        {
            try
            {
                if (getLogger().isDebugEnabled())
                {
                    getLogger().debug(session.toString());
                }

                // Destroy
                try (SocketChannel ch = session.getChannel())
                {
                    SelectionKey key = session.getSelectionKey();

                    if (key != null)
                    {
                        key.cancel();
                    }
                }

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
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug(session.toString());
        }

        boolean hasFragmentation = true;
        // int readBytes = 0;
        @SuppressWarnings("unused")
        int read = 0;

        try
        {
            AbstractIoBuffer buffer = AbstractIoBuffer.allocate(1024, true);
            buffer.setAutoExpand(true);

            try
            {
                if (hasFragmentation)
                {
                    while ((read = session.getChannel().read(buffer.getByteBuffer())) > 0)
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
                    read = session.getChannel().read(buffer.getByteBuffer());

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
     * @see de.freese.littlemina.core.processor.IoProcessor#scheduleAdd(de.freese.littlemina.core.session.IoSession)
     */
    @Override
    public void scheduleAdd(final NioSocketSession session)
    {
        if (isDisposing())
        {
            throw new IllegalStateException("Already disposed.");
        }

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug(session.toString());
        }

        this.newSessions.add(session);

        // startup();
        wakeup();
    }

    /**
     * @see de.freese.littlemina.core.processor.IoProcessor#scheduleRemove(de.freese.littlemina.core.session.IoSession)
     */
    @Override
    public void scheduleRemove(final NioSocketSession session)
    {
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug(session.toString());
        }

        this.removingSessions.add(session);

        // startup();
        wakeup();
    }

    /**
     * @see de.freese.littlemina.core.processor.IoProcessor#scheduleWrite(de.freese.littlemina.core.session.IoSession)
     */
    @Override
    public void scheduleWrite(final NioSocketSession session) throws Exception
    {
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug(session.toString());
        }

        setInterestedInWrite(session, true);

        // startup();
        wakeup();
    }

    /**
     * @see de.freese.littlemina.core.service.AbstractIoService#shutdown()
     */
    @Override
    protected void shutdown()
    {
        for (Iterator<NioSocketSession> i = getAllSessions(); i.hasNext();)
        {
            NioSocketSession session = i.next();
            i.remove();

            scheduleRemove(session);
        }

        wakeup(); // Um den Selector und Sessions zu schliessen.
        processRemoveSessions();

        try
        {
            closeSelector();
        }
        catch (Throwable ex)
        {
            getLogger().warn(null, ex);
        }

        // this.processor = null;
    }

    /**
     * @see de.freese.littlemina.core.service.AbstractIoService#startup()
     */
    @Override
    protected void startup()
    {
        executeWorker(() -> process());
    }

    /**
     * @param session {@link NioSocketSession}
     */
    private void write(final NioSocketSession session)
    {
        if (getLogger().isDebugEnabled())
        {
            getLogger().debug(session.toString());
        }

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
