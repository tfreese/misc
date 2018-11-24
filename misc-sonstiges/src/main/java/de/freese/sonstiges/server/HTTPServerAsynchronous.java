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
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.sonstiges.server.handler.HttpIoHandler;
import de.freese.sonstiges.server.handler.IoHandler;

/**
 * Der Server kümmert sich um alle Verbindungen in separaten Threads.
 *
 * @author Thomas Freese
 */
public class HTTPServerAsynchronous
{
    /**
     * @author Thomas Freese
     */
    private class HttpReadHandler implements CompletionHandler<Integer, MyAttachment>
    {
        /**
        *
        */
        private final Logger LOGGER = LoggerFactory.getLogger(HttpReadHandler.class);

        /**
         * Erstellt ein neues {@link HttpReadHandler} Object.
         */
        public HttpReadHandler()
        {
            super();
        }

        /**
         * @see java.nio.channels.CompletionHandler#completed(java.lang.Object, java.lang.Object)
         */
        @Override
        public void completed(final Integer bytesRead, final MyAttachment attachment)
        {
            AsynchronousSocketChannel channel = attachment.channel;
            ByteBuffer byteBuffer = attachment.byteBuffer;
            StringBuilder httpHeader = attachment.httpHeader;

            if (bytesRead <= 0)
            {
                // Nichts mehr gelesen, Request vollständig.
                // close(channel);
                write(channel);
                return;
            }

            Charset charset = IoHandler.DEFAULT_CHARSET;

            byteBuffer.flip();

            CharBuffer charBuffer = charset.decode(byteBuffer);

            String request = charBuffer.toString();
            getLogger().debug("\n" + request);

            httpHeader.append(request);

            byteBuffer.clear();

            int length = httpHeader.length();

            // if ((httpHeader.charAt(length - 2) == '\r') && (httpHeader.charAt(length - 1) == '\n') && (httpHeader.charAt(length - 4) == '\r')
            // && (httpHeader.charAt(length - 3) == '\n'))

            char[] endOfHeader = new char[4];
            httpHeader.getChars(length - 4, length, endOfHeader, 0);

            if ((endOfHeader[0] == '\r') && (endOfHeader[1] == '\n') && (endOfHeader[2] == '\r') && (endOfHeader[3] == '\n'))
            {
                // Leerzeile = Ende des HttpHeaders.
                write(channel);
            }
            else
            {
                // Nächster Lese Vorgang.
                channel.read(byteBuffer, attachment, this);

                // Nächster Lese Vorgang im anderen Thread.
                // read(channel);
            }
        }

        /**
         * @see java.nio.channels.CompletionHandler#failed(java.lang.Throwable, java.lang.Object)
         */
        @Override
        public void failed(final Throwable exc, final MyAttachment attachment)
        {
            AsynchronousSocketChannel channel = attachment.channel;

            close(channel);
            getLogger().error(null, exc);
        }

        /**
         * @return {@link Logger}
         */
        private Logger getLogger()
        {
            return this.LOGGER;
        }
    }

    /**
     * @author Thomas Freese
     */
    private class HttpWriteHandler implements CompletionHandler<Integer, MyAttachment>
    {
        /**
        *
        */
        private final Logger LOGGER = LoggerFactory.getLogger(HttpReadHandler.class);

        /**
         * Erstellt ein neues {@link HttpWriteHandler} Object.
         */
        public HttpWriteHandler()
        {
            super();
        }

        /**
         * @see java.nio.channels.CompletionHandler#completed(java.lang.Object, java.lang.Object)
         */
        @Override
        public void completed(final Integer result, final MyAttachment attachment)
        {
            AsynchronousSocketChannel channel = attachment.channel;
            ByteBuffer byteBuffer = attachment.byteBuffer;

            while (byteBuffer.hasRemaining())
            {
                channel.write(byteBuffer, null, this);
            }

            close(channel);
        }

        /**
         * @see java.nio.channels.CompletionHandler#failed(java.lang.Throwable, java.lang.Object)
         */
        @Override
        public void failed(final Throwable exc, final MyAttachment attachment)
        {
            AsynchronousSocketChannel channel = attachment.channel;

            close(channel);
            getLogger().error(null, exc);
        }

        /**
         * @return {@link Logger}
         */
        private Logger getLogger()
        {
            return this.LOGGER;
        }
    }

    /**
     * @author Thomas Freese
     */
    public static class MyAttachment
    {
        /**
         *
         */
        ByteBuffer byteBuffer = null;

        /**
         *
         */
        AsynchronousSocketChannel channel = null;

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
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        int poolSize = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(poolSize));

        HTTPServerAsynchronous server = new HTTPServerAsynchronous(8001, channelGroup);
        server.setIoHandler(new HttpIoHandler());
        server.start();

        System.out.println();
        System.out.println();
        System.out.println("******************************************************************************************************************");
        System.out.println("You're using an IDE, click in this console and press ENTER to call System.exit() and trigger the shutdown routine.");
        System.out.println("******************************************************************************************************************");
        System.out.println();
        System.out.println();

        // Client Task starten
        ForkJoinPool.commonPool().submit((Callable<Void>) () -> {

            Thread.sleep(1000);

            InetSocketAddress serverAddress = new InetSocketAddress("localhost", 8001);
            Charset charset = IoHandler.DEFAULT_CHARSET;

            // try (SocketChannel client = SocketChannel.open(hostAddress))
            try (AsynchronousSocketChannel client = AsynchronousSocketChannel.open(channelGroup))
            {
                Future<Void> futureConnect = client.connect(serverAddress);
                futureConnect.get();

                // Request
                CharBuffer charBufferHeader = CharBuffer.allocate(256);
                charBufferHeader.put("GET / HTTP/1.1").put("\r\n");
                charBufferHeader.put("Host: localhost:8001").put("\r\n");
                charBufferHeader.put("User-Agent: " + HTTPServerAsynchronous.class.getSimpleName()).put("\r\n");
                charBufferHeader.put("Accept: text/html").put("\r\n");
                charBufferHeader.put("Accept-Language: de").put("\r\n");
                charBufferHeader.put("Accept-Encoding: gzip, deflate").put("\r\n");
                charBufferHeader.put("Connection: keep-alive").put("\r\n");
                charBufferHeader.put("").put("\r\n");
                charBufferHeader.flip();

                ByteBuffer buffer = charset.encode(charBufferHeader);

                Future<Integer> futureRequest = client.write(buffer);

                while (futureRequest.get() > 0)
                {
                    futureRequest = client.write(buffer);
                }

                // Response
                buffer = ByteBuffer.allocate(256);
                Future<Integer> futureResponse = client.read(buffer);

                while (futureResponse.get() > 0)
                {
                    buffer.flip();

                    CharBuffer charBuffer = charset.decode(buffer);

                    LOGGER.debug("\n" + charBuffer.toString().trim());

                    buffer.clear();
                    futureResponse = client.read(buffer);
                }
            }

            return null;
        });

        try
        {
            System.in.read();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        server.stop();
        System.exit(0);
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
                channelGroup.shutdownNow(); // Cancel currently executing tasks

                // Wait a while for tasks to respond to being cancelled
                if (!channelGroup.awaitTermination(5, TimeUnit.SECONDS))
                {
                    logger.error("Pool did not terminate");
                }
            }
        }
        catch (InterruptedException | IOException ex)
        {
            // (Re-)Cancel if current thread also interrupted
            try
            {
                channelGroup.shutdownNow();
            }
            catch (IOException ex2)
            {
                logger.error("Pool did not terminate");
            }

            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    /**
    *
    */
    private final AsynchronousChannelGroup channelGroup;

    /**
     *
     */
    private IoHandler ioHandler = null;

    /**
    *
    */
    private final int port;

    /**
     *
     */
    private AsynchronousServerSocketChannel serverSocketChannel = null;

    /**
     * Erstellt ein neues {@link HTTPServerAsynchronous} Object.
     *
     * @param port int
     * @throws IOException Falls was schief geht.
     */
    public HTTPServerAsynchronous(final int port) throws IOException
    {
        this(port, Math.max(1, Runtime.getRuntime().availableProcessors() - 1));
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
                if (getLogger().isDebugEnabled())
                {
                    try
                    {
                        getLogger().debug("Connection Accepted: {}", channel.getRemoteAddress());
                    }
                    catch (IOException ioex)
                    {
                        failed(ioex, null);
                    }
                }

                // Nächster Request an anderen Thread übergeben.
                accept();

                // Lese-Vorgang an anderen Thread übergeben.
                read(channel);
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
     */
    private void close(final AsynchronousSocketChannel channel)
    {
        try
        {
            channel.shutdownInput();
            channel.shutdownOutput();
            channel.close();
        }
        catch (IOException ex)
        {
            getLogger().error(null, ex);
        }
    }

    /**
     * @return {@link IoHandler}
     */
    protected IoHandler getIoHandler()
    {
        return this.ioHandler;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return LOGGER;
    }

    /**
     * @param channel {@link AsynchronousSocketChannel}
     */
    private void read(final AsynchronousSocketChannel channel)
    {
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);

        MyAttachment attachment = new MyAttachment();
        attachment.channel = channel;
        attachment.byteBuffer = byteBuffer;

        channel.read(byteBuffer, attachment, new HttpReadHandler());
    }

    /**
     * @param ioHandler {@link IoHandler}
     */
    public void setIoHandler(final IoHandler ioHandler)
    {
        this.ioHandler = ioHandler;
    }

    /**
     * Stoppen des Servers.
     *
     * @throws IOException Falls was schief geht.
     */
    public void start() throws IOException
    {
        getLogger().info("starting server on port: {}", this.port);

        Objects.requireNonNull(this.ioHandler, "ioHandler requried");

        this.serverSocketChannel = AsynchronousServerSocketChannel.open(this.channelGroup);
        this.serverSocketChannel.bind(new InetSocketAddress(this.port), 50);

        accept();
    }

    /**
     * Stoppen des Servers.
     */
    public void stop()
    {
        getLogger().info("stopping server on port: {}", this.port);

        shutdown(this.channelGroup, getLogger());
    }

    /**
     * @param channel {@link AsynchronousSocketChannel}
     */
    private void write(final AsynchronousSocketChannel channel)
    {
        Charset charset = IoHandler.DEFAULT_CHARSET;

        CharBuffer charBufferBody = CharBuffer.allocate(256);
        charBufferBody.put("<html>").put("\r\n");
        charBufferBody.put("<head>").put("\r\n");
        charBufferBody.put("<title>NIO Test</title>").put("\r\n");
        charBufferBody.put("<meta charset=\"UTF-8\">").put("\r\n");
        charBufferBody.put("</head>").put("\r\n");
        charBufferBody.put("<body>").put("\r\n");
        charBufferBody.put("Date: " + new Date().toString() + "<br>").put("\r\n");
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
}
