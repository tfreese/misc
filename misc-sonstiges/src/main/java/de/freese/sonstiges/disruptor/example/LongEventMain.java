// Created: 26.08.2020
package de.freese.sonstiges.disruptor.example;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

/**
 * https://github.com/LMAX-Exchange/disruptor/wiki/Getting-Started
 *
 * @author Thomas Freese
 */
public class LongEventMain
{
    /**
     * -2 damit noch Platz für den CleaningEventHandler und sonstige Resourcen bleibt.
     */
    public static final int THREAD_COUNT = Math.max(2, Runtime.getRuntime().availableProcessors() - 2);

    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        // The factory for the event
        LongEventFactory factory = new LongEventFactory();

        // Specify the size of the ring buffer, must be power of 2.
        // int ringBufferSize = Integer.highestOneBit(31) << 1;
        int ringBufferSize = 32;

        // Threads werden vom Distributor exklusiv belegt und erst beim Shutdown wieder freigegeben.
        // Daher ist ein Executor nicht empfohlen, jeder Distuptor braucht seinen eigenen exklusiven ThreadPool.
        ThreadFactory threadFactory = DaemonThreadFactory.INSTANCE;
        // ThreadFactory threadFactory = new CustomizableThreadFactory("disruptor-thread-");
        // ThreadFactory threadFactory = new
        // BasicThreadFactory.Builder().namingPattern("disruptor-thread-%d").daemon(true).priority(Thread.NORM_PRIORITY).build();

        ProducerType producerType = ProducerType.SINGLE; // Nur ein exklusiver Thread schreibt Daten in den RingBuffer.
        // ProducerType producerType = ProducerType.MULTI; // Verschiedene Threads schreiben Daten in den RingBuffer.

        WaitStrategy waitStrategy = null;

        // The BlockingWaitStrategy is the slowest of the available wait strategies, but is the most conservative with the respect
        // to CPU usage and will give the most consistent behaviour across the widest variety of deployment options.
        // waitStrategy = new BlockingWaitStrategy();

        // It works best in situations where low latency is not required, but a low impact on the producing thread is desired.
        // A common use case is for asynchronous logging.
        // waitStrategy = new SleepingWaitStrategy();

        // This is the recommended wait strategy when need very high performance and the number of Event Handler threads is
        // less than the total number of logical cores, e.g. you have hyper-threading enabled.
        // waitStrategy = new YieldingWaitStrategy();

        // This wait strategy should only be used if the number of Event Handler threads is smaller than the number of physical cores on the box.
        waitStrategy = new BusySpinWaitStrategy();

        // Disruptor<LongEvent> disruptor = new Disruptor<>(factory, ringBufferSize, Executors.newFixedThreadPool(8), producerType, waitStrategy);
        Disruptor<LongEvent> disruptor = new Disruptor<>(factory, ringBufferSize, threadFactory, producerType, waitStrategy);

        // Connect a single handler
        // disruptor.handleEventsWith(new LongEventHandler(-1)).then(new CleaningEventHandler());

        // Connect multiple Handlers
        LongEventHandler[] handlers = new LongEventHandler[THREAD_COUNT];

        for (int i = 0; i < handlers.length; i++)
        {
            handlers[i] = new LongEventHandler(i);
        }

        disruptor.handleEventsWith(handlers).then(new CleaningEventHandler());

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        LongEventProducer producer = new LongEventProducer(ringBuffer);

        ByteBuffer bb = ByteBuffer.allocate(8);

        for (long l = 0; l < 50; l++)
        {
            bb.putLong(0, l);
            producer.onData(bb); // Wartet, wenn der RingBuffer voll ist -> ringBufferSize anpassen
            Thread.sleep(100);
        }

        // Nur notwending, wenn die Event-Publizierung noch nicht abgeschlossen ist.
        disruptor.halt();
        disruptor.shutdown(5, TimeUnit.SECONDS);
    }
}
