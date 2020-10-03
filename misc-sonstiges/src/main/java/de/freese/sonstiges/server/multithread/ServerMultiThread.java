/**
 * Created: 31.10.2016
 */

package de.freese.sonstiges.server.multithread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Objects;
import de.freese.sonstiges.server.AbstractServer;
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
public class ServerMultiThread extends AbstractServer
{
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
    private final SelectorProvider selectorProvider;
    /**
     *
     */
    private ServerSocketChannel serverSocketChannel;

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
        super(port);

        this.dispatcherPool = new DispatcherPool(numOfDispatcher, numOfWorker);
        this.selectorProvider = Objects.requireNonNull(selectorProvider, "selectorProvider required");
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        getLogger().info("starting '{}' on port: {}", getName(), getPort());

        Objects.requireNonNull(getIoHandler(), "ioHandler requried");

        try
        {
            // this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel = this.selectorProvider.openServerSocketChannel();
            this.serverSocketChannel.configureBlocking(false);

            if (this.serverSocketChannel.supportedOptions().contains(StandardSocketOptions.SO_REUSEADDR))
            {
                // this.serverSocketChannel.getOption(StandardSocketOptions.SO_REUSEADDR);
                this.serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            }

            if (this.serverSocketChannel.supportedOptions().contains(StandardSocketOptions.SO_REUSEPORT))
            {
                // this.serverSocketChannel.getOption(StandardSocketOptions.SO_REUSEPORT);
                this.serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEPORT, true);
            }

            if (this.serverSocketChannel.supportedOptions().contains(StandardSocketOptions.SO_RCVBUF))
            {
                // this.serverSocketChannel.getOption(StandardSocketOptions.SO_RCVBUF);
                this.serverSocketChannel.setOption(StandardSocketOptions.SO_RCVBUF, 64 * 1024);
            }

            if (this.serverSocketChannel.supportedOptions().contains(StandardSocketOptions.SO_SNDBUF))
            {
                // this.serverSocketChannel.getOption(StandardSocketOptions.SO_SNDBUF);
                this.serverSocketChannel.setOption(StandardSocketOptions.SO_SNDBUF, 64 * 1024);
            }

            this.serverSocketChannel.bind(new InetSocketAddress(getPort()), 50);

            // ServerSocket socket = this.serverSocketChannel.socket();
            // socket.setReuseAddress(true);
            // socket.bind(new InetSocketAddress(getPort()), 50);

            // Erzeugen der Dispatcher.
            this.dispatcherPool.start(getIoHandler(), this.selectorProvider, getName());

            // Erzeugen des Acceptors
            this.acceptor = new Acceptor(this.selectorProvider.openSelector(), this.serverSocketChannel, this.dispatcherPool);

            Thread thread = new ServerThreadFactory(getName() + "-acceptor-").newThread(this.acceptor);
            getLogger().debug("start {}", thread.getName());
            thread.start();

            getLogger().info("'{}' listening on port: {}", getName(), getPort());
            getStartLock().release();
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }

    /**
     * @see de.freese.sonstiges.server.AbstractServer#start()
     */
    @Override
    public void start()
    {
        run();

        // Warten bis fertich.
        // this.startLock.acquireUninterruptibly();
        // this.startLock.release();
    }

    /**
     * @see de.freese.sonstiges.server.AbstractServer#stop()
     */
    @Override
    public void stop()
    {
        getLogger().info("stopping '{}' on port: {}", getName(), getPort());

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

        getLogger().info("'{}' stopped on port: {}", getName(), getPort());
    }
}
