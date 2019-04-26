// Created: 12.05.2017
package de.freese.jsensors.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link Backend} für die Ausgabe der Sensorwerte auf der Logger-Console.
 *
 * @author Thomas Freese
 */
public class LoggingBackend implements Backend
{
    /**
    *
    */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erzeugt eine neue Instanz von {@link LoggingBackend}.
     */
    public LoggingBackend()
    {
        super();
    }

    /**
     * @return {@link Logger}
     */
    public Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#save(de.freese.jsensors.backend.SensorValue)
     */
    @Override
    public void save(final SensorValue sensorValue)
    {
        getLogger().info("{}", sensorValue);
    }

    /**
     * @param logger {@link Logger}
     */
    public void setLogger(final Logger logger)
    {
        this.logger = logger;
    }
}
