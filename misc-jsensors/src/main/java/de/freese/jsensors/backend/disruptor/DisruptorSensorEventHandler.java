// Created: 27.10.2020
package de.freese.jsensors.backend.disruptor;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lmax.disruptor.EventHandler;
import de.freese.jsensors.SensorRegistry;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.Backend;

/**
 * @author Thomas Freese
 */
public class DisruptorSensorEventHandler implements EventHandler<SensorEvent>, Backend
{
    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DisruptorSensorEventHandler.class);

    /**
     *
     */
    private final int ordinal;

    /**
     *
     */
    private final int parallelism;

    /**
     *
     */
    private final SensorRegistry sensorRegistry;

    /**
     * @param ordinal int
     * @param parallelism int
     * @param sensorRegistry {@link SensorRegistry}
     */
    public DisruptorSensorEventHandler(final int ordinal, final int parallelism, final SensorRegistry sensorRegistry)
    {
        super();

        this.ordinal = ordinal;
        this.parallelism = parallelism;
        this.sensorRegistry = Objects.requireNonNull(sensorRegistry, "sensorRegistry required");
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return LOGGER;
    }

    /**
     * @see com.lmax.disruptor.EventHandler#onEvent(java.lang.Object, long, boolean)
     */
    @Override
    public void onEvent(final SensorEvent event, final long sequence, final boolean endOfBatch) throws Exception
    {
        // Load-Balancing auf die Handler über die Sequence.
        if (this.ordinal == (sequence % this.parallelism))
        {
            SensorValue sensorValue = event.getSensorValue();
            event.setSensorValue(null);

            store(sensorValue);
        }
    }

    /**
     * @see de.freese.jsensors.backend.Backend#store(de.freese.jsensors.SensorValue)
     */
    @Override
    public void store(final SensorValue sensorValue)
    {
        this.sensorRegistry.store(sensorValue);
    }
}
