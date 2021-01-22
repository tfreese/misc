// Created: 31.05.2017
package de.freese.jsensors.sensor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.jsensors.SensorRegistry;
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
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private SensorRegistry registry;

    /**
     * Erstellt ein neues {@link AbstractSensor} Object.
     */
    protected AbstractSensor()
    {
        super();

    }

    /**
     * @see de.freese.jsensors.sensor.Sensor#bindTo(de.freese.jsensors.SensorRegistry)
     */
    @Override
    public void bindTo(final SensorRegistry registry)
    {
        this.registry = registry;

        this.registry.bind(this);
    }

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

        this.registry.store(sensorValue);
    }
}
