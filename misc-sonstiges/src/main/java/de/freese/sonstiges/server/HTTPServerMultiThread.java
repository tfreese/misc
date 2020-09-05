/**
 * Created: 31.10.2016
 */

package de.freese.sonstiges.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.sonstiges.server.handler.IoHandler;

/**
 * Der Server nimmt nur die neuen Client-Verbindungen entgegen und übergibt sie einem {@link Worker}.<br>
 * Der {@link Worker} kümmert dann sich um das Connection-Handling.<br>
 * Der {@link IoHandler} übernimmt das Lesen und Schreiben von Request und Response.<br>
 * Zur Performance-Optimierung können mehrere {@link Worker} gestartet werden,<br>
 * die im Wechsel (RoundRobin) mit neuen Verbindungen versorgt werden.
 *
 * @author Thomas Freese
 */
public class HTTPServerMultiThread implements Runnable
{
    /**
     * Übernimmt das Connection-Handling.<br>
     * Ein Worker kann für mehrere Connections zuständig sein.
     *
     * @author Thomas Freese
     */
    private static class Worker implements Runnable
    {
        /**
         *
         */
        private static final Logger LOGGER = LoggerFactory.getLogger(Worker.class);

        /**
         *
         */
        private final boolean directRegistration = true;

        /**
         *
         */
        private final IoHandler<SelectionKey> ioHandler;

        /**
         *
         */
        private boolean isShutdown;

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
         * Erstellt ein neues {@link Worker} Object.
         *
         * @param ioHandler {@link IoHandler}
         * @throws IOException Falls was schief geht.
         */
        public Worker(final IoHandler<SelectionKey> ioHandler) throws IOException
        {
            super();

            this.ioHandler = Objects.requireNonNull(ioHandler, "ioHandler required");
            this.selector = Selector.open();
        }

        /**
         * Neue Session zum Worker hinzufügen.
         *
         * @param socketChannel {@link SocketChannel}
         * @throws IOException Falls was schief geht.
         * @see #processNewSessions()
         */
        void addSession(final SocketChannel socketChannel) throws IOException
        {
            Objects.requireNonNull(socketChannel, "socketChannel required");

            if (this.directRegistration)
            {
                socketChannel.configureBlocking(false);
                getLogger().debug("{}: attach new session", socketChannel.getRemoteAddress());
                socketChannel.register(this.selector, SelectionKey.OP_READ);
            }
            else
            {
                this.newSessions.add(socketChannel);
            }

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
         * @see #addSession(SocketChannel)
         */
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

                getLogger().debug("{}: attach new session", socketChannel.getRemoteAddress());

                // SelectionKey selectionKey =
                socketChannel.register(this.selector, SelectionKey.OP_READ);
                // selectionKey.attach(obj)
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
                                getLogger().debug("{}: SelectionKey not valid", getRemoteAddress(selectionKey));
                            }

                            if (selectionKey.isReadable())
                            {
                                getLogger().debug("{}: Read Request", getRemoteAddress(selectionKey));

                                // Request lesen.
                                this.ioHandler.read(selectionKey);
                            }
                            else if (selectionKey.isWritable())
                            {
                                getLogger().debug("{}: Write Response", getRemoteAddress(selectionKey));

                                // Response schreiben.
                                this.ioHandler.write(selectionKey);
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

            getLogger().debug("worker stopped");
        }

        /**
         * Stoppen des Workers.
         */
        void stop()
        {
            getLogger().debug("stopping worker");

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
    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPServerMultiThread.class);

    /**
     * @param selectionKey {@link SelectionKey}
     * @return String
     * @throws IOException Falls was schief geht.
     */
    private static String getRemoteAddress(final SelectionKey selectionKey) throws IOException
    {
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        String remoteAddress = channel.getRemoteAddress().toString();

        return remoteAddress;
    }

    /**
     *
     */
    private IoHandler<SelectionKey> ioHandler;

    /**
     *
     */
    private boolean isShutdown;

    /**
     *
     */
    private final int numOfWorkers;

    /**
     *
     */
    private final int port;

    /**
     *
     */
    private Selector selector;

    /**
     *
     */
    private final SelectorProvider selectorProvider;

    /**
     *
     */
    private ServerSocketChannel serverSocketChannel;

    /**
     *
     */
    private final Semaphore stopLock = new Semaphore(1, true);

    /**
     *
     */
    private ThreadFactory threadFactory = Executors.defaultThreadFactory();

    /**
     * Queue für die {@link Worker}.
     */
    private final LinkedList<Worker> workers = new LinkedList<>();

    /**
     * Erstellt ein neues {@link HTTPServerMultiThread} Object.
     *
     * @param port int
     * @param numOfWorkers int
     * @throws IOException Falls was schief geht.
     */
    public HTTPServerMultiThread(final int port, final int numOfWorkers) throws IOException
    {
        this(port, numOfWorkers, SelectorProvider.provider());
    }

    /**
     * Erstellt ein neues {@link HTTPServerMultiThread} Object.
     *
     * @param port int
     * @param numOfWorkers int
     * @param selectorProvider {@link SelectorProvider}
     * @throws IOException Falls was schief geht.
     */
    public HTTPServerMultiThread(final int port, final int numOfWorkers, final SelectorProvider selectorProvider) throws IOException
    {
        super();

        this.port = port;
        this.numOfWorkers = numOfWorkers;
        this.selectorProvider = Objects.requireNonNull(selectorProvider, "selectorProvider required");
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
        getLogger().info("server listening on port: {}", this.port);

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
                            getLogger().debug("{}: SelectionKey not valid", getRemoteAddress(selectionKey));
                        }

                        if (selectionKey.isAcceptable())
                        {
                            // Verbindung mit Client herstellen.
                            SocketChannel socketChannel = this.serverSocketChannel.accept();

                            getLogger().debug("{}: Connection Accepted", socketChannel.getRemoteAddress());

                            // Socket dem Worker übergeben.
                            nextWorker().addSession(socketChannel);
                        }
                        else if (selectionKey.isConnectable())
                        {
                            getLogger().debug("{}: Client Connected", getRemoteAddress(selectionKey));
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

        getLogger().info("server stopped on port: {}", this.port);
    }

    /**
     * Liefert den nächsten {@link Worker} im RoundRobin-Verfahren.<br>
     *
     * @return {@link Worker}
     * @throws IOException Falls was schief geht.
     */
    private synchronized Worker nextWorker() throws IOException
    {
        if (this.isShutdown)
        {
            return null;
        }

        // Ersten Worker entnehmen.
        Worker worker = this.workers.poll();

        // Worker wieder hinten dran hängen.
        this.workers.add(worker);

        return worker;
    }

    /**
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        getLogger().info("starting server on port: {}", this.port);

        Objects.requireNonNull(this.ioHandler, "ioHandler requried");

        try
        {
            this.selector = this.selectorProvider.openSelector();

            this.serverSocketChannel = this.selectorProvider.openServerSocketChannel();
            this.serverSocketChannel.configureBlocking(false);

            ServerSocket socket = this.serverSocketChannel.socket();
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(this.port), 50);

            // SelectionKey selectionKey =
            // this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
            // selectionKey.attach(this);

            // Erzeugen der Worker.
            while (this.workers.size() < this.numOfWorkers)
            {
                Worker worker = new Worker(this.ioHandler);

                this.workers.add(worker);

                String threadName = "Worker-" + this.workers.size();

                getLogger().info("start worker: {}", threadName);

                Thread thread = this.threadFactory.newThread(worker);
                thread.setName(threadName);
                thread.setDaemon(true);
                thread.start();
            }

            @SuppressWarnings("unused")
            SelectionKey selectionKey = this.serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);
            // selectionKey.attach(this);

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
    public void setIoHandler(final IoHandler<SelectionKey> ioHandler)
    {
        this.ioHandler = ioHandler;
    }

    /**
     * @param threadFactory {@link ThreadFactory}
     */
    public void setThreadFactory(final ThreadFactory threadFactory)
    {
        this.threadFactory = Objects.requireNonNull(threadFactory, "threadFactory required");
        ;
    }

    /**
     * Starten des Servers.
     *
     * @throws IOException Falls was schief geht.
     */
    public void start() throws IOException
    {
        Thread thread = this.threadFactory.newThread(this::run);
        thread.setName(getClass().getSimpleName());
        thread.setDaemon(false);
        thread.start();
    }

    /**
     * Stoppen des Servers.
     */
    public void stop()
    {
        getLogger().info("stopping server on port: {}", this.port);

        this.isShutdown = true;

        this.workers.forEach(Worker::stop);

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
