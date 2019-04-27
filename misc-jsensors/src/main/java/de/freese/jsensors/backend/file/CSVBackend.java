// Created: 31.05.2017
package de.freese.jsensors.backend.file;

import java.io.IOException;
import java.io.PrintStream;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.Sensor;

/**
 * {@link Backend} für die Ausgabe der Sensorwerte in einer CSV-Datei.<br>
 * Jeder {@link Sensor} hat seine eigene Datei.<br>
 * Format: TIMESTAMP;VALUE
 *
 * @author Thomas Freese
 */
public class CSVBackend extends AbstractFileBackend
{
    /**
     * Erzeugt eine neue Instanz von {@link CSVBackend}.
     */
    public CSVBackend()
    {
        super();
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#saveValue(de.freese.jsensors.SensorValue)
     */
    @SuppressWarnings("resource")
    @Override
    protected void saveValue(final SensorValue sensorValue)
    {
        try
        {
            PrintStream ps = getPrintStream(sensorValue.getName() + ".csv", true);

            ps.format("%d;%s%n", sensorValue.getTimestamp(), sensorValue.getValue());
        }
        catch (IOException ioex)
        {
            getLogger().error(null, ioex);
        }
    }
}
