// Created: 07.12.2020
package de.freese.jconky.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Thomas Freese
 */
public class ProcessInfos
{
    /**
     *
     */
    private static final Predicate<ProcessInfo> PREDICATE_RUNNING = ph -> "R".equals(ph.getState());

    /**
     *
     */
    private static final Predicate<ProcessInfo> PREDICATE_SLEEPING = ph -> "S".equals(ph.getState());

    /**
     *
     */
    private static final Predicate<ProcessInfo> PREDICATE_WAITING = ph -> "W".equals(ph.getState());

    /**
     *
     */
    private final List<ProcessInfo> infos;

    /**
     *
     */
    private final double uptimeInSeconds;

    /**
     * Erstellt ein neues {@link ProcessInfos} Object.
     */
    public ProcessInfos()
    {
        this(Collections.emptyList(), 0D);
    }

    /**
     * Erstellt ein neues {@link ProcessInfos} Object.
     *
     * @param infos {@link List}
     * @param uptimeInSeconds double
     */
    public ProcessInfos(final List<ProcessInfo> infos, final double uptimeInSeconds)
    {
        super();

        this.infos = infos;
        this.uptimeInSeconds = uptimeInSeconds;
    }

    /**
     * @return int
     */
    public int getAlive()
    {
        Predicate<ProcessInfo> predicateAlive = PREDICATE_RUNNING.or(PREDICATE_SLEEPING).or(PREDICATE_WAITING);

        return (int) this.infos.stream().filter(predicateAlive).count();
    }

    /**
     * @return int
     */
    public int getRunning()
    {
        return (int) this.infos.stream().filter(PREDICATE_RUNNING).count();
    }

    /**
     * Liefert die höchsten N Prozesse.
     *
     * @param count int
     * @return {@link List}
     */
    public List<ProcessInfo> getSortedByCpuUsage(final int count)
    {
        return this.infos.stream().sorted(Comparator.comparing(ProcessInfo::getCpuUsage).reversed()).limit(count).collect(Collectors.toList());
    }

    /**
     * Liefert die höchsten N Prozesse.
     *
     * @param count int
     * @return {@link List}
     */
    public List<ProcessInfo> getSortedByMemoryUsage(final int count)
    {
        return this.infos.stream().sorted(Comparator.comparing(ProcessInfo::getTotalBytes).reversed()).limit(count).collect(Collectors.toList());
    }

    /**
     * @param count int
     * @return {@link List}
     */
    public List<ProcessInfo> getSortedByName(final int count)
    {
        return this.infos.stream().sorted(Comparator.comparing(ProcessInfo::getName)).limit(count).collect(Collectors.toList());
    }

    /**
     * @return String
     */
    public String getUptime()
    {
        int seconds = (int) getUptimeInSeconds() % 60;
        int minutes = (int) (getUptimeInSeconds() / 60) % 60;
        int hours = (int) (getUptimeInSeconds() / 60 / 60) % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    /**
     * @return double
     */
    public double getUptimeInSeconds()
    {
        return this.uptimeInSeconds;
    }

    /**
     * @return int
     */
    public int size()
    {
        return this.infos.size();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(getClass().getSimpleName());
        builder.append("[");
        builder.append("size=").append(size());
        builder.append(", uptimeInSeconds=").append(this.uptimeInSeconds);
        builder.append("]");

        return builder.toString();
    }
}
