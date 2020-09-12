/**
 * Created: 31.10.2016
 */

package de.freese.sonstiges.server.singlethread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.sonstiges.server.ServerMain;
import de.freese.sonstiges.server.ServerThreadFactory;
import de.freese.sonstiges.server.handler.IoHandler;

/**
 * Der Server kümmert sich um alle Verbindungen in einem einzelnen Thread.
 *
 * @author Thomas Freese
 */
public class ServerSingleThread implements Runnable
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSingleThread.class);

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
     * ReentrantLock nicht möglich, da dort die Locks auf Thread-Ebene verwaltet werden.
     */
    private final Semaphore startLock = new Semaphore(1, true);
    /**
     * ReentrantLock nicht möglich, da dort die Locks auf Thread-Ebene verwaltet werden.
     */
    private final Semaphore stopLock = new Semaphore(1, true);

    /**
     * Erstellt ein neues {@link ServerSingleThread} Object.
     *
     * @param port int
     * @throws IOException Falls was schief geht.
     */
    public ServerSingleThread(final int port) throws IOException
    {
        this(port, SelectorProvider.provider());
    }

    /**
     * Erstellt ein neues {@link ServerSingleThread} Object.
     *
     * @param port int
     * @param selectorProvider {@link SelectorProvider}
     * @throws IOException Falls was schief geht.
     */
    public ServerSingleThread(final int port, final SelectorProvider selectorProvider) throws IOException
    {
        super();

        if (port <= 0)
        {
            throw new IllegalArgumentException("port <= 0: " + port);
        }

        this.port = port;
        this.selectorProvider = Objects.requireNonNull(selectorProvider, "selectorProvider required");

        this.startLock.acquireUninterruptibly();
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return LOGGER;
    }

    /**
     * @return boolean
     */
    public boolean isStarted()
    {
        return this.startLock.availablePermits() > 0;
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

            // this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel = this.selectorProvider.openServerSocketChannel();
            this.serverSocketChannel.configureBlocking(false);
            this.serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            // this.serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEPORT, true); // Wird nicht von jedem OS unterstützt.
            this.serverSocketChannel.bind(new InetSocketAddress(this.port), 50);

            // ServerSocket socket = this.serverSocketChannel.socket();
            // socket.setReuseAddress(true);
            // socket.bind(new InetSocketAddress(this.port), 50);

            // SelectionKey selectionKey =
            this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
            // selectionKey.attach(this);

            getLogger().info("server listening on port: {}", this.port);

            this.stopLock.acquireUninterruptibly();
            this.startLock.release();

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

                    try
                    {
                        while (iterator.hasNext())
                        {
                            SelectionKey selectionKey = iterator.next();
                            iterator.remove();

                            if (!selectionKey.isValid())
                            {
                                getLogger().info("{}: SelectionKey not valid", ServerMain.getRemoteAddress(selectionKey));
                            }
                            else if (selectionKey.isAcceptable())
                            {
                                // Verbindung mit Client herstellen.
                                SocketChannel socketChannel = this.serverSocketChannel.accept();
                                socketChannel.configureBlocking(false);
                                socketChannel.register(this.selector, SelectionKey.OP_READ);

                                getLogger().info("{}: Connection Accepted", socketChannel.getRemoteAddress());

                                // SelectionKey sk = socketChannel.register(this.selector, SelectionKey.OP_READ);
                                // sk.attach(obj)

                                // Selector aufwecken.
                                this.selector.wakeup();
                            }
                            else if (selectionKey.isConnectable())
                            {
                                getLogger().info("{}: Client Connected", ServerMain.getRemoteAddress(selectionKey));
                            }
                            else if (selectionKey.isReadable())
                            {
                                getLogger().info("{}: Read Request", ServerMain.getRemoteAddress(selectionKey));

                                // Request lesen.
                                this.ioHandler.read(selectionKey);
                            }
                            else if (selectionKey.isWritable())
                            {
                                getLogger().info("{}: Write Response", ServerMain.getRemoteAddress(selectionKey));

                                // Response schreiben.
                                this.ioHandler.write(selectionKey);
                            }
                        }
                    }
                    finally
                    {
                        selected.clear();
                    }
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
     * @param ioHandler {@link IoHandler}
     */
    public void setIoHandler(final IoHandler<SelectionKey> ioHandler)
    {
        this.ioHandler = Objects.requireNonNull(ioHandler, "ioHandler required");
    }

    /**
     * Stoppen des Servers.
     *
     * @throws IOException Falls was schief geht.
     */
    public void start() throws IOException
    {
        Thread thread = new ServerThreadFactory(getClass().getSimpleName() + "-").newThread(this::run);
        thread.start();

        // Warten bis fertich.
        // this.startLock.acquireUninterruptibly();
        // this.startLock.release();
    }

    /**
     * Stoppen des Servers.
     */
    public void stop()
    {
        getLogger().info("stopping server on port: {}", this.port);

        this.isShutdown = true;
        this.selector.wakeup();

        // Warten bis Thread beendet.
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
