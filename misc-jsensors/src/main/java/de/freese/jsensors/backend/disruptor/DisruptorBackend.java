// Created: 27.10.2020
package de.freese.jsensors.backend.disruptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.TimeoutException;
import com.lmax.disruptor.dsl.Disruptor;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.AbstractBackend;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.Sensor;
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
    public static final int THREAD_COUNT = 3;

    /**
     *
     */
    private Disruptor<SensorEvent> disruptor;

    /**
    *
    */
    private final Map<String, List<Backend>> registry = new HashMap<>();

    /**
     * int ringBufferSize = Integer.highestOneBit(31) << 1;
     */
    private int ringBufferSize = 128;

    /**
     * @param sensorName String
     * @return {@link List}
     */
    private List<Backend> getBackends(final String sensorName)
    {
        List<Backend> backends = this.registry.computeIfAbsent(sensorName, key -> new ArrayList<>());

        return backends;
    }

    /**
     * Hier wird ein SensorName mit seinen Backends verknüft.<br>
     * Diese Verknüpfung wird von den {@link DisruptorSensorEventHandler} benötigt um die {@link SensorValue}s weiter zu leiten.
     *
     * @param sensor {@link Sensor}
     * @param backends {@link Backend}[]
     */
    public void register(final Sensor sensor, final Backend...backends)
    {
        for (Backend backend : backends)
        {
            register(sensor.getName(), backend);
        }
    }

    /**
     * Hier wird ein SensorName mit seinen Backends verknüft.<br>
     * Diese Verknüpfung wird von den {@link DisruptorSensorEventHandler} benötigt um die {@link SensorValue}s weiter zu leiten.
     *
     * @param sensorName String
     * @param backend {@link Backend}
     */
    public void register(final String sensorName, final Backend backend)
    {
        List<Backend> backends = this.registry.computeIfAbsent(sensorName, key -> new ArrayList<>());

        if (backends.contains(backend))
        {
            getLogger().warn("backend '{}' already bound to sensor '{}'", backend.getClass().getSimpleName(), sensorName);

            return;
        }

        backends.add(backend);
    }

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
     * Default: 128
     *
     * @param ringBufferSize int
     */
    public void setRingBufferSize(final int ringBufferSize)
    {
        this.ringBufferSize = ringBufferSize;
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#start()
     */
    @Override
    public void start()
    {
        if (this.ringBufferSize < 1)
        {
            throw new IllegalArgumentException("ringBufferSize must be >= 1");
        }

        this.disruptor = new Disruptor<>(SensorEvent::new, this.ringBufferSize, new JSensorThreadFactory("jsensor-disruptor-"));

        DisruptorSensorEventHandler[] handlers = new DisruptorSensorEventHandler[THREAD_COUNT];

        for (int i = 0; i < handlers.length; i++)
        {
            handlers[i] = new DisruptorSensorEventHandler(i, this::getBackends);
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
