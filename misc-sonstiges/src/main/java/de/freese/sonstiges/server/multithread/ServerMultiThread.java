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
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.sonstiges.server.handler.IoHandler;

/**
 * Der {@link Acceptor} nimmt die neuen Client-Verbindungen entgegen und übergibt sie einem {@link Reactor}.<br>
 * Der {@link Reactor} kümmert sich asynchron um das weitere Connection-Handling.<br>
 * Der {@link IoHandler} übernimmt das Lesen und Schreiben von Request und Response.<br>
 * Hier werden die {@link Reactor} im Round-Robin Verfahren wiederverwendet.<br>
 * Anderfalls müsste ein ThreadPool verwendet werden, wenn ein Reactor nur jeweils einen Client bedienen soll, siehe {@link ReactorSingleClient}.
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
    private IoHandler<SelectionKey> ioHandler;

    /**
     *
     */
    private boolean isShutdown;

    /**
     *
     */
    private final int numOfReactors;

    /**
     *
     */
    private final int port;

    /**
     *
     */
    private final LinkedList<Reactor> reactors = new LinkedList<>();

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
    private final Semaphore startLock = new Semaphore(1, true);

    /**
     *
     */
    private ThreadFactory threadFactory = Executors.defaultThreadFactory();

    /**
     * Erstellt ein neues {@link ServerMultiThread} Object.
     *
     * @param port int
     * @param numOfReactors int
     * @throws IOException Falls was schief geht.
     */
    public ServerMultiThread(final int port, final int numOfReactors) throws IOException
    {
        this(port, numOfReactors, SelectorProvider.provider());
    }

    /**
     * Erstellt ein neues {@link ServerMultiThread} Object.
     *
     * @param port int
     * @param numOfReactors int
     * @param selectorProvider {@link SelectorProvider}
     * @throws IOException Falls was schief geht.
     */
    public ServerMultiThread(final int port, final int numOfReactors, final SelectorProvider selectorProvider) throws IOException
    {
        super();

        if (port <= 0)
        {
            throw new IllegalArgumentException("port <= 0: " + port);
        }

        if (numOfReactors < 1)
        {
            throw new IllegalArgumentException("numOfReactors < 1: " + numOfReactors);
        }

        this.port = port;
        this.numOfReactors = numOfReactors;
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
     * Liefert den nächsten {@link Reactor} im Round-Robin Verfahren.<br>
     *
     * @return {@link Reactor}
     */
    private synchronized Reactor nextReactor()
    {
        // Ersten Reactor entnehmen.
        Reactor reactor = this.reactors.poll();

        // Reactor wieder hinten dran hängen.
        this.reactors.add(reactor);

        return reactor;
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

            // Erzeugen der Reactors.
            while (this.reactors.size() < this.numOfReactors)
            {
                Reactor reactor = new Reactor(this.selectorProvider.openSelector(), this.ioHandler);
                this.reactors.add(reactor);

                String threadName = "reactor-" + this.reactors.size();
                getLogger().info("start reactor: {}", threadName);

                Thread thread = this.threadFactory.newThread(reactor);
                thread.setName(threadName);
                thread.setDaemon(true);
                thread.start();
            }

            // Erzeugen des Acceptors
            this.acceptor = new Acceptor(this.selectorProvider.openSelector(), this.serverSocketChannel, this::nextReactor);
            getLogger().info("start acceptor");

            Thread thread = this.threadFactory.newThread(this.acceptor);
            thread.setName("acceptor");
            thread.setDaemon(true);
            thread.start();

            getLogger().info("server listening on port: {}", this.port);
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
        finally
        {
            this.startLock.release();
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
    }

    /**
     * Starten des Servers.
     */
    public void start()
    {
        this.startLock.acquireUninterruptibly();

        run();

        // Warten bis die Initialisierung fertig ist.
        this.startLock.acquireUninterruptibly();
    }

    /**
     * Stoppen des Servers.
     */
    public void stop()
    {
        getLogger().info("stopping server on port: {}", this.port);

        this.isShutdown = true;

        this.acceptor.stop();
        this.reactors.forEach(Reactor::stop);

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
