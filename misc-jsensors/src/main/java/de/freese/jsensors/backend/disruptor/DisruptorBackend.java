// Created: 27.10.2020
package de.freese.jsensors.backend.disruptor;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;
import de.freese.jsensors.SensorBackendRegistry;
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
    private SensorBackendRegistry sensorBackendRegistry;

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#saveValue(de.freese.jsensors.SensorValue)
     */
    @Override
    protected void saveValue(final SensorValue sensorValue) throws Exception
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
     * @param sensorBackendRegistry {@link SensorBackendRegistry}
     */
    public void setSensorBackendRegistry(final SensorBackendRegistry sensorBackendRegistry)
    {
        this.sensorBackendRegistry = sensorBackendRegistry;
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#start()
     */
    @Override
    public void start()
    {
        Objects.requireNonNull(this.sensorBackendRegistry, "sensorBackendRegistry required");

        if (this.ringBufferSize < 1)
        {
            throw new IllegalArgumentException("ringBufferSize must be >= 1");
        }

        this.disruptor = new Disruptor<>(SensorEvent::new, this.ringBufferSize, new JSensorThreadFactory("jsensor-disruptor-"));

        DisruptorSensorEventHandler[] handlers = new DisruptorSensorEventHandler[this.parallelism];

        for (int i = 0; i < handlers.length; i++)
        {
            handlers[i] = new DisruptorSensorEventHandler(i, this.parallelism, this.sensorBackendRegistry);
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
}
