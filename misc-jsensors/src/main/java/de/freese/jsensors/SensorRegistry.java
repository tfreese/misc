// Created: 08.11.2020
package de.freese.jsensors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.utils.LifeCycle;

/**
 * @author Thomas Freese
 */
public class SensorRegistry implements LifeCycle
{
    /**
     *
     */
    private final Set<LifeCycle> lifecyleBackends = new HashSet<>();

    /**
     *
     */
    private final Set<LifeCycle> lifecyleSensors = new HashSet<>();

    /**
     *
     */
    private final Map<String, List<Backend>> registry = new HashMap<>();

    /**
     * Erstellt ein neues {@link SensorRegistry} Object.
     */
    public SensorRegistry()
    {
        super();
    }

    /**
     * @param sensor Sensor
     */
    public void bind(final Sensor sensor)
    {
        for (String name : sensor.getNames())
        {
            if ((name == null) || name.isBlank())
            {
                throw new IllegalArgumentException("sensor name must not null or blank");
            }

            if (this.registry.containsKey(name))
            {
                throw new IllegalArgumentException(String.format("sensor name '%s' already exist", name));
            }

            this.registry.put(name, new ArrayList<>());
        }

        if (sensor instanceof LifeCycle)
        {
            this.lifecyleSensors.add((LifeCycle) sensor);
        }
    }

    /**
     * Hier wird ein Sensor mit seinen Backends verknüft.<br>
     *
     * @param name String
     * @param backends {@link Backend}[]
     */
    public void bind(final String name, final Backend...backends)
    {
        for (Backend backend : backends)
        {
            bind(name, backend);
        }
    }

    /**
     * Hier wird ein Sensor mit seinen Backends verknüft.<br>
     *
     * @param name String
     * @param backend {@link Backend}
     */
    public void bind(final String name, final Backend backend)
    {
        List<Backend> backends = this.registry.computeIfAbsent(name, key -> new ArrayList<>());

        backends.add(backend);

        if (backend instanceof LifeCycle)
        {
            this.lifecyleBackends.add((LifeCycle) backend);
        }
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#start()
     */
    @Override
    public void start()
    {
        this.lifecyleSensors.forEach(LifeCycle::start);
        this.lifecyleBackends.forEach(LifeCycle::start);
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#stop()
     */
    @Override
    public void stop()
    {
        this.lifecyleSensors.forEach(LifeCycle::stop);
        this.lifecyleBackends.forEach(LifeCycle::stop);
    }

    /**
     * @param sensorValue {@link SensorValue}
     */
    public void store(final SensorValue sensorValue)
    {
        // List<Backend> backends = this.registry.computeIfAbsent(name, key -> new ArrayList<>());
        List<Backend> backends = this.registry.get(sensorValue.getName());

        if ((backends == null) || backends.isEmpty())
        {
            throw new IllegalStateException(String.format("no backends configured for sensor name '%s'", sensorValue.getName()));
        }

        backends.forEach(be -> be.store(sensorValue));
    }
}
