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
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @see de.freese.jsensors.backend.Backend#store(de.freese.jsensors.SensorValue)
     */
    @Override
    public final void store(final SensorValue sensorValue)
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
            storeValue(sensorValue);
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
    protected abstract void storeValue(SensorValue sensorValue) throws Exception;
}
