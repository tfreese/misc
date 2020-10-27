// Created: 27.10.2020
package de.freese.jsensors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.jsensors.backend.AbstractBackend;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.Sensor;

/**
 * Definiert in welche {@link Backend}s die Daten eines {@link Sensor}s geschrieben werden.
 *
 * @author Thomas Freese
 */
public final class SensorBackendRegistry extends AbstractBackend
{
    /**
     * ThreadSafe Singleton-Pattern.
     *
     * @author Thomas Freese
     */
    private static final class SensorBackendRegistryHolder
    {
        /**
         *
         */
        private static final SensorBackendRegistry INSTANCE = new SensorBackendRegistry();

        /**
         * Erstellt ein neues {@link SensorBackendRegistryHolder} Object.
         */
        private SensorBackendRegistryHolder()
        {
            super();
        }
    }

    /**
     *
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorBackendRegistry.class);

    /**
     * @return {@link SensorBackendRegistry}
     */
    public static SensorBackendRegistry getInstance()
    {
        return SensorBackendRegistryHolder.INSTANCE;
    }

    /**
     *
     */
    private final Map<String, List<Backend>> registry = new HashMap<>();

    /**
     * Erstellt ein neues {@link SensorBackendRegistry} Object.
     */
    private SensorBackendRegistry()
    {
        super();

        start();
    }

    /**
     * @see de.freese.jsensors.lifecycle.AbstractLifeCycle#doStop()
     */
    @Override
    protected void doStop() throws Exception
    {
        this.registry.clear();
    }

    /**
     * @return {@link Logger}
     */
    @Override
    protected Logger getLogger()
    {
        return LOGGER;
    }

    /**
     * @param sensor {@link Sensor}
     * @param backend {@link Backend}
     */
    public void register(final Sensor sensor, final Backend backend)
    {
        List<Backend> backends = this.registry.computeIfAbsent(sensor.getName(), key -> new ArrayList<>());

        if (backends.contains(backend))
        {
            getLogger().warn("backend '{}' already bound to sensor '{}'", backend.getClass().getSimpleName(), sensor.getName());

            return;
        }

        backends.add(backend);
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#saveValue(de.freese.jsensors.SensorValue)
     */
    @Override
    protected void saveValue(final SensorValue sensorValue)
    {
        List<Backend> backends = this.registry.computeIfAbsent(sensorValue.getName(), key -> new ArrayList<>());

        for (Backend backend : backends)
        {
            try
            {
                backend.save(sensorValue);
            }
            catch (Exception ex)
            {
                getLogger().error(null, ex);
            }
        }
    }
}
