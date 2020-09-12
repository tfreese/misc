/**
 * Created: 31.10.2016
 */

package de.freese.sonstiges.server.async;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.sonstiges.server.ServerThreadFactory;
import de.freese.sonstiges.server.handler.IoHandler;

/**
 * Der Server kümmert sich um alle Verbindungen in separaten Threads.
 *
 * @author Thomas Freese
 */
public class ServerAsync implements Runnable
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerAsync.class);

    /**
     * @param channel {@link AsynchronousSocketChannel}
     * @param logger {@link Logger}
     */
    public static void close(final AsynchronousSocketChannel channel, final Logger logger)
    {
        try
        {
            channel.shutdownInput();
            channel.shutdownOutput();
            channel.close();
        }
        catch (IOException ex)
        {
            logger.error(null, ex);
        }
    }

    /**
     *
     */
    private final AsynchronousChannelGroup channelGroup;

    /**
     *
     */
    private final int port;

    /**
     *
     */
    private AsynchronousServerSocketChannel serverSocketChannel;

    /**
    *
    */
    private final Semaphore startLock = new Semaphore(1, true);

    /**
     * Erstellt ein neues {@link ServerAsync} Object.
     *
     * @param port int
     * @throws IOException Falls was schief geht.
     */
    public ServerAsync(final int port) throws IOException
    {
        this(port, 3);
    }

    /**
     * Erstellt ein neues {@link ServerAsync} Object.
     *
     * @param port int
     * @param channelGroup {@link AsynchronousChannelGroup}
     * @throws IOException Falls was schief geht.
     */
    public ServerAsync(final int port, final AsynchronousChannelGroup channelGroup) throws IOException
    {
        super();

        if (port <= 0)
        {
            throw new IllegalArgumentException("port <= 0: " + port);
        }

        this.port = port;
        this.channelGroup = Objects.requireNonNull(channelGroup, "channelGroup required");
    }

    /**
     * Erstellt ein neues {@link ServerAsync} Object.
     *
     * @param port int
     * @param poolSize int
     * @throws IOException Falls was schief geht.
     */
    public ServerAsync(final int port, final int poolSize) throws IOException
    {
        this(port, AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(poolSize, new ServerThreadFactory("worker-"))));
    }

    /**
     * Wartet auf neue Connections.
     */
    private void accept()
    {
        this.serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>()
        {
            /**
             * @see java.nio.channels.CompletionHandler#completed(java.lang.Object, java.lang.Object)
             */
            @Override
            public void completed(final AsynchronousSocketChannel channel, final Void attachment)
            {
                try
                {
                    LOGGER.info("{}: Connection Accepted", channel.getRemoteAddress());
                }
                catch (IOException ioex)
                {
                    failed(ioex, null);
                }

                // Nächster Request an anderen Thread übergeben.
                accept();

                // Lese-Vorgang an anderen Thread übergeben.
                read(channel, ByteBuffer.allocate(256));
            }

            /**
             * @see java.nio.channels.CompletionHandler#failed(java.lang.Throwable, java.lang.Object)
             */
            @Override
            public void failed(final Throwable ex, final Void attachment)
            {
                LOGGER.error(null, ex);
            }
        });
    }

    /**
     * @return boolean
     */
    public boolean isStarted()
    {
        return this.startLock.availablePermits() > 0;
    }

    /**
     * @param channel {@link AsynchronousSocketChannel}
     * @param byteBuffer {@link ByteBuffer}
     */
    private void read(final AsynchronousSocketChannel channel, final ByteBuffer byteBuffer)
    {
        MyAttachment attachment = new MyAttachment();
        attachment.channel = channel;
        attachment.byteBuffer = byteBuffer;

        channel.read(byteBuffer, attachment, new HttpReadHandler());
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        LOGGER.info("starting server on port: {}", this.port);

        try
        {
            // this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel = AsynchronousServerSocketChannel.open(this.channelGroup);
            this.serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            // this.serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEPORT, true); // Wird nicht von jedem OS unterstützt.
            this.serverSocketChannel.bind(new InetSocketAddress(this.port), 50);

            LOGGER.info("server listening on port: {}", this.port);
            this.startLock.release();

            accept();
        }
        catch (Exception ex)
        {
            LOGGER.error(null, ex);
        }
    }

    /**
     * @param ioHandler {@link IoHandler}
     */
    public void setIoHandler(final IoHandler<?> ioHandler)
    {
        // Empty
        // Es gibt kein gemeinsames Interface von Socketchannel und AsynchronousSocketChannel.
    }

    /**
     * Shutdown der {@link AsynchronousChannelGroup}.
     *
     * @param channelGroup {@link AsynchronousChannelGroup}
     * @param logger {@link Logger}
     */
    private void shutdown(final AsynchronousChannelGroup channelGroup, final Logger logger)
    {
        logger.info("shutdown AsynchronousChannelGroup");
        channelGroup.shutdown();

        try
        {
            // Wait a while for existing tasks to terminate.
            if (!channelGroup.awaitTermination(10, TimeUnit.SECONDS))
            {
                logger.warn("Timed out while waiting for AsynchronousChannelGroup");

                // Cancel currently executing tasks
                channelGroup.shutdownNow();

                // Wait a while for tasks to respond to being cancelled.
                if (!channelGroup.awaitTermination(5, TimeUnit.SECONDS))
                {
                    logger.error("AsynchronousChannelGroup did not terminate");
                }
            }
        }
        catch (InterruptedException | IOException ex)
        {
            // (Re-)Cancel if current thread also interrupted.
            try
            {
                channelGroup.shutdownNow();
            }
            catch (IOException ex2)
            {
                logger.error("AsynchronousChannelGroup did not terminate");
            }

            // Preserve interrupt status.
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Stoppen des Servers.
     *
     * @throws IOException Falls was schief geht.
     */
    public void start() throws IOException
    {
        Thread thread = new Thread(this::run, getClass().getSimpleName());
        thread.setDaemon(false);
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
        LOGGER.info("stopping server on port: {}", this.port);

        shutdown(this.channelGroup, LOGGER);

        LOGGER.info("server stopped on port: {}", this.port);
    }
}
