// Created: 31.05.2017
package de.freese.jsensors.backend;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
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
     * @see de.freese.jsensors.backend.AbstractBackend#saveImpl(de.freese.jsensors.backend.SensorValue)
     */
    @Override
    protected void saveImpl(final SensorValue sensorValue)
    {
        // "csv.zip"
        Path path = getBasePath().resolve(sensorValue.getName() + ".csv");

        // GZIPOutputStream
        // ZipOutputStream
        try (PrintStream ps = new PrintStream(Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND)))
        {
            ps.format("%d;%s%n", sensorValue.getTimestamp(), sensorValue.getValue());
        }
        catch (IOException ioex)
        {
            getLogger().error(null, ioex);
        }
    }
}
