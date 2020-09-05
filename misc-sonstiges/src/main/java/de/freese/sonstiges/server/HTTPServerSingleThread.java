/**
 * Created: 31.10.2016
 */

package de.freese.sonstiges.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.sonstiges.server.handler.IoHandler;

/**
 * Der Server kümmert sich um alle Verbindungen in einem einzelnen Thread.
 *
 * @author Thomas Freese
 */
public class HTTPServerSingleThread implements Runnable
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPServerSingleThread.class);

    /**
     * @param selectionKey {@link SelectionKey}
     * @return String
     * @throws IOException Falls was schief geht.
     */
    private static String getRemoteAddress(final SelectionKey selectionKey) throws IOException
    {
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        String remoteAddress = channel.getRemoteAddress().toString();

        return remoteAddress;
    }

    /**
     *
     */
    private IoHandler<SelectionKey> ioHandler;

    /**
     *
     */
    private boolean isShutdown;

    /**
     *
     */
    private final int port;

    /**
     *
     */
    private Selector selector;

    /**
     *
     */
    private final SelectorProvider selectorProvider;

    /**
     *
     */
    private ServerSocketChannel serverSocketChannel;

    /**
     *
     */
    private final Semaphore stopLock = new Semaphore(1, true);

    /**
     *
     */
    private ThreadFactory threadFactory = Executors.defaultThreadFactory();

    /**
     * Erstellt ein neues {@link HTTPServerSingleThread} Object.
     *
     * @param port int
     * @throws IOException Falls was schief geht.
     */
    public HTTPServerSingleThread(final int port) throws IOException
    {
        this(port, SelectorProvider.provider());
    }

    /**
     * Erstellt ein neues {@link HTTPServerSingleThread} Object.
     *
     * @param port int
     * @param selectorProvider {@link SelectorProvider}
     * @throws IOException Falls was schief geht.
     */
    public HTTPServerSingleThread(final int port, final SelectorProvider selectorProvider) throws IOException
    {
        super();

        this.port = port;
        this.selectorProvider = Objects.requireNonNull(selectorProvider, "selectorProvider required");
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return LOGGER;
    }

    /**
     * Wartet auf neue Connections.
     */
    private void listen()
    {
        getLogger().info("server listening on port: {}", this.port);

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
                            getLogger().debug("{}: SelectionKey not valid", getRemoteAddress(selectionKey));
                        }

                        if (selectionKey.isAcceptable())
                        {
                            // Verbindung mit Client herstellen.
                            SocketChannel socketChannel = this.serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(this.selector, SelectionKey.OP_READ);

                            getLogger().debug("{}: Connection Accepted", socketChannel.getRemoteAddress());

                            // SelectionKey sk = socketChannel.register(this.selector, SelectionKey.OP_READ);
                            // sk.attach(obj)

                            // Selector aufwecken.
                            this.selector.wakeup();
                        }
                        else if (selectionKey.isConnectable())
                        {
                            getLogger().debug("{}: Client Connected", getRemoteAddress(selectionKey));
                        }
                        else if (selectionKey.isReadable())
                        {
                            getLogger().debug("{}: Read Request", getRemoteAddress(selectionKey));

                            // Request lesen.
                            this.ioHandler.read(selectionKey);
                        }
                        else if (selectionKey.isWritable())
                        {
                            getLogger().debug("{}: Write Response", getRemoteAddress(selectionKey));

                            // Response schreiben.
                            this.ioHandler.write(selectionKey);
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

        getLogger().info("server stopped on port: {}", this.port);
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        getLogger().info("starting server on port: {}", this.port);

        Objects.requireNonNull(this.ioHandler, "ioHandler requried");

        try
        {
            this.selector = this.selectorProvider.openSelector();

            this.serverSocketChannel = this.selectorProvider.openServerSocketChannel();
            this.serverSocketChannel.configureBlocking(false);

            ServerSocket socket = this.serverSocketChannel.socket();
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(this.port), 50);

            @SuppressWarnings("unused")
            SelectionKey selectionKey = this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
            // selectionKey.attach(this);

            listen();
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }

    /**
     * @param ioHandler {@link IoHandler}
     */
    public void setIoHandler(final IoHandler<SelectionKey> ioHandler)
    {
        this.ioHandler = ioHandler;
    }

    /**
     * @param threadFactory {@link ThreadFactory}
     */
    public void setThreadFactory(final ThreadFactory threadFactory)
    {
        this.threadFactory = Objects.requireNonNull(threadFactory, "threadFactory required");
        ;
    }

    /**
     * Stoppen des Servers.
     *
     * @throws IOException Falls was schief geht.
     */
    public void start() throws IOException
    {
        Thread thread = this.threadFactory.newThread(this::run);
        thread.setName(getClass().getSimpleName());
        thread.setDaemon(false);
        thread.start();
    }

    /**
     * Stoppen des Servers.
     */
    public void stop()
    {
        getLogger().info("stopping server on port: {}", this.port);

        this.isShutdown = true;
        this.selector.wakeup();

        this.stopLock.acquireUninterruptibly();

        try
        {
            SelectionKey selectionKey = this.serverSocketChannel.keyFor(this.selector);

            if (selectionKey != null)
            {
                selectionKey.cancel();
            }

            this.serverSocketChannel.close();

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
