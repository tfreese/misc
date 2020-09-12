/**
 * Created: 31.10.2016
 */

package de.freese.sonstiges.server.multithread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.sonstiges.server.ServerThreadFactory;
import de.freese.sonstiges.server.handler.IoHandler;
import de.freese.sonstiges.server.multithread.dispatcher.Dispatcher;
import de.freese.sonstiges.server.multithread.dispatcher.DispatcherPool;

/**
 * Dieser Server arbeitet nach dem Acceptor-Reactor Pattern.<br>
 * Der {@link Acceptor} nimmt die neuen Client-Verbindungen entgegen und übergibt sie einem {@link Dispatcher}.<br>
 * Der {@link Dispatcher} kümmert sich um das Connection-Handling der Clients nach dem 'accept'.<br>
 * Der {@link IoHandler} übernimmt das Lesen und Schreiben von Request und Response in einem separatem Thread.<br>
 *
 * @author Thomas Freese
 */
public class ServerMultiThread implements Runnable
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerMultiThread.class);
    /**
     *
     */
    private Acceptor acceptor;
    /**
     *
     */
    private final DispatcherPool dispatcherPool;
    /**
     *
     */
    private IoHandler<SelectionKey> ioHandler;
    /**
     *
     */
    private final int port;
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
     * Erstellt ein neues {@link ServerMultiThread} Object.
     *
     * @param port int
     * @param numOfDispatcher int
     * @param numOfWorker int
     * @throws IOException Falls was schief geht.
     */
    public ServerMultiThread(final int port, final int numOfDispatcher, final int numOfWorker) throws IOException
    {
        this(port, numOfDispatcher, numOfWorker, SelectorProvider.provider());
    }

    /**
     * Erstellt ein neues {@link ServerMultiThread} Object.
     *
     * @param port int
     * @param numOfDispatcher int
     * @param numOfWorker int
     * @param selectorProvider {@link SelectorProvider}
     * @throws IOException Falls was schief geht.
     */
    public ServerMultiThread(final int port, final int numOfDispatcher, final int numOfWorker, final SelectorProvider selectorProvider) throws IOException
    {
        super();

        if (port <= 0)
        {
            throw new IllegalArgumentException("port <= 0: " + port);
        }

        this.port = port;
        this.dispatcherPool = new DispatcherPool(numOfDispatcher, numOfWorker);
        this.selectorProvider = Objects.requireNonNull(selectorProvider, "selectorProvider required");

        this.startLock.acquireUninterruptibly();
    }

    /**
     * @return {@link Logger}
     */
    private Logger getLogger()
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
            // this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel = this.selectorProvider.openServerSocketChannel();
            this.serverSocketChannel.configureBlocking(false);
            this.serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            // this.serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEPORT, true); // Wird nicht von jedem OS unterstützt.
            this.serverSocketChannel.bind(new InetSocketAddress(this.port), 50);

            // ServerSocket socket = this.serverSocketChannel.socket();
            // socket.setReuseAddress(true);
            // socket.bind(new InetSocketAddress(this.port), 50);

            // Erzeugen der Dispatcher.
            this.dispatcherPool.start(this.ioHandler, this.selectorProvider);

            // Erzeugen des Acceptors
            this.acceptor = new Acceptor(this.selectorProvider.openSelector(), this.serverSocketChannel, this.dispatcherPool);
            getLogger().info("start acceptor");

            Thread thread = new ServerThreadFactory("acceptor-").newThread(this.acceptor);
            thread.start();

            getLogger().info("server listening on port: {}", this.port);
            this.startLock.release();
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
        this.ioHandler = Objects.requireNonNull(ioHandler, "ioHandler requried");
    }

    /**
     * Starten des Servers.
     */
    public void start()
    {
        run();

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

        this.acceptor.stop();
        this.dispatcherPool.stop();

        try
        {
            // SelectionKey selectionKey = this.serverSocketChannel.keyFor(this.selector);
            //
            // if (selectionKey != null)
            // {
            // selectionKey.cancel();
            // }

            this.serverSocketChannel.close();
        }
        catch (IOException ex)
        {
            getLogger().error(null, ex);
        }

        getLogger().info("server stopped on port: {}", this.port);
    }
}
