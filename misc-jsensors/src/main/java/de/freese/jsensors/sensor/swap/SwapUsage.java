// Created: 01.06.2017
package de.freese.jsensors.sensor.swap;

import com.jezhumble.javasysmon.JavaSysMon;
import com.jezhumble.javasysmon.MemoryStats;
import de.freese.jsensors.sensor.AbstractSensor;
import de.freese.jsensors.utils.Utils;

/**
 * Sensor für die Swap Auslastung.
 *
 * @author Thomas Freese
 */
public class SwapUsage extends AbstractSensor
{
    /**
     *
     */
    private final JavaSysMon monitor;

    /**
     * Erzeugt eine neue Instanz von {@link SwapUsage}.
     */
    public SwapUsage()
    {
        super();

        this.monitor = new JavaSysMon();
    }

    /**
     * @see de.freese.jsensors.sensor.AbstractSensor#scanValue()
     */
    @Override
    protected void scanValue() throws Exception
    {
        MemoryStats stats = this.monitor.swap();
        double free = stats.getFreeBytes();
        double total = stats.getTotalBytes();
        double usage = (1.0D - (free / total)) * 100.0D;

        String value = Utils.format(usage);

        save(value);
    }
}
