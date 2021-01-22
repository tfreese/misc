// Created: 02.06.2017
package de.freese.jsensors.backend.file;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Objects;
import de.freese.jsensors.SensorValue;
import de.freese.jsensors.backend.AbstractBatchBackend;

/**
 * Basis-implementierung eines Backends für Dateien.
 *
 * @author Thomas Freese
 */
public abstract class AbstractFileBackend extends AbstractBatchBackend
{
    /**
     * @author Thomas Freese
     */
    static final class NullOutputStream extends OutputStream
    {
        /**
         * @see java.io.OutputStream#write(byte[])
         */
        @Override
        public void write(final byte[] b) throws IOException
        {
            // to /dev/null
        }

        /**
         * @see java.io.OutputStream#write(byte[], int, int)
         */
        @Override
        public void write(final byte[] b, final int off, final int len)
        {
            // to /dev/null
        }

        /**
         * @see java.io.OutputStream#write(int)
         */
        @Override
        public void write(final int b)
        {
            // to /dev/null
        }
    }

    /**
     *
     */
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

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
     * @return String
     */
    protected String getLineSeparator()
    {
        return LINE_SEPARATOR;
    }

    /**
     * @return {@link Path}
     */
    protected Path getPath()
    {
        return this.path;
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
        super.start();

        if (getPath() == null)
        {
            throw new NullPointerException("path required");
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
