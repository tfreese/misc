// Created: 31.05.2017
package de.freese.jsensors.backend.file;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.Backend;

/**
 * {@link Backend} für die Ausgabe der Sensorwerte in eine CSV-Datei.<br>
 *
 * @author Thomas Freese
 */
public class CSVBackend extends AbstractFileBackend
{

    /**
     * @see de.freese.jsensors.backend.file.AbstractFileBackend#createOutputStream(java.nio.file.Path)
     */
    @Override
    protected OutputStream createOutputStream(final Path path) throws IOException
    {
        boolean createHeader = !Files.exists(path);

        OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        if (createHeader)
        {
            // CSV-Header schreiben
            String header = null;

            if (isExclusive())
            {
                // Ohne SensorName.
                header = String.format("%s;%s;%s%n", "VALUE", "TIMESTAMP", "TIME");
            }
            else
            {
                // Mit SensorName.
                header = String.format("%s;%s;%s;%s%n", "NAME", "VALUE", "TIMESTAMP", "TIME");
            }

            byte[] bytes = header.getBytes(StandardCharsets.UTF_8);

            outputStream.write(bytes);
        }

        return outputStream;
    }

    /**
     * @see de.freese.jsensors.backend.file.AbstractFileBackend#encode(de.freese.jsensors.SensorValue)
     */
    @Override
    protected byte[] encode(final SensorValue sensorValue)
    {
        String formatted = null;

        if (isExclusive())
        {
            // Ohne SensorName.
            formatted = String.format("%s;%d;%s%n", sensorValue.getValue(), sensorValue.getTimestamp(), sensorValue.getLocalDateTime());
        }
        else
        {
            // Mit SensorName.
            formatted =
                    String.format("%s;%s;%d;%s%n", sensorValue.getName(), sensorValue.getValue(), sensorValue.getTimestamp(), sensorValue.getLocalDateTime());
        }

        byte[] bytes = formatted.getBytes(StandardCharsets.UTF_8);

        return bytes;
    }
}
