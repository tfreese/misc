// Created: 31.05.2017
package de.freese.jsensors.backend;

import de.freese.jsensors.SensorValue;
import de.freese.jsensors.lifecycle.AbstractLifeCycle;
import de.freese.jsensors.registry.LifeCycleManager;

/**
 * Basis-implementierung eines {@link Backend}s.
 *
 * @author Thomas Freese
 */
public abstract class AbstractBackend extends AbstractLifeCycle implements Backend
{
    /**
     * Erstellt ein neues {@link AbstractBackend} Object.
     */
    public AbstractBackend()
    {
        super();

        LifeCycleManager.getInstance().register(this);
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

        if (isStopped())
        {
            // throw new IllegalStateException("backend already stopped, sensorvalue discarted");
            getLogger().error("backend already stopped, sensorvalue discarted");
            return;
        }

        if (!isStarted())
        {
            getLogger().error("backend not started, sensorvalue discarted");
            return;
        }

        getLogger().debug("{}", sensorValue);

        saveValue(sensorValue);
    }

    /**
     * Speichert den Sensorwert.
     *
     * @param sensorValue {@link SensorValue}
     */
    protected abstract void saveValue(SensorValue sensorValue);
}
