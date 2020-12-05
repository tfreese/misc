// Created: 05.12.2020
package de.freese.jconky.model;

import java.util.Collections;
import java.util.Map;

/**
 * @author Thomas Freese
 */
public class CpuInfos
{
    /**
     *
     */
    private static final CpuInfo DEFAUL_CPU_INFO = new CpuInfo();

    /**
     *
     */
    private final Map<Integer, CpuInfo> infos;

    /**
     *
     */
    private final int numCpus;

    /**
     * Erstellt ein neues {@link CpuInfos} Object.
     */
    public CpuInfos()
    {
        this(Runtime.getRuntime().availableProcessors(), Collections.emptyMap());
    }

    /**
     * Erstellt ein neues {@link CpuInfos} Object.
     *
     * @param numCpus int
     * @param infos {@link Map}
     */
    public CpuInfos(final int numCpus, final Map<Integer, CpuInfo> infos)
    {
        super();

        this.numCpus = numCpus;
        this.infos = infos;
    }

    /**
     * Infos pro Kern.
     *
     * @param core int
     * @return {@link CpuInfo}
     */
    public CpuInfo get(final int core)
    {
        return this.infos.getOrDefault(core, DEFAUL_CPU_INFO);
    }

    /**
     * @return int
     */
    public int getNumCpus()
    {
        return this.numCpus;
    }

    /**
     * Zusammenfassung des Prozessors.
     *
     * @return {@link CpuInfo}
     */
    public CpuInfo getTotal()
    {
        return get(-1);
    }
}
