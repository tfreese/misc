// Created: 01.12.2020
package de.freese.jconky.system;

import de.freese.jconky.model.HostInfo;

/**
 * @author Thomas Freese
 */
public interface SystemMonitor
{
    /**
     * @return {@link HostInfo}
     */
    public HostInfo getHostInfo();
}
