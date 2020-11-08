// Created: 27.10.2020
package de.freese.jsensors.backend.disruptor;

import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.lmax.disruptor.EventHandler;
import de.freese.jsensors.SensorBackendRegistry;
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
    private final SensorBackendRegistry sensorBackendRegistry;

    /**
     * @param ordinal int
     * @param parallelism int
     * @param sensorBackendRegistry {@link SensorBackendRegistry}
     */
    public DisruptorSensorEventHandler(final int ordinal, final int parallelism, final SensorBackendRegistry sensorBackendRegistry)
    {
        super();

        this.ordinal = ordinal;
        this.parallelism = parallelism;
        this.sensorBackendRegistry = Objects.requireNonNull(sensorBackendRegistry, "sensorBackendRegistry required");
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

            save(sensorValue);
        }
    }

    /**
     * @see de.freese.jsensors.backend.Backend#save(de.freese.jsensors.SensorValue)
     */
    @Override
    public void save(final SensorValue sensorValue)
    {
        List<Backend> backends = this.sensorBackendRegistry.getBackends(sensorValue.getName());

        if ((backends == null) || backends.isEmpty())
        {
            getLogger().error("no backends configured for sensor '{}'", sensorValue.getName());
            return;
        }

        for (Backend backend : backends)
        {
            try
            {
                backend.save(sensorValue);
            }
            catch (Exception ex)
            {
                getLogger().error(null, ex);
            }
        }
    }
}
