/**
 * Created: 31.10.2016
 */

package de.freese.sonstiges.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.sonstiges.server.handler.HttpIoHandler;
import de.freese.sonstiges.server.handler.IoHandler;

/**
 * Der Server kümmert sich um alle Verbindungen in einem einzelnen Thread.
 *
 * @author Thomas Freese
 */
public class HTTPServerSingleThread
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPServerSingleThread.class);

    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        HTTPServerSingleThread server = new HTTPServerSingleThread(8001);
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

            try (SocketChannel client = SocketChannel.open(serverAddress))
            {
                client.configureBlocking(true);

                // Request
                CharBuffer charBufferHeader = CharBuffer.allocate(256);
                charBufferHeader.put("GET / HTTP/1.1").put("\r\n");
                charBufferHeader.put("Host: localhost:8001").put("\r\n");
                charBufferHeader.put("User-Agent: " + HTTPServerSingleThread.class.getSimpleName()).put("\r\n");
                charBufferHeader.put("Accept: text/html").put("\r\n");
                charBufferHeader.put("Accept-Language: de").put("\r\n");
                charBufferHeader.put("Accept-Encoding: gzip, deflate").put("\r\n");
                charBufferHeader.put("Connection: keep-alive").put("\r\n");
                charBufferHeader.put("").put("\r\n");
                charBufferHeader.flip();

                ByteBuffer buffer = charset.encode(charBufferHeader);
                // int bytesWritten = 0;

                while (buffer.hasRemaining())
                {
                    // bytesWritten +=
                    client.write(buffer);
                }

                // Response
                buffer = ByteBuffer.allocate(1024);

                while (client.read(buffer) > 0)
                {
                    buffer.flip();

                    CharBuffer charBuffer = charset.decode(buffer);

                    LOGGER.debug("\n" + charBuffer.toString().trim());

                    buffer.clear();
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
     *
     */
    private IoHandler ioHandler = null;

    /**
     *
     */
    private boolean isShutdown = false;

    /**
    *
    */
    private final int port;

    /**
    *
    */
    private Selector selector = null;

    /**
    *
    */
    private ServerSocketChannel serverSocketChannel = null;

    /**
    *
    */
    private final Semaphore stopLock = new Semaphore(1, true);

    /**
     * Erstellt ein neues {@link HTTPServerSingleThread} Object.
     *
     * @param port int
     * @throws IOException Falls was schief geht.
     */
    public HTTPServerSingleThread(final int port) throws IOException
    {
        super();

        this.port = port;
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
     * Wartet auf neue Connections.
     */
    private void listen()
    {
        LOGGER.info("server listening on port: {}", this.serverSocketChannel.socket().getLocalPort());

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
                            getLogger().debug("SelectionKey not valid: {}", selectionKey);
                        }

                        if (selectionKey.isAcceptable())
                        {
                            // Verbindung mit Client herstellen.
                            @SuppressWarnings("resource")
                            SocketChannel socketChannel = this.serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(this.selector, SelectionKey.OP_READ);

                            getLogger().debug("Connection Accepted: {}", socketChannel.getRemoteAddress());

                            // SelectionKey sk = socketChannel.register(this.selector, SelectionKey.OP_READ);
                            // sk.attach(obj)

                            // Selector aufwecken.
                            this.selector.wakeup();
                        }
                        else if (selectionKey.isConnectable())
                        {
                            getLogger().debug("Client Connected");
                        }
                        else if (selectionKey.isReadable())
                        {
                            getLogger().debug("Read Request");

                            // Request lesen.
                            getIoHandler().read(selectionKey, getLogger());
                        }
                        else if (selectionKey.isWritable())
                        {
                            getLogger().debug("Write Response");

                            // Response schreiben.
                            getIoHandler().write(selectionKey, getLogger());
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

        this.selector = Selector.open();

        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.configureBlocking(false);

        @SuppressWarnings("resource")
        ServerSocket socket = this.serverSocketChannel.socket();
        socket.setReuseAddress(true);
        socket.bind(new InetSocketAddress(this.port), 50);

        @SuppressWarnings("unused")
        SelectionKey selectionKey = this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        // selectionKey.attach(this);

        new Thread(this::listen, getClass().getSimpleName()).start();
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