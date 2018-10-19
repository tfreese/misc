/**
 * Created: 31.10.2016
 */

package de.freese.sonstiges.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Charset CHARSET = StandardCharsets.US_ASCII;

    /**
     *
     */
    private static final CharsetDecoder CHARSET_DECODER = CHARSET.newDecoder();

    /**
     *
     */
    private static final CharsetEncoder CHARSET_ENCODER = CHARSET.newEncoder();

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
        HTTPServerSingleThread server = new HTTPServerSingleThread(8000);
        new Thread(() -> server.listen(), "HTTPServerSingleThread").start();

        // server.shutdown();
    }

    /**
     *
     */
    private boolean isShutdown = false;

    /**
    *
    */
    private final Selector selector;

    /**
    *
    */
    private final ServerSocketChannel serverSocketChannel;

    /**
     * Erstellt ein neues {@link HTTPServerSingleThread} Object.
     *
     * @param port int
     * @throws IOException Falls was schief geht.
     */
    public HTTPServerSingleThread(final int port) throws IOException
    {
        super();

        this.selector = Selector.open();

        this.serverSocketChannel = ServerSocketChannel.open();
        this.serverSocketChannel.configureBlocking(false);

        @SuppressWarnings("resource")
        ServerSocket socket = this.serverSocketChannel.socket();
        socket.setReuseAddress(true);
        socket.bind(new InetSocketAddress(port), 50);

        @SuppressWarnings("unused")
        SelectionKey selectionKey = this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        // selectionKey.attach(this);
    }

    /**
     * Aktiviert den Server.
     */
    public void listen()
    {
        LOGGER.info("server listening on port: {}", this.serverSocketChannel.socket().getLocalPort());

        try
        {
            while (!Thread.interrupted())
            {
                int readyChannels = this.selector.select();

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
                            LOGGER.info("SelectionKey not valid: {}", selectionKey);
                        }

                        if (selectionKey.isAcceptable())
                        {
                            LOGGER.info("Connection Accepted");

                            // Verbindung mit Client herstellen.
                            @SuppressWarnings("resource")
                            SocketChannel socketChannel = this.serverSocketChannel.accept();
                            socketChannel.configureBlocking(false);
                            socketChannel.register(this.selector, SelectionKey.OP_READ);

                            // SelectionKey sk = socketChannel.register(this.selector, SelectionKey.OP_READ);
                            // sk.attach(obj)

                            // Selector aufwecken.
                            this.selector.wakeup();
                        }
                        else if (selectionKey.isConnectable())
                        {
                            LOGGER.info("Client Connected");
                        }
                        else if (selectionKey.isReadable())
                        {
                            LOGGER.info("Read Request");

                            // Request lesen.
                            readRequest(selectionKey);
                            selectionKey.interestOps(SelectionKey.OP_WRITE);
                        }
                        else if (selectionKey.isWritable())
                        {
                            LOGGER.info("Write Response");

                            // Response schreiben.
                            writeResponse(selectionKey);

                            // Bei HTTP ist nach dem Response die Session vorbei.
                            selectionKey.channel().close();
                            selectionKey.cancel();

                            // Ansonsten wieder: selectionKey.interestOps(SelectionKey.OP_READ);
                        }
                    }

                    selected.clear();
                }

                if (this.isShutdown)
                {
                    break;
                }
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Lesen des Requests.
     *
     * @param selectionKey {@link SelectionKey}
     * @throws IOException Falls was schief geht.
     */
    private void readRequest(final SelectionKey selectionKey) throws IOException
    {
        @SuppressWarnings("resource")
        ReadableByteChannel channel = (ReadableByteChannel) selectionKey.channel();

        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(1024);

        int bytesRead = channel.read(inputBuffer);

        while (bytesRead > 0)
        {
            inputBuffer.flip();

            CharBuffer charBuffer = CHARSET_DECODER.reset().decode(inputBuffer);

            LOGGER.info(charBuffer.toString());

            // inputBuffer.compact();
            inputBuffer.clear();

            bytesRead = channel.read(inputBuffer);
        }
    }

    /**
     * Stoppen des Servers.
     */
    public void shutdown()
    {
        this.isShutdown = true;
        this.selector.wakeup();

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
            ex.printStackTrace();
        }
    }

    /**
     * Schreiben des Response.
     *
     * @param selectionKey {@link SelectionKey}
     * @throws IOException Falls was schief geht.
     */
    private void writeResponse(final SelectionKey selectionKey) throws IOException
    {
        // CharBuffer charBuffer = CharBuffer.allocate(1024);
        // charBuffer.put("HTTP/1.1 200 OK").put("\r\n");
        // charBuffer.put("Server: MEINER !").put("\r\n");
        // charBuffer.put("Content-type: text/html").put("\r\n");
        // charBuffer.put("Content-length: 99").put("\r\n");
        // charBuffer.put("\r\n");
        // charBuffer.put("<html>").put("\r\n");
        // charBuffer.put("<head></head>").put("\r\n");
        // charBuffer.put("<body>").put("\r\n");
        // charBuffer.put("Date: " + new Date().toString() + "<br>").put("\r\n");
        // charBuffer.put("</body>").put("\r\n");
        // charBuffer.put("</html>").put("\r\n");
        //
        // charBuffer.flip();
        //
        // ByteBuffer outputBuffer = CHARSET_ENCODER.encode(charBuffer);
        //
        // @SuppressWarnings("resource")
        // WritableByteChannel channel = (WritableByteChannel) selectionKey.channel();
        // channel.write(outputBuffer);

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

        CharBuffer charBufferHeader = CharBuffer.allocate(256);
        charBufferHeader.put("HTTP/1.1 200 OK").put("\r\n");
        charBufferHeader.put("Server: nio").put("\r\n");
        charBufferHeader.put("Content-type:  text/html").put("\r\n");
        charBufferHeader.put("Content-length: " + (charBufferBody.position() * 2)).put("\r\n");
        charBufferHeader.put("\r\n");

        charBufferBody.flip();
        charBufferHeader.flip();

        ByteBuffer outputBufferHeader = CHARSET_ENCODER.reset().encode(charBufferHeader);
        ByteBuffer outputBufferBody = CHARSET_ENCODER.reset().encode(charBufferBody);

        @SuppressWarnings("resource")
        GatheringByteChannel channel = (GatheringByteChannel) selectionKey.channel();
        channel.write(new ByteBuffer[]
        {
                outputBufferHeader, outputBufferBody
        });
    }
}
