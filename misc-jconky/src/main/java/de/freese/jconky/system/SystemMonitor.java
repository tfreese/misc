// Created: 01.12.2020
package de.freese.jconky.system;

import java.util.Map;
import de.freese.jconky.model.CpuInfos;
import de.freese.jconky.model.CpuLoadAvg;
import de.freese.jconky.model.HostInfo;
import de.freese.jconky.model.NetworkInfos;
import de.freese.jconky.model.ProcessInfos;
import de.freese.jconky.model.UsageInfo;

/**
 * @author Thomas Freese
 */
public interface SystemMonitor
{
    /**
     * @return {@link CpuInfos}
     */
    public CpuInfos getCpuInfos();

    /**
     * @return {@link CpuLoadAvg}
     */
    public CpuLoadAvg getCpuLoadAvg();

    /**
     * @return String
     */
    public String getExternalIp();

    /**
     * @return {@link Map}
     */
    public Map<String, UsageInfo> getFilesystems();

    /**
     * @return {@link HostInfo}
     */
    public HostInfo getHostInfo();

    /**
     * @return {@link NetworkInfos}
     */
    public NetworkInfos getNetworkInfos();

    /**
     * @return int
     */
    public int getNumberOfCores();

    /**
     * @param uptimeInSeconds double
     * @param totalSystemMemory long
     * @return {@link ProcessInfos}
     */
    public ProcessInfos getProcessInfos(double uptimeInSeconds, long totalSystemMemory);

    /**
     * @return {@link Map}
     */
    public Map<String, UsageInfo> getRamAndSwap();

    /**
     * @return long
     */
    public long getTotalSystemMemory();

    /**
     * @return int
     */
    public int getUpdates();

    /**
     * @return double
     */
    public double getUptimeInSeconds();
}
