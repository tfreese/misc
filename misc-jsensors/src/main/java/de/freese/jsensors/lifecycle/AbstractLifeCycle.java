// Created: 27.10.2020
package de.freese.jsensors.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 */
public abstract class AbstractLifeCycle implements LifeCycle
{
    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     *
     */
    private boolean started;

    /**
     *
     */
    private boolean stopped;

    /**
     * Erstellt ein neues {@link AbstractLifeCycle} Object.
     */
    public AbstractLifeCycle()
    {
        super();
    }

    /**
     *
     */
    protected void beforeStart()
    {
        // Empty
    }

    /**
     *
     */
    protected void beforeStop()
    {
        // Empty
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @see de.freese.jsensors.lifecycle.LifeCycle#isStarted()
     */
    @Override
    public boolean isStarted()
    {
        return this.started;
    }

    /**
     * @see de.freese.jsensors.lifecycle.LifeCycle#isStopped()
     */
    @Override
    public boolean isStopped()
    {
        return this.stopped;
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    protected void onStart() throws Exception
    {
        // Empty
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    protected void onStop() throws Exception
    {
        // Empty
    }

    /**
     * @see de.freese.jsensors.lifecycle.LifeCycle#start()
     */
    @Override
    public final void start()
    {
        beforeStart();

        getLogger().debug("starting");

        if (this instanceof Sensor)
        {
            String sensorName = ((Sensor) this).getName();

            if ((sensorName == null) || sensorName.isBlank())
            {
                throw new IllegalArgumentException("sensor name is null or empty");
            }
        }

        if (isStarted())
        {
            getLogger().error("already started");
            return;
        }

        if (isStopped())
        {
            getLogger().error("already stopped");
            return;
        }

        try
        {
            onStart();

            this.started = true;
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }

    /**
     * @see de.freese.jsensors.lifecycle.LifeCycle#stop()
     */
    @Override
    public final void stop()
    {
        getLogger().debug("stopping");

        if (isStopped())
        {
            getLogger().error("already stopped");
            return;
        }

        try
        {
            onStop();

            this.stopped = true;
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }
}
