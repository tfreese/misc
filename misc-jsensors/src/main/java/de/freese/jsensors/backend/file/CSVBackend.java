// Created: 31.05.2017
package de.freese.jsensors.backend.file;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.AbstractBatchBackend;
import de.freese.jsensors.backend.Backend;

/**
 * {@link Backend} für die Ausgabe der Sensorwerte in eine CSV-Datei.<br>
 *
 * @author Thomas Freese
 */
public class CSVBackend extends AbstractBatchBackend
{
    /**
    *
    */
    private OutputStream outputStream;

    /**
    *
    */
    private final Path path;

    /**
     * Erstellt ein neues {@link CSVBackend} Object.
     *
     * @param path {@link Path}
     */
    public CSVBackend(final Path path)
    {
        super();

        this.path = Objects.requireNonNull(path, "path required");
    }

    /**
     * @param path {@link Path}
     * @return {@link OutputStream}
     * @throws IOException Falls was schief geht.
     */
    protected OutputStream createOutputStream(final Path path) throws IOException
    {
        boolean createHeader = !Files.exists(path);

        OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        if (createHeader)
        {
            // CSV-Header schreiben
            String header = String.format("%s;%s;%s;%s%n", "NAME", "VALUE", "TIMESTAMP", "TIME");

            byte[] bytes = header.getBytes(StandardCharsets.UTF_8);

            outputStream.write(bytes);
        }

        return outputStream;
    }

    /**
     * @param sensorValue {@link SensorValue}
     * @return byte[]
     */
    protected byte[] encode(final SensorValue sensorValue)
    {
        String formatted =
                String.format("%s;%s;%d;%s%n", sensorValue.getName(), sensorValue.getValue(), sensorValue.getTimestamp(), sensorValue.getLocalDateTime());

        byte[] bytes = formatted.getBytes(StandardCharsets.UTF_8);

        return bytes;
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBatchBackend#start()
     */
    @Override
    public void start()
    {
        super.start();

        try
        {
            Path parent = this.path.getParent();
            Files.createDirectories(parent);

            this.outputStream = createOutputStream(this.path);
        }
        catch (IOException ex)
        {
            throw new UncheckedIOException(ex);
        }
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBatchBackend#stop()
     */
    @Override
    public void stop()
    {
        super.stop();

        try
        {
            this.outputStream.flush();
            this.outputStream.close();
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBatchBackend#storeValues(java.util.List)
     */
    @Override
    protected void storeValues(final List<SensorValue> values) throws Exception
    {
        if ((values == null) || values.isEmpty())
        {
            return;
        }

        for (SensorValue sensorValue : values)
        {
            byte[] bytes = encode(sensorValue);

            this.outputStream.write(bytes);
        }

        this.outputStream.flush();
    }
}
