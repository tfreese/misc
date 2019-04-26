// Created: 12.05.2017
package de.freese.jsensors.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.jsensors.SensorValue;

/**
 * {@link Backend} für die Ausgabe der Sensorwerte auf der Logger-Console.
 *
 * @author Thomas Freese
 */
public class LoggingBackend extends AbstractBackend
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
     * @see de.freese.jsensors.backend.AbstractBackend#getLogger()
     */
    @Override
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#saveImpl(de.freese.jsensors.SensorValue)
     */
    @Override
    protected void saveImpl(final SensorValue sensorValue)
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
