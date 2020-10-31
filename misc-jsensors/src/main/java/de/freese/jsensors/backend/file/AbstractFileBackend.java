// Created: 02.06.2017
package de.freese.jsensors.backend.file;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.AbstractBackend;
import de.freese.jsensors.utils.LifeCycle;

/**
 * Basis-implementierung eines Backends für Dateien.
 *
 * @author Thomas Freese
 */
public abstract class AbstractFileBackend extends AbstractBackend implements LifeCycle
{
    /**
     *
     */
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     *
     */
    private int batchSize = 5;

    /**
     *
     */
    private List<SensorValue> buffer;

    /**
     *
     */
    private boolean exclusive;

    /**
     *
     */
    private OutputStream outputStream;

    /**
    *
    */
    private Path path;

    /**
     * @param path {@link Path}
     * @return {@link OutputStream}
     * @throws IOException Falls was schief geht.
     */
    protected OutputStream createOutputStream(final Path path) throws IOException
    {
        return Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    /**
     * @param sensorValue {@link SensorValue}
     * @return byte[]
     */
    protected abstract byte[] encode(SensorValue sensorValue);

    /**
     * @return {@link List}
     */
    private synchronized List<SensorValue> flush()
    {
        List<SensorValue> list = this.buffer;
        this.buffer = null;

        return list;
    }

    /**
     * @return int
     */
    private int getBatchSize()
    {
        return this.batchSize;
    }

    /**
     * @return String
     */
    protected String getLineSeparator()
    {
        return LINE_SEPARATOR;
    }

    /**
     * @return {@link Path}
     */
    private Path getPath()
    {
        return this.path;
    }

    /**
     * Die Datei ist exklusiv nur für einen Sensor.
     *
     * @return boolean
     */
    protected boolean isExclusive()
    {
        return this.exclusive;
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#saveValue(de.freese.jsensors.SensorValue)
     */
    @Override
    protected void saveValue(final SensorValue sensorValue) throws Exception
    {
        if (sensorValue == null)
        {
            return;
        }

        if (this.buffer == null)
        {
            this.buffer = new ArrayList<>();
        }

        this.buffer.add(sensorValue);

        if (this.buffer.size() >= getBatchSize())
        {
            saveValues(flush());
        }
    }

    /**
     * @param values {@link List}
     * @throws Exception Falls was schief geht.
     */
    private void saveValues(final List<SensorValue> values) throws Exception
    {
        if (values == null)
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

    /**
     * @param batchSize int
     */
    public void setBatchSize(final int batchSize)
    {
        this.batchSize = batchSize;
    }

    /**
     * Die Datei ist exklusiv nur für einen Sensor.
     *
     * @param exclusive boolean
     */
    public void setExclusive(final boolean exclusive)
    {
        this.exclusive = exclusive;
    }

    /**
     * @param path {@link Path}
     */
    public void setPath(final Path path)
    {
        this.path = Objects.requireNonNull(path, "path required");
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#start()
     */
    @Override
    public void start()
    {
        if (getPath() == null)
        {
            throw new NullPointerException("path required");
        }

        if (getBatchSize() < 1)
        {
            throw new IllegalArgumentException("batchSize must be >= 1");
        }

        try
        {
            Path parent = getPath().getParent();
            Files.createDirectories(parent);

            this.outputStream = createOutputStream(getPath());
        }
        catch (IOException ex)
        {
            throw new UncheckedIOException(ex);
        }
    }

    /**
     * @see de.freese.jsensors.utils.LifeCycle#stop()
     */
    @Override
    public void stop()
    {
        try
        {
            saveValues(flush());
        }
        catch (Exception ex)
        {
            getLogger().error(null, ex);
        }

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
}
