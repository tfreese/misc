/**
 * Created: 31.10.2016
 */

package de.freese.sonstiges.server;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.sonstiges.server.handler.HttpIoHandler;
import de.freese.sonstiges.server.handler.IoHandler;

/**
 * Der Server nimmt nur die neuen Client-Verbindungen entgegen und übergibt sie einem {@link Processor}.<br>
 * Der {@link Processor} kümmert dann sich um das Connection-Handling.<br>
 * Der {@link IoHandler} übernimmt das Lesen und Schreiben von Request und Response.<br>
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
            final Thread currentThread = Thread.currentThread();
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
     * Übernimmt das Connection-Handling.<br>
     * Ein Processor kann für mehrere Connections zuständig sein.
     *
     * @author Thomas Freese
     */
    private static class Processor implements Runnable
    {
        /**
        *
        */
        private static final Logger LOGGER = LoggerFactory.getLogger(Processor.class);

        /**
        *
        */
        private final IoHandler ioHandler;

        /**
        *
        */
        private boolean isShutdown = false;

        /**
         * Queue für die neuen {@link SocketChannel}s.
         */
        private final Queue<SocketChannel> newSessions = new ConcurrentLinkedQueue<>();

        /**
        *
        */
        private final Selector selector;

        /**
        *
        */
        private final Semaphore stopLock = new Semaphore(1, true);

        /**
         * Erstellt ein neues {@link Processor} Object.
         *
         * @param ioHandler {@link IoHandler}
         * @throws IOException Falls was schief geht.
         */
        public Processor(final IoHandler ioHandler) throws IOException
        {
            super();

            this.ioHandler = Objects.requireNonNull(ioHandler, "ioHandler required");
            this.selector = Selector.open();
        }

        /**
         * Neue Session zum Processor hinzufügen.
         *
         * @param socketChannel {@link SocketChannel}
         * @throws IOException Falls was schief geht.
         */
        @SuppressWarnings("resource")
        public void addSession(final SocketChannel socketChannel) throws IOException
        {
            Objects.requireNonNull(socketChannel, "socketChannel required");

            this.newSessions.add(socketChannel);

            this.selector.wakeup();
        }

        /**
         * @return {@link Logger}
         */
        private Logger getLogger()
        {
            return LOGGER;
        }

        /**
         * Die neuen Sessions zum Selector hinzufügen.
         *
         * @throws IOException Falls was schief geht.
         */
        @SuppressWarnings("resource")
        private void processNewSessions() throws IOException
        {
            // for (SocketChannel socketChannel = this.newSessions.poll(); socketChannel != null; socketChannel =
            // this.newSessions.poll())
            while (!this.newSessions.isEmpty())
            {
                SocketChannel socketChannel = this.newSessions.poll();

                if (socketChannel == null)
                {
                    continue;
                }

                socketChannel.configureBlocking(false);

                getLogger().debug("attach new session: {}", socketChannel);

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

                            if (selectionKey.isReadable())
                            {
                                getLogger().debug("Read Request");

                                // Request lesen.
                                this.ioHandler.read(selectionKey, getLogger());
                            }
                            else if (selectionKey.isWritable())
                            {
                                getLogger().debug("Write Response");

                                // Response schreiben.
                                this.ioHandler.write(selectionKey, getLogger());
                            }
                        }

                        selected.clear();
                    }

                    // Die neuen Sessions zum Selector hinzufügen.
                    processNewSessions();
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
         * Stoppen des Processors.
         */
        protected void stop()
        {
            getLogger().debug("stopping Processor");

            this.isShutdown = true;
            this.selector.wakeup();

            this.stopLock.acquireUninterruptibly();

            try
            {
                Set<SelectionKey> selected = this.selector.selectedKeys();
                Iterator<SelectionKey> iterator = selected.iterator();

                while (iterator.hasNext())
                {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();

                    if (selectionKey != null)
                    {
                        selectionKey.cancel();
                    }
                }

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

    /**
    *
    */
    private final static Logger LOGGER = LoggerFactory.getLogger(HTTPServerMultiThread.class);

    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    @SuppressWarnings(
    {
            "resource", "unused"
    })
    public static void main(final String[] args) throws Exception
    {
        final SelectorProvider selectorProvider = SelectorProvider.provider();

        int poolSize = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        ExecutorService executorService = Executors.newFixedThreadPool(poolSize);

        HTTPServerMultiThread server = new HTTPServerMultiThread(8001, executorService, selectorProvider);
        server.setIoHandler(new HttpIoHandler());
        server.start();

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

        // Client Task starten
        executorService.submit((Callable<Void>) () -> {

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
                charBufferHeader.put("User-Agent: " + HTTPServerMultiThread.class.getSimpleName()).put("\r\n");
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

            // Console simulieren.
            // pos.write(0);

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
        HTTPServerMultiThread.shutdown(executorService, LOGGER);
        System.exit(0);
    }

    /**
     * Shutdown des {@link ExecutorService}.
     *
     * @param executorService {@link ExecutorService}
     * @param logger {@link Logger}
     */
    public static void shutdown(final ExecutorService executorService, final Logger logger)
    {
        logger.info("shutdown ExecutorService");

        if (executorService == null)
        {
            return;
        }

        executorService.shutdown();

        try
        {
            // Wait a while for existing tasks to terminate.
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS))
            {
                if (logger.isWarnEnabled())
                {
                    logger.warn("Timed out while waiting for executorService");
                }

                // Cancel currently executing tasks.
                for (Runnable remainingTask : executorService.shutdownNow())
                {
                    if (remainingTask instanceof Future)
                    {
                        ((Future<?>) remainingTask).cancel(true);
                    }
                }

                // Wait a while for tasks to respond to being cancelled
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS))
                {
                    logger.error("Pool did not terminate");
                }
            }
        }
        catch (InterruptedException iex)
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("Interrupted while waiting for executorService");
            }

            // (Re-)Cancel if current thread also interrupted.
            executorService.shutdownNow();

            // Preserve interrupt status.
            Thread.currentThread().interrupt();
        }
    }

    /**
     *
     */
    private final ExecutorService executorService;

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
    private final int numOfProcessors = 3;

    /**
    *
    */
    private final int port;

    /**
     * Queue für die {@link Processor}.
     */
    // private final Queue<Processor> processors = new ArrayBlockingQueue<>(NUM_OF_PROCESSORS);
    // private final Queue<Processor> processors = new ConcurrentLinkedQueue<>();
    private final LinkedList<Processor> processors = new LinkedList<>();

    /**
    *
    */
    private Selector selector = null;

    /**
    *
    */
    private final SelectorProvider selectorProvider;

    /**
    *
    */
    private ServerSocketChannel serverSocketChannel = null;

    /**
    *
    */
    private final Semaphore stopLock = new Semaphore(1, true);

    /**
     * Erstellt ein neues {@link HTTPServerMultiThread} Object.
     *
     * @param port int
     * @param executorService {@link ExecutorService}; optional
     * @throws IOException Falls was schief geht.
     */
    public HTTPServerMultiThread(final int port, final ExecutorService executorService) throws IOException
    {
        this(port, executorService, SelectorProvider.provider());
    }

    /**
     * Erstellt ein neues {@link HTTPServerMultiThread} Object.
     *
     * @param port int
     * @param executorService {@link ExecutorService}; optional
     * @param selectorProvider {@link SelectorProvider}
     * @throws IOException Falls was schief geht.
     */
    public HTTPServerMultiThread(final int port, final ExecutorService executorService, final SelectorProvider selectorProvider) throws IOException
    {
        super();

        this.port = port;

        this.executorService = Objects.requireNonNull(executorService, "executorService required");
        this.selectorProvider = Objects.requireNonNull(selectorProvider, "selectorProvider required");
    }

    /**
     * @return {@link ExecutorService}
     */
    protected ExecutorService getExecutorService()
    {
        return this.executorService;
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
     * @return int
     */
    protected int getNumOfProcessors()
    {
        return this.numOfProcessors;
    }

    /**
     * @return {@link SelectorProvider}
     */
    protected SelectorProvider getSelectorProvider()
    {
        return this.selectorProvider;
    }

    /**
     * Wartet auf neue Connections.
     */
    @SuppressWarnings("resource")
    private void listen()
    {
        getLogger().info("server listening on port: {}", this.serverSocketChannel.socket().getLocalPort());

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
                            SocketChannel socketChannel = this.serverSocketChannel.accept();

                            getLogger().debug("Connection Accepted: {}", socketChannel.getRemoteAddress());
                            getLogger().debug("add new session: {}", socketChannel);

                            // Socket dem Processor übergeben.
                            nextProcessor().addSession(socketChannel);
                        }
                        else if (selectionKey.isConnectable())
                        {
                            getLogger().debug("Client Connected");
                        }
                    }

                    selected.clear();
                }
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

    /**
     * Liefert den nächsten {@link Processor} im RoundRobin-Verfahren.<br>
     *
     * @return {@link Processor}
     * @throws IOException Falls was schief geht.
     */
    private synchronized Processor nextProcessor() throws IOException
    {
        if (this.isShutdown)
        {
            return null;
        }

        // Ersten Processor entnehmen.
        Processor processor = this.processors.poll();

        // Processor wieder hinten dran hängen.
        this.processors.add(processor);

        return processor;
    }

    /**
     * @param ioHandler {@link IoHandler}
     */
    public void setIoHandler(final IoHandler ioHandler)
    {
        this.ioHandler = ioHandler;
    }

    /**
     * Starten des Servers.
     *
     * @throws IOException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    public void start() throws IOException
    {
        getLogger().info("starting server on port: {}", this.port);

        Objects.requireNonNull(this.ioHandler, "ioHandler requried");

        this.selector = getSelectorProvider().openSelector();

        this.serverSocketChannel = getSelectorProvider().openServerSocketChannel();
        this.serverSocketChannel.configureBlocking(false);

        ServerSocket socket = this.serverSocketChannel.socket();
        socket.setReuseAddress(true);
        socket.bind(new InetSocketAddress(this.port), 50);

        @SuppressWarnings("unused")
        SelectionKey selectionKey = this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
        // selectionKey.attach(this);

        // Erzeugen der Prozessoren.
        while (this.processors.size() < getNumOfProcessors())
        {
            Processor processor = new Processor(getIoHandler());

            this.processors.add(processor);
            getExecutorService().execute(new NamePreservingRunnable(processor, "Processor-" + this.processors.size()));
        }

        getExecutorService().execute(new NamePreservingRunnable(this::listen, getClass().getSimpleName()));
    }

    /**
     * Stoppen des Servers.
     */
    public void stop()
    {
        getLogger().info("stopping server on port: {}", this.port);

        this.isShutdown = true;

        this.processors.forEach(Processor::stop);

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
