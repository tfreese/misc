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
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import de.freese.sonstiges.server.AbstractServer;
import de.freese.sonstiges.server.ServerThreadFactory;

/**
 * Der Server kümmert sich um alle Verbindungen in separaten Threads.
 *
 * @author Thomas Freese
 */
public class ServerAsync extends AbstractServer
{
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
    private AsynchronousServerSocketChannel serverSocketChannel;

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
        super(port);

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
                    getLogger().debug("{}: Connection Accepted", channel.getRemoteAddress());
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
                getLogger().error(null, ex);
            }
        });
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
        getLogger().info("starting '{}' on port: {}", getName(), getPort());

        try
        {
            // this.serverSocketChannel = ServerSocketChannel.open();
            this.serverSocketChannel = AsynchronousServerSocketChannel.open(this.channelGroup);
            this.serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            // this.serverSocketChannel.setOption(StandardSocketOptions.SO_REUSEPORT, true); // Wird nicht von jedem OS unterstützt.
            this.serverSocketChannel.bind(new InetSocketAddress(getPort()), 50);

            getLogger().info("'{}' listening on port: {}", getName(), getPort());
            getStartLock().release();

            accept();
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }

    /**
     * Shutdown der {@link AsynchronousChannelGroup}.
     *
     * @param channelGroup {@link AsynchronousChannelGroup}
     * @param logger {@link Logger}
     */
    private void shutdown(final AsynchronousChannelGroup channelGroup, final Logger logger)
    {
        logger.debug("shutdown AsynchronousChannelGroup");
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

        shutdown(this.channelGroup, getLogger());

        getLogger().info("'{}' stopped on port: {}", getName(), getPort());
    }
}
