// Created: 05.09.2020
package de.freese.sonstiges.server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import de.freese.sonstiges.server.async.ServerAsync;
import de.freese.sonstiges.server.handler.HttpIoHandler;
import de.freese.sonstiges.server.handler.IoHandler;
import de.freese.sonstiges.server.singlethread.ServerSingleThread;

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
        // final SelectorProvider selectorProvider = SelectorProvider.provider();

        // ServerSingleThread server = new ServerSingleThread(8001);
        // ServerMultiThread server = new ServerMultiThread(8001, 3);
        ServerAsync server = new ServerAsync(8001, AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(4)));

        server.setIoHandler(new HttpIoHandler());
        server.start();
        // ForkJoinPool.commonPool().execute(server);

        System.out.println();
        System.out.println();
        System.out.println("******************************************************************************************************************");
        System.out.println("You're using an IDE, click in this console and press ENTER to call System.exit() and trigger the shutdown routine.");
        System.out.println("******************************************************************************************************************");
        System.out.println();
        System.out.println();

        // Console für programmatische Eingabe simulieren.
        // PipedOutputStream pos = new PipedOutputStream();
        // PipedInputStream pis = new PipedInputStream(pos);
        // System.setIn(pis);

        Thread.sleep(1000);

        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 8001);
        Charset charset = IoHandler.DEFAULT_CHARSET;

        // try (SocketChannel client = selectorProvider.openSocketChannel())
        try (SocketChannel client = SocketChannel.open(serverAddress))
        {
            // client.connect(serverAddress);
            client.configureBlocking(true);

            // Request
            CharBuffer charBufferHeader = CharBuffer.allocate(256);
            charBufferHeader.put("GET / HTTP/1.1").put("\r\n");
            charBufferHeader.put("Host: localhost:8001").put("\r\n");
            charBufferHeader.put("User-Agent: " + ServerSingleThread.class.getSimpleName()).put("\r\n");
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
