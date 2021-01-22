// Created: 27.10.2020
package de.freese.jsensors.backend.disruptor;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;
import de.freese.jsensors.SensorRegistry;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.AbstractBackend;
import de.freese.jsensors.utils.JSensorThreadFactory;
import de.freese.jsensors.utils.LifeCycle;

/**
 * @author Thomas Freese
 */
public class DisruptorBackend extends AbstractBackend implements LifeCycle
{
    /**
     *
     */
    private Disruptor<SensorEvent> disruptor;

    /**
     *
     */
    private int parallelism = 3;

    /**
     * int ringBufferSize = Integer.highestOneBit(31) << 1;
     */
    private int ringBufferSize = 128;

    /**
     *
     */
    private SensorRegistry sensorRegistry;

    /**
     * Default: 3
     *
     * @param parallelism int
     */
    public void setParallelism(final int parallelism)
    {
        this.parallelism = parallelism;
    }

    /**
     * Default: 128<br>
     * int ringBufferSize = Integer.highestOneBit(31) << 1;
     *
     * @param ringBufferSize int
     */
    public void setRingBufferSize(final int ringBufferSize)
    {
        this.ringBufferSize = ringBufferSize;
    }

    /**
     * @param sensorRegistry {@link SensorRegistry}
     */
    public void setSensorRegistry(final SensorRegistry sensorRegistry)
    {
        this.sensorRegistry = sensorRegistry;
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#start()
     */
    @Override
    public void start()
    {
        Objects.requireNonNull(this.sensorRegistry, "sensorRegistry required");

        if (this.ringBufferSize < 1)
        {
            throw new IllegalArgumentException("ringBufferSize must be >= 1");
        }

        this.disruptor = new Disruptor<>(SensorEvent::new, this.ringBufferSize, new JSensorThreadFactory("jsensor-disruptor-"));

        DisruptorSensorEventHandler[] handlers = new DisruptorSensorEventHandler[this.parallelism];

        for (int i = 0; i < handlers.length; i++)
        {
            handlers[i] = new DisruptorSensorEventHandler(i, this.parallelism, this.sensorRegistry);
        }

        // disruptor.handleEventsWith(handlers).then(new CleaningEventHandler());
        this.disruptor.handleEventsWith(handlers);

        this.disruptor.start();
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#stop()
     */
    @Override
    public void stop()
    {
        this.disruptor.halt();

        try
        {
            this.disruptor.shutdown(3, TimeUnit.SECONDS);
        }
        catch (TimeoutException ex)
        {
            getLogger().error(null, ex);
        }
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#storeValue(de.freese.jsensors.SensorValue)
     */
    @Override
    protected void storeValue(final SensorValue sensorValue) throws Exception
    {
        RingBuffer<SensorEvent> ringBuffer = this.disruptor.getRingBuffer();

        long sequence = ringBuffer.next();

        try
        {
            SensorEvent event = ringBuffer.get(sequence);

            event.setSensorValue(sensorValue);
        }
        finally
        {
            ringBuffer.publish(sequence);
        }
    }
}
