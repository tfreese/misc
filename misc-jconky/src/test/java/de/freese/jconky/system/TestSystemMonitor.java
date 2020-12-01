// Created: 01.12.2020
package de.freese.jconky.system;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.jconky.model.HostInfo;

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
    void testHostInfo()
    {
        HostInfo hostInfo = new LinuxSystemMonitor().getHostInfo();

        assertNotNull(hostInfo);
        assertNotNull(hostInfo.getName());
        assertNotNull(hostInfo.getVersion());
        assertNotNull(hostInfo.getArchitecture());
    }
}
