// Created: 31.05.2017
package de.freese.jsensors.backend.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.AbstractBatchBackend;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.utils.Utils;

/**
 * {@link Backend} für die Ausgabe der Sensorwerte in einer RRDTool-Datei.<br>
 * Jeder {@link Sensor} hat seine eigene Datei.<br>
 *
 * @author Thomas Freese
 */
public class RRDToolBackend extends AbstractBatchBackend
{
    /**
    *
    */
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     *
     */
    private final Path path;

    /**
     * Erstellt ein neues {@link RRDToolBackend} Object.
     *
     * @param path {@link Path}
     */
    public RRDToolBackend(final Path path)
    {
        super();

        this.path = Objects.requireNonNull(path, "path required");
    }

    /**
     * @param path {@link Path}
     * @throws IOException Falls was schief geht.
     */
    protected void createRrdFileIfNotExist(final Path path) throws IOException
    {
        if (Files.exists(path))
        {
            return;
        }

        String pathString = path.toString();

        getLogger().info("create RRD: {}", pathString);

        // Create default RRD.
        List<String> command = new ArrayList<>();
        command.add("rrdtool");
        command.add("create");
        command.add(pathString);
        command.add("--step");
        command.add("60");
        command.add("DS:value_gauge:GAUGE:600:0:U");
        command.add("RRA:MIN:0.5:60:168");
        command.add("RRA:MAX:0.5:60:168");
        command.add("RRA:AVERAGE:0.5:1:10080");

        // System.out.println(command.stream().collect(Collectors.joining(" ")));

        List<String> lines = Utils.executeCommand(command.toArray(Utils.EMPTY_STRING_ARRAY));

        if (!lines.isEmpty())
        {
            throw new IOException(lines.stream().collect(Collectors.joining(LINE_SEPARATOR)));
        }
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

            createRrdFileIfNotExist(this.path);
        }
        catch (IOException ex)
        {
            throw new UncheckedIOException(ex);
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

        String pathString = this.path.toString();

        for (SensorValue sensorValue : values)
        {
            // Update RRD.
            List<String> command = new ArrayList<>();
            command.add("rrdtool");
            command.add("update");
            command.add(pathString);
            command.add(String.format("%s:%s", sensorValue.getTimestamp(), sensorValue.getValue()));

            // System.out.println(command.stream().collect(Collectors.joining(" ")));

            List<String> lines = Utils.executeCommand(command.toArray(Utils.EMPTY_STRING_ARRAY));

            if (!lines.isEmpty())
            {
                throw new IOException(lines.stream().collect(Collectors.joining(LINE_SEPARATOR)));
            }
        }
    }
}
