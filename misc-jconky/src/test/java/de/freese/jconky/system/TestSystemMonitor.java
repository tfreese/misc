// Created: 01.12.2020
package de.freese.jconky.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.jconky.model.CpuInfo;
import de.freese.jconky.model.CpuInfos;
import de.freese.jconky.model.CpuLoadAvg;
import de.freese.jconky.model.CpuTimes;
import de.freese.jconky.model.HostInfo;
import de.freese.jconky.model.ProcessInfo;
import de.freese.jconky.model.ProcessInfos;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
class TestSystemMonitor
{
    /**
    *
    */
    @Test
    void testCpuInfos()
    {
        CpuInfos cpuInfos = new LinuxSystemMonitor().getCpuInfos();
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
    void testCpuLoadAvg()
    {
        CpuLoadAvg loadAvg = new LinuxSystemMonitor().getCpuLoadAvg();

        assertNotNull(loadAvg);
        assertTrue(loadAvg.getOneMinute() > 0D);
        assertTrue(loadAvg.getFiveMinutes() > 0D);
        assertTrue(loadAvg.getFifteenMinutes() > 0D);
    }

    /**
     *
     */
    @Test
    void testHostInfo()
    {
        HostInfo hostInfo = new LinuxSystemMonitor().getHostInfo();

        assertNotNull(hostInfo);
        assertNotNull(hostInfo.getName());
        assertNotNull(hostInfo.getVersion());
        assertNotNull(hostInfo.getArchitecture());
    }

    /**
    *
    */
    @Test
    void testProcessInfos()
    {
        ProcessInfos processInfos = new LinuxSystemMonitor().getProcessInfos();
        assertNotNull(processInfos);

        for (ProcessInfo processInfo : processInfos.getSortedByName(Integer.MAX_VALUE))
        {
            System.out.println(processInfo);
        }
    }
}
