// Created: 08.11.2020
package de.freese.jsensors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.Sensor;

/**
 * @author Thomas Freese
 */
public class SensorBackendRegistry
{
    /**
    *
    */
    private final Map<String, List<Backend>> registry = new HashMap<>();

    /**
     * Erstellt ein neues {@link SensorBackendRegistry} Object.
     */
    public SensorBackendRegistry()
    {
        super();
    }

    /**
     * @param sensorName String
     * @return {@link List}
     */
    public List<Backend> getBackends(final String sensorName)
    {
        List<Backend> backends = this.registry.computeIfAbsent(sensorName, key -> new ArrayList<>());

        return backends;
    }

    /**
     * Hier wird ein SensorName mit seinen Backends verknüft.<br>
     *
     * @param sensor {@link Sensor}
     * @param backends {@link Backend}[]
     */
    public void register(final Sensor sensor, final Backend...backends)
    {
        for (Backend backend : backends)
        {
            register(sensor.getName(), backend);
        }
    }

    /**
     * Hier wird ein SensorName mit seinen Backends verknüft.<br>
     *
     * @param sensor {@link Sensor}
     * @param backend {@link Backend}
     */
    public void register(final Sensor sensor, final Backend backend)
    {
        register(sensor.getName(), backend);
    }

    /**
     * Hier wird ein SensorName mit seinen Backends verknüft.<br>
     *
     * @param sensorName String
     * @param backends {@link Backend}[]
     */
    public void register(final String sensorName, final Backend...backends)
    {
        for (Backend backend : backends)
        {
            register(sensorName, backend);
        }
    }

    /**
     * Hier wird ein SensorName mit seinen Backends verknüft.<br>
     *
     * @param sensorName String
     * @param backend {@link Backend}
     */
    public void register(final String sensorName, final Backend backend)
    {
        List<Backend> backends = this.registry.computeIfAbsent(sensorName, key -> new ArrayList<>());

        backends.add(backend);
    }
}
