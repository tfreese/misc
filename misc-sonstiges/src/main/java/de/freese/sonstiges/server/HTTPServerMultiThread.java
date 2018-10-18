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
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Der Server nimmt nur die neuen Client-Verbindungen entgegen und übergibt sie dem {@link Processor}.<br>
 * Der {@link Processor} kümmert dann sich um das Lesen und Schreiben.<br>
 * Zur Performance-Optimierung können mehrere Processoren gestartet werden,<br>
 * die im Wechsel (RoundRobin) mit neuen Verbindungen versorgt werden.
 *
 * @author Thomas Freese
 */
public class HTTPServerMultiThread
{
    /**
     * Ein {@link Runnable} Wrapper, welcher den Namen des aktuellen Threads durch den eigenen ersetzt.<br>
     * Nach der run-Methode wird der Original-Name wiederhergestellt.
     *
     * @author Thomas Freese
     */
    private static class NamePreservingRunnable implements Runnable
    {
        /**
        *
        */
        private final Runnable runnable;

        /**
        *
        */
        private final String runnableName;

        /**
         * Erstellt ein neues {@link NamePreservingRunnable} Object.
         *
         * @param runnable {@link Runnable}
         * @param runnableName String
         */
        public NamePreservingRunnable(final Runnable runnable, final String runnableName)
        {
            super();

            this.runnable = Objects.requireNonNull(runnable, "runnable required");
            this.runnableName = Objects.requireNonNull(runnableName, "runnableName required");
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            Thread currentThread = Thread.currentThread();
            String oldName = currentThread.getName();

            setName(currentThread, this.runnableName);

            try
            {
                this.runnable.run();
            }
            finally
            {
                setName(currentThread, oldName);
            }
        }

        /**
         * Ändert den Namen des Threads.<br>
         * Eine auftretende {@link SecurityException} wird als Warning geloggt.
         *
         * @param thread {@link Thread}
         * @param name String
         */
        private void setName(final Thread thread, final String name)
        {
            try
            {
                thread.setName(name);
            }
            catch (SecurityException sex)
            {
                if (LOGGER.isWarnEnabled())
                {
                    LOGGER.warn("Failed to set the thread name.", sex);
                }
            }
        }
    }

    /**
     * @author Thomas Freese
     */
    private class Processor implements Runnable
    {
        /**
         * Queue für die neuen {@link SocketChannel}s.
         */
        private final Queue<SocketChannel> newSessions = new ConcurrentLinkedQueue<>();

        /**
        *
        */
        private final Selector selector;

        /**
         * Erstellt ein neues {@link Processor} Object.
         *
         * @throws IOException Falls was schief geht.
         */
        public Processor() throws IOException
        {
            super();

            this.selector = Selector.open();
        }

        /**
         * Neue Session zum Processor hinzufügen.
         *
         * @param socketChannel {@link SocketChannel}
         * @throws IOException Falls was schief geht.
         */
        public void addSession(final SocketChannel socketChannel) throws IOException
        {
            Objects.requireNonNull(socketChannel, "socketChannel required");

            this.newSessions.add(socketChannel);

            this.selector.wakeup();
        }

        /**
         * Die neuen Sessions zum Selector hinzufügen.
         *
         * @throws IOException Falls was schief geht.
         */
        private void processNewSessions() throws IOException
        {
            // for (SocketChannel socketChannel = this.newSessions.poll(); socketChannel != null; socketChannel =
            // this.newSessions.poll())
            while (!this.newSessions.isEmpty())
            {
                @SuppressWarnings("resource")
                SocketChannel socketChannel = this.newSessions.poll();

                if (socketChannel == null)
                {
                    continue;
                }

                socketChannel.configureBlocking(false);

                @SuppressWarnings("unused")
                SelectionKey selectionKey = socketChannel.register(this.selector, SelectionKey.OP_READ);
                // sk.attach(obj)
            }
        }

        /**
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            while (!Thread.interrupted())
            {
                try
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
                                log("SelectionKey not valid: %s", selectionKey);
                            }

                            if (selectionKey.isReadable())
                            {
                                log("Read Request");

                                // Request lesen.
                                readRequest(selectionKey);
                                selectionKey.interestOps(SelectionKey.OP_WRITE);
                            }
                            else if (selectionKey.isWritable())
                            {
                                log("Write Response");

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

                    // Die neuen Sessions zum Selector hinzufügen.
                    processNewSessions();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     *
     */
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    /**
    *
    */
    private final static Logger LOGGER = LoggerFactory.getLogger(HTTPServerMultiThread.class);

    /**
     *
     */
    private static final int NUM_OF_PROCESSORS = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);

    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        Executor executor = Executors.newCachedThreadPool();

        HTTPServerMultiThread server = new HTTPServerMultiThread(executor, 8001);
        executor.execute(new NamePreservingRunnable(() -> server.listen(), "HTTPServerMultiThread"));

        // server.shutdown();
    }

    /**
     *
     */
    private final Executor executor;

    /**
    *
    */
    private boolean isShutdown = false;

    /**
     * Queue für die {@link Processor}.
     */
    // private final Queue<Processor> processors = new ArrayBlockingQueue<>(NUM_OF_PROCESSORS);
    // private final Queue<Processor> processors = new ConcurrentLinkedQueue<>();
    private final LinkedList<Processor> processors = new LinkedList<>();

    /**
    *
    */
    private final Selector selector;

    /**
    *
    */
    private final ServerSocketChannel serverSocketChannel;

    /**
     * Erstellt ein neues {@link HTTPServerMultiThread} Object.
     *
     * @param executor {@link Executor}
     * @param port int
     * @throws IOException Falls was schief geht.
     */
    public HTTPServerMultiThread(final Executor executor, final int port) throws IOException
    {
        super();

        this.executor = Objects.requireNonNull(executor, "executor required");

        // this.processor = new Processor();
        // this.executor.execute(this.processor);

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
        log("server listening on port: %d", this.serverSocketChannel.socket().getLocalPort());

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
                            log("SelectionKey not valid: %s", selectionKey);
                        }

                        if (selectionKey.isAcceptable())
                        {
                            log("Connection Accepted");

                            // Verbindung mit Client herstellen.
                            @SuppressWarnings("resource")
                            SocketChannel socketChannel = this.serverSocketChannel.accept();

                            // Socket dem Processor übergeben.
                            nextProcessor().addSession(socketChannel);
                        }
                        else if (selectionKey.isConnectable())
                        {
                            log("Client Connected");
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
     * Erweitert die Log-Ausgabe um den Thread-Namen.
     *
     * @param format String
     * @param args Object[]
     */
    private void log(final String format, final Object...args)
    {
        Object[] newArgs = new Object[args.length + 1];

        System.arraycopy(args, 0, newArgs, 1, args.length);
        newArgs[0] = Thread.currentThread().getName();

        System.out.printf("[%s]: " + format + "%n", newArgs);
    }

    /**
     * Liefert den nächsten {@link Processor} im RoundRobin-Verfahren.<br>
     *
     * @return {@link Processor}
     * @throws IOException Falls was schief geht.
     */
    private synchronized Processor nextProcessor() throws IOException
    {
        // return this.processor;

        Processor processor = null;

        if (this.processors.size() < NUM_OF_PROCESSORS)
        {
            // Anzahl möglicher Processoren noch nicht erreicht.
            processor = new Processor();

            this.processors.add(processor);
            // this.executor.execute(processor);
            this.executor.execute(new NamePreservingRunnable(processor, "Processor-" + this.processors.size()));
        }

        // Ersten Processor entnehmen.
        processor = this.processors.poll();

        // Processor wieder hinten dran hängen.
        this.processors.add(processor);

        return processor;
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
        channel.read(inputBuffer);

        inputBuffer.flip();

        CharsetDecoder decoder = CHARSET.newDecoder();
        CharBuffer charBuffer = decoder.decode(inputBuffer);

        log(charBuffer.toString());
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
        CharsetEncoder encoder = CHARSET.newEncoder();

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
        // ByteBuffer outputBuffer = encoder.encode(charBuffer);
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

        ByteBuffer outputBufferHeader = encoder.reset().encode(charBufferHeader);
        ByteBuffer outputBufferBody = encoder.reset().encode(charBufferBody);

        @SuppressWarnings("resource")
        GatheringByteChannel channel = (GatheringByteChannel) selectionKey.channel();
        channel.write(new ByteBuffer[]
        {
                outputBufferHeader, outputBufferBody
        });
    }
}
