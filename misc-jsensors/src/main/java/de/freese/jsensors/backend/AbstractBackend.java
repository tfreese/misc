// Created: 31.05.2017
package de.freese.jsensors.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.jsensors.SensorValue;

/**
 * Basis-implementierung eines {@link Backend}s.
 *
 * @author Thomas Freese
 */
public abstract class AbstractBackend implements Backend
{
    /**
    *
    */
    private boolean exclusive;

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erstellt ein neues {@link AbstractBackend} Object.
     */
    public AbstractBackend()
    {
        super();
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Das Backend ist exklusiv nur für einen Sensor.
     *
     * @return boolean
     */
    protected boolean isExclusive()
    {
        return this.exclusive;
    }

    /**
     * @see de.freese.jsensors.backend.Backend#save(de.freese.jsensors.SensorValue)
     */
    @Override
    public final void save(final SensorValue sensorValue)
    {
        if (sensorValue == null)
        {
            getLogger().warn("sensorvalue is null");
            return;
        }

        // if (isStopped())
        // {
        // // throw new IllegalStateException("backend already stopped, sensorvalue discarted");
        // getLogger().error("backend already stopped, sensorvalue discarted");
        // return;
        // }
        //
        // if (!isStarted())
        // {
        // getLogger().error("backend not started, sensorvalue discarted");
        // return;
        // }

        getLogger().debug("{}", sensorValue);

        try
        {
            saveValue(sensorValue);
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }

    /**
     * Speichert den Sensorwert.
     *
     * @param sensorValue {@link SensorValue}
     * @throws Exception Falls was schief geht.
     */
    protected abstract void saveValue(SensorValue sensorValue) throws Exception;

    /**
     * Das Backend ist exklusiv nur für einen Sensor.
     *
     * @param exclusive boolean
     */
    public void setExclusive(final boolean exclusive)
    {
        this.exclusive = exclusive;
    }
}
