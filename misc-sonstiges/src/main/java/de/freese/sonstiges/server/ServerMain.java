// Created: 05.09.2020
package de.freese.sonstiges.server;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import de.freese.sonstiges.server.handler.HttpIoHandler;
import de.freese.sonstiges.server.handler.IoHandler;

/**
 * @author Thomas Freese
 */
public class ServerMain
{
    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        final SelectorProvider selectorProvider = SelectorProvider.provider();

        // HTTPServerSingleThread server = new HTTPServerSingleThread(8001, selectorProvider);
        // HTTPServerMultiThread server = new HTTPServerMultiThread(8001, 3, selectorProvider);
        HTTPServerAsynchronous server = new HTTPServerAsynchronous(8001, AsynchronousChannelGroup.withThreadPool(Executors.newCachedThreadPool()));

        server.setIoHandler(new HttpIoHandler());
        // server.start();
        ForkJoinPool.commonPool().execute(server);

        System.out.println();
        System.out.println();
        System.out.println("******************************************************************************************************************");
        System.out.println("You're using an IDE, click in this console and press ENTER to call System.exit() and trigger the shutdown routine.");
        System.out.println("******************************************************************************************************************");
        System.out.println();
        System.out.println();

        // Console für programmatische Eingabe simulieren.
        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos);
        // System.setIn(pis);

        Thread.sleep(1000);

        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 8001);
        Charset charset = IoHandler.DEFAULT_CHARSET;

        try (SocketChannel client = selectorProvider.openSocketChannel())
        {
            client.connect(serverAddress);
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

            while (buffer.hasRemaining())
            {
                client.write(buffer);
            }

            // Response
            buffer = ByteBuffer.allocate(1024);

            while (client.read(buffer) > 0)
            {
                buffer.flip();

                CharBuffer charBuffer = charset.decode(buffer);

                System.out.println();
                System.out.println(charBuffer.toString().trim());

                buffer.clear();
            }
        }

        // Console simulieren.
        // pos.write(0);

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
}
