// Created: 31.05.2017
package de.freese.jsensors.backend.file;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.Backend;
import de.freese.jsensors.sensor.Sensor;
import de.freese.jsensors.utils.Utils;

/**
 * {@link Backend} für die Ausgabe der Sensorwerte in einer RRDTool-Datei.<br>
 * Jeder {@link Sensor} hat seine eigene Datei.<br>
 *
 * @author Thomas Freese
 */
public class RRDToolBackend extends AbstractFileBackend
{
    /**
     * @see de.freese.jsensors.backend.file.AbstractFileBackend#createOutputStream(java.nio.file.Path)
     */
    @Override
    protected OutputStream createOutputStream(final Path path) throws IOException
    {
        if (!Files.exists(path))
        {
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
                throw new IOException(lines.stream().collect(Collectors.joining(getLineSeparator())));
            }
        }

        // Dummy
        return new NullOutputStream();
    }

    /**
     * @see de.freese.jsensors.backend.file.AbstractFileBackend#encode(de.freese.jsensors.SensorValue)
     */
    @Override
    protected byte[] encode(final SensorValue sensorValue)
    {
        // Für RRDTool nicht notwendig.
        return null;
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#isExclusive()
     */
    @Override
    protected boolean isExclusive()
    {
        // TODO Das muss hier eleganter gehen !
        return true;
    }

    /**
     * @see de.freese.jsensors.backend.file.AbstractFileBackend#start()
     */
    @Override
    public void start()
    {
        super.start();

        if (!isExclusive())
        {
            throw new IllegalArgumentException("RRDTool Backend must be exclusive for one sensor");
        }

        String pathString = getPath().toString();

        if (!pathString.toLowerCase().endsWith(".rrd"))
        {
            throw new IllegalArgumentException("RRD files must have '.rrd' extension");
        }
    }

    /**
     * @see de.freese.jsensors.backend.file.AbstractFileBackend#storeValues(java.util.List)
     */
    @Override
    protected void storeValues(final List<SensorValue> values) throws Exception
    {
        if ((values == null) || values.isEmpty())
        {
            return;
        }

        String pathString = getPath().toString();

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
                throw new IOException(lines.stream().collect(Collectors.joining(getLineSeparator())));
            }
        }
    }
}
