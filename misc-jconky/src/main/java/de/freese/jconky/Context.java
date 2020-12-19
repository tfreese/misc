// Created: 13.12.2020
package de.freese.jconky;

import de.freese.jconky.model.CpuInfos;
import de.freese.jconky.model.CpuLoadAvg;
import de.freese.jconky.model.HostInfo;
import de.freese.jconky.model.NetworkInfos;
import de.freese.jconky.model.ProcessInfos;
import de.freese.jconky.system.SystemMonitor;

/**
 * @author Thomas Freese
 */
public final class Context
{
    /**
     * ThreadSafe Singleton-Pattern.
     *
     * @author Thomas Freese
     */
    private static final class JConkyContextHolder
    {
        /**
         *
         */
        private static final Context INSTANCE = new Context();

        /**
         * Erstellt ein neues {@link JConkyContextHolder} Object.
         */
        private JConkyContextHolder()
        {
            super();
        }
    }

    /**
     * @return {@link Context}
     */
    public static Context getInstance()
    {
        return JConkyContextHolder.INSTANCE;
    }

    /**
     *
     */
    private CpuInfos cpuInfos = new CpuInfos();

    /**
     *
     */
    private CpuLoadAvg cpuLoadAvg = new CpuLoadAvg();

    /**
     *
     */
    private String externalIp = "";

    /**
     *
     */
    private HostInfo hostInfo = new HostInfo();

    /**
     *
     */
    private NetworkInfos networkInfos = new NetworkInfos();

    /**
     *
     */
    private int numberOfCores;

    /**
     *
     */
    private ProcessInfos processInfos = new ProcessInfos();

    /**
     *
     */
    private long totalSystemMemory;

    /**
     *
     */
    private double uptimeInSeconds;

    /**
     * Erstellt ein neues {@link Context} Object.
     */
    private Context()
    {
        super();
    }

    /**
     * @return {@link CpuInfos}
     */
    public CpuInfos getCpuInfos()
    {
        return this.cpuInfos;
    }

    /**
     * @return {@link CpuLoadAvg}
     */
    public CpuLoadAvg getCpuLoadAvg()
    {
        return this.cpuLoadAvg;
    }

    /**
     * @return String
     */
    public String getExternalIp()
    {
        return this.externalIp;
    }

    /**
     * @return {@link HostInfo}
     */
    public HostInfo getHostInfo()
    {
        return this.hostInfo;
    }

    /**
     * @return {@link NetworkInfos}
     */
    public NetworkInfos getNetworkInfos()
    {
        return this.networkInfos;
    }

    /**
     * @return int
     */
    public int getNumberOfCores()
    {
        return this.numberOfCores;
    }

    /**
     * @return {@link ProcessInfos}
     */
    public ProcessInfos getProcessInfos()
    {
        return this.processInfos;
    }

    /**
     * @return {@link Settings}
     */
    private Settings getSettings()
    {
        return Settings.getInstance();
    }

    /**
     * @return {@link SystemMonitor}
     */
    private SystemMonitor getSystemMonitor()
    {
        return getSettings().getSystemMonitor();
    }

    /**
     * @return long
     */
    public long getTotalSystemMemory()
    {
        return this.totalSystemMemory;
    }

    /**
     * @return double
     */
    public double getUptimeInSeconds()
    {
        return this.uptimeInSeconds;
    }

    /**
     * Daten, die alle paar Muniten aktualisiert werden müssen.
     */
    public void updateLongScheduled()
    {
        this.hostInfo = getSystemMonitor().getHostInfo();
    }

    /**
     * Daten, die einmal ermittelt werden müssen.
     */
    public void updateOneShot()
    {
        this.numberOfCores = getSystemMonitor().getNumberOfCores();
        this.totalSystemMemory = getSystemMonitor().getTotalSystemMemory();
        this.externalIp = getSystemMonitor().getExternalIp();
    }

    /**
     * Daten, die alle paar Sekunden aktualisiert werden müssen.
     */
    public void updateShortScheduled()
    {
        this.cpuLoadAvg = getSystemMonitor().getCpuLoadAvg();
        this.uptimeInSeconds = getSystemMonitor().getUptimeInSeconds();

        // CpuUsages berechnen.
        CpuInfos cpuInfosPrevious = this.cpuInfos;
        this.cpuInfos = getSystemMonitor().getCpuInfos();

        double usage = this.cpuInfos.getTotal().getCpuTimes().getCpuUsage(cpuInfosPrevious.getTotal().getCpuTimes());
        this.cpuInfos.getTotal().setUsage(usage);

        for (int i = 0; i < getNumberOfCores(); i++)
        {
            usage = this.cpuInfos.get(i).getCpuTimes().getCpuUsage(cpuInfosPrevious.get(i).getCpuTimes());
            this.cpuInfos.get(i).setUsage(usage);
        }

        this.processInfos = getSystemMonitor().getProcessInfos(getUptimeInSeconds(), getTotalSystemMemory());
        this.networkInfos = getSystemMonitor().getNetworkInfos();
    }
}
