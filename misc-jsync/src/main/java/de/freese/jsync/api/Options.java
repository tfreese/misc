// Created: 28.10.2016
package de.freese.jsync.api;

import java.io.PrintWriter;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * Enthält die Optionen für die Synchronisierung.
 *
 * @author Thomas Freese
 */
public class Options
{
    /**
     * Default: 4 MB
     */
    public static final int BUFFER_SIZE = 4 * 1024 * 1024;

    /**
    *
    */
    public static final String EMPTY_STRING = "";

    /**
     *
     */
    public static final boolean IS_LINUX = System.getProperty("os.name").toLowerCase().startsWith("linux");

    /**
     *
     */
    public static final boolean IS_WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows");

    /**
     * Mit 2 Nachkommastellen.
     *
     * @param value long
     * @param max long
     * @return float 0.0 - 100.0
     */
    public static float getPercent(final long value, final long max)
    {
        float progress = getProgress(value, max);
        float percentage = progress * 100F;

        return percentage;
    }

    /**
     * @param value long
     * @param max long
     * @return float 0.0 - 1.0
     */
    public static float getProgress(final long value, final long max)
    {
        if ((value <= 0) || (value > max))
        {
            // throw new IllegalArgumentException("invalid value: " + value);
            return 0.0F;
        }

        float progress = (float) value / (float) max;

        // // Nachkommastellen
        // int rounded = Math.round(progress * 100);
        // progress = rounded / 100F;

        return progress;
    }

    /**
    *
    */
    private boolean checksum = false;

    /**
     *
     */
    private boolean delete = false;

    /**
     *
     */
    private boolean dryRun = true;

    /**
    *
    */
    private Executor executor = ForkJoinPool.commonPool();

    /**
    *
    */
    private boolean followSymLinks = false;

    /**
     *
     */
    private PrintWriter printWriter = new PrintWriter(System.out, true);

    /**
     * Erzeugt eine neue Instanz von {@link Options}
     */
    public Options()
    {
        super();
    }

    /**
     * @return {@link Executor}
     */
    public Executor getExecutor()
    {
        return this.executor;
    }

    /**
     * Für die Ausgabe der Logs.
     *
     * @return {@link PrintWriter}
     */
    public PrintWriter getPrintWriter()
    {
        return this.printWriter;
    }

    /**
     * @return boolean
     */
    public boolean isChecksum()
    {
        return this.checksum;
    }

    /**
     * @return boolean
     */
    public boolean isDelete()
    {
        return this.delete;
    }

    /**
     * @return boolean
     */
    public boolean isDryRun()
    {
        return this.dryRun;
    }

    /**
     * @return boolean
     */
    public boolean isFollowSymLinks()
    {
        return this.followSymLinks;
    }

    /**
     * @param checksum boolean
     */
    public void setChecksum(final boolean checksum)
    {
        this.checksum = checksum;
    }

    /**
     * @param delete boolean
     */
    public void setDelete(final boolean delete)
    {
        this.delete = delete;
    }

    /**
     * @param dryRun boolean
     */
    public void setDryRun(final boolean dryRun)
    {
        this.dryRun = dryRun;
    }

    /**
     * @param executor {@link Executor}
     */
    public void setExecutor(final Executor executor)
    {
        this.executor = executor;
    }

    /**
     * @param followSymLinks boolean
     */
    public void setFollowSymLinks(final boolean followSymLinks)
    {
        this.followSymLinks = followSymLinks;
    }

    /**
     * @param printWriter {@link PrintWriter}
     */
    public void setPrintWriter(final PrintWriter printWriter)
    {
        this.printWriter = printWriter;
    }
}
