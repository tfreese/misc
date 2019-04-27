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
     *
     */
    private boolean started = false;

    /**
     *
     */
    private boolean stopped = false;

    /**
     * Erzeugt eine neue Instanz von {@link AbstractBackend}.
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
     * @return boolean
     */
    protected boolean isStarted()
    {
        return this.started;
    }

    /**
     * @return boolean
     */
    protected boolean isStopped()
    {
        return this.stopped;
    }

    /**
     * @see de.freese.jsensors.backend.Backend#save(de.freese.jsensors.SensorValue)
     */
    @Override
    public void save(final SensorValue sensorValue)
    {
        if (sensorValue == null)
        {
            getLogger().warn("sensorvalue is null");
            return;
        }

        if (isStopped())
        {
            // throw new IllegalStateException("backend already stopped, sensorvalue discarted");
            getLogger().error("backend already stopped, sensorvalue discarted");
            return;
        }

        if (!isStarted())
        {
            getLogger().error("backend not started started, sensorvalue discarted");
            return;
        }

        saveValue(sensorValue);
    }

    /**
     * Speichert den Sensorwert.
     *
     * @param sensorValue {@link SensorValue}
     */
    protected abstract void saveValue(SensorValue sensorValue);

    /**
     * @see de.freese.jsensors.LifeCycle#start()
     */
    @Override
    public void start()
    {
        getLogger().info("starting backend");

        if (isStarted())
        {
            // throw new IllegalStateException("backend already started");
            getLogger().error("backend already started");
            return;
        }

        if (isStopped())
        {
            getLogger().error("backend already stopped");
            return;
        }

        this.started = true;
    }

    /**
     * @see de.freese.jsensors.LifeCycle#stop()
     */
    @Override
    public void stop()
    {
        getLogger().info("stopping backend");

        if (isStopped())
        {
            getLogger().error("backend already stopped");
            return;
        }

        this.stopped = true;
    }
}
