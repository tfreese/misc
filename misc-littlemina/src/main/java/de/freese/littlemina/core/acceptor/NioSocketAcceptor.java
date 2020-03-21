// Created: 10.01.2010
/**
 * 10.01.2010
 */
package de.freese.littlemina.core.acceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import de.freese.littlemina.core.IoHandler;
import de.freese.littlemina.core.processor.IoProcessor;
import de.freese.littlemina.core.processor.NioSocketProcessor;
import de.freese.littlemina.core.processor.NioSocketProcessorPool;
import de.freese.littlemina.core.service.AbstractIoService;
import de.freese.littlemina.core.session.IoSession;
import de.freese.littlemina.core.session.NioSocketSession;

/**
 * Basisimplementierung eines {@link IoProcessor}s.<br>
 * Der {@link IoProcessor} teilt sich den {@link Executor} mit dem {@link IoAcceptor}.<br>
 * Der Acceptor wartet auf Requests und reicht diese an den {@link NioSocketProcessor} weiter.
 *
 * @author Thomas Freese
 */
public class NioSocketAcceptor extends AbstractIoService implements IoAcceptor
{
    /**
     * Liefert <tt>true</tt>, wenn der {@link Executor} innerhalb dieser Instanz erzeugt und nicht vom Caller übergeben wurde.
     */
    private boolean createdExecutor = false;

    /**
     *
     */
    private ServerSocketChannel handle = null;

    /**
     *
     */
    private IoHandler handler = null;

    /**
     *
     */
    private InetSocketAddress localAddress = null;

    /**
     *
     */
    private final IoProcessor<NioSocketSession> processor;

    /**
     * Erstellt ein neues {@link NioSocketAcceptor} Object.
     */
    public NioSocketAcceptor()
    {
        this(Executors.newCachedThreadPool());

        this.createdExecutor = true;
    }

    /**
     * Erstellt ein neues {@link NioSocketAcceptor} Object.
     *
     * @param executor {@link Executor}
     */
    @SuppressWarnings("resource")
    public NioSocketAcceptor(final Executor executor)
    {
        super(executor);

        Supplier<NioSocketProcessor> creator = () -> new NioSocketProcessor(executor);
        Consumer<NioSocketProcessor> disposer = NioSocketProcessor::dispose;

        NioSocketProcessorPool pool = new NioSocketProcessorPool(creator, disposer);
        // pool.fillPool();

        this.processor = pool;
        // this.processor = new NioSocketProcessor(this.executor);

        try
        {
            // Initialize the selector
            setSelector(Selector.open());
        }
        catch (IOException ex)
        {
            try
            {
                closeSelector();
            }
            catch (IOException ex2)
            {
                getLogger().warn(null, ex2);
            }

            throw new RuntimeException("Failed to initialize.", ex);
        }
    }

    /**
     * Methode zum Verarbeiten der Requests.
     */
    private void accept()
    {
        registerHandles();

        while (true)
        {
            try
            {
                // Auf neue Abfragen warten.
                int readyChannels = select();

                if (readyChannels > 0)
                {
                    // This method will process new sessions for the Worker class.
                    // All keys that have had their status updates as per the Selector.selectedKeys() method
                    // will be processed here. Only keys that are ready to accept connections are handled here.
                    // Session objects are created by making new instances of SocketSessionImpl and passing
                    // the session object to the SocketIoProcessor class.
                    for (Iterator<ServerSocketChannel> i = getAcceptableHandles(); i.hasNext();)
                    {
                        @SuppressWarnings("resource")
                        ServerSocketChannel handle = i.next();
                        i.remove(); // Wichtig damit der Key aus dem Selector entfernt wird.

                        NioSocketSession session = accept(handle);

                        // add the session to the SocketIoProcessor
                        session.getProcessor().scheduleAdd(session);
                    }
                }

                if (isDisposing())
                {
                    break;
                }
            }
            catch (Throwable ex)
            {
                getLogger().warn(null, ex);
            }
        }
    }

    /**
     * Accept a client connection for a server socket and return a new {@link IoSession} associated with the given {@link IoProcessor}
     *
     * @param handle the server handle
     * @return {@link NioSocketSession}
     * @throws Exception any exception thrown by the underlying systems calls
     */
    @SuppressWarnings("resource")
    private NioSocketSession accept(final ServerSocketChannel handle) throws Exception
    {
        // SelectionKey key = handle.keyFor(getSelector());

        // accept the connection from the client
        SocketChannel socketChannel = handle.accept();

        if (socketChannel == null)
        {
            return null;
        }

        return new NioSocketSession(this.processor, getHandler(), socketChannel);
    }

    /**
     * @see de.freese.littlemina.core.service.AbstractIoService#appendThreadName(java.lang.String)
     */
    @Override
    protected String appendThreadName(final String threadName)
    {
        return threadName + ": " + getLocalAddress().getPort();
    }

    /**
     * @see de.freese.littlemina.core.acceptor.IoAcceptor#bind(java.net.InetSocketAddress)
     */
    @Override
    public void bind(final InetSocketAddress localAddress) throws IOException
    {
        if (isDisposing())
        {
            throw new IllegalStateException("Already disposed.");
        }

        Objects.requireNonNull(localAddress, "localAddress required");
        Objects.requireNonNull(getHandler(), "getHandler() required");

        this.localAddress = localAddress;

        startup();

        // As we just started the acceptor, we have to unblock the select()
        // in order to process the bind request we just have added to the
        // registerQueue.
        wakeup();

        getLogger().info("Listening on port {}", localAddress);
    }

    /**
     * @see de.freese.littlemina.core.acceptor.IoAcceptor#bind(int)
     */
    @Override
    public void bind(final int port) throws IOException
    {
        bind(new InetSocketAddress(port));
    }

    /**
     * Close a server socket.
     *
     * @param handle the server socket
     * @throws Exception any exception thrown by the underlying systems calls
     */
    @SuppressWarnings("resource")
    private void close(final ServerSocketChannel handle) throws Exception
    {
        SelectionKey key = handle.keyFor(getSelector());

        if (key != null)
        {
            key.cancel();
        }

        handle.close();
    }

    /**
     * @see de.freese.littlemina.core.service.AbstractIoService#dispose()
     */
    @Override
    public void dispose()
    {
        super.dispose();

        if (this.createdExecutor && (getExecutor() instanceof ExecutorService))
        {
            ExecutorService executorService = (ExecutorService) getExecutor();

            if (!executorService.isShutdown())
            {
                executorService.shutdown();

                while (!executorService.isTerminated())
                {
                    try
                    {
                        // Warten bis laufende Tasks sich beenden.
                        if (!executorService.awaitTermination(3, TimeUnit.SECONDS))
                        {
                            executorService.shutdownNow();

                            // Laufende Tasks abbrechen und warten auf Rückmeldung.
                            if (!executorService.awaitTermination(3, TimeUnit.SECONDS))
                            {
                                getLogger().error("Pool did not terminate");
                            }
                        }
                    }
                    catch (InterruptedException ie)
                    {
                        // Abbruch wenn laufender Thread interruped ist.
                        executorService.shutdownNow();

                        // Interruptd Status signalisieren.
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    /**
     * {@link Iterator} for the set of server sockets found with acceptable incoming connections during the last {@link Selector#select()} call.
     *
     * @return {@link Iterator}
     */
    @SuppressWarnings("resource")
    private Iterator<ServerSocketChannel> getAcceptableHandles()
    {
        return new ServerSocketChannelIterator(getSelector().selectedKeys());
    }

    /**
     * {@link IoHandler} für das konkrete Protokoll.
     *
     * @return {@link IoHandler}
     */
    private IoHandler getHandler()
    {
        return this.handler;
    }

    /**
     * Liefert die mit dem {@link IoAcceptor} verbundenen lokalen Addresse.
     *
     * @return {@link InetSocketAddress}
     */
    private InetSocketAddress getLocalAddress()
    {
        return this.localAddress;
    }

    /**
     * Open a server socket for a given local address.
     *
     * @param localAddress the associated local address
     * @return the opened server socket
     * @throws Exception any exception thrown by the underlying systems calls
     */
    @SuppressWarnings("resource")
    private ServerSocketChannel open(final SocketAddress localAddress) throws Exception
    {
        // Creates the listening ServerSocket
        ServerSocketChannel channel = ServerSocketChannel.open();

        boolean success = false;

        try
        {
            // This is a non blocking socket channel
            channel.configureBlocking(false);

            // Configure the server socket,
            ServerSocket socket = channel.socket();

            // Set the reuseAddress flag accordingly with the setting
            socket.setReuseAddress(true);

            // XXX: Do we need to provide this property? (I think we need to remove it.)
            socket.setReceiveBufferSize(2048);

            // and bind.
            socket.bind(localAddress, 50);

            // Register the channel within the selector for ACCEPT event
            channel.register(getSelector(), SelectionKey.OP_ACCEPT);
            success = true;

        }
        finally
        {
            if (!success)
            {
                close(channel);
            }
        }

        return channel;
    }

    /**
     * Sets up the socket communications. Sets items such as: Blocking Reuse address Receive buffer size Bind to listen port Registers OP_ACCEPT for selector.
     */
    private void registerHandles()
    {
        try
        {
            this.handle = open(getLocalAddress());
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }

    /**
     * {@link IoHandler} für das konkrete Protokoll.
     *
     * @param handler {@link IoHandler}
     */
    public void setHandler(final IoHandler handler)
    {
        this.handler = handler;
    }

    /**
     * @see de.freese.littlemina.core.service.AbstractIoService#shutdown()
     */
    @Override
    protected void shutdown()
    {
        wakeup(); // Um den Selector zu schliessen.

        // Aufräumen
        unregisterHandles();

        // Cleanup all the processors, and shutdown the acceptor.
        try
        {
            this.processor.dispose();
        }
        catch (Exception ex)
        {
            getLogger().warn(null, ex);
        }

        try
        {
            closeSelector();
        }
        catch (IOException ex)
        {
            getLogger().warn(null, ex);
        }
    }

    /**
     * @see de.freese.littlemina.core.service.AbstractIoService#startup()
     */
    @Override
    protected void startup()
    {
        executeWorker(() -> accept());
    }

    /**
     * This method just checks to see if anything has been placed into the cancellation queue. The only thing that should be in the cancelQueue is
     * CancellationRequest objects and the only place this happens is in the doUnbind() method.
     */
    private void unregisterHandles()
    {
        try
        {
            close(this.handle);
        }
        catch (Throwable ex)
        {
            getLogger().error(null, ex);
        }
    }
}
