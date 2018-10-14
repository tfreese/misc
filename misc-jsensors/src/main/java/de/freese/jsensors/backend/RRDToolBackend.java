// Created: 31.05.2017
package de.freese.jsensors.backend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import de.freese.jsensors.Utils;
import de.freese.jsensors.sensor.Sensor;

/**
 * {@link Backend} für die Ausgabe der Sensorwerte in einer RRDTool-Datei.<br>
 * Jeder {@link Sensor} hat seine eigene Datei.<br>
 *
 * @author Thomas Freese
 */
public class RRDToolBackend extends AbstractFileBackend
{
    /**
     *
     */
    private final Set<String> existingFiles = Collections.synchronizedSet(new TreeSet<>());

    /**
     * Erzeugt eine neue Instanz von {@link RRDToolBackend}.
     */
    public RRDToolBackend()
    {
        super();
    }

    /**
     * Erzeugt die RRD-Datei, falls diese noch nicht existiert.
     *
     * @param path {@link Path}
     * @throws Exception Falls was schief geht.
     */
    private void createFileIfNotExist(final Path path) throws Exception
    {
        if (Files.exists(path))
        {
            return;
        }

        String pathString = path.toString();

        getLogger().info("Create default RRD: {}", pathString);

        // Create default RRD.
        List<String> command = new ArrayList<>();
        command.add("rrdtool");
        command.add("create");
        command.add(pathString);
        command.add("--step");
        command.add("60");
        command.add("DS:value_gauge:GAUGE:600:0:U");
        command.add("DS:value_counter:COUNTER:600:0:U");
        command.add("RRA:MIN:0.5:60:168");
        command.add("RRA:MAX:0.5:60:168");
        command.add("RRA:AVERAGE:0.5:1:10080");

        // System.out.println(command.stream().collect(Collectors.joining(" ")));

        List<String> lines = Utils.executeCommand(command.toArray(Utils.EMPTY_STRING_ARRAY));

        if (!lines.isEmpty())
        {
            throw new IOException(lines.stream().collect(Collectors.joining(System.getProperty("line.separator"))));
        }
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#save(de.freese.jsensors.backend.SensorValue)
     */
    @Override
    protected void save(final SensorValue sensorValue)
    {
        Path path = getBasePath().resolve(sensorValue.getSensor() + ".rrd");
        String pathString = path.toString();

        try
        {
            if (!this.existingFiles.contains(pathString))
            {
                synchronized (this.existingFiles)
                {
                    // DoubleCheckLock
                    if (!this.existingFiles.contains(pathString))
                    {
                        createFileIfNotExist(path);

                        this.existingFiles.add(pathString);
                    }
                }
            }

            // Update RRD.
            List<String> command = new ArrayList<>();
            command.add("rrdtool");
            command.add("update");
            command.add(pathString);
            command.add(String.format("%s:%s:%s", sensorValue.getTimestamp(), sensorValue.getValue(), sensorValue.getValue()));

            // System.out.println(command.stream().collect(Collectors.joining(" ")));

            List<String> lines = Utils.executeCommand(command.toArray(Utils.EMPTY_STRING_ARRAY));

            if (!lines.isEmpty())
            {
                throw new IOException(lines.stream().collect(Collectors.joining(System.getProperty("line.separator"))));
            }
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }
    }
}
