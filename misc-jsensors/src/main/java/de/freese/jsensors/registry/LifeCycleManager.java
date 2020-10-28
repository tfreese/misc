// Created: 28.10.2020
package de.freese.jsensors.registry;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.lifecycle.LifeCycle;
import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 */
public final class LifeCycleManager
{
    /**
     * ThreadSafe Singleton-Pattern.
     *
     * @author Thomas Freese
     */
    private static final class LifeCycleManagerHolder
    {
        /**
         *
         */
        private static final LifeCycleManager INSTANCE = new LifeCycleManager();

        /**
         * Erstellt ein neues {@link LifeCycleManagerHolder} Object.
         */
        private LifeCycleManagerHolder()
        {
            super();
        }
    }

    /**
     * @return {@link LifeCycleManager}
     */
    public static LifeCycleManager getInstance()
    {
        return LifeCycleManagerHolder.INSTANCE;
    }

    /**
    *
    */
    private final Set<LifeCycle> components = new HashSet<>();

    /**
    *
    */
    private final Map<String, Sensor> sensorRegistry = new TreeMap<>();

    /**
     * Erstellt ein neues {@link LifeCycleManager} Object.
     */
    private LifeCycleManager()
    {
        super();
    }

    /**
     * @return {@link List}
     */
    private List<Backend> getBackends()
    {
        return this.components.stream().filter(c -> c instanceof Backend).map(c -> (Backend) c).collect(Collectors.toList());
    }

    /**
     * @return {@link List}
     */
    private List<Sensor> getSensors()
    {
        return this.components.stream().filter(c -> c instanceof Sensor).map(c -> (Sensor) c).collect(Collectors.toList());
    }

    /**
     * @param lifeCycle {@link LifeCycle}
     */
    public void register(final LifeCycle lifeCycle)
    {
        this.components.add(lifeCycle);
    }

    /**
     *
     */
    public void start()
    {
        startBackends();
        startSensors();
    }

    /**
     *
     */
    private void startBackends()
    {
        for (Backend backend : getBackends())
        {
            backend.start();
        }
    }

    /**
     *
     */
    private void startSensors()
    {
        for (Sensor sensor : getSensors())
        {
            String sensorName = sensor.getName();

            if (this.sensorRegistry.containsKey(sensorName))
            {
                String message = String.format("sensor '%s' already exist", sensor.getName());

                throw new IllegalStateException(message);
            }

            sensor.start();

            this.sensorRegistry.put(sensor.getName(), sensor);
        }
    }

    /**
     *
     */
    public void stop()
    {
        getSensors().forEach(Sensor::stop);
        getBackends().forEach(Backend::stop);

        this.components.clear();
        this.sensorRegistry.clear();
    }
}
