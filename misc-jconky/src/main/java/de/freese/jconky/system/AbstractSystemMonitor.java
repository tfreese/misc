// Created: 01.12.2020
package de.freese.jconky.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public abstract class AbstractSystemMonitor implements SystemMonitor
{
    /**
    *
    */
    private static final com.sun.management.OperatingSystemMXBean OPERATING_SYSTEM_MX_BEAN =
            (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    /**
     * "[ ]" = "\\s+" = Whitespace: einer oder mehrere
     */
    protected static final Pattern SPACE_PATTERN = Pattern.compile("\\s+", Pattern.UNICODE_CHARACTER_CLASS);

    /**
     *
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erstellt ein neues {@link AbstractSystemMonitor} Object.
     */
    public AbstractSystemMonitor()
    {
        super();
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @see de.freese.jconky.system.SystemMonitor#getTotalSystemMemory()
     */
    @Override
    public long getTotalSystemMemory()
    {
        return OPERATING_SYSTEM_MX_BEAN.getTotalMemorySize();
    }

    /**
     * @param processBuilder {@link ProcessBuilder}
     * @return {@link List}
     */
    protected List<String> readContent(final ProcessBuilder processBuilder)
    {
        List<String> lines = null;
        List<String> errors = null;

        try
        {
            Process process = processBuilder.start();

            try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8)))
            {
                lines = inputReader.lines().collect(Collectors.toList());
                errors = errorReader.lines().collect(Collectors.toList());
            }

            try
            {
                process.waitFor();
            }
            catch (InterruptedException ex)
            {
                getLogger().error(ex.getMessage());
            }

            process.destroy();
        }
        catch (IOException ex)
        {
            throw new UncheckedIOException(ex);
        }

        if ((errors != null) && !errors.isEmpty())
        {
            getLogger().error(errors.stream().collect(Collectors.joining("\n")));
        }

        return lines;
    }

    /**
     * @param fileName String
     * @return {@link List}
     */
    protected List<String> readContent(final String fileName)
    {
        return readContent(fileName, StandardCharsets.UTF_8);
    }

    /**
     * @param fileName String
     * @param charset {@link Charset}
     * @return {@link List}
     */
    protected List<String> readContent(final String fileName, final Charset charset)
    {
        Path path = Paths.get(fileName);

        try
        {
            List<String> lines = Files.readAllLines(path, charset);

            // lines = Files.lines(path, charset).collect(Collectors.toList());

            // lines = new ArrayList<>();
            //
            // try (BufferedReader reader = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8)))
            // {
            // for (;;)
            // {
            // String line = reader.readLine();
            //
            // if (line == null)
            // {
            // break;
            // }
            //
            // lines.add(line);
            // }
            // }

            return lines;
        }
        catch (IOException ex)
        {
            throw new UncheckedIOException(ex);
        }
    }
}
