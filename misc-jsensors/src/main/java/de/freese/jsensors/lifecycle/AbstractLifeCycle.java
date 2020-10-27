// Created: 27.10.2020
package de.freese.jsensors.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * @throws Exception Falls was schief geht.
     */
    protected void doStart() throws Exception
    {
        // Empty
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    protected void doStop() throws Exception
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
     * @see de.freese.jsensors.lifecycle.LifeCycle#start()
     */
    @Override
    public final void start()
    {
        getLogger().info("starting");

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
            doStart();

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
        getLogger().info("stopping");

        if (isStopped())
        {
            getLogger().error("already stopped");
            return;
        }

        try
        {
            doStop();

            this.stopped = true;
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }
}
