// Created: 02.06.2017
package de.freese.jsensors.backend.file;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import de.freese.jsensors.backend.AbstractBackend;

/**
 * Basis-implementierung eines Backends für Dateien.
 *
 * @author Thomas Freese
 */
public abstract class AbstractFileBackend extends AbstractBackend
{
    /**
    *
    */
    private Path directory = null;

    /**
     *
     */
    private final Map<Path, OutputStream> outputStreams = new HashMap<>();

    /**
    *
    */
    private final Map<Path, PrintStream> printStreams = new HashMap<>();

    /**
     * Erzeugt eine neue Instanz von {@link AbstractFileBackend}.
     */
    public AbstractFileBackend()
    {
        super();
    }

    /**
     * @return {@link Path}
     */
    protected Path getDirectory()
    {
        return this.directory;
    }

    /**
     * @param file {@link Path}
     * @param buffered boolean
     * @return {@link OutputStream}
     * @throws IOException Falls was schief geht.
     */
    @SuppressWarnings("resource")
    protected OutputStream getOutputStream(final Path file, final boolean buffered) throws IOException
    {
        // OutputStream os = outputStreams.computeIfAbsent(path, key -> Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND));

        OutputStream os = this.outputStreams.get(file);

        if (os == null)
        {
            os = Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            if (buffered)
            {
                os = new BufferedOutputStream(os, 1024);
            }

            this.outputStreams.put(file, os);
        }

        return os;
    }

    /**
     * @param fileName String
     * @param buffered boolean
     * @return {@link OutputStream}
     * @throws IOException Falls was schief geht.
     */
    protected OutputStream getOutputStream(final String fileName, final boolean buffered) throws IOException
    {
        Path file = getDirectory().resolve(fileName);

        return getOutputStream(file, buffered);
    }

    /**
     * @param file {@link Path}
     * @param buffered boolean
     * @return {@link PrintStream}
     * @throws IOException Falls was schief geht.
     */
    protected PrintStream getPrintStream(final Path file, final boolean buffered) throws IOException
    {
        PrintStream ps = this.printStreams.get(file);

        if (ps == null)
        {
            ps = new PrintStream(getOutputStream(file, buffered));
            this.outputStreams.put(file, ps);
        }

        return ps;
    }

    /**
     * @param fileName String
     * @param buffered boolean
     * @return {@link PrintStream}
     * @throws IOException Falls was schief geht.
     */
    protected PrintStream getPrintStream(final String fileName, final boolean buffered) throws IOException
    {
        Path file = getDirectory().resolve(fileName);

        return getPrintStream(file, buffered);
    }

    /**
     * @param directory {@link Path}
     */
    public void setDirectory(final Path directory)
    {
        this.directory = Objects.requireNonNull(directory, "directory required");
    }

    /**
     * Setzt das Basis-Verzeichnis.
     *
     * @param directory String
     */
    public void setDirectory(final String directory)
    {
        if ((directory == null) || directory.isEmpty())
        {
            throw new IllegalArgumentException("directory is null or empty");
        }

        setDirectory(Paths.get(directory));
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#start()
     */
    @Override
    public void start()
    {
        super.start();

        if (getDirectory() == null)
        {
            throw new NullPointerException("directory required");
        }

        try
        {
            Files.createDirectories(getDirectory());
        }
        catch (IOException ioex)
        {
            getLogger().error(null, ioex);
        }
    }

    /**
     * @see de.freese.jsensors.backend.AbstractBackend#stop()
     */
    @Override
    public void stop()
    {
        super.stop();

        this.printStreams.values().forEach(ps -> {
            ps.flush();
            ps.close();
        });
        this.printStreams.clear();

        this.outputStreams.values().forEach(os -> {
            try
            {
                os.flush();
                os.close();
            }
            catch (IOException ioex)
            {
                getLogger().error(null, ioex);
            }
        });
        this.outputStreams.clear();
    }
}
