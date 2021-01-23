// Created: 08.11.2020
package de.freese.jsensors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.freese.jsensors.backend.Backend;

/**
 * @author Thomas Freese
 */
public class SensorRegistry
{
    /**
     *
     */
    private final Map<String, List<Backend>> registry = new HashMap<>();

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
     * Hier wird ein Sensor mit seinem Backend verknüft.<br>
     *
     * @param name String
     * @param backend {@link Backend}
     */
    public void bind(final String name, final Backend backend)
    {
        this.registry.computeIfAbsent(name, key -> new ArrayList<>()).add(backend);
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
