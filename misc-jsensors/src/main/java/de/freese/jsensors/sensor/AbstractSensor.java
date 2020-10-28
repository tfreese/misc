// Created: 31.05.2017
package de.freese.jsensors.sensor;

import java.util.Objects;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.lifecycle.AbstractLifeCycle;
import de.freese.jsensors.registry.LifeCycleManager;
import de.freese.jsensors.utils.Utils;

/**
 * Basis-implementierung eines Sensors.
 *
 * @author Thomas Freese
 */
public abstract class AbstractSensor extends AbstractLifeCycle implements Sensor
{
    /**
     *
     */
    private Backend backend;

    /**
     *
     */
    private boolean exclusive;

    /**
    *
    */
    private String name;

    /**
     * Erstellt ein neues {@link AbstractSensor} Object.
     */
    public AbstractSensor()
    {
        super();

        LifeCycleManager.getInstance().register(this);
    }

    /**
     * @see de.freese.jsensors.lifecycle.AbstractLifeCycle#beforeStart()
     */
    @Override
    protected void beforeStart()
    {
        String sensorName = getName();

        if ((sensorName == null) || sensorName.isBlank())
        {
            throw new IllegalArgumentException("sensor name is null or empty");
        }
    }

    /**
     * @return {@link Backend}
     */
    protected Backend getBackend()
    {
        return this.backend;
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
     * @return boolean
     */
    protected boolean isExclusive()
    {
        return this.exclusive;
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
        final SensorValue sensorValue = new SensorValue(sensorName, value, timestamp, isExclusive());

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

    /**
     * @see de.freese.jsensors.sensor.Sensor#setExclusive(boolean)
     */
    @Override
    public void setExclusive(final boolean exclusive)
    {
        this.exclusive = exclusive;
    }

    /**
     * @see de.freese.jsensors.sensor.Sensor#setName(java.lang.String)
     */
    @Override
    public void setName(final String name)
    {
        Objects.requireNonNull(name, "name required");

        String formattedName = Utils.sensorNameToTableName(name);

        if (!name.equals(formattedName))
        {
            getLogger().info("change sensor name from '{}' to '{}'", name, formattedName);
        }

        this.name = formattedName;
    }
}
