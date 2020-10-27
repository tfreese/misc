// Created: 27.10.2020
package de.freese.jsensors.sensor;

import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.jsensors.lifecycle.AbstractLifeCycle;

/**
 * Stellt sicher das es keine Sensoren mit doppelten Namen vorkommen.
 *
 * @author Thomas Freese
 */
public final class SensorRegistry extends AbstractLifeCycle
{
    /**
     * ThreadSafe Singleton-Pattern.
     *
     * @author Thomas Freese
     */
    private static final class SensorRegistryHolder
    {
        /**
         *
         */
        private static final SensorRegistry INSTANCE = new SensorRegistry();

        /**
         * Erstellt ein neues {@link SensorRegistryHolder} Object.
         */
        private SensorRegistryHolder()
        {
            super();
        }
    }

    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorRegistry.class);

    /**
     * @return {@link SensorRegistry}
     */
    public static SensorRegistry getInstance()
    {
        return SensorRegistryHolder.INSTANCE;
    }

    /**
    *
    */
    private final Map<String, Sensor> registry = new TreeMap<>();

    /**
     * Erstellt ein neues {@link SensorRegistry} Object.
     */
    private SensorRegistry()
    {
        super();
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
     */
    public void register(final Sensor sensor)
    {
        if (this.registry.containsKey(sensor.getName()))
        {
            String message = String.format("sensor '%s' already exist", sensor.getName());

            getLogger().error(message);

            throw new IllegalStateException(message);
        }

        this.registry.put(sensor.getName(), sensor);
    }
}
