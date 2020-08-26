// Created: 26.08.2020
package de.freese.sonstiges.disruptor.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.RandomStringUtils;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

/**
 * https://stackoverflow.com/questions/55827411/how-to-make-a-java-nio-non-blocking-io-based-tcp-server-using-disruptor
 *
 * @author Thomas Freese
 */
public class HttpEventMain
{
    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        System.out.println("----- Running the server on machine with " + Runtime.getRuntime().availableProcessors() + " cores -----");

        HttpEventMain server = new HttpEventMain(null, 4333);

        HttpEventFactory factory = new HttpEventFactory();

        int bufferSize = 1024;

        // Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); // a thread pool to which we can assign tasks
        // Disruptor<HttpEvent> disruptor = new Disruptor<>(factory, bufferSize, executor);

        // Disruptor<HttpEvent> disruptor = new Disruptor(factory, bufferSize, DaemonThreadFactory.INSTANCE);
        Disruptor<HttpEvent> disruptor = new Disruptor<>(factory, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new BusySpinWaitStrategy());

        // -1 damit noch Platz für den CleaningEventHandler bleibt.
        HttpEventHandler[] handlers = new HttpEventHandler[Runtime.getRuntime().availableProcessors() - 1];

        for (int i = 0; i < handlers.length; i++)
        {
            handlers[i] = new HttpEventHandler(i, server.getMapResponse());
        }

        disruptor.handleEventsWith(handlers).then(new CleaningEventHandler());

        disruptor.start();

        RingBuffer<HttpEvent> ringBuffer = disruptor.getRingBuffer();

        server.setProducer(new HttpEventProducer(ringBuffer, server.getMapResponse()));

        System.out.println("\n====================Server Details====================");
        System.out.println("Server Machine: " + InetAddress.getLocalHost().getCanonicalHostName());
        System.out.println("Port number: " + server.getPort());

        try
        {
            server.start();
        }
        catch (IOException ex)
        {
            System.err.println("Error occured in HttpEventMain:" + ex.getMessage());
            System.exit(0);
        }
    }

    /**
     *
     */
    private InetAddress addr;

    /**
     *
     */
    private Map<SelectionKey, String> mapKey;

    /**
     *
     */
    private Map<String, Object> mapResponse;

    /**
     *
     */
    private int port;

    /**
     *
     */
    private HttpEventProducer producer;

    /**
     *
     */
    private Selector selector;

    /**
     * @param addr {@link java.net.InetAddress}
     * @param port int
     * @throws IOException Falls was schief geht.
     */
    public HttpEventMain(final InetAddress addr, final int port) throws IOException
    {
        super();

        this.addr = addr;
        this.port = port;
        this.mapResponse = new ConcurrentHashMap<>();
        this.mapKey = new ConcurrentHashMap<>();
    }

    /**
     * @return {@link Map}
     */
    public Map<String, Object> getMapResponse()
    {
        return this.mapResponse;
    }

    /**
     * @return int
     */
    public int getPort()
    {
        return this.port;
    }

    /**
     * @param producer {@link HttpEventProducer}
     */
    public void setProducer(final HttpEventProducer producer)
    {
        this.producer = producer;
    }

    /**
     * @param key {@link SelectionKey}
     * @throws IOException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    private void accept(final SelectionKey key) throws IOException
    {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);

        // Socket socket = channel.socket();
        // SocketAddress remoteAddr = socket.getRemoteSocketAddress();

        channel.register(this.selector, SelectionKey.OP_READ);
    }

    /**
     * @param key {@link SelectionKey}
     * @throws IOException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    private void read(final SelectionKey key) throws IOException
    {
        SocketChannel channel = (SocketChannel) key.channel();

        ByteBuffer buffer = ByteBuffer.allocate(8192);
        int numRead = -1;

        numRead = channel.read(buffer);

        if (numRead == -1)
        {
            // Socket socket = channel.socket();
            // SocketAddress remoteAddr = socket.getRemoteSocketAddress();
            channel.close();
            key.cancel();

            return;
        }

        String requestID = RandomStringUtils.random(15, true, true);

        while (this.mapKey.containsValue(requestID) || this.mapResponse.containsKey(requestID))
        {
            requestID = RandomStringUtils.random(15, true, true);
        }

        this.mapKey.put(key, requestID);

        this.producer.onData(requestID, buffer, numRead);

        channel.register(this.selector, SelectionKey.OP_WRITE, buffer);
    }

    /**
     * @param key {@link SelectionKey}
     * @return boolean
     */
    private boolean responseReady(final SelectionKey key)
    {
        String requestId = this.mapKey.get(key);
        String response = this.mapResponse.get(requestId).toString();

        if ("0".equals(response))
        {
            return false;
        }

        this.mapKey.remove(key);
        this.mapResponse.remove(requestId);

        return true;
    }

    /**
     * @throws IOException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    private void start() throws IOException
    {
        this.selector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        InetSocketAddress listenAddr = new InetSocketAddress(this.addr, this.port);
        serverChannel.socket().bind(listenAddr);
        serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server ready. Ctrl-C to stop.");

        while (!Thread.interrupted())
        {
            int readyChannels = this.selector.select();

            if (readyChannels == 0)
            {
                continue;
            }

            Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();

            while (keys.hasNext())
            {
                SelectionKey key = keys.next();
                keys.remove();

                if (!key.isValid())
                {
                    continue;
                }

                if (key.isAcceptable())
                {
                    accept(key);
                }
                else if (key.isReadable())
                {
                    read(key);
                }
                else if (key.isWritable())
                {
                    write(key);
                }
            }
        }
    }

    /**
     * @param key {@link SelectionKey}
     * @throws IOException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    private void write(final SelectionKey key) throws IOException
    {
        if (responseReady(key))
        {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = (ByteBuffer) key.attachment();

            buffer.flip();
            channel.write(buffer);
            channel.close();

            key.cancel();
        }
    }
}
