// Created: 12.05.2017
package de.freese.jsensors.backend;

/**
 * {@link Backend} für die Ausgabe der Sensorwerte auf der Logger-Console.
 *
 * @author Thomas Freese
 */
public class LoggingBackend extends AbstractBackend
{
    /**
     * Erzeugt eine neue Instanz von {@link LoggingBackend}.
     */
    public LoggingBackend()
    {
        super();
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#save(java.lang.String, long, java.lang.String)
     */
    @Override
    public void save(final String value, final long timestamp, final String sensor)
    {
        SensorValue sensorValue = new SensorValue(value, timestamp, sensor);

        save(sensorValue);
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#initialize()
     */
    @Override
    protected void initialize() throws Exception
    {
        // Keine Initialisierung notwendig.
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#save(de.freese.jsensors.backend.SensorValue)
     */
    @Override
    protected void save(final SensorValue sensorValue)
    {
        getLogger().info("{}", sensorValue);
    }
}
