// Created: 07.12.2020
package de.freese.jconky.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Thomas Freese
 */
public class ProcessInfos
{
    /**
     *
     */
    private final List<ProcessInfo> infos;

    /**
     * Erstellt ein neues {@link ProcessInfos} Object.
     */
    public ProcessInfos()
    {
        this(Collections.emptyList());
    }

    /**
     * Erstellt ein neues {@link ProcessInfos} Object.
     *
     * @param infos {@link List}
     */
    public ProcessInfos(final List<ProcessInfo> infos)
    {
        super();

        this.infos = infos;
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
}
