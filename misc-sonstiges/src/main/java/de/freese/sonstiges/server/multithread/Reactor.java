// Created: 08.09.2020
package de.freese.sonstiges.server.multithread;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.sonstiges.server.handler.IoHandler;

/**
 * Ein {@link Reactor} übernimmt für mehrere Clients das Connection-Handlng.
 *
 * @author Thomas Freese
 */
class Reactor implements Runnable
{
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(Reactor.class);
    /**
    *
    */
    private final IoHandler<SelectionKey> ioHandler;
    /**
    *
    */
    private boolean isShutdown;
    /**
     * Queue für die neuen {@link SocketChannel}s.
     */
    private final Queue<SocketChannel> newSessions = new ConcurrentLinkedQueue<>();
    /**
    *
    */
    private final Selector selector;
    /**
    *
    */
    private final Semaphore stopLock = new Semaphore(1, true);

    /**
     * Erstellt ein neues {@link Reactor} Object.
     *
     * @param selector {@link Selector}
     * @param ioHandler {@link IoHandler}
     */
    Reactor(final Selector selector, final IoHandler<SelectionKey> ioHandler)
    {
        super();

        this.selector = Objects.requireNonNull(selector, "selector required");
        this.ioHandler = Objects.requireNonNull(ioHandler, "ioHandler required");

    }

    /**
     * Neue Session zum Worker hinzufügen.
     *
     * @param socketChannel {@link SocketChannel}
     * @throws IOException Falls was schief geht.
     * @see #processNewSessions()
     */
    void addSession(final SocketChannel socketChannel) throws IOException
    {
        Objects.requireNonNull(socketChannel, "socketChannel required");

        this.newSessions.add(socketChannel);

        this.selector.wakeup();
    }

    /**
     * @return {@link Logger}
     */
    private Logger getLogger()
    {
        return LOGGER;
    }

    /**
     * Die neuen Sessions zum Selector hinzufügen.
     *
     * @throws IOException Falls was schief geht.
     * @see #addSession(SocketChannel)
     */
    @SuppressWarnings("unused")
    private void processNewSessions() throws IOException
    {
        // for (SocketChannel socketChannel = this.newSessions.poll(); socketChannel != null; socketChannel =
        // this.newSessions.poll())
        while (!this.newSessions.isEmpty())
        {
            SocketChannel socketChannel = this.newSessions.poll();

            if (socketChannel == null)
            {
                continue;
            }

            socketChannel.configureBlocking(false);

            getLogger().info("{}: attach new session", socketChannel.getRemoteAddress());

            SelectionKey selectionKey = socketChannel.register(this.selector, SelectionKey.OP_READ);
            // selectionKey.attach(obj)
        }
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        this.stopLock.acquireUninterruptibly();

        try
        {
            while (!Thread.interrupted())
            {
                int readyChannels = this.selector.select();

                if (this.isShutdown || !this.selector.isOpen())
                {
                    break;
                }

                if (readyChannels > 0)
                {
                    Set<SelectionKey> selected = this.selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selected.iterator();

                    while (iterator.hasNext())
                    {
                        SelectionKey selectionKey = iterator.next();
                        iterator.remove();

                        if (!selectionKey.isValid())
                        {
                            getLogger().info("{}: SelectionKey not valid", ServerMultiThread.getRemoteAddress(selectionKey));
                        }

                        if (selectionKey.isReadable())
                        {
                            getLogger().info("{}: Read Request", ServerMultiThread.getRemoteAddress(selectionKey));

                            // Request lesen.
                            this.ioHandler.read(selectionKey);
                        }
                        else if (selectionKey.isWritable())
                        {
                            getLogger().info("{}: Write Response", ServerMultiThread.getRemoteAddress(selectionKey));

                            // Response schreiben.
                            this.ioHandler.write(selectionKey);
                        }
                    }

                    selected.clear();
                }

                // Die neuen Sessions zum Selector hinzufügen.
                processNewSessions();
            }
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
        finally
        {
            this.stopLock.release();
        }

        getLogger().info("reactor stopped");
    }

    /**
     * Stoppen des Reactors.
     */
    void stop()
    {
        getLogger().info("stopping reactor");

        this.isShutdown = true;
        this.selector.wakeup();

        this.stopLock.acquireUninterruptibly();

        try
        {
            // Neue Channels gleich wieder schliessen.
            for (Iterator<SocketChannel> iterator = this.newSessions.iterator(); iterator.hasNext();)
            {
                SocketChannel socketChannel = iterator.next();

                socketChannel.close();

                iterator.remove();
            }

            // Die Keys aufräumen.
            Set<SelectionKey> selected = this.selector.selectedKeys();
            Iterator<SelectionKey> iterator = selected.iterator();

            while (iterator.hasNext())
            {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();

                if (selectionKey != null)
                {
                    selectionKey.cancel();
                }
            }

            // Selector schliessen.
            if (this.selector.isOpen())
            {
                this.selector.close();
            }
        }
        catch (IOException ex)
        {
            getLogger().error(null, ex);
        }
        finally
        {
            this.stopLock.release();
        }
    }
}
