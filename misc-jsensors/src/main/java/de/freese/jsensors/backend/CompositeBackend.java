// Created: 29.10.2020
package de.freese.jsensors.backend;

import java.util.ArrayList;
import java.util.List;
import de.freese.jsensors.SensorValue;

/**
 * @author Thomas Freese
 */
public class CompositeBackend implements Backend
{
    /**
     *
     */
    private final List<Backend> backends = new ArrayList<>();

    /**
     * Erstellt ein neues {@link CompositeBackend} Object.
     */
    public CompositeBackend()
    {
        super();
    }

    /**
     * @param backend {@link Backend}
     */
    public void addBackend(final Backend backend)
    {
        this.backends.add(backend);
    }

    /**
     * @see de.freese.jsensors.backend.Backend#save(de.freese.jsensors.SensorValue)
     */
    @Override
    public void save(final SensorValue sensorValue)
    {
        this.backends.forEach(backend -> backend.save(sensorValue));
    }

    /**
     * @param backends {@link List}
     */
    public void setBackends(final List<Backend> backends)
    {
        this.backends.addAll(backends);
    }
}
