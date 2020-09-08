// Created: 08.09.2020
package de.freese.sonstiges.server.multithread;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class Acceptor implements Runnable
{
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(Acceptor.class);
    /**
    *
    */
    private boolean isShutdown;
    /**
     *
     */
    private final Supplier<Reactor> reactorSupplier;
    /**
    *
    */
    private final Selector selector;
    /**
    *
    */
    private final ServerSocketChannel serverSocketChannel;
    /**
    *
    */
    private final Semaphore stopLock = new Semaphore(1, true);

    /**
     * Erstellt ein neues {@link Acceptor} Object.
     *
     * @param selector {@link Selector}
     * @param serverSocketChannel {@link ServerSocketChannel}
     * @param reactorSupplier {@link Supplier}
     */
    public Acceptor(final Selector selector, final ServerSocketChannel serverSocketChannel, final Supplier<Reactor> reactorSupplier)
    {
        super();

        this.selector = Objects.requireNonNull(selector, "selector required");
        this.serverSocketChannel = Objects.requireNonNull(serverSocketChannel, "serverSocketChannel required");
        this.reactorSupplier = Objects.requireNonNull(reactorSupplier, "reactorSupplier required");
    }

    /**
     * @return {@link Logger}
     */
    private Logger getLogger()
    {
        return LOGGER;
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
            this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);

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
                        else if (selectionKey.isAcceptable())
                        {
                            // Verbindung mit Client herstellen.
                            SocketChannel socketChannel = this.serverSocketChannel.accept();

                            getLogger().info("{}: Connection Accepted", socketChannel.getRemoteAddress());

                            // Socket dem Reactor übergeben.
                            this.reactorSupplier.get().addSession(socketChannel);
                        }
                        else if (selectionKey.isConnectable())
                        {
                            getLogger().info("{}: Client Connected", ServerMultiThread.getRemoteAddress(selectionKey));
                        }
                    }

                    selected.clear();
                }
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

        getLogger().info("acceptor stopped");
    }

    /**
     * Stoppen des Reactors.
     */
    void stop()
    {
        getLogger().info("stopping acceptor");

        this.isShutdown = true;
        this.selector.wakeup();

        this.stopLock.acquireUninterruptibly();

        try
        {
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
