/**
 * Created: 31.10.2016
 */

package de.freese.sonstiges.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.sonstiges.server.handler.IoHandler;

/**
 * Der Server kümmert sich um alle Verbindungen in separaten Threads.
 *
 * @author Thomas Freese
 */
public class HTTPServerAsynchronous implements Runnable
{
    /**
     * @author Thomas Freese
     */
    private static class HttpReadHandler implements CompletionHandler<Integer, MyAttachment>
    {
        /**
         *
         */
        private static final Logger LOGGER = LoggerFactory.getLogger(HttpReadHandler.class);

        /**
         * @see java.nio.channels.CompletionHandler#completed(java.lang.Object, java.lang.Object)
         */
        @Override
        public void completed(final Integer bytesRead, final MyAttachment attachment)
        {
            AsynchronousSocketChannel channel = attachment.channel;
            ByteBuffer byteBuffer = attachment.byteBuffer;
            StringBuilder httpHeader = attachment.httpHeader;

            try
            {
                LOGGER.debug("{}: Read Request", channel.getRemoteAddress());
            }
            catch (IOException ioex)
            {
                failed(ioex, null);
            }

            if (bytesRead <= 0)
            {
                // Nichts mehr zum lesen, Request vollständig.
                // Write Vorgang an anderen Thread übergeben.
                write(channel);
                return;
            }

            Charset charset = IoHandler.DEFAULT_CHARSET;

            byteBuffer.flip();
            CharBuffer charBuffer = charset.decode(byteBuffer);

            String request = charBuffer.toString();
            LOGGER.debug("\n{}", request);

            httpHeader.append(request);

            byteBuffer.clear();

            int length = httpHeader.length();

            char[] endOfHeader = new char[4];
            httpHeader.getChars(length - 4, length, endOfHeader, 0);

            if ((endOfHeader[0] == '\r') && (endOfHeader[1] == '\n') && (endOfHeader[2] == '\r') && (endOfHeader[3] == '\n'))
            {
                // Leerzeile = Ende des HttpHeaders.
                write(channel);
            }
            else
            {
                // Nächster Lese Vorgang in diesem Thread,
                channel.read(byteBuffer, attachment, this);

                // Nächster Lese Vorgang im anderen Thread.
                // read(channel, byteBuffer);
            }
        }

        /**
         * @see java.nio.channels.CompletionHandler#failed(java.lang.Throwable, java.lang.Object)
         */
        @Override
        public void failed(final Throwable exc, final MyAttachment attachment)
        {
            AsynchronousSocketChannel channel = attachment.channel;

            close(channel, LOGGER);
            LOGGER.error(null, exc);
        }
    }

    /**
     * @author Thomas Freese
     */
    private static class HttpWriteHandler implements CompletionHandler<Integer, MyAttachment>
    {
        /**
         *
         */
        private final Logger LOGGER = LoggerFactory.getLogger(HttpWriteHandler.class);

        /**
         * @see java.nio.channels.CompletionHandler#completed(java.lang.Object, java.lang.Object)
         */
        @Override
        public void completed(final Integer result, final MyAttachment attachment)
        {
            AsynchronousSocketChannel channel = attachment.channel;
            ByteBuffer byteBuffer = attachment.byteBuffer;

            try
            {
                this.LOGGER.debug("{}: Write Response", channel.getRemoteAddress());
            }
            catch (IOException ioex)
            {
                failed(ioex, null);
            }

            while (byteBuffer.hasRemaining())
            {
                channel.write(byteBuffer, null, this);
            }

            close(channel, this.LOGGER);
        }

        /**
         * @see java.nio.channels.CompletionHandler#failed(java.lang.Throwable, java.lang.Object)
         */
        @Override
        public void failed(final Throwable exc, final MyAttachment attachment)
        {
            AsynchronousSocketChannel channel = attachment.channel;

            close(channel, this.LOGGER);
            this.LOGGER.error(null, exc);
        }
    }

    /**
     * @author Thomas Freese
     */
    private static class MyAttachment
    {
        /**
         *
         */
        ByteBuffer byteBuffer;

        /**
         *
         */
        AsynchronousSocketChannel channel;

        /**
         *
         */
        StringBuilder httpHeader = new StringBuilder();
    }

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPServerAsynchronous.class);

    /**
     * @param channel {@link AsynchronousSocketChannel}
     * @param logger {@link Logger}
     */
    private static void close(final AsynchronousSocketChannel channel, final Logger logger)
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
     * @param channel {@link AsynchronousSocketChannel}
     * @param byteBuffer {@link ByteBuffer}
     */
    private static void read(final AsynchronousSocketChannel channel, final ByteBuffer byteBuffer)
    {
        MyAttachment attachment = new MyAttachment();
        attachment.channel = channel;
        attachment.byteBuffer = byteBuffer;

        channel.read(byteBuffer, attachment, new HttpReadHandler());
    }

    /**
     * Shutdown der {@link AsynchronousChannelGroup}.
     *
     * @param channelGroup {@link AsynchronousChannelGroup}
     * @param logger {@link Logger}
     */
    public static void shutdown(final AsynchronousChannelGroup channelGroup, final Logger logger)
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
     * @param channel {@link AsynchronousSocketChannel}
     */
    private static void write(final AsynchronousSocketChannel channel)
    {
        Charset charset = IoHandler.DEFAULT_CHARSET;

        CharBuffer charBufferBody = CharBuffer.allocate(256);
        charBufferBody.put("<html>").put("\r\n");
        charBufferBody.put("<head>").put("\r\n");
        charBufferBody.put("<title>NIO Test</title>").put("\r\n");
        charBufferBody.put("<meta charset=\"UTF-8\">").put("\r\n");
        charBufferBody.put("</head>").put("\r\n");
        charBufferBody.put("<body>").put("\r\n");
        charBufferBody.put("Date: " + LocalDateTime.now() + "<br>").put("\r\n");
        charBufferBody.put("</body>").put("\r\n");
        charBufferBody.put("</html>").put("\r\n");

        CharBuffer charBuffer = CharBuffer.allocate(1024);
        charBuffer.put("HTTP/1.1 200 OK").put("\r\n");
        charBuffer.put("Server: nio").put("\r\n");
        charBuffer.put("Content-type: text/html").put("\r\n");
        charBuffer.put("Content-length: " + (charBufferBody.position() * 2)).put("\r\n");
        charBuffer.put("\r\n");

        charBufferBody.flip();
        charBuffer.put(charBufferBody);
        charBuffer.flip();

        ByteBuffer byteBuffer = charset.encode(charBuffer);

        MyAttachment attachment = new MyAttachment();
        attachment.channel = channel;
        attachment.byteBuffer = byteBuffer;

        channel.write(byteBuffer, attachment, new HttpWriteHandler());
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
     * Erstellt ein neues {@link HTTPServerAsynchronous} Object.
     *
     * @param port int
     * @throws IOException Falls was schief geht.
     */
    public HTTPServerAsynchronous(final int port) throws IOException
    {
        this(port, 3);
    }

    /**
     * Erstellt ein neues {@link HTTPServerAsynchronous} Object.
     *
     * @param port int
     * @param channelGroup {@link AsynchronousChannelGroup}
     * @throws IOException Falls was schief geht.
     */
    public HTTPServerAsynchronous(final int port, final AsynchronousChannelGroup channelGroup) throws IOException
    {
        super();

        this.port = port;
        this.channelGroup = Objects.requireNonNull(channelGroup, "channelGroup required");
    }

    /**
     * Erstellt ein neues {@link HTTPServerAsynchronous} Object.
     *
     * @param port int
     * @param poolSize int
     * @throws IOException Falls was schief geht.
     */
    public HTTPServerAsynchronous(final int port, final int poolSize) throws IOException
    {
        this(port, AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(poolSize)));
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
        this.startLock.release();

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
                listen();

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
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        getLogger().info("starting server on port: {}", this.port);

        try
        {
            this.serverSocketChannel = AsynchronousServerSocketChannel.open(this.channelGroup);
            this.serverSocketChannel.bind(new InetSocketAddress(this.port), 50);

            getLogger().info("server listening on port: {}", this.port);
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
    public void setIoHandler(final IoHandler<?> ioHandler)
    {
        // Empty
    }

    /**
     * Stoppen des Servers.
     *
     * @throws IOException Falls was schief geht.
     */
    public void start() throws IOException
    {
        this.startLock.acquireUninterruptibly();

        Thread thread = new Thread(this::run, getClass().getSimpleName());
        thread.setDaemon(false);
        thread.start();

        // Warten bis die Initialisierung fertig ist.
        this.startLock.acquireUninterruptibly();
    }

    /**
     * Stoppen des Servers.
     */
    public void stop()
    {
        getLogger().info("stopping server on port: {}", this.port);

        shutdown(this.channelGroup, getLogger());

        getLogger().info("server stopped on port: {}", this.port);
    }
}
