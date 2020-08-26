// Created: 26.08.2020
package de.freese.sonstiges.disruptor.example;

import java.nio.ByteBuffer;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;

/**
 * @author Thomas Freese
 */
public class LongEventMain
{
    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        // The factory for the event
        LongEventFactory factory = new LongEventFactory();

        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 1024;

        // Construct the Disruptor
        // Disruptor<LongEvent> disruptor = new Disruptor<>(factory, bufferSize, DaemonThreadFactory.INSTANCE);

        // Construct the Disruptor with a SingleProducerSequencer
        Disruptor<LongEvent> disruptor = new Disruptor<>(factory, bufferSize, DaemonThreadFactory.INSTANCE, ProducerType.SINGLE, new BusySpinWaitStrategy());

        // Connect a single handler
        // disruptor.handleEventsWith(new LongEventHandler()).then(new CleaningEventHandler());

        // Connect 2 handlers
        // disruptor.handleEventsWith(new LongEventHandler(), new LongEventHandler()).then(new CleaningEventHandler());

        // Connect multiple Handlers; -1 damit noch Platz für den CleaningEventHandler bleibt.
        LongEventHandler[] handlers = new LongEventHandler[Runtime.getRuntime().availableProcessors() - 1];

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

        for (long l = 0; true; l++)
        {
            bb.putLong(0, l);
            producer.onData(bb);
            Thread.sleep(100);
        }

        // disruptor.shutdown();
    }
}
