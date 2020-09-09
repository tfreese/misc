// Created: 08.09.2020
package de.freese.sonstiges.server.multithread;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import de.freese.sonstiges.server.handler.IoHandler;

/**
 * Der {@link Reactor} kümmert sich asynchron um das weitere Connection-Handling für mehrere Clients.<br>
 * Der {@link IoHandler} übernimmt das Lesen und Schreiben von Request und Response.<br>
 * Anderfalls müsste ein ThreadPool verwendet werden, wenn ein Reactor nur jeweils einen Client bedienen soll, siehe {@link ReactorSingleClient}.
 *
 * @author Thomas Freese
 */
class Reactor extends AbstractNioProcessor
{
    /**
    *
    */
    private final IoHandler<SelectionKey> ioHandler;

    /**
     * Queue für die neuen {@link SocketChannel}s.
     */
    private final Queue<SocketChannel> newSessions = new ConcurrentLinkedQueue<>();

    /**
     * Erstellt ein neues {@link Reactor} Object.
     *
     * @param selector {@link Selector}
     * @param ioHandler {@link IoHandler}
     */
    Reactor(final Selector selector, final IoHandler<SelectionKey> ioHandler)
    {
        super(selector);

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
        if (isShutdown())
        {
            return;
        }

        Objects.requireNonNull(socketChannel, "socketChannel required");

        getNewSessions().add(socketChannel);

        getSelector().wakeup();
    }

    /**
     * @see de.freese.sonstiges.server.multithread.AbstractNioProcessor#afterSelectorLoop()
     */
    @Override
    protected void afterSelectorLoop()
    {
        // Die neuen Sessions zum Selector hinzufügen.
        processNewSessions();
    }

    /**
     * @see de.freese.sonstiges.server.multithread.AbstractNioProcessor#afterSelectorWhile()
     */
    @Override
    protected void afterSelectorWhile()
    {
        // Neue Channels gleich wieder schliessen.
        for (Iterator<SocketChannel> iterator = getNewSessions().iterator(); iterator.hasNext();)
        {
            SocketChannel socketChannel = iterator.next();
            iterator.remove();

            try
            {

                socketChannel.close();
            }
            catch (Exception ex)
            {
                getLogger().error(null, ex);
            }
        }

        super.afterSelectorWhile();
    }

    /**
     * @return {@link Queue}
     */
    protected Queue<SocketChannel> getNewSessions()
    {
        return this.newSessions;
    }

    /**
     * @see de.freese.sonstiges.server.multithread.AbstractNioProcessor#onReadable(java.nio.channels.SelectionKey)
     */
    @Override
    protected void onReadable(final SelectionKey selectionKey)
    {
        // Request lesen.
        this.ioHandler.read(selectionKey);
    }

    /**
     * @see de.freese.sonstiges.server.multithread.AbstractNioProcessor#onWritable(java.nio.channels.SelectionKey)
     */
    @Override
    protected void onWritable(final SelectionKey selectionKey)
    {
        // Response schreiben.
        this.ioHandler.write(selectionKey);
    }

    /**
     * Die neuen Sessions zum Selector hinzufügen.
     *
     * @see #addSession(SocketChannel)
     */
    @SuppressWarnings("unused")
    private void processNewSessions()
    {
        if (isShutdown())
        {
            return;
        }

        // for (SocketChannel socketChannel = getNewSessions().poll(); socketChannel != null; socketChannel = this.newSessions.poll())
        while (!getNewSessions().isEmpty())
        {
            SocketChannel socketChannel = getNewSessions().poll();

            if (socketChannel == null)
            {
                continue;
            }

            try
            {
                socketChannel.configureBlocking(false);

                getLogger().info("{}: attach new session", socketChannel.getRemoteAddress());

                SelectionKey selectionKey = socketChannel.register(getSelector(), SelectionKey.OP_READ);
                // selectionKey.attach(obj)
            }
            catch (Exception ex)
            {
                getLogger().error(null, ex);
            }
        }
    }
}
