// Created: 01.12.2020
package de.freese.jconky.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import de.freese.jconky.model.CpuInfo;
import de.freese.jconky.model.CpuInfos;
import de.freese.jconky.model.CpuLoadAvg;
import de.freese.jconky.model.CpuTimes;
import de.freese.jconky.model.HostInfo;
import de.freese.jconky.model.NetworkInfos;
import de.freese.jconky.model.ProcessInfo;
import de.freese.jconky.model.ProcessInfos;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestSystemMonitor
{
    /**
     * @return {@link SystemMonitor}
     */
    private SystemMonitor createSystemMonitor()
    {
        return new LinuxSystemMonitor();
    }

    /**
    *
    */
    @Test
    @EnabledOnOs(OS.LINUX)
    void testCpuInfos()
    {
        SystemMonitor systemMonitor = createSystemMonitor();

        CpuInfos cpuInfos = systemMonitor.getCpuInfos();
        assertNotNull(cpuInfos);

        CpuInfo cpuInfo = cpuInfos.get(-1);
        assertNotNull(cpuInfos);
        assertEquals(-1, cpuInfo.getCore());

        int processors = Runtime.getRuntime().availableProcessors();

        for (int i = 0; i < processors; i++)
        {
            cpuInfo = cpuInfos.get(i);
            assertNotNull(cpuInfos);
            assertEquals(i, cpuInfo.getCore());

            // Temperaturen sind nur für die "realen" Cores verfügbar.
            if (i < (processors / 2))
            {
                assertTrue(cpuInfo.getTemperature() > 0D);
            }
        }

        CpuTimes cpuTimes = cpuInfo.getCpuTimes();
        assertNotNull(cpuTimes);
    }

    /**
    *
    */
    @Test
    @EnabledOnOs(OS.LINUX)
    void testCpuLoadAvg()
    {
        SystemMonitor systemMonitor = createSystemMonitor();

        CpuLoadAvg loadAvg = systemMonitor.getCpuLoadAvg();

        assertNotNull(loadAvg);
        assertTrue(loadAvg.getOneMinute() > 0D);
        assertTrue(loadAvg.getFiveMinutes() > 0D);
        assertTrue(loadAvg.getFifteenMinutes() > 0D);
    }

    /**
    *
    */
    @Test
    void testExternalIp()
    {
        SystemMonitor systemMonitor = createSystemMonitor();

        String externalIp = systemMonitor.getExternalIp();

        assertNotNull(externalIp);
        assertTrue(!externalIp.isBlank());
    }

    /**
     *
     */
    @Test
    @EnabledOnOs(OS.LINUX)
    void testHostInfo()
    {
        SystemMonitor systemMonitor = createSystemMonitor();

        HostInfo hostInfo = systemMonitor.getHostInfo();

        assertNotNull(hostInfo);
        assertNotNull(hostInfo.getName());
        assertNotNull(hostInfo.getVersion());
        assertNotNull(hostInfo.getArchitecture());
    }

    /**
    *
    */
    @Test
    @EnabledOnOs(OS.LINUX)
    void testNetworkInfos()
    {
        SystemMonitor systemMonitor = createSystemMonitor();

        NetworkInfos networkInfos = systemMonitor.getNetworkInfos();

        assertNotNull(networkInfos);
        assertTrue(networkInfos.size() > 1);
        assertNotNull(networkInfos.getProtocolInfo());
    }

    /**
    *
    */
    @Test
    @EnabledOnOs(OS.LINUX)
    void testProcessInfos()
    {
        SystemMonitor systemMonitor = createSystemMonitor();

        double uptimeInSeconds = systemMonitor.getUptimeInSeconds();
        long totalSystemMemory = systemMonitor.getTotalSystemMemory();

        ProcessInfos processInfos = systemMonitor.getProcessInfos(uptimeInSeconds, totalSystemMemory);
        assertNotNull(processInfos);

        for (ProcessInfo processInfo : processInfos.getSortedByName(Integer.MAX_VALUE))
        {
            if ("clementine".equals(processInfo.getName()))
            {
                System.out.print("");
            }

            System.out.println(processInfo);
        }
    }
}
