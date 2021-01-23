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
     * Default: Runtime.getRuntime().availableProcessors()
     */
    private final int parallelism;

    /**
     * Default: Integer.highestOneBit(Runtime.getRuntime().availableProcessors()) << 4)<br>
     * Beispiel:<br>
     * 32 << 4 = 512<br>
     * 24 << 4 = 256<br>
     * 16 << 4 = 256<br>
     * 8 << 4 = 128<br>
     */
    private final int ringBufferSize;

    /**
     *
     */
    private final SensorRegistry sensorRegistry;

    /**
     * Erstellt ein neues {@link DisruptorBackend} Object.
     *
     * @param sensorRegistry {@link SensorRegistry}
     */
    public DisruptorBackend(final SensorRegistry sensorRegistry)
    {
        this(sensorRegistry, Runtime.getRuntime().availableProcessors());
    }

    /**
     * Erstellt ein neues {@link DisruptorBackend} Object.
     *
     * @param sensorRegistry {@link SensorRegistry}
     * @param parallelism int
     */
    public DisruptorBackend(final SensorRegistry sensorRegistry, final int parallelism)
    {
        this(sensorRegistry, parallelism, Integer.highestOneBit(parallelism) << 4);
    }

    /**
     * Erstellt ein neues {@link DisruptorBackend} Object.
     *
     * @param sensorRegistry {@link SensorRegistry}
     * @param parallelism int
     * @param ringBufferSize int
     */
    public DisruptorBackend(final SensorRegistry sensorRegistry, final int parallelism, final int ringBufferSize)
    {
        super();

        this.sensorRegistry = Objects.requireNonNull(sensorRegistry, "sensorRegistry required");

        if (parallelism < 1)
        {
            throw new IllegalArgumentException("parallelism must be >= 1");
        }

        this.parallelism = parallelism;

        if (ringBufferSize < 1)
        {
            throw new IllegalArgumentException("ringBufferSize must be >= 1");
        }

        this.ringBufferSize = ringBufferSize;
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#start()
     */
    @Override
    public void start()
    {
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
