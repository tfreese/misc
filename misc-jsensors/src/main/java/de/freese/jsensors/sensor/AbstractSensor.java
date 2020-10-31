// Created: 31.05.2017
package de.freese.jsensors.sensor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
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
    private static final Set<String> NAMES = new HashSet<>();

    /**
     *
     */
    private Backend backend;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
    *
    */
    private final String name;

    /**
     * Erstellt ein neues {@link AbstractSensor} Object.
     *
     * @param name String; Der Name wird in ein Datei- und Datenbankverträgliches Format umgewandelt.
     */
    public AbstractSensor(final String name)
    {
        super();

        if ((name == null) || name.isBlank())
        {
            throw new IllegalArgumentException("name must not null or blank");
        }

        String formattedName = name.trim().toUpperCase();

        if (NAMES.contains(formattedName))
        {
            String message = String.format("sensor '%s' already exist", formattedName);

            throw new IllegalArgumentException(message);
        }

        NAMES.add(formattedName);

        this.name = name;
    }

    /**
     * @return {@link Backend}
     */
    protected Backend getBackend()
    {
        return this.backend;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @see de.freese.jsensors.sensor.Sensor#getName()
     */
    @Override
    public String getName()
    {
        return this.name;
    }

    /**
     * Speichert den Sensorwert in den Backends.
     *
     * @param value String
     */
    protected void save(final String value)
    {
        save(value, System.currentTimeMillis(), getName());
    }

    /**
     * Speichert den Sensorwert im {@link Backend}.
     *
     * @param value String
     * @param timestamp long
     * @param sensorName String
     */
    protected void save(final String value, final long timestamp, final String sensorName)
    {
        final SensorValue sensorValue = new SensorValue(sensorName, value, timestamp);

        this.backend.save(sensorValue);
    }

    /**
     * @see de.freese.jsensors.sensor.Sensor#scan()
     */
    @Override
    public final void scan()
    {
        try
        {
            scanValue();
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
    protected abstract void scanValue() throws Exception;

    /**
     * @see de.freese.jsensors.sensor.Sensor#setBackend(de.freese.jsensors.backend.Backend)
     */
    @Override
    public void setBackend(final Backend backend)
    {
        this.backend = Objects.requireNonNull(backend, "backend required");
    }
}
