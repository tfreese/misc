// Created: 31.05.2017
package de.freese.jsensors.sensor;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.Backend;

/**
 * Basis-implementierung eines Sensors.
 *
 * @author Thomas Freese
 */
public abstract class AbstractSensor implements Sensor
{
    /**
     *
     */
    private Backend backend;

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @see de.freese.jsensors.sensor.Sensor#measure()
     */
    @Override
    public void measure()
    {
        try
        {
            measureImpl();
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }

    /**
     * Messen des Wertes.
     *
     * @throws Exception Falls was schief geht.
     */
    protected abstract void measureImpl() throws Exception;

    /**
     * @see de.freese.jsensors.sensor.Sensor#setBackend(de.freese.jsensors.backend.Backend)
     */
    @Override
    public void setBackend(final Backend backend)
    {
        this.backend = Objects.requireNonNull(backend, "backend required");
    }

    /**
     * Speichert den Sensorwert in den Backends.
     *
     * @param name String
     * @param value String
     */
    protected void store(final String name, final String value)
    {
        store(name, value, System.currentTimeMillis());
    }

    /**
     * Speichert den Sensorwert im {@link Backend}.
     *
     * @param name String
     * @param value String
     * @param timestamp long
     */
    protected void store(final String name, final String value, final long timestamp)
    {
        final SensorValue sensorValue = new SensorValue(name, value, timestamp);

        this.backend.store(sensorValue);
    }
}
